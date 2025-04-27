from django.db import models
from .user import User

class Workout(models.Model):
    workout_id = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, to_field='user_id', on_delete=models.CASCADE)  # Reference user_id explicitly
    workout_date = models.DateField()

    def __str__(self):
        return f"Workout {self.workout_id} - {self.workout_date}"