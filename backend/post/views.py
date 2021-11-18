from django.shortcuts import render
from rest_framework.generics import ListAPIView, CreateAPIView, RetrieveAPIView, UpdateAPIView, DestroyAPIView
from rest_framework.decorators import api_view
from .serializers import PostSerializer, CreateSerializer
from .models import Post

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

class DetailAPI(RetrieveAPIView):
    queryset = Post.objects.all()
    serializer_class = PostSerializer

class UpdateAPI(UpdateAPIView):
    queryset = Post.objects.all()
    serializer_class = CreateSerializer

    def perform_create(self, serializer):
        serializer.save(owner=self.request.user.username)

class DeleteAPI(DestroyAPIView):
    queryset = Post.objects.all()
    serializer_class = PostSerializer