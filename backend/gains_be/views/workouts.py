# Django imports
from django.http import HttpResponse, JsonResponse
from django.views.decorators.http import require_http_methods
from ..models import Workout, ExerciseSet, Exercise, User
from django.core.serializers import serialize
from django.views.decorators.csrf import csrf_exempt
from django.db import transaction

import json
from ..services.gemini_service import generate_workout_routine, format_llm_workout, build_llm_form
import datetime
from datetime import datetime as dt
import logging

class ExerciseSetData: 
    def __init__(self, exercise_set_id, exercise_id, sets, reps, weight): 
        self.exercise_set_id = exercise_set_id
        self.exercise_id = exercise_id
        self.sets = sets
        self.reps = reps
        self.weight = weight 
        
    def __str__(self):
        return "exercise_set_id: {}, exercise_id: {}, reps: {}, sets: {}, weight: {}".format(self.exercise_set_id, self.exercise_id, self.reps, self.sets, self.weight)
        
    def get_json_properties(self): 
        return {
            "exercise_set_id": self.exercise_set_id,
            "exercise_id": self.exercise_id,
            "sets": self.sets,
            "reps": self.reps, 
            "weight": self.weight, 
            "is_done": False
        }
# to do: 
# - add Schedule Data 
# - add LLM input data


def is_valid_user_id(user_id):
    '''Given a user_id, return a tuple of (is_valid, error_response).
    If valid, returns (True, None).
    If invalid, returns (False, JsonResponse with error).'''
    if not user_id:
        return False, JsonResponse({'error': 'user_id is required'}, status=400)            
    try:
        User.objects.get(user_id=user_id)
        return True, None
    except User.DoesNotExist:
        return False, JsonResponse({'error': 'User not found'}, status=404)

def is_valid_workout_ids(workout_ids ): 
    '''Given a list of workout ids, return True if they are valid, False otherwise.
    '''
    for workout_id in workout_ids: 
        try: 
            Workout.objects.get(workout_id=workout_id)
        except Workout.DoesNotExist:
            return False
    return True

def is_valid_exercise_set_object(exercise_set_object):
    '''Given an exercise set object, return True if it is valid, False otherwise.
    Only accounts for structure, not whether it exists in db. 
    '''
    if not exercise_set_object: 
        return False
    for field in ['exercise_id', 'sets', 'reps', 'weight']:
        if field not in exercise_set_object:
            return False
    return True

# when do we really need to check the valid user_id with workout check? 
def is_valid_workout_object(workout_data_object):
    '''Given a workout object, return True if valid i.e. 
    (1) the workout object is not None, and 
    (2) all of the exercise_set items are accounted for. 
    Return False otherwise.
    Just checks for structural integrity, not user id matching
    '''
    if not workout_data_object:
        return False, ValueError('Workout data object not provided')
    exercise_sets = workout_data_object.get('exercise_sets')
    if not exercise_sets: 
        return False, ValueError("Exercise sets provided is empty")
    for exercise_set in exercise_sets:
        if not is_valid_exercise_set_object(exercise_set):
            return False, ValueError('Invalid exercise set object')
    return True, None

def is_valid_workout_db_object(workout_id, user_id):
    workout = Workout.objects.get(workout_id=workout_id)
    if not workout:
        return False, ValueError('Workout not found in database')
    if workout.user.user_id != user_id:
        return False, ValueError('Invalid user id associated with workout')
    return True, None

def get_workout_objects_from_llm(schedule_llm): 
    """Given LLM output, get the workout frontend payload"""
    workouts = []
    for workout in schedule_llm: 
        exercise_sets = []
        for exercise_set in workout: 
            ex_object = ExerciseSetData(None, exercise_set['exercise_id'], exercise_set['sets'], exercise_set['reps'], exercise_set['weight'])        
            exercise_sets.append(ex_object.get_json_properties())
        clean_workout = {
            "created_at": datetime.date.today(),
            "exercise_sets": exercise_sets
        }
        workouts.append(clean_workout)
    return workouts

def create_exercise_set(exercise_set_data, workout):
    '''Given an exercise set data, return an exercise set object.'''
    return ExerciseSet.objects.create(
        workout=workout,
        exercise_id=exercise_set_data['exercise_id'],
        sets=exercise_set_data['sets'],
        reps=exercise_set_data['reps'],
        weight=exercise_set_data['weight'],
        is_done=exercise_set_data['is_done']
    )

def update_exercise_set(exercise_set_data):
    exercise_set_object = ExerciseSet.objects.get(exercise_set_id=exercise_set_data['exercise_set_id'])
    for attr, val in exercise_set_data.items():
        setattr(exercise_set_object, attr, val)
    exercise_set_object.save()

