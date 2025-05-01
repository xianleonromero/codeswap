from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import MatchViewSet

router = DefaultRouter()
router.register(r'', MatchViewSet, basename='matches')

urlpatterns = [
    path('', include(router.urls)),
    path('admin/generate-matches/', generate_matches_admin, name='generate-matches-admin'),
]