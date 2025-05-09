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

def get_workout_objects(schedule_llm): 
    """given LLM output, get the workout frontend payload"""
    workouts = []
    for workout in schedule_llm: 
        exercise_sets = []
        for exercise_set in workout: 
            ex_object = ExerciseSetData(exercise_set['exercise_id'], exercise_set['sets'], exercise_set['reps'], exercise_set['weight'])        
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
def serialize_exercise_set(exercise_set): 
    '''Given an exercise set, return a dictionary of the exercise set.'''
    return {
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


def is_valid_exercise_set_object(exercise_set_object):
    '''Given an exercise set object, return True if it is valid, False otherwise.'''
    if not exercise_set_object: 
        return False
    for field in ['exercise_id', 'sets', 'reps', 'weight']:
        if field not in exercise_set_object:
            return False
    return True

def is_valid_workout_object(workout_object):
    '''Given a workout object, return True if it is valid, False otherwise.'''
    if not workout_object: 
        return False
    exercise_sets = workout_object.get('exercise_sets')
    if not exercise_sets: 
        return False
    for exercise_set in exercise_sets:
        if not is_valid_exercise_set_object(exercise_set):
            return False
    return True

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
        if not workout_data_object: 
            raise ValueError('No workout object provided')
        workout = Workout.objects.get(workout_id=workout_id)
        if not workout:
            raise ValueError('Workout not found')
        if workout.user.user_id != user_id:
            raise ValueError('Invalid user id')
        if not is_valid_workout_object(workout_data_object):
            raise ValueError('Invalid workout object')
        
        # update workout
        if workout_data_object.get('execution_date'):
            workout.execution_date = dt.fromisoformat(workout_data_object['execution_date'].replace('Z', '+00:00')).date()
        # delete all exercise sets for the workout
        ExerciseSet.objects.filter(workout_id=workout_id).delete()      
        # create new exercise sets
        for exercise_set in workout_data_object['exercise_sets']:
            create_exercise_set(exercise_set, workout)
        workout.save()
        
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


def create_workout(request): 
    pass

def delete_workout(request, user_id, workout_id):
    '''Given a user_id and a workout_id, delete the workout. Cascade delete the exercise sets.'''
    try:
        is_valid, err = is_valid_user_id(user_id)
        if not is_valid:
            return err
        
        workout = Workout.objects.get(workout_id=workout_id)
        workout.delete()
    except Workout.DoesNotExist:
        return JsonResponse({'error': 'Workout not found'}, status=404)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

@csrf_exempt
@require_http_methods(["POST"])
def generate_workout(request, user_id): 
    #First upon creation of the account
    #Second Add Workout go to AI this part (random)
    #Third Add Workout (history)
    '''Given user input, call the LLM, generate a workout, save it, and return the workout.'''

    try:
        is_valid, err = is_valid_user_id(user_id)
        if not is_valid:
            return err
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
def save_workout(request, user_id):
    '''Save a new workout from request data to the database.'''
    try:
        is_valid, err = is_valid_user_id(user_id)
        if not is_valid:
            return err
        data = json.loads(request.body)        
        workout_data = data['workout']
        is_valid = is_valid_workout_object(workout_data)
        if not is_valid:
            return JsonResponse({'error': 'Invalid workout object'}, status=400)
        
        # Create the workout
        try:
            workout = Workout.objects.create(
                user_id=user_id,
                execution_date=dt.fromisoformat(workout_data['execution_date'].replace('Z', '+00:00')).date()
            )
        except Exception as e:
            print("Error creating workout:", str(e)) 
            return JsonResponse({'error': f'Failed to create workout: {str(e)}'}, status=500)
        # Create exercise sets
        for set_data in workout_data['exercise_sets']:
            try:
                create_exercise_set(set_data, workout)
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
        print("Unexpected error:", str(e)) 
        return JsonResponse({'error': str(e)}, status=500)
    


@csrf_exempt
@require_http_methods(["POST"])
def save_schedule(request, user_id):
    '''Save a schedule, a list of workouts from request data to the database.'''
    try:
        is_valid, err = is_valid_user_id(user_id)
        if not is_valid:
            return err
        data = json.loads(request.body)        
        schedule = data['schedule']
        for workout_data in schedule: 
            is_valid = is_valid_workout_object(workout_data)
            if not is_valid:
                print("Invalid workout object:", workout_data)
                return JsonResponse({'error': 'Invalid workout object'}, status=400)
        
        # Create all workouts gracefully
        workouts_created = []
        for workout_data in schedule: 
            try:
                workout = Workout.objects.create(
                    user_id=user_id,
                    execution_date=dt.fromisoformat(workout_data['execution_date'].replace('Z', '+00:00')).date()
                )
                workouts_created.append(workout)
            except Exception as e:
                print("Error creating workout:", str(e)) 
                workouts_created.apply(lambda x: x.delete())
                return JsonResponse({'error': f'Failed to create workout: {str(e)}'}, status=500)
            # Create exercise sets
            for set_data in workout_data['exercise_sets']:
                try:
                    create_exercise_set(set_data, workout)
                except Exception as e:
                    workouts_created.apply(lambda x: x.delete())
                    workout.delete()
                    return JsonResponse({'error': f'Failed to create exercise set: {str(e)}'}, status=500)
            return JsonResponse({
                'message': 'Successfully saved workout',
                'schedule': [serialize_workout(workout) for workout in workouts_created]
            })
        
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        print("Unexpected error:", str(e)) 
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