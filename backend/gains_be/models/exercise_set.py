from django.db import models
from .workout import Workout
from .exercise import Exercise
from django.core.validators import MinValueValidator, MaxValueValidator

class ExerciseSet(models.Model):
    exercise_set_id = models.AutoField(primary_key=True)
    workout = models.ForeignKey(Workout, on_delete=models.CASCADE, related_name='exercise_sets')
    exercise = models.ForeignKey(Exercise, on_delete=models.CASCADE)
    sets = models.IntegerField(
        default=1,
        validators=[
            MinValueValidator(0),
            MaxValueValidator(10000)
        ]
    )    
    reps = models.IntegerField(
        default=10,
        validators=[
            MinValueValidator(0),
            MaxValueValidator(10000)
        ]
    )
    weight = models.DecimalField(max_digits=10, decimal_places=4)
    is_done = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Set {self.set_id} - {self.exercise.name} ({self.reps} reps @ {self.weight})" 