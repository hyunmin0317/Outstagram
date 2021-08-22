# api/urls.py
from django.urls import path
from .views import HelloAPI, CreateAPI, ListAPI, MyListAPI

urlpatterns = [
    path("hello/", HelloAPI),
    path("all/", ListAPI.as_view()),
    path("create/", CreateAPI.as_view()),
    path("mylist/", MyListAPI.as_view())
]