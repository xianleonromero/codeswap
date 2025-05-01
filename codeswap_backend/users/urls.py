from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (UserViewSet, ProfileViewSet, OfferedSkillViewSet,
                    WantedSkillViewSet, register_user, login_user, MyProfileView)

router = DefaultRouter()
router.register(r'all', UserViewSet)
router.register(r'profiles', ProfileViewSet)
router.register(r'offered-skills', OfferedSkillViewSet, basename='offered-skills')
router.register(r'wanted-skills', WantedSkillViewSet, basename='wanted-skills')

urlpatterns = [
    path('', include(router.urls)),
    path('register/', register_user, name='register'),
    path('login/', login_user, name='login'),
    path('me/', MyProfileView.as_view(), name='my-profile'),
]