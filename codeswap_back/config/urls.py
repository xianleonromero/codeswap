"""
URL configuration for config project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from api import endpoints

urlpatterns = [
    path('admin/', admin.site.urls),

    # Endpoints de autenticación
    path('api/auth/login/', endpoints.login, name='login'),
    path('api/auth/registration/', endpoints.register, name='register'),

    # Endpoint para verificar estado
    path('api/health/', endpoints.health_check, name='health-check'),

    # Endpoints para lenguajes de programación
    path('api/languages/', endpoints.languages, name='languages'),

    # Endpoints para perfil de usuario
    path('api/profile/', endpoints.profile, name='profile'),

    # Endpoints para matches
    path('api/matches/potential/', endpoints.potential_matches, name='potential-matches'),
    path('api/matches/normal/', endpoints.normal_matches, name='normal-matches'),
    path('api/matches/refresh/', endpoints.refresh_matches, name='refresh-matches'),

    # Endpoints para sesiones
    path('api/sessions/upcoming/', endpoints.upcoming_sessions, name='upcoming-sessions'),
    path('api/sessions/past/', endpoints.past_sessions, name='past-sessions'),
    path('api/sessions/', endpoints.create_session, name='create-session'),
    path('api/sessions/<int:session_id>/', endpoints.update_session_status, name='update-session'),
]