from django.core.management.base import BaseCommand
from django.contrib.auth.models import User
from api.models import ProgrammingLanguage, OfferedSkill, WantedSkill
import random


class Command(BaseCommand):
    help = 'Create test users with skills'

    def handle(self, *args, **options):
        test_users_data = [
            {
                'username': 'juandev',
                'email': 'juan@example.com',
                'first_name': 'Juan',
                'last_name': 'Pérez',
                'password': 'testpass123',
                'offers': ['Java', 'Python', 'Spring Boot'],
                'wants': ['React', 'Angular', 'JavaScript']
            },
            {
                'username': 'mariacode',
                'email': 'maria@example.com',
                'first_name': 'María',
                'last_name': 'García',
                'password': 'testpass123',
                'offers': ['React', 'JavaScript', 'TypeScript'],
                'wants': ['Python', 'Django', 'Java']
            },
            {
                'username': 'carlostech',
                'email': 'carlos@example.com',
                'first_name': 'Carlos',
                'last_name': 'López',
                'password': 'testpass123',
                'offers': ['Angular', 'Node.js', 'TypeScript'],
                'wants': ['Flutter', 'Kotlin', 'Swift']
            },
            {
                'username': 'anaprog',
                'email': 'ana@example.com',
                'first_name': 'Ana',
                'last_name': 'Martín',
                'password': 'testpass123',
                'offers': ['Flutter', 'Kotlin', 'Swift'],
                'wants': ['Java', 'Spring Boot', 'C#']
            }
        ]

        created_users = 0

        for user_data in test_users_data:
            user, created = User.objects.get_or_create(
                username=user_data['username'],
                defaults={
                    'email': user_data['email'],
                    'first_name': user_data['first_name'],
                    'last_name': user_data['last_name']
                }
            )

            if created:
                user.set_password(user_data['password'])
                user.save()
                created_users += 1
                self.stdout.write(f"Created user: {user.username}")

                for skill_name in user_data['offers']:
                    try:
                        language = ProgrammingLanguage.objects.get(name=skill_name)
                        OfferedSkill.objects.get_or_create(
                            user=user,
                            language=language,
                            defaults={'level': random.randint(3, 5)}
                        )
                    except ProgrammingLanguage.DoesNotExist:
                        self.stdout.write(f"Language not found: {skill_name}")

                for skill_name in user_data['wants']:
                    try:
                        language = ProgrammingLanguage.objects.get(name=skill_name)
                        WantedSkill.objects.get_or_create(
                            user=user,
                            language=language
                        )
                    except ProgrammingLanguage.DoesNotExist:
                        self.stdout.write(f"Language not found: {skill_name}")

            else:
                self.stdout.write(f"User already exists: {user.username}")

        self.stdout.write(
            self.style.SUCCESS(f'Processed {len(test_users_data)} users. Created: {created_users}')
        )