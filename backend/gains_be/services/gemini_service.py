from dotenv import load_dotenv
import os
from google import genai
import json
import re

load_dotenv(os.path.join(os.path.dirname(__file__), '..', '.env'))

api_key = os.getenv("GEMINI_API_KEY")

if api_key is None:
    raise ValueError("API key not set in environment variables")

client = genai.Client(api_key=api_key)

def build_llm_form(data):
    ''' Helper functiont to build form data for generate_workout_routine call '''
    age = data.get('age')
    height = data.get('height')
    weight = data.get('weight')
    goal = data.get('goal')
    experience = data.get('experience')  # Example: "Intermediate"
    workout_days = data.get('workout_days', 5)  # Default to 5 days/week if not provided
    location = data.get('location', 'Gym')  # Default to 'Gym'
    muscle_focus = data.get('muscle_focus')

    # Build the llm form here
    form_text = f"""
    I am a {age}-year-old individual.
    My height is {height} and my weight is {weight} kgs.
    My goal is {goal}.
    My experience level is {experience}.
    I am willing to work {workout_days} days a week.
    I will workout from {location}.
    I want to build my {muscle_focus} muscles.
    Give me a workout routine only for targeted muscle.
    """
    return form_text




def generate_workout_routine(form_text, max_input_len=5000, max_output_len=10000):
    try:
        # Clean and curtail input
        cleaned_input = form_text.strip()[:max_input_len]
        #recognized_exercises = db.get(recognized_exercises).toList()
        #Exercise list

        #Beginner: 20-45lbs Beginner
        #Intermediate: 45-60
        
        #history = db.get(user_id, exercises.get(n-1))
        # Ask for JSON only
        #Make sure that the exercise follows the list of recognized exercises provided above.
        #"and ensure that it follows the recognized exercises in the  + recognized_exercise + and given  + history +"
        #approved_exercise_list = db.getExercises()
        approved_exercise_list = [{1, 'Bench Press'}, {2, 'Dips'}, {3, 'Back Squat'}, {4, 'Front Squat'}, {5, 'Barbell Row'}, {6, 'Bent-over Dumbell Row'}, {7, 'Box Jumps'}, {8, 'Bulgarian Split Squat'}, {9, 'Chest Fly'}, {10, 'Clean and Press'}, {11, 'Deadlift'}, {12, 'Incline Bench Press'}, {13, 'Decline Bench Press'}, {14, 'Kettlebell Swings'}, {15, 'Lunges'}, {16, 'Mountain Climbers'}, {17, 'Overhead Press'}, {18, 'Plank'}, {19, 'Power Clean'}, {20, 'Pull-ups'}, {21, 'Push-ups'}, {22, 'Sit-ups'}, {23, 'Romanian Deadlift'}, {24, 'Russian Twists'}, {25, 'Hip Thrust'}, {26, 'Wall Balls'}, {27, 'Lateral Raises'}, {28, 'Egyptian Lateral Raise'}, {29, 'Jogging'}, {30, 'Running'}, {31, 'Sprinting'}, {32, 'Goblet Squats'}, {33, 'Leg Press'}, {34, 'Calf Raises'}, {35, 'Leg Extension'}, {36, 'Leg Curls'}, {37, 'Good mornings'}, {38, 'Single-Leg Deadlift'}, {39, 'Triceps Extension'}, {40, 'Skull Crushers'}, {41, 'Tricep Pushdown'}, {42, 'Diamond Push-ups'}, {43, 'Dumbell Bench Press'}, {44, 'Close-grip Bench Press'}, {45, 'Close-grip Dumbell Bench Press'}, {46, 'Bird Dog'}, {47, 'Heel Tap'}, {48, 'Side Plank'}, {49, 'Side Plank with Dips'}, {50, 'Flutter Kicks'}, {51, 'Dead Bugs'}, {52, 'V-ups'}]

        final_prompt = (
            cleaned_input +
            """
            Respond ONLY with a raw JSON object (not a string or code block). Do NOT wrap in quotes, markdown, or escape characters. Ensure it is directly parseable by json.loads().
            Generate a workout routine. Do not add any explanation.
            Ensure the response is complete and NOT truncated. Also have it conform to this format
            as an example
            [
                [
                {
                    "exercise_id": 1,
                    "sets": 3,
                    "reps": 8,
                    "weight": 52.5
                },
                {
                    "exercise_id": 12,
                    "sets": 3,
                    "reps": 8,
                    "weight": 42.5
                }
                ],
                [
                {
                    "exercise_id": 12,
                    "sets": 2,
                    "reps": 5,
                    "weight": 65
                },
                {
                    "exercise_id": 45,
                    "sets": 3,
                    "reps": 6,
                    "weight": 85
                },
                {
                    "exercise_id": 32,
                    "sets": 5,
                    "reps": 8,
                    "weight": 50
                }
                ]
            ]

            Makes sure it includes exercise_id number, sets, reps, and weight for each exercise. Make sure exercises for each day are grouped appropriately
            and not over the place like day 1 having bicep curls and squats.
            Also ensure that we don't get leg exercises or chest exercises in core exercises.
            Create a workout with exercise names first and then convert each name to its corresponding exercise_id number provided below
            Do not output the exercise name.
            Make sure all exercises you give are within the approved_exercise_list.
            Make sure all exercise_id numbers you give are within the approved_exercise_list number length 
            approved_exercise_list is this:  approved_exercise_list = [{1, 'Bench Press'}, {2, 'Dips'}, {3, 'Back Squat'}, {4, 'Front Squat'}, {5, 'Barbell Row'}, {6, 'Bent-over Dumbell Row'}, {7, 'Box Jumps'}, {8, 'Bulgarian Split Squat'}, {9, 'Chest Fly'}, {10, 'Clean and Press'}, {11, 'Deadlift'}, {12, 'Incline Bench Press'}, {13, 'Decline Bench Press'}, {14, 'Kettlebell Swings'}, {15, 'Lunges'}, {16, 'Mountain Climbers'}, {17, 'Overhead Press'}, {18, 'Plank'}, {19, 'Power Clean'}, {20, 'Pull-ups'}, {21, 'Push-ups'}, {22, 'Sit-ups'}, {23, 'Romanian Deadlift'}, {24, 'Russian Twists'}, {25, 'Hip Thrust'}, {26, 'Wall Balls'}, {27, 'Lateral Raises'}, {28, 'Egyptian Lateral Raise'}, {29, 'Jogging'}, {30, 'Running'}, {31, 'Sprinting'}, {32, 'Goblet Squats'}, {33, 'Leg Press'}, {34, 'Calf Raises'}, {35, 'Leg Extension'}, {36, 'Leg Curls'}, {37, 'Good mornings'}, {38, 'Single-Leg Deadlift'}, {39, 'Triceps Extension'}, {40, 'Skull Crushers'}, {41, 'Tricep Pushdown'}, {42, 'Diamond Push-ups'}, {43, 'Dumbell Bench Press'}, {44, 'Close-grip Bench Press'}, {45, 'Close-grip Dumbell Bench Press'}, {46, 'Bird Dog'}, {47, 'Heel Tap'}, {48, 'Side Plank'}, {49, 'Side Plank with Dips'}, {50, 'Flutter Kicks'}, {51, 'Dead Bugs'}, {52, 'V-ups'}]
            Also keep in mind experience level as Beginners should not be doing too many exercises and or should start at low weight, Intermediate should either increase reps, more exercises, or increase weight, Advanced should either increase reps, more exercises, or increase weight too but more than at Intermediate
            For example, Beginners may do 3-5 exercises, Intermediate may do 5-6 exercises, and Advanced may do 6-8 exercises
            Avoid using Moderate for weight or 50-70% 1RM. Make an actual workout regimen.
            Also no need to add per dumbell or per leg as we will have images for each exercise.
            Try to avoid having a range for reps except when the exercise is timed, just settle on a reasonable number based on their experience_level.
            When an exercise is typically timed in seconds you can say in reps the number of seconds.
            For example with plank exercises
            When an exercise is typically timed in minutes you can say in reps the number of minutes.
            For example with Jogging exercises
            All weight should return a float number.
            Do not include any workouts where reps is "Until Failure" or "As Many Reps as Possible". 
            If a user says their workout style is descending pyramid make weight have different weight based on the number of sets like 'sets': '3', 'weight': '20, 15, 10'
            """
        )
            #         If the user says their workout style is until failure reps should say Until Failure
            # If you do respond with As Many Reps as Possible change that response to Until Failure.
            # {"workout_routine":{"creation_date": "2025-04-28","schedule":[{"workout_id" : 2,"exercise_sets":[{"exercise_id":1,"sets":"3","reps":"8","weights":52.5,},{"exercise_id":12,"sets":"3","reps":"8","weights":42.5},{"exercise_id":"45","sets":"3","reps":"8","weights":42.5},{"exercise_id":"56","sets":"3","reps":"8","weights":27.5},{"exercise_id":"35","sets":"3","reps":"10","weights":22.5},{"exercise_id":"22","sets":"3","reps":"12","weights":32.5}]},{"workout_id" : 2,"exercises":[{"exercise_id":"12","sets":"1","reps":"5","weights":65.0},{"exercise_id":"45","sets":"3","reps":"10","weights":85.0},{"exercise_id":"32","sets":"3","reps":"8","weights":50.0},{"exercise_id":"27","sets":"3","reps":"10","weights":17.5},{"exercise_id":"56","sets":"3","reps":"10","weights":17.5},{"exercise_id":"13","sets":"3","reps":"10","weights":42.5}]},{"workout_id" : 2,"exercises":[{"exercise_id":"3","sets":"3","reps":"8","weights":0.0},{"exercise_id":"5","sets":"3","reps":"12","weights":12.5},{"exercise_id":"6","sets":"3","reps":"12","weights":12.5},{"exercise_id":"18","sets":"3","reps":"12","weights":17.5},{"exercise_id":"9","sets":"3","reps":"10","weights":50.0},{"exercise_id":"4","sets":"3","reps":"8","weights":15.0}]}]}}

        response = client.models.generate_content(
            model="gemini-2.0-flash",
            contents=final_prompt
        )

        # Trim output just in case
        trimmed_output = response.text.strip()[:max_output_len]

        return clean_and_parse_json(trimmed_output)

    except Exception as e:
        return {"error": str(e)}
    
