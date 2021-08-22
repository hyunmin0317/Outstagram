from django.shortcuts import render

from rest_framework.generics import ListAPIView, CreateAPIView, GenericAPIView
from rest_framework.response import Response
from rest_framework.decorators import api_view
from .serializers import PostSerializer, CreateSerializer
from .models import Post

@api_view(["GET"])
def HelloAPI(request):
    return Response("hello world!")

class CreateAPI(CreateAPIView):
    serializer_class = CreateSerializer
    def perform_create(self, serializer):
        serializer.save(owner=self.request.user.username)

class ListAPI(ListAPIView):
    queryset = Post.objects.all()
    serializer_class = PostSerializer

class MyListAPI(ListAPIView):
    serializer_class = PostSerializer
    def get_queryset(self):
        return Post.objects.filter(owner = self.request.user.username)