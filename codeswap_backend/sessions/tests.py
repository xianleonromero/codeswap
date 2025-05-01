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
        # Crear usuarios de proba
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

        # Crear lenguaxe de programación de proba
        self.language = ProgrammingLanguage.objects.create(name='Python')

        # Data futura para a sesión
        self.future_date = timezone.now() + datetime.timedelta(days=7)

        # Cliente API
        self.client = APIClient()

    def test_session_creation(self):
        # Iniciar sesión como estudiante
        self.client.force_authenticate(user=self.student)

        # Crear sesión
        data = {
            'teacher': self.teacher.id,
            'student': self.student.id,
            'language': self.language.id,
            'date_time': self.future_date.isoformat(),
            'duration_minutes': 60
        }
        response = self.client.post('/api/sessions/', data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        session_id = response.data['id']

        # Verificar que a sesión se creou correctamente
        session = Session.objects.get(id=session_id)
        self.assertEqual(session.status, 'PENDING')

        # Confirmar sesión como profesor
        self.client.force_authenticate(user=self.teacher)
        response = self.client.post(f'/api/sessions/{session_id}/confirm/', {}, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        # Verificar que a sesión se confirmou
        session.refresh_from_db()
        self.assertEqual(session.status, 'CONFIRMED')

        # Completar sesión
        response = self.client.post(f'/api/sessions/{session_id}/complete/', {}, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        # Verificar que a sesión se completou
        session.refresh_from_db()
        self.assertEqual(session.status, 'COMPLETED')

    def test_session_cancellation(self):
        # Crear sesión directamente na base de datos
        session = Session.objects.create(
            teacher=self.teacher,
            student=self.student,
            language=self.language,
            date_time=self.future_date,
            duration_minutes=60,
            status='CONFIRMED'
        )

        # Iniciar sesión como estudiante
        self.client.force_authenticate(user=self.student)

        # Cancelar sesión
        response = self.client.post(f'/api/sessions/{session.id}/cancel/', {}, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        # Verificar que a sesión se cancelou
        session.refresh_from_db()
        self.assertEqual(session.status, 'CANCELLED')