# api/serializers.py
from rest_framework import serializers
from .models import Post
from django.contrib.auth import authenticate

class PostSerializer(serializers.ModelSerializer):
    class Meta:
        model = Post
        fields = '__all__'

class CreateSerializer(serializers.ModelSerializer):
    owner = serializers.ReadOnlyField(source='user.id')
    class Meta:
        model = Post
        fields = '__all__'