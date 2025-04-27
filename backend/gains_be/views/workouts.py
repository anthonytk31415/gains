from django.http import HttpResponse, JsonResponse
from django.views.decorators.http import require_http_methods
from ..models import Workout, ExerciseSet, Exercise
from django.core.serializers import serialize
from django.views.decorators.csrf import csrf_exempt
import json

def get_workouts(request): 
    pass

# focus now 
def get_last_week_workouts(request): 
    pass

def get_this_week_workouts(request): 
    pass

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


@csrf_exempt
@require_http_methods(["POST"])
def generate_workout(request):
    '''Given user input, call the LLM, generate a workout, save it, and return the workout.'''

    try:
        # Parse the request body
        data = json.loads(request.body)        
        print("data for generate workout call: ", data)
        # create dummy workout id as a placeholder
        
        # call LLM and get the workout 
        print("calling LLM for workout...")
        workout_id = 123

        return JsonResponse({
            'message': 'Successfully created workout',
            'workout': {
                'workout_id': workout_id,
                # 'email': email
            }
        })
        
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)
    

@require_http_methods(["GET"])
def get_workout(request, workout_id):
    '''Given a workout id, return the workout.'''
    try:
        # Get the workout with related exercise sets and exercises
        workout = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).get(workout_id=workout_id)
        
        workout_data = {
            'workout_id': workout.workout_id,
            'user_id': workout.user.user_id,
            'workout_date': workout.workout_date.isoformat(),
            'exercise_sets': []
        }
        
        # Add exercise sets and their exercises
        for exercise_set in workout.exercise_sets.all():
            set_data = {
                'set_id': exercise_set.set_id,
                'exercise': {
                    'exercise_id': exercise_set.exercise.exercise_id,
                    'name': exercise_set.exercise.name
                },
                'reps': exercise_set.reps,
                'weight': float(exercise_set.weight),  # Convert Decimal to float for JSON
                'is_done': exercise_set.is_done
            }
            workout_data['exercise_sets'].append(set_data)
        
        return JsonResponse(workout_data)
        
    except Workout.DoesNotExist:
        return JsonResponse({'error': 'Workout not found'}, status=404)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)
