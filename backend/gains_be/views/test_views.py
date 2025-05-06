from django.http import HttpResponse, JsonResponse
from django.views.decorators.http import require_http_methods
from ..models import Workout, ExerciseSet, Exercise
from django.core.serializers import serialize
import json
import os
import psycopg2
# from django.http import JsonResponse
from dotenv import load_dotenv

def test_view(request):
    response = "Hello World"
    print(response)
    return HttpResponse(response)

@require_http_methods(["GET"])
def test_get_workout(request, workout_id):
    try:
        # Get the workout with related exercise sets and exercises
        workout = Workout.objects.select_related('user').prefetch_related(
            'exercise_sets__exercise'
        ).get(workout_id=workout_id)
        
        # Build the response data
        workout_data = {
            'workout_id': workout.workout_id,
            'user_id': workout.user.user_id,
            'execution_date': workout.execution_date.isoformat() if workout.execution_date else None,
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



# Load environment variables
load_dotenv()

# def test_db(request):
#     try:
#         # Connect to the database
#         connection = psycopg2.connect(
#             dbname=os.getenv("DB_NAME"),
#             user=os.getenv("DB_USER"),
#             password=os.getenv("DB_PASSWORD"),
#             host=os.getenv("DB_HOST"),
#             port=os.getenv("DB_PORT")
#         )

#         with connection.cursor() as cursor:
#             cursor.execute("SELECT NOW();")
#             row = cursor.fetchone()
#         connection.close()

#         return JsonResponse({"db_time": str(row[0])}) 
#     except Exception as e:
#         return JsonResponse({"error": str(e)}, status=500)


def test_db(request):
    return 


