from django.http import HttpResponse

def test_view(request):
    response = "Hello World"
    print(response)
    return HttpResponse(response) 