def serialize_exercise_set(exercise_set): 
    '''Given an exercise set, return a dictionary of the exercise set.'''
    return {
        'exercise_set_id': exercise_set.exercise_set_id,
        'exercise_id': exercise_set.exercise.exercise_id,
        'sets': exercise_set.sets,
        'reps': exercise_set.reps,
        'weight': float(exercise_set.weight),
        'is_done': exercise_set.is_done
    }

def serialize_workout(workout): 
    return {
        'workout_id': workout.workout_id,
        'created_at': workout.created_at.date().isoformat() if workout.created_at else None,
        'execution_date': workout.execution_date.isoformat() if workout.execution_date else None,
        'exercise_sets': [serialize_exercise_set(exercise_set) for exercise_set in workout.exercise_sets.all()]
    }

def create_workout(workout_data, user_id):
    if not is_valid_workout_object(workout_data):
        raise ValueError("Invalid workout object")
    with transaction.atomic():
        workout = Workout.objects.create(
            user_id=user_id,
            execution_date= dt.fromisoformat(
                workout_data.get('execution_date').replace('Z', '+00:00')
                ).date() if 'execution_date' in workout_data else None
        )
        for set_data in workout_data['exercise_sets']:
            create_exercise_set(set_data, workout)
        return workout

def create_workouts(schedule, user_id):
    results = []
    with transaction.atomic():
        for workout_data in schedule:
            try:
                workout = create_workout(workout_data, user_id)
                results.append(workout)
            except Exception as e:
                logging.error(f"Failed to save workout: {e}")
                raise 
    return results

@csrf_exempt
@require_http_methods(["POST"])
def save_workout(request, user_id):
    '''Save a new workout from request data to the database.
    All inputs are new and saved to db. 
    '''
    try:
        is_valid, err = is_valid_user_id(user_id)
        if not is_valid:
            return err
        data = json.loads(request.body)        
        workout_data = data['workout']
        is_valid = is_valid_workout_object(workout_data)
        if not is_valid:
            return JsonResponse({'error': 'Invalid workout object'}, status=400)
        
        workout = create_workout(workout_data, user_id)
        return JsonResponse({
            'message': 'Successfully saved workout',
            'workout': serialize_workout(workout)
        })
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        logging.error(f"Unexpected error: {str(e)}")
        return JsonResponse({'error': str(e)}, status=500)

def update_workout_helper(workout_data_object):
    ''' Assumes a valid data object'''
    workout_id = workout_data_object['workout_id']
    workout = Workout.objects.get(workout_id=workout_id)

    with transaction.atomic():
        # handle exercise_sets
        new_exercise_sets = workout_data_object['exercise_sets']
        new_exercise_set_ids = []
        for new_exercise_set in new_exercise_sets: 
            # udpate new ones 
            if 'exercise_set_id' in new_exercise_set and new_exercise_set['exercise_set_id'] != None: 
                update_exercise_set(new_exercise_set)
                new_exercise_set_ids.append(new_exercise_set['exercise_set_id'])
            # create new ones if not in db
            else: 
                new_exercise_set_obj = create_exercise_set(new_exercise_set, workout)
                new_exercise_set_ids.append(new_exercise_set_obj.exercise_set_id)

        # remove old ones
        for old_exercise_set_id in [exercise_set.exercise_set_id for exercise_set in workout.exercise_sets.all()]: 
            if old_exercise_set_id not in new_exercise_set_ids:
                ExerciseSet.objects.get(exercise_set_id=old_exercise_set_id).delete()

        # update workout data
        if workout_data_object.get('execution_date'):
            workout.execution_date = dt.fromisoformat(workout_data_object['execution_date'].replace('Z', '+00:00')).date()
        workout.save() 
        return workout

@csrf_exempt
@require_http_methods(["PUT"])
def update_workout(request, user_id, workout_id): 
    '''Given a workout id, update the workout, or return a json response with an error.'''
    try:
        # check validity of: (1) data, (2) user_id, (3) workout_id, (4) workout_object
        data = json.loads(request.body)
        if not data: 
            raise ValueError('No data provided')
        workout_data_object = data.get('workout')        
        is_valid_workout_data_object, err = is_valid_workout_object(workout_data_object)
        if not is_valid_workout_data_object:
            return err
        is_valid_workout_db_obj, err = is_valid_workout_db_object(workout_id, user_id)
        if not is_valid_workout_db_obj:
            return err  
        
        workout = update_workout_helper(workout_data_object)      
        return JsonResponse({
            'message': 'Successfully updated workout',
            'workout': serialize_workout(workout)
        })
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

@csrf_exempt
@require_http_methods(["GET"])
def get_workouts(request, user_id):     
    '''Given a user_id, return all workouts.'''
    try:            
        is_valid, err = is_valid_user_id(user_id)
        if not is_valid:
            return err
        workouts = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).filter(
            user_id=user_id
        ).order_by('-execution_date')
        workouts_data = [serialize_workout(workout) for workout in workouts]
        return JsonResponse({'workouts': workouts_data})    
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

