from django.http import JsonResponse
from django.views.decorators.http import require_http_methods
from gains_be.models.user import User
from django.views.decorators.csrf import csrf_exempt
from django.core.serializers import serialize
import json

# from database.connection import Database

# DUMMY_USER = User(
#     user_id=1,  # or any integer
#     email="markhenry@google.com",  # any email string
#     password="123456789"  # any password string
# )

def create_user(request): 
    pass

def get_user(request): 
    pass

def delete_user(request): 
    pass

@require_http_methods(["GET"])
def get_all_users(request):
    '''Get all users from the database.'''
    try:
        users = User.objects.all()
        users_data = list(users.values('user_id', 'age', 'height', 'weight', 'email'))  # Exclude password for security
        return JsonResponse({
            'users': users_data
        })
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

@csrf_exempt
@require_http_methods(["PUT"])
def update_user(request):
    '''Given the user id and a payload of user data, update the user.'''

    try:
        # Parse the request body
        data = json.loads(request.body)        
        user_id = data.get('user_id')        
        if not user_id:
            return JsonResponse({'error': 'user_id is required'}, status=400)
            
        # Initialize database connection and repository
        # db = Database()  # You'll need to configure this with your actual database settings
        # user_repo = UserRepository(db)
        
        # Update the user
        # updated_user = user_repo.update_user(
        #     user_id=user_id,
        #     email=email,
        #     password=password
        # )
        
        # if not updated_user:
        #     return JsonResponse({'error': 'User not found'}, status=404)
            
        return JsonResponse({
            'message': 'Successfully updated user',
            'user': {
                'user_id': user_id,
                # 'email': email
            }
        })
        
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)