from django.http import JsonResponse
from django.views.decorators.http import require_http_methods
from gains_be.models.user import User
from django.views.decorators.csrf import csrf_exempt
from django.core.serializers import serialize
import json
from datetime import datetime

@csrf_exempt
@require_http_methods(["POST"])
def create_user(request): 
    '''Create a new user.'''
    try:
        data = json.loads(request.body)
        print("data", data)
        # required fields
        for field in ['email']:  #maybe include password later
            if not data.get(field):
                return JsonResponse({'error': f'{field} is required'}, status=400)
        
        # Check if user already exists
        if User.objects.filter(email=data['email']).exists():
            return JsonResponse({'error': 'Email address already exists'}, status=400)

        # Create user with required fields and optional fields if provided
        user = User.objects.create(
            email=data.get('email'),
            dob=data.get('dob'),  
            height=data.get('height'),  
            weight=data.get('weight')  
        )
        user.save()
        return JsonResponse({
            'message': 'User created successfully',
            'user_id': user.user_id
        })
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

@csrf_exempt
@require_http_methods(["POST"])
def login_user(request): 
    ''' Given a email, return the user_id and thus login the user'''
    try:
        print('hello this path1')
        data = json.loads(request.body) 
        print("data", data)
        email = data.get('email')
        print(email)
        if not email:
            return JsonResponse({'error': 'Email is required'}, status=400)
        print('hello this path')
        user = User.objects.get(email=email)
        print(user)
        return JsonResponse({'user_id': user.user_id})
    except User.DoesNotExist:
        return JsonResponse({'error': 'Error with email address'}, status=404)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

def delete_user(request): 
    pass


def serialize_user(user): 
    '''Given a user, return a dictionary of the user.'''
    return {
        # 'exercise_set_id': exercise_set.exercise_set_id,
        'user_id': user.user_id,
        'email': user.email,
        'dob': user.dob,
        'height': user.height,
        'weight': user.weight
    }

@csrf_exempt
@require_http_methods(["GET"])
def get_user(request, user_id):
    '''Given the user id, return the user.'''
    try:
        print("hello get user")
        print(user_id)
        user = User.objects.get(user_id=user_id)
        return JsonResponse({'user': serialize_user(user)})
    except User.DoesNotExist:
        return JsonResponse({'error': 'User not found'}, status=404)


# @require_http_methods(["GET"])
# def get_all_users(request):
#     '''Get all users from the database.'''
#     try:
#         users = User.objects.all()
#         users_data = list(users.values('user_id', 'email', 'dob', 'height', 'weight'))  # Updated fields to match model
#         return JsonResponse({
#             'users': users_data
#         })
#     except Exception as e:
#         return JsonResponse({'error': str(e)}, status=500)

@csrf_exempt
@require_http_methods(["PUT"])
def update_user(request, user_id):
    '''Given the user id and a payload of user data, update the user.'''
    try:
        # Parse the request body
        data = json.loads(request.body)                    
        try:
            user = User.objects.get(user_id=user_id)
        except User.DoesNotExist:
            return JsonResponse({'error': 'User not found'}, status=404)
            
        # Update allowed fields
        if 'email' in data:
            if User.objects.filter(email=data['email']).exists():
                return JsonResponse({'error': 'Email address already exists'}, status=400)
            else:
                user.email = data['email']

        # if email is provided, update the email
        allowed_fields = ['dob', 'height', 'weight']
        for field in allowed_fields:
            if field in data:
                setattr(user, field, data[field])
                
        user.updated_at = datetime.now()
        user.save()
            
        return JsonResponse({
            'message': 'Successfully updated user',
            'user': {
                'user_id': user.user_id,
                'email': user.email,
                'dob': user.dob,
                'height': user.height,
                'weight': user.weight
            }
        })
        
    except json.JSONDecodeError:
        return JsonResponse({'error': 'Invalid JSON'}, status=400)
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)