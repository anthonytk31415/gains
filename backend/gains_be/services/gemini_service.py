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

def generate_workout_routine(form_text, max_input_len=1000, max_output_len=2000):
    try:
        # Clean and curtail input
        cleaned_input = form_text.strip()[:max_input_len]

        # Ask for JSON only
        final_prompt = (
            cleaned_input +
            """
            Generate a workout routine in JSON format ONLY. Do not wrap in markdown or add any explanation.
            Make sure it’s compact (under 1000 characters if possible). Also have it conform to this format
            as an example
            {
            "workout_routine": {
                "days_per_week": 4,
                "focus": "Legs, Biceps",
                "experience_level": "Intermediate",
                "location": "Home",
                "schedule": [
                {
                    "day": "1",
                    "exercises": [
                    {"name": "Squats", "sets": "3", "reps": "8-12", "weights": "15"},
                    {"name": "Lunges", "sets": "3", "reps": "10-15 per leg", "weights": "0"},
                    {"name": "Glute Bridges", "sets": "3", "reps": "15-20", "weights": "0"},
                    {"name": "Calf Raises", "sets": "3", "reps": "15-20", "weights": "30"}
                    ]
                }
                ]
            }
            }
            Makes sure it includes name, sets, reps, and weights for each exercise. Make sure exercises for each day are grouped appropriately
            and not over the place like day 1 having bicep curls and squats.
            \n\nRespond ONLY with a valid JSON object response containing the workout routine. Do not include any explanations or extra text in the response.

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
    # Remove markdown code block wrapper if it exists
    cleaned = re.sub(r"^```json\s*|\s*```$", "", raw_text.strip(), flags=re.DOTALL)

    try:
        return json.loads(cleaned)
    except json.JSONDecodeError:
        return {"error": "Response is not valid JSON", "raw": cleaned}

# Sample form data
form_input = """
I am a Male 25 years of age
My height is 5'6 and weight is 55 kgs
My goal is Muscle Gain
My experience is Intermediate
I am willing to work 5 days a week
I will workout from my gym
I want to build my every muscles
Give me a workout routine only for targeted muscle
"""

result = generate_workout_routine(form_input)
print(result)