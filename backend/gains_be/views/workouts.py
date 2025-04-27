from django.http import HttpResponse, JsonResponse
from django.views.decorators.http import require_http_methods
from ..models import Workout, ExerciseSet, Exercise
from django.core.serializers import serialize
from django.views.decorators.csrf import csrf_exempt
import json
from services.gemini_service import generate_workout_routine
import datetime
from datetime import datetime as dt

def marshall_exercise_set(exercise_set): 
    '''Given an exercise set, return a dictionary of the exercise set.'''
    return {
        'set_id': exercise_set.set_id,
        'exercise_id': exercise_set.exercise.exercise_id,
        'reps': exercise_set.reps,
        'weight': float(exercise_set.weight),
        'is_done': exercise_set.is_done
    }

def marshall_workout(workout): 
    return {
        'workout_id': workout.workout_id,
        'user_id': workout.user.user_id,
        'workout_date': workout.workout_date.isoformat(),
        'exercise_sets': [marshall_exercise_set(exercise_set) for exercise_set in workout.exercise_sets.all()]
    }

@csrf_exempt
@require_http_methods(["GET"])
def get_workouts(request): 
    '''Get all workouts for a user.'''
    try:
        user_id = request.GET.get('user_id')
        if not user_id:
            return JsonResponse({'error': 'user_id is required'}, status=400)

        # Get all workouts for the user with related exercise sets and exercises
        workouts = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).filter(user_id=user_id).order_by('-workout_date')
        workouts_data = [marshall_workout(workout) for workout in workouts]
        return JsonResponse({'workouts': workouts_data})
        
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

@csrf_exempt
@require_http_methods(["GET"])
def get_workouts_by_date_range(request, start_date, end_date): 
    '''Given a user_id, Given a start date and end date, return all workouts in that date range.'''
    try:
        # Get user_id from query parameters
        user_id = request.GET.get('user_id')
        if not user_id:
            return JsonResponse({'error': 'user_id is required'}, status=400)

        # Get workouts within the date range for the user
        workouts = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).filter(
            user_id=user_id,
            workout_date__gte=start_date,
            workout_date__lte=end_date
        ).order_by('-workout_date')
        
        workouts_data = [marshall_workout(workout) for workout in workouts]
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
def get_last_week_workouts(request): 
    '''Given a user_id, return all workouts for the last week.'''
    try:
        data = json.loads(request.body)   
        user_id = data.get('user_id')
        if not user_id:
            return JsonResponse({'error': 'user_id is required'}, status=400)
        
        start_date, end_date = get_last_week_range(datetime.datetime.now())
        workouts = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).filter(
            user_id=user_id,
            workout_date__gte=start_date,
            workout_date__lte=end_date
        ).order_by('-workout_date')
        workouts_data = [marshall_workout(workout) for workout in workouts]
        return JsonResponse({'workouts': workouts_data})
    
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

@csrf_exempt
@require_http_methods(["GET"])
def get_current_week_workouts(request): 
    '''Given a user_id, return all workouts for the current week.'''
    try:
        data = json.loads(request.body)   
        user_id = data.get('user_id')
        if not user_id:
            return JsonResponse({'error': 'user_id is required'}, status=400)
        
        start_date, end_date = get_current_week_range(datetime.datetime.now())
        workouts = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).filter(
            user_id=user_id,
            workout_date__gte=start_date,
            workout_date__lte=end_date
        ).order_by('-workout_date') 
        workouts_data = [marshall_workout(workout) for workout in workouts]
        return JsonResponse({'workouts': workouts_data})
    
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

def get_last_month_workouts(request): 
    pass

def get_this_month_workouts(request): 
    pass

def create_workout(request): 
    pass

def edit_workout(request): 
    pass

def delete_workout(request): 
    pass

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
def generate_workout(request):
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
        print("calling LLM for workout...")
        workout_id = 123
        workout_data = generate_workout_routine(form_text)

        if 'error' in workout_data:
            return JsonResponse({'error': workout_data['error']}, status=500)

        return JsonResponse({
            'message': 'Successfully created workout',
            'workout': {
                'workout_id': workout_id,
                'workout_data': workout_data,
                # 'email': email
            }
        })
        
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)
    
@csrf_exempt
@require_http_methods(["GET"])
def get_workout(request, workout_id):
    '''Given a workout id, return the workout.'''
    try:
        # build safeguard so that body must have user id in it and and we filter on user id? 
        
        workout = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).get(workout_id=workout_id)       
        return JsonResponse(marshall_workout(workout))
        
    except Workout.DoesNotExist:
        return JsonResponse({'error': 'Workout not found'}, status=404)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

@csrf_exempt
@require_http_methods(["POST"])
def save_workout(request):
    '''Save a workout from request data to the database.'''
    try:
        data = json.loads(request.body)
        print("Received data:", data)  # Debug log
        
        # Validate required fields
        if not data.get('user_id'):
            return JsonResponse({'error': 'user_id is required'}, status=400)
            
        if not data.get('workout'):
            return JsonResponse({'error': 'workout data is required'}, status=400)
            
        workout_data = data['workout']
        print("Workout data:", workout_data)  # Debug log
        
        # Parse the workout date
        try:
            workout_date = dt.fromisoformat(workout_data['workout_date'].replace('Z', '+00:00'))
        except (ValueError, TypeError) as e:
            return JsonResponse({'error': f'Invalid date format: {str(e)}'}, status=400)
        
        # Create the workout
        try:
            workout = Workout.objects.create(
                user_id=data['user_id'],
                workout_date=workout_date
            )
            print("Created workout:", workout.workout_id)  # Debug log
        except Exception as e:
            print("Error creating workout:", str(e))  # Debug log
            return JsonResponse({'error': f'Failed to create workout: {str(e)}'}, status=500)
        
        # Create exercise sets
        for set_data in workout_data['exercise_sets']:
            try:
                exercise_set = ExerciseSet.objects.create(
                    workout=workout,
                    exercise_id=set_data['exercise_id'],
                    reps=set_data['reps'],
                    weight=set_data['weight'],
                    is_done=set_data.get('is_done', False)
                )
                print("Created exercise set:", exercise_set.set_id)  # Debug log
            except Exception as e:
                print("Error creating exercise set:", str(e))  # Debug log
                # If exercise set creation fails, delete the workout to maintain consistency
                workout.delete()
                return JsonResponse({'error': f'Failed to create exercise set: {str(e)}'}, status=500)
            
        return JsonResponse({
            'message': 'Successfully saved workout',
            'workout': marshall_workout(workout)
        })
        
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        print("Unexpected error:", str(e))  # Debug log
        return JsonResponse({'error': str(e)}, status=500)
