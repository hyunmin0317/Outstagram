# mysite/urls.py
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static
from django.contrib import admin

urlpatterns = [
    path("admin/", admin.site.urls),
    path("user/", include("user.urls")),
    path("post/", include("post.urls")),
    path("api/auth", include("knox.urls")),
]

urlpatterns += static(settings.MEDIA_URL,document_root=settings.MEDIA_ROOT)