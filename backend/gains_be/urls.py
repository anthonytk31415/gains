"""
URL configuration for gains_be project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))



We'll use the urls in the app to map the urls to the views. 
"""
from django.contrib import admin
from django.urls import path
from .views.test_views import test_view
from .views.users import update_user, create_user, login_user, get_user
from .views.workouts import generate_workout, get_workout, get_last_week_workouts, get_current_week_workouts, save_workout, get_workouts, update_workout
from .views.exercises import get_all_exercises

urlpatterns = [
    path('admin/', admin.site.urls),
    path('test/', test_view, name='test'),
    path('api/<int:user_id>/workouts/<int:workout_id>/', get_workout, name='get_workout'),
    path('api/<int:user_id>/workouts/generate/', generate_workout, name='generate_workout'), # test
    path('api/<int:user_id>/workouts/last_week/', get_last_week_workouts, name='get_last_week_workouts'),
    path('api/<int:user_id>/workouts/current_week/', get_current_week_workouts, name='get_current_week_workouts'),
    path('api/<int:user_id>/workouts/save/', save_workout, name='save_workout'),
    path('api/<int:user_id>/workouts/all/', get_workouts, name='get_workouts'),
    
    path('api/<int:user_id>/workouts/update/', update_workout, name='update_workout'),

    path('api/<int:user_id>/user/', get_user, name='get_user'),
    path('api/<int:user_id>/user/update/', update_user, name='update_user'),
    # path('api/<int:user_id>/user/all/', get_all_users, name='get_all_users'),
    path('api/user/create/', create_user, name='create_user'),
    path('api/user/login/', login_user, name='login_user'),
    path('api/exercises/all/', get_all_exercises, name='get_all_exercises'),

]
