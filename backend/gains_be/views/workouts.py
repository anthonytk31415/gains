# Django imports
from django.http import HttpResponse, JsonResponse
from django.views.decorators.http import require_http_methods
from ..models import Workout, ExerciseSet, Exercise, User
from django.core.serializers import serialize
from django.views.decorators.csrf import csrf_exempt
import json
from ..services.gemini_service import generate_workout_routine, format_llm_workout
import datetime
from datetime import datetime as dt




class ExerciseSetData: 
    def __init__(self, exercise_id, sets, reps, weight): 
        self.exercise_id = exercise_id
        self.sets = sets
        self.reps = reps
        self.weight = weight 
        
    def __str__(self):
        return "exercise_id: {}, reps: {}, sets: {}, weight: {}".format(self.exercise_id, self.reps, self.sets, self.weight)
        
    def get_json_properties(self): 
        return {
            "exercise_id": self.exercise_id,
            "sets": self.sets,
            "reps": self.reps, 
            "weight": self.weight, 
            "is_done": False
        }


def get_workout_objects(schedule_llm): 
    """given LLM output, get the workout frontend payload"""
    workouts = []
    for workout in schedule_llm: 
        # print("workout: ", i)
        exercise_sets = []
        for exercise_set in workout: 
            ex_object = ExerciseSetData(exercise_set['exercise_id'], exercise_set['sets'], exercise_set['reps'], exercise_set['weight'])        
            exercise_sets.append(ex_object.get_json_properties())
            # print(ex_object)
        clean_workout = {
            "created_at": datetime.date.today(),
            "exercise_sets": exercise_sets
        }
        workouts.append(clean_workout)
    return workouts

def build_exercise_set(exercise_set_data, workout):
    '''Given an exercise set data, return an exercise set object.'''
    return ExerciseSet.objects.create(
        workout=workout,
        exercise_id=exercise_set_data['exercise_id'],
        sets=exercise_set_data['sets'],
        reps=exercise_set_data['reps'],
        weight=exercise_set_data['weight']
    )


def serialize_exercise_set(exercise_set): 
    '''Given an exercise set, return a dictionary of the exercise set.'''
    return {
        # 'exercise_set_id': exercise_set.exercise_set_id,
        'exercise_id': exercise_set.exercise.exercise_id,
        # 'exercise_name': exercise_set.exercise.name,
        'sets': exercise_set.sets,
        'reps': exercise_set.reps,
        'weight': float(exercise_set.weight),
        'is_done': exercise_set.is_done
    }

def serialize_workout(workout): 
    return {
        'workout_id': workout.workout_id,
        # 'user_id': workout.user.user_id,
        'execution_date': workout.execution_date.isoformat() if workout.execution_date else None,
        'created_at': workout.created_at.date().isoformat() if workout.created_at else None,
        'exercise_sets': [serialize_exercise_set(exercise_set) for exercise_set in workout.exercise_sets.all()]
    }

# def deserialize_workout(workout):
#     ''' Given a workout object, return a workout object that can be saved to the database.''' 
#     pass

# NEED TO TEST
def update_exercise_set(exercise_set_object):
    '''Prepare an exercise set with the provided data.
    if it is acceptable, return the exercise set. If not, raise an exception. 
    '''
    try:
        exercise_set_id = exercise_set_object.get('exercise_set_id')
        if not exercise_set_id:
            raise ValueError('exercise_set_id is required')
            
        try:
            exercise_set = ExerciseSet.objects.get(exercise_set_id=exercise_set_id)
        except ExerciseSet.DoesNotExist:
            raise ValueError('Exercise set not found')
            
        # Update the exercise set fields
        fields_to_update = ['exercise_id', 'sets', 'reps', 'weight', 'is_done']
        for field in exercise_set_object:
            print(field)
            # if field in fields_to_update:
            #     setattr(exercise_set, field, exercise_set_object[field])
            # else:
            #     raise ValueError(f'Invalid field: {field}')            
        return exercise_set
    except KeyError as e:
        raise ValueError(f'Missing required field: {str(e)}')
    except Exception as e:
        raise ValueError(str(e))

# NEED TO TEST
def delete_exercise_set(exercise_set_id):
    '''Given an exercise set id, delete the exercise set.'''
    try:
        exercise_set = ExerciseSet.objects.get(set_id=exercise_set_id)
        exercise_set.delete()
    except ExerciseSet.DoesNotExist:
        raise ValueError('Exercise set not found')

# NEED TO TEST
def delete_old_exercise_sets(set_old_exercise_sets_ids, set_new_exercise_sets_ids):
    '''Given a set of old exercise sets and a set of new exercise sets, delete the old exercise sets.'''
    for old_exercise_set_id in set_old_exercise_sets_ids:
        if old_exercise_set_id not in set_new_exercise_sets_ids:
            delete_exercise_set(old_exercise_set_id)

