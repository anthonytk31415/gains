from django.db import models

class User(models.Model):
    user_id = models.AutoField(primary_key=True)
    email = models.EmailField(max_length=200, unique=True)
    age = models.IntegerField(null=True, blank=True)
    height = models.FloatField(null=True, blank=True)  # Height in cm
    weight = models.FloatField(null=True, blank=True)  # Weight in kg
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.email} (ID: {self.user_id})"


# # Purpose of models/ User is to create the data class 
# # and then to have class/methods within the class that can interact with the database

# @dataclass
# class User:
#     user_id: int
#     email: str
#     password: str

# class UserRepository:
#     def __init__(self, db_connection: Database):
#         self.db = db_connection

#     async def create_user(self, email: str, password: str) -> User:
#     async def get_user_by_id(self, user_id: int) -> Optional[User]:
#     async def get_user_by_email(self, email: str) -> Optional[User]:
#     async def update_user(self, user_id: int, email: Optional[str] = None, password: Optional[str] = None) -> Optional[User]:
#     async def delete_user(self, user_id: int) -> bool: