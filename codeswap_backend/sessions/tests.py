from django.test import TestCase
from django.contrib.auth.models import User
from django.utils import timezone
from rest_framework.test import APIClient
from rest_framework import status
from .models import Session
from skills.models import ProgrammingLanguage
import datetime

class SessionAPITests(TestCase):
    def setUp(self):
        # Crear usuarios de prueba
        self.teacher = User.objects.create_user(
            username='teacher',
            email='teacher@example.com',
            password='teacherpass123'
        )

        self.student = User.objects.create_user(
            username='student',
            email='student@example.com',
            password='studentpass123'
        )

        # Crear lenguaje de programación de prueba
        self.language = ProgrammingLanguage.objects.create(name='Python')

        # Fecha futura para la sesión
        self.future_date = timezone.now() + datetime.timedelta(days=7)

        # Cliente API
        self.client = APIClient()

    def test_session_creation(self):
        # Iniciar sesión como estudi