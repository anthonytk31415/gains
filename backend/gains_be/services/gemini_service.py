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

def generate_workout_routine(form_text, max_input_len=3000, max_output_len=10000):
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
        final_prompt = (
            cleaned_input + 
            """
            Respond ONLY with a raw JSON object (not a string or code block). Do NOT wrap in quotes, markdown, or escape characters. Ensure it is directly parseable by json.loads().
            Generate a workout routine. Do not add any explanation.
            Ensure the response is complete and NOT truncated. Also have it conform to this format
            as an example
            {'workout_routine': {'days_per_week': 5, 'focus': 'Core', 'experience_level': 'Intermediate', 'location': 'Gym', 'schedule': [{'day': '1', 'exercises': [{'name': 'Plank', 'sets': '3', 'reps': '60 seconds', 'weights': '0'}, {'name': 'Russian Twists', 'sets': '3', 'reps': '20', 'weights': '5'}, {'name': 'Hanging Leg Raises', 'sets': '3', 'reps': '15', 'weights': '0'}]}, {'day': '2', 'exercises': [{'name': 'Crunches', 'sets': '3', 'reps': '15', 'weights': '0'}, {'name': 'Leg Raises', 'sets': '3', 'reps': '15', 'weights': '0'}, {'name': 'Bicycle Crunches', 'sets': '3', 'reps': '15', 'weights': '0'}]}, {'day': '3', 'exercises': [{'name': 'Side Plank', 'sets': '3', 'reps': '60 seconds', 'weights': '0'}, {'name': 'Woodchops', 'sets': '3', 'reps': '15', 'weights': '10'}, {'name': 'Dead Bug', 'sets': '3', 'reps': '15', 'weights': '0'}]}, {'day': '4', 'exercises': [{'name': 'Reverse Crunches', 'sets': '3', 'reps': '20', 'weights': '0'}, {'name': 'Flutter Kicks', 'sets': '3', 'reps': '30', 'weights': '0'}, {'name': 'Heel Touches', 'sets': '3', 'reps': '20', 'weights': '0'}]}, {'day': '5', 'exercises': [{'name': 'Cable Crunch', 'sets': '3', 'reps': '15', 'weights': '15'}, {'name': 'Hyperextensions', 'sets': '3', 'reps': '20', 'weights': '0'}, {'name': 'Barbell Rollout', 'sets': '3', 'reps': '12', 'weights': '0'}]}]}}
            Makes sure it includes name, sets, reps, and weights for each exercise. Make sure exercises for each day are grouped appropriately
            and not over the place like day 1 having bicep curls and squats. 
            Also keep in mind experience level as Beginners should not be doing too many exercises or should start at low weights, Intermediate should either increase reps, more exercises, or increase weight, Advanced should either increase reps, more exercises, or increase weight too but more than at Intermediate
            Avoid using Moderate for weights or 50-70% 1RM. Make an actual workout regimen.
            Also no need to add per dumbell or per leg as we will have images for each exercise.
            Try to avoid having a range for reps except when the exercise is timed, just settle on a reasonable number based on their experience_level.
            If the user says their workout style is until failure reps should say Until Failure
            If you do respond with As Many Reps as Possible change that response to Until Failure.
            If a user says their workout style is descending pyramid make weights have different weights based on the number of sets like 'sets': '3', 'weights': '20, 15, 10'
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

# Sample form data
form_input = """
I am a Male 25 years of age
My height is 5'6 and weight is 55 kgs
My goal is Muscle Gain
My experience is Intermediate
I am willing to work 3 days a week
I will workout from my gym
I want to build my every muscles
Give me a workout routine only for targeted muscle
Add a plank exercise for each day
Focus on a descending pyramid workout style
Include a cardio exercise at the end of each day
"""

result = generate_workout_routine(form_input)
print(result)