def generate_workout_routine_with_history(form_text, max_input_len=4000, max_output_len=10000):
    try:
        # Clean and curtail input
        cleaned_input = form_text.strip()[:max_input_len]
        #recognized_exercises = db.get(recognized_exercises).toList()
        #Exercise list

        #Beginner: 20-45lbs Beginner
        #Intermediate: 45-60
        

        #history = db.get(user_id, exercises.get(n-1))
        history = '''
        {'workout_routine': {'days_per_week': 3, 'focus': 'Full Body', 'experience_level': 'Intermediate', 'location': 'Gym', 'schedule': [{'day': '1', 'exercises': [{'name': 'Barbell Squats', 'sets': '3', 'reps': '8', 'weights': '50'}, {'name': 'Bench Press', 'sets': '3', 'reps': '8', 'weights': '40'}, {'name': 'Bent-Over Rows', 'sets': '3', 'reps': '8', 'weights': '40'}, {'name': 'Overhead Press', 'sets': '3', 'reps': '8', 'weights': '25'}, {'name': 'Barbell Bicep Curls', 'sets': '3', 'reps': '10', 'weights': '20'}, {'name': 'Triceps Pushdowns', 'sets': '3', 'reps': '12', 'weights': '30'}]}, {'day': '2', 'exercises': [{'name': 'Deadlifts', 'sets': '1', 'reps': '5', 'weights': '60'}, {'name': 'Leg Press', 'sets': '3', 'reps': '10', 'weights': '80'}, {'name': 'Lat Pulldowns', 'sets': '3', 'reps': '8', 'weights': '45'}, {'name': 'Dumbbell Shoulder Press', 'sets': '3', 'reps': '10', 'weights': '15'}, {'name': 'Hammer Curls', 'sets': '3', 'reps': '10', 'weights': '15'}, {'name': 'Close-Grip Bench Press', 'sets': '3', 'reps': '10', 'weights': '40'},{'name': 'Pull-Ups', 'sets': '3', 'reps': '10', 'weights': '0'}, {'name': 'Dumbbell Lateral Raises', 'sets': '3', 'reps': '12', 'weights': '10'}, {'name': 'Concentration Curls', 'sets': '3', 'reps': '12', 'weights': '10'}, {'name': 'Overhead Triceps Extensions', 'sets': '3', 'reps': '12', 'weights': '15'}]}]}}
        '''
        # Ask for JSON only
        #Make sure that the exercise follows the list of recognized exercises provided above.
        #"and ensure that it follows the recognized exercises in the  + recognized_exercise + and given  + history +"
        final_prompt = (
            cleaned_input + history +
            """
            Respond ONLY with a raw JSON object (not a string or code block). Do NOT wrap in quotes, markdown, or escape characters. Ensure it is directly parseable by json.loads().
            Generate a workout routine. Do not add any explanation.
            Ensure the response is complete and NOT truncated. Also have it conform to this format
            as an example
            {"workout_routine":{"creation_date": "2025-04-28","schedule":[{"workout_id" : 2,"exercise_sets":[{"exercise_id":1,"sets":"3","reps":"8","weights":52.5,},{"exercise_id":12,"sets":"3","reps":"8","weights":42.5},{"exercise_id":"45","sets":"3","reps":"8","weights":42.5},{"exercise_id":"56","sets":"3","reps":"8","weights":27.5},{"exercise_id":"35","sets":"3","reps":"10","weights":22.5},{"exercise_id":"22","sets":"3","reps":"12","weights":32.5}]},{"workout_id" : 2,"exercises":[{"exercise_id":"12","sets":"1","reps":"5","weights":65.0},{"exercise_id":"45","sets":"3","reps":"10","weights":85.0},{"exercise_id":"32","sets":"3","reps":"8","weights":50.0},{"exercise_id":"87","sets":"3","reps":"10","weights":17.5},{"exercise_id":"56","sets":"3","reps":"10","weights":17.5},{"exercise_id":"13","sets":"3","reps":"10","weights":42.5}]},{"workout_id" : 2,"exercises":[{"exercise_id":"3","sets":"3","reps":"8","weights":0.0},{"exercise_id":"5","sets":"3","reps":"12","weights":12.5},{"exercise_id":"6","sets":"3","reps":"12","weights":12.5},{"exercise_id":"78","sets":"3","reps":"12","weights":17.5},{"exercise_id":"9","sets":"3","reps":"10","weights":50.0},{"exercise_id":"4","sets":"3","reps":"8","weights":15.0}]}]}}
            Makes sure it includes exercise_id number, sets, reps, and weights for each exercise. Make sure exercises for each day are grouped appropriately
            and not over the place like day 1 having bicep curls and squats. 
            Also keep in mind experience level as Beginners should not be doing too many exercises or should start at low weights, Intermediate should either increase reps, more exercises, or increase weight, Advanced should either increase reps, more exercises, or increase weight too but more than at Intermediate
            Avoid using Moderate for weights or 50-70% 1RM. Make an actual workout regimen.
            Also no need to add per dumbell or per leg as we will have images for each exercise.
            Try to avoid having a range for reps except when the exercise is timed, just settle on a reasonable number based on their experience_level.
            If the user says their workout style is until failure reps should say Until Failure.
            If you do respond with As Many Reps as Possible change that response to Until Failure.
            If a user says their workout style is descending pyramid make weights have different weights based on the number of sets like 'sets': '3', 'weights': '20, 15, 10'.
            If the user says to include my history try giving the same exercises or very similar ones to last week.
            Make sure that for weights it is a floating point number or floating point number list, it cannot have Bodyweight as a response.
            In each exercise_set, Sets and Reps are all integers.
            """
        )

        response = client.models.generate_content(
            model="gemini-2.0-flash",
            contents=final_prompt
        )

        # Trim output just in case
        trimmed_output = response.text.strip()[:max_output_len]

        return clean_and_parse_json(trimmed_output)

    except Exception as e:
        return {"error": str(e)}
    
