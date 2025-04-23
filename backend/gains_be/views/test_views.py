from django.http import HttpResponse

def test_view(request):
    response = "Hello World"
    print(response)
    return HttpResponse(response)



import os
import psycopg2
from django.http import JsonResponse
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

def test_db(request):
    try:
        # Connect to the database
        connection = psycopg2.connect(
            dbname=os.getenv("DB_NAME"),
            user=os.getenv("DB_USER"),
            password=os.getenv("DB_PASSWORD"),
            host=os.getenv("DB_HOST"),
            port=os.getenv("DB_PORT")
        )

        with connection.cursor() as cursor:
            cursor.execute("SELECT NOW();")
            row = cursor.fetchone()
        connection.close()

        return JsonResponse({"db_time": str(row[0])}) 
    except Exception as e:
        return JsonResponse({"error": str(e)}, status=500)