from django.test import TestCase
from django.contrib.auth.models import User
from rest_framework.test import APIClient
from rest_framework import status
from .models import Profile, OfferedSkill, WantedSkill
from skills.models import ProgrammingLanguage

class UserAPITests(TestCase):
    def setUp(self):
        # Crear usuario de prueba
        self.user = User.objects.create_user(
            username='testuser',
            email='test@example.com',
            password='testpassword123'
        )
        self.profile = Profile.objects.get(user=self.user)

        # Crear lenguaje de programación de prueba
        self.language = ProgrammingLanguage.objects.create(name='Python')

        # Cliente API
        self.client = APIClient()

    def test_user_registration(self):
        data = {
            'username': 'newuser',
            'email': 'newuser@example.com',
            'password': 'newpassword123',
            'first_name': 'New',
            'last_name': 'User'
        }
        response = self.client.post('/api/users/register/', data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertTrue('token' in response.data)
        self.assertTrue(User.objects.filter(username='newuser').exists())
        self.assertTrue(Profile.objects.filter(user__username='newuser').exists())

    def test_user_login(self):
        data = {
            'username': 'testuser',
            'password': 'testpassword123'
        }
        response = self.client.post('/api/users/login/', data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertTrue('token' in response.data)

    def test_offered_skill_crud(self):
        # Iniciar sesión
        self.client.force_authenticate(user=self.user)

        # Crear skill ofrecida
        data = {
            'language': self.language.id,
            'level': 3
        }
        response = self.client.post('/api/users/offered-skills/', data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        skill_id = response.data['id']

        # Verificar skill creada
        response = self.client.get(f'/api/users/offered-skills/{skill_id}/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['level'], 3)

        # Actualizar skill
        data = {
            'language': self.language.id,
            'level': 4
        }
        response = self.client.put(f'/api/users/offered-skills/{skill_id}/', data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['level'], 4)

        # Eliminar skill
        response = self.client.delete(f'/api/users/offered-skills/{skill_id}/')
        self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)
        self.assertFalse(OfferedSkill.objects.filter(id=skill_id).exists())