def clean_and_parse_json(raw_text):
    # Remove code block formatting if present
    cleaned = re.sub(r"^```json\s*|\s*```$", "", raw_text.strip(), flags=re.DOTALL)

    try:
        parsed = json.loads(cleaned)
        if isinstance(parsed, str):
            # If it's a stringified JSON, unescape and parse again
            unescaped = parsed.encode().decode('unicode_escape')
            return json.loads(unescaped)
        return parsed
    except json.JSONDecodeError:
        try:
            # Fallback: try unescaping the raw text and parsing
            unescaped = cleaned.encode().decode('unicode_escape')
            return json.loads(unescaped)
        except json.JSONDecodeError:
            return {"error": "Response is not valid JSON", "raw": cleaned}

# # # Sample form data
form_input = """
I am a Male 25 years of age
My height is 180 cm and weight is 90 kgs
My goal is Fat Loss
My experience is Beginner
I am willing to work 3 days a week
I will workout from my Home
I want to build my Chest and Shoulders muscles
Give me a workout routine only for targeted muscle
 """
# # #Add a plank exercise for each day
# # #Focus on a descending pyramid workout style
# # #Include a cardio exercise at the end of each day


def format_llm_workout(workout_data_llm):
    """
    Format the workout data from LLM into a structured workout object.
    
    Args:
        workout_data_llm (dict): The raw workout data from LLM
        
    Returns:
        dict: Formatted workout object
    """
    try:
        # Extract the workout routine data
        workout_routine = workout_data_llm.get('workout', {}).get('workout_data', {}).get('workout_routine', {})
        
        # Create the formatted workout object
        formatted_workout = {
            'workout_id': workout_data_llm.get('workout', {}).get('workout_id'),
            'creation_date': workout_routine.get('creation_date'),
            'schedule': []
        }
        
        # Process each workout day in the schedule
        for day in workout_routine.get('schedule', []):
            workout_day = {
                'workout_id': day.get('workout_id'),
                'exercises': []
            }
            
            # Process each exercise in the workout day
            for exercise in day.get('exercise_sets', []):
                formatted_exercise = {
                    'exercise_id': exercise.get('exercise_id'),
                    'sets': exercise.get('sets'),
                    'reps': exercise.get('reps'),
                    'weights': exercise.get('weights')
                }
                workout_day['exercises'].append(formatted_exercise)
            
            formatted_workout['schedule'].append(workout_day)
        
        return formatted_workout
    
    except Exception as e:
        return {'error': f'Error formatting workout data: {str(e)}'}