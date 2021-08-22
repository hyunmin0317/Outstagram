from django.shortcuts import render

from rest_framework.generics import ListAPIView, CreateAPIView
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
        serializer.save(owner=self.request.user)

class ListAPI(ListAPIView):
    queryset = Post.objects.all()
    serializer_class = PostSerializer

class MyListAPI(ListAPIView):
    serializer_class = PostSerializer
    def get_queryset(self):
        return Post.objects.filter(owner = self.request.user)

    # def post(self, request, *args, **kwargs):
    #     serializer = self.get_serializer(data=request.data)
    #     serializer.is_valid(raise_exception=True)
    #     post = serializer.save()
    #     return Response(
    #         {
    #             "created": post.created,
    #             "content" : post.content,
    #             "image": str(post.image)
    #         }
    #     )