def get_last_week_range(date):
    weekdate_num = (date.weekday() + 1)%7    
    last_sun = date + datetime.timedelta(days=- weekdate_num - 7)
    last_sat = last_sun + datetime.timedelta(days=6)    
    return last_sun, last_sat

def get_current_week_range(date):
    weekdate_num = (date.weekday() + 1)%7    
    cur_sun = date + datetime.timedelta(days=- weekdate_num)
    cur_sat = cur_sun + datetime.timedelta(days=6)    
    return cur_sun, cur_sat

@csrf_exempt
@require_http_methods(["GET"])
def get_last_week_workouts(request, user_id): 
    '''Given a user_id, return all workouts for the last week.'''
    try:
        if not user_id:
            return JsonResponse({'error': 'user_id is required'}, status=400)            
        try:
            User.objects.get(user_id=user_id)
        except User.DoesNotExist:
            return JsonResponse({'error': 'User not found'}, status=404)
        
        start_date, end_date = get_last_week_range(datetime.datetime.now())
        workouts = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).filter(
            user_id=user_id,
            execution_date__gte=start_date,
            execution_date__lte=end_date
        ).order_by('-execution_date')
        workouts_data = [serialize_workout(workout) for workout in workouts]
        return JsonResponse({'schedule': workouts_data})
    
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

@csrf_exempt
@require_http_methods(["GET"])
def get_current_week_workouts(request, user_id): 
    '''Given a user_id, return all workouts for the current week.'''
    try:
        if not user_id:
            return JsonResponse({'error': 'user_id is required'}, status=400)            
        try:
            User.objects.get(user_id=user_id)
        except User.DoesNotExist:
            return JsonResponse({'error': 'User not found'}, status=404)
        
        start_date, end_date = get_current_week_range(datetime.datetime.now())
        workouts = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).filter(
            user_id=user_id,
            execution_date__gte=start_date,
            execution_date__lte=end_date
        ).order_by('-execution_date') 
        workouts_data = [serialize_workout(workout) for workout in workouts]
        return JsonResponse({'schedule': workouts_data})
    
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)


@csrf_exempt
@require_http_methods(["POST"])
def generate_workout(request, user_id): 
    '''Given user input, call the LLM, generate a workout, save it, and return the workout.'''
    try:
        is_valid, err = is_valid_user_id(user_id)
        if not is_valid:
            return err
        data = json.loads(request.body)        
        # TO DO: extract data in a predefined object
        form_text = build_llm_form(data)
        if not form_text.strip():
            return JsonResponse({'error': 'Invalid or incomplete form data'}, status=400)        
        # call LLM and get the workout 
        workouts_data_llm = generate_workout_routine(form_text)
        workouts_data = get_workout_objects_from_llm(workouts_data_llm)
        if 'error' in workouts_data_llm:
            return JsonResponse({'error': workouts_data_llm['error']}, status=500)
        return JsonResponse({
            'message': 'Successfully created workout',
            'schedule': workouts_data
        })
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)
    
@csrf_exempt
@require_http_methods(["GET"])
def get_workout(request, user_id, workout_id):
    '''Given a workout id, return the workout.'''
    try:
        is_valid, err = is_valid_user_id(user_id)
        if not is_valid:
            return err

        workout = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).get(workout_id=workout_id)       
        return JsonResponse(serialize_workout(workout))
        
    except Workout.DoesNotExist:
        return JsonResponse({'error': 'Workout not found'}, status=404)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)


@csrf_exempt
@require_http_methods(["POST"])
def save_schedule(request, user_id):
    '''Save a schedule, a list of workouts from request data to the database.
    Note: we don't define it anywhere, but a schedule is a list of workouts.
    '''
    try:
        # check validity of user_id, schedule, workout_ids, exercise_set_objects
        is_valid, err = is_valid_user_id(user_id)
        if not is_valid:
            return err
        data = json.loads(request.body)        
        schedule = data['schedule']
        for workout_data in schedule: 
            if not is_valid_workout_object(workout_data):
                return JsonResponse({'error': 'Invalid workout object'}, status=400)
        # Create all workouts gracefully
        workouts_created = []
        try: 
            with transaction.atomic():
                workouts_created = create_workouts(schedule, user_id)
                return JsonResponse({
                    'message': 'Successfully saved workout',
                    'schedule': [serialize_workout(workout) for workout in workouts_created]
                })
        except Exception as e:
            return JsonResponse({'error': f'Failed to create workout: {str(e)}'}, status=500)

    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        logging.error(f"Unexpected error: {e}")
        return JsonResponse({'error': str(e)}, status=500)
    