# NEED TO TEST
@csrf_exempt
@require_http_methods(["PUT"])
def update_workout(request, user_id): 
    '''Given a workout id, update the workout, or return a json response with an error.'''
    try:
        data = json.loads(request.body)
        workout_object = data.get('workout')
        workout_id = workout_object['workout_id']
        workout = Workout.objects.get(workout_id=workout_id)        
        if workout_object.get('execution_date'): 
            workout.execution_date = workout_object['execution_date']

        new_exercise_sets = workout_object['exercise_sets']
        if not new_exercise_sets: 
            raise ValueError('No exercise sets provided')

        # print([exercise_set.get('exercise_set_id') for exercise_set in new_exercise_sets])
        # exercise_set_objects_to_save = [update_exercise_set(exercise_set) for exercise_set in new_exercise_sets]
        
        # # all exercises and workouts are valid
        # # delete old exercise sets that are not in the new workout
        # set_old_exercise_sets_ids = set([exercise_set.exercise_set_id for exercise_set in workout.exercise_sets.all()])
        # set_new_exercise_sets_ids = set([exercise_set.exercise_set_id for exercise_set in exercise_set_objects_to_save])
        # delete_old_exercise_sets(set_old_exercise_sets_ids, set_new_exercise_sets_ids)
        # workout.exercise_sets.set(exercise_set_objects_to_save)

        # # save all exercise sets and workout and return payload
        # for exercise_set in exercise_set_objects_to_save:
        #     exercise_set.save()
        # workout.save()
        return JsonResponse({
            'message': 'Successfully updated workout',
            # 'workout': workout_object
            # 'workout': serialize_workout(workout)
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
        if not user_id:
            return JsonResponse({'error': 'user_id is required'}, status=400)            
        try:
            User.objects.get(user_id=user_id)
        except User.DoesNotExist:
            return JsonResponse({'error': 'User not found'}, status=404)
            
        workouts = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).filter(
            user_id=user_id
        ).order_by('-execution_date')
        workouts_data = [serialize_workout(workout) for workout in workouts]
        return JsonResponse({'workouts': workouts_data})    
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

def get_last_month_workouts(request): 
    pass

def get_this_month_workouts(request): 
    pass


def create_workout(request): 
    pass

def delete_workout(request, user_id, workout_id):
    '''Given a user_id and a workout_id, delete the workout. Cascade delete the exercise sets.'''
    try:            
        workout = Workout.objects.get(workout_id=workout_id)
        workout.delete()
    except Workout.DoesNotExist:
        return JsonResponse({'error': 'Workout not found'}, status=404)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

def mark_exercise_set_done(request): 
    pass

def mark_exercise_set_undone(request): 
    pass

def mark_workout_done(request): 
    pass

def mark_workout_undone(request): 
    pass



@csrf_exempt
@require_http_methods(["POST"])
def generate_workout(request, user_id): 
    #First upon creation of the account
    #Second Add Workout go to AI this part (random)
    #Third Add Workout (history)
    '''Given user input, call the LLM, generate a workout, save it, and return the workout.'''

    try:
        data = json.loads(request.body)        
        print("data for generate workout call: ", data)

        #Extracting the structured data from  the frontend
        age = data.get('age')
        height = data.get('height')
        weight = data.get('weight')
        goal = data.get('goal')
        experience = data.get('experience')  # Example: "Intermediate"
        workout_days = data.get('workout_days', 5)  # Default to 5 days/week if not provided
        location = data.get('location', 'Gym')  # Default to 'Gym'
        muscle_focus = data.get('muscle_focus')

        # Build the llm form here
        form_text = f"""
        I am a {age}-year-old individual.
        My height is {height} and my weight is {weight} kgs.
        My goal is {goal}.
        My experience level is {experience}.
        I am willing to work {workout_days} days a week.
        I will workout from {location}.
        I want to build my {muscle_focus} muscles.
        Give me a workout routine only for targeted muscle.
        """

        if not form_text.strip():
            return JsonResponse({'error': 'Invalid or incomplete form data'}, status=400)
        
        # call LLM and get the workout 
        # create dummy workout id as a placeholder
        # print("calling LLM for workout...")

        workouts_data_llm = generate_workout_routine(form_text)
        workkouts_data = get_workout_objects(workouts_data_llm)
        # workout_data = format_llm_workout(workout_data_llm)
        print("workkouts_data: ", workkouts_data)
        if 'error' in workouts_data_llm:
            return JsonResponse({'error': workouts_data_llm['error']}, status=500)

        return JsonResponse({
            'message': 'Successfully created workout',
            'schedule': workkouts_data
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
        if not user_id:
            return JsonResponse({'error': 'user_id is required'}, status=400)            
        try:
            User.objects.get(user_id=user_id)
        except User.DoesNotExist:
            return JsonResponse({'error': 'User not found'}, status=404)

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
def save_workout(request, user_id):
    '''Save a new workout from request data to the database.'''
    try:
        if not user_id:
            return JsonResponse({'error': 'user_id is required'}, status=400)            
        try:
            User.objects.get(user_id=user_id)
        except User.DoesNotExist:
            return JsonResponse({'error': 'User not found'}, status=404)    
        data = json.loads(request.body)        
        workout_data = data['workout']
        # Parse the workout date
        try:
            if workout_data['execution_date']:
                execution_date = dt.fromisoformat(workout_data['execution_date'].replace('Z', '+00:00'))
            else: 
                execution_date = None
        except (ValueError, TypeError) as e:
            return JsonResponse({'error': f'Invalid date format: {str(e)}'}, status=400)
        # Create the workout
        try:
            workout = Workout.objects.create(
                user_id=user_id,
                execution_date=execution_date
            )
        except Exception as e:
            print("Error creating workout:", str(e))  # Debug log
            return JsonResponse({'error': f'Failed to create workout: {str(e)}'}, status=500)
        # Create exercise sets
        for set_data in workout_data['exercise_sets']:
            try:
                exercise_set = ExerciseSet.objects.create(
                    workout=workout,
                    exercise_id=set_data['exercise_id'],
                    sets=set_data.get('sets', 1),  # Default to 1 if not provided
                    reps=set_data['reps'],
                    weight=set_data['weight'],
                    is_done=set_data.get('is_done', False)
                )
            except Exception as e:
                workout.delete()
                return JsonResponse({'error': f'Failed to create exercise set: {str(e)}'}, status=500)
        return JsonResponse({
            'message': 'Successfully saved workout',
            'workout': serialize_workout(workout)
        })
        
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        print("Unexpected error:", str(e))  # Debug log
        return JsonResponse({'error': str(e)}, status=500)




# save workouts 