from django.db import models

class Exercise(models.Model):
    exercise_id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=200)

    def __str__(self):
        return f"{self.name} (ID: {self.exercise_id})" 