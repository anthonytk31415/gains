from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_http_methods
import json
# from django.http import JsonResponse


def get_workout(request): 
    pass

def get_workouts(request): 
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