@csrf_exempt
@require_http_methods(["PUT"])
def update_schedule(request, user_id): 
    '''Update a schedule, a list of workouts from request data to the database.'''
    try:
        # check validity of user_id, schedule, workout_ids, exercise_set_objects
        is_valid, err = is_valid_user_id(user_id)
        if not is_valid:
            return err
        data = json.loads(request.body)
        schedule = data['schedule']

        workout_ids = [workout_data['workout_id'] for workout_data in schedule]
        if not is_valid_workout_ids(workout_ids):
            return JsonResponse({'error': 'Invalid workout ids'}, status=400)

        try: 
            with transaction.atomic(): 
                workouts_updated = []
                for workout_data in schedule: 
                    workouts_updated.append(update_workout_helper(workout_data))
        
            return JsonResponse({
                'message': 'Successfully saved workout',
                'schedule': [serialize_workout(workout) for workout in workouts_updated]
            })

        except Exception as e:
            return JsonResponse({'error': str(e)}, status=500)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)








# -- need upated; do we need?
# @csrf_exempt
# @require_http_methods(["GET"])
# def get_workouts_by_date_range(request, start_date, end_date): 
#     '''Given a user_id, Given a start date and end date, return all workouts in that date range.'''
#     try:
#         # Get user_id from query parameters
#         user_id = request.GET.get('user_id')
#         if not user_id:
#             return JsonResponse({'error': 'user_id is required'}, status=400)

#         # Get workouts within the date range for the user
#         workouts = Workout.objects.select_related('user').prefetch_related(
#             'exercise_sets__exercise'
#         ).filter(
#             user_id=user_id,
#             execution_date__gte=start_date,
#             execution_date__lte=end_date
#         ).order_by('-execution_date')
        
#         workouts_data = [serialize_workout(workout) for workout in workouts]
#         return JsonResponse({'workouts': workouts_data})
        
#     except Exception as e:
#         return JsonResponse({'error': str(e)}, status=500)






# # do we really need this? 
# @csrf_exempt
# @require_http_methods(["POST"])
# def save_workout(request, user_id):
#     '''Save a new workout from request data to the database.
#     All inputs are new and saved to db. 
#     '''
#     try:
#         is_valid, err = is_valid_user_id(user_id)
#         if not is_valid:
#             return err
#         data = json.loads(request.body)        
#         workout_data = data['workout']
#         is_valid = is_valid_workout_object(workout_data)
#         if not is_valid:
#             return JsonResponse({'error': 'Invalid workout object'}, status=400)
        
#         try:
#             workout = Workout.objects.create(
#                 user_id=user_id,
#                 execution_date=dt.fromisoformat(workout_data['execution_date'].replace('Z', '+00:00')).date()
#             )
#         except Exception as e:
#             print("Error creating workout:", str(e)) 
#             return JsonResponse({'error': f'Failed to create workout: {str(e)}'}, status=500)
#         # Create exercise sets
#         for set_data in workout_data['exercise_sets']:
#             try:
#                 create_exercise_set(set_data, workout)
#             except Exception as e:
#                 workout.delete()
#                 return JsonResponse({'error': f'Failed to create exercise set: {str(e)}'}, status=500)
#         return JsonResponse({
#             'message': 'Successfully saved workout',
#             'workout': serialize_workout(workout)
#         })
        
#     except json.JSONDecodeError:
#         return JsonResponse({'error': 'Invalid JSON'}, status=400)
#     except Exception as e:
#         print("Unexpected error:", str(e)) 
#         return JsonResponse({'error': str(e)}, status=500)
    



#### still needs work on the create logic.  - DELETE? 
# def create_workout(workout_data_object):
#     """ assumes valid workout data object
#     create new workput and new corresponding exercise sets""" 

#         workout = Workout.objects.create(
#             user_id=workout_data_object['user_id'],
#             execution_date=dt.fromisoformat(workout_data_object['execution_date'].replace('Z', '+00:00')).date()
#         )
#         for exercise_set_data in workout_data_object['exercise_sets']:
#             create_exercise_set(exercise_set_data, workout)
#         return workout
#     except Exception as e:
#         if workout: 
#             workout.delete()        
#         return JsonResponse({'error': f'Failed to create workout: {str(e)}'}, status=500)


# def delete_workout(request, user_id, workout_id):
#     '''Given a user_id and a workout_id, delete the workout. Cascade delete the exercise sets.'''
#     try:
#         is_valid, err = is_valid_user_id(user_id)
#         if not is_valid:
#             return err
        
#         workout = Workout.objects.get(workout_id=workout_id)
#         workout.delete()
#     except Workout.DoesNotExist:
#         return JsonResponse({'error': 'Workout not found'}, status=404)
#     except Exception as e:
#         return JsonResponse({'error': str(e)}, status=500)