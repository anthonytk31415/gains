from ..models.exercise import Exercise
from django.http import JsonResponse
from django.views.decorators.http import require_http_methods

@require_http_methods(["GET"])
def get_all_exercises(request):
    """
    Get all exercises
    """
    try:
        exercises = Exercise.objects.all()
        exercises_data = [{
            'exercise_id': exercise.exercise_id,
            'name': exercise.name
        } for exercise in exercises]
        return JsonResponse({'exercises': exercises_data})
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)