from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ProgrammingLanguageViewSet

router = DefaultRouter()
router.register(r'languages', ProgrammingLanguageViewSet)

urlpatterns = [
    path('', include(router.urls)),
]