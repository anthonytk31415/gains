from django.db import models
from datetime import datetime
class User(models.Model):
    user_id = models.AutoField(primary_key=True)
    email = models.EmailField(max_length=200, unique=True)
    dob = models.DateField(null=True, blank=True)
    height = models.FloatField(null=True, blank=True)  # Height in cm
    weight = models.FloatField(null=True, blank=True)  # Weight in kg
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.email} (ID: {self.user_id})"
    
    def get_age(self):
        if self.dob:
            today = datetime.now()
            return today.year - self.dob.year
        return None
