from django.db import models
from .workout import Workout
from .exercise import Exercise

class ExerciseSet(models.Model):
    set_id = models.AutoField(primary_key=True)
    workout = models.ForeignKey(Workout, on_delete=models.CASCADE, related_name='exercise_sets')
    exercise = models.ForeignKey(Exercise, on_delete=models.CASCADE)
    reps = models.IntegerField()
    weight = models.DecimalField(max_digits=10, decimal_places=2)  # Using DecimalField for precise weight values
    is_done = models.BooleanField(default=False)

    def __str__(self):
        return f"Set {self.set_id} - {self.exercise.name} ({self.reps} reps @ {self.weight})" 