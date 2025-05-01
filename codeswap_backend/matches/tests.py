from django.test import TestCase
from django.contrib.auth.models import User
from rest_framework.test import APIClient
from rest_framework import status
from .models import Match
from .utils import calculate_compatibility_score, generate_potential_matches
from users.models import OfferedSkill, WantedSkill
from skills.models import ProgrammingLanguage

class MatchSystemTests(TestCase):
    def setUp(self):
        # Crear usuarios de proba
        self.user1 = User.objects.create_user(
            username='user1',
            email='user1@example.com',
            password='user1pass123'
        )

        self.user2 = User.objects.create_user(
            username='user2',
            email='user2@example.com',
            password='user2pass123'
        )

        # Crear lenguaxes de programación
        self.python = ProgrammingLanguage.objects.create(name='Python')
        self.javascript = ProgrammingLanguage.objects.create(name='JavaScript')
        self.java = ProgrammingLanguage.objects.create(name='Java')

        # Cliente API
        self.client = APIClient()

    def test_compatibility_calculation(self):
        # User1 ofrece Python e quere aprender JavaScript
        OfferedSkill.objects.create(user=self.user1, language=self.python, level=4)
        WantedSkill.objects.create(user=self.user1, language=self.javascript)

        # User2 ofrece JavaScript e quere aprender Python
        OfferedSkill.objects.create(user=self.user2, language=self.javascript, level=3)
        WantedSkill.objects.create(user=self.user2, language=self.python)

        # Calcular compatibilidad
        score = calculate_compatibility_score(self.user1, self.user2)

        # Ambos usuarios ofrecen o que o outro quere, e é bidireccional
        # Polo tanto: 10 + 10 + 20 (bonus bidireccional) = 40
        self.assertEqual(score, 40)

    def test_match_generation(self):
        # Configurar skills para crear matches
        OfferedSkill.objects.create(user=self.user1, language=self.python, level=4)
        WantedSkill.objects.create(user=self.user1, language=self.javascript)

        OfferedSkill.objects.create(user=self.user2, language=self.javascript, level=3)
        WantedSkill.objects.create(user=self.user2, language=self.python)

        # Generar matches potenciales
        matches_created = generate_potential_matches()

        # Verificar que se creou polo menos un match
        self.assertGreaterEqual(matches_created, 1)

        # Verificar que existe un match entre user1 e user2
        match_exists = Match.objects.filter(
            user1=self.user1,
            user2=self.user2,
            match_type='POTENTIAL'
        ).exists()

        self.assertTrue(match_exists)

    def test_match_api_endpoints(self):
        # Crear un match directamente
        match = Match.objects.create(
            user1=self.user1,
            user2=self.user2,
            match_type='POTENTIAL',
            compatibility_score=40
        )

        # Iniciar sesión como user1
        self.client.force_authenticate(user=self.user1)

        # Obter lista de matches
        response = self.client.get('/api/matches/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 1)

        # Aceptar match
        response = self.client.post(f'/api/matches/{match.id}/accept/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        # Verificar que o match se actualizou a NORMAL
        match.refresh_from_db()
        self.assertEqual(match.match_type, 'NORMAL')