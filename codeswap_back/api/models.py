from django.db import models
from django.contrib.auth.models import User
from django.dispatch import receiver
from django.db.models.signals import post_save
from django.db.models.signals import post_migrate
import random

class ProgrammingLanguage(models.Model):
    name = models.CharField(max_length=100)
    icon = models.CharField(max_length=255, blank=True, null=True)

    def __str__(self):
        return self.name

class UserProfile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, related_name='profile')
    bio = models.TextField(blank=True, null=True)
    avatar_url = models.CharField(max_length=255, blank=True, null=True)
    rating = models.FloatField(default=0.0)

    def __str__(self):
        return self.user.username

@receiver(post_save, sender=User)
def create_user_profile(sender, instance, created, **kwargs):
    if created:
        UserProfile.objects.create(user=instance)

@receiver(post_save, sender=User)
def save_user_profile(sender, instance, **kwargs):
    instance.profile.save()

class OfferedSkill(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='offered_skills')
    language = models.ForeignKey(ProgrammingLanguage, on_delete=models.CASCADE)
    level = models.IntegerField(default=1)  # 1-5 como en el modelo front

    class Meta:
        unique_together = ('user', 'language')

    def __str__(self):
        return f"{self.user.username} offers {self.language.name}"

class WantedSkill(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='wanted_skills')
    language = models.ForeignKey(ProgrammingLanguage, on_delete=models.CASCADE)

    class Meta:
        unique_together = ('user', 'language')

    def __str__(self):
        return f"{self.user.username} wants {self.language.name}"

class Match(models.Model):
    TYPE_POTENTIAL = 'POTENTIAL'
    TYPE_NORMAL = 'NORMAL'

    MATCH_TYPES = [
        (TYPE_POTENTIAL, 'Potential Match'),
        (TYPE_NORMAL, 'Normal Match'),
    ]

    user1 = models.ForeignKey(User, on_delete=models.CASCADE, related_name='matches_as_user1')
    user2 = models.ForeignKey(User, on_delete=models.CASCADE, related_name='matches_as_user2')
    match_type = models.CharField(max_length=20, choices=MATCH_TYPES, default=TYPE_POTENTIAL)
    compatibility_score = models.FloatField(default=0.0)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        unique_together = ('user1', 'user2')

    def __str__(self):
        return f"{self.user1.username} - {self.user2.username} ({self.match_type})"

    def is_potential_match(self):
        return self.match_type == self.TYPE_POTENTIAL

class Session(models.Model):
    STATUS_PENDING = 'PENDING'
    STATUS_CONFIRMED = 'CONFIRMED'
    STATUS_COMPLETED = 'COMPLETED'
    STATUS_CANCELLED = 'CANCELLED'

    STATUS_CHOICES = [
        (STATUS_PENDING, 'Pending'),
        (STATUS_CONFIRMED, 'Confirmed'),
        (STATUS_COMPLETED, 'Completed'),
        (STATUS_CANCELLED, 'Cancelled'),
    ]

    teacher = models.ForeignKey(User, on_delete=models.CASCADE, related_name='sessions_as_teacher')
    student = models.ForeignKey(User, on_delete=models.CASCADE, related_name='sessions_as_student')
    language = models.ForeignKey(ProgrammingLanguage, on_delete=models.CASCADE)
    status = models.CharField(max_length=20, choices=STATUS_CHOICES, default=STATUS_PENDING)
    date_time = models.DateTimeField()
    duration_minutes = models.IntegerField(default=60)
    google_calendar_event_id = models.CharField(max_length=255, blank=True, null=True)

    def __str__(self):
        return f"{self.teacher.username} teaching {self.student.username} - {self.language.name}"

    def is_pending(self):
        return self.status == self.STATUS_PENDING

    def is_confirmed(self):
        return self.status == self.STATUS_CONFIRMED

    def is_completed(self):
        return self.status == self.STATUS_COMPLETED

    def is_cancelled(self):
        return self.status == self.STATUS_CANCELLED


@receiver(post_migrate)
def create_initial_data(sender, **kwargs):
    if sender.name == 'api':
        print("🚀 Creating initial data...")

        # Crear admin si no existe
        from django.contrib.auth.models import User
        if not User.objects.filter(username='admin').exists():
            User.objects.create_superuser('admin', 'admin@codeswap.com', 'codeswap2024')
            print("✅ Admin user created: admin/codeswap2024")

        # Crear lenguajes si no existen
        languages_data = [
            {'name': 'Java', 'icon': 'java_icon.png'},
            {'name': 'Python', 'icon': 'python_icon.png'},
            {'name': 'JavaScript', 'icon': 'js_icon.png'},
            {'name': 'React', 'icon': 'react_icon.png'},
            {'name': 'Angular', 'icon': 'angular_icon.png'},
            {'name': 'Vue.js', 'icon': 'vue_icon.png'},
            {'name': 'Spring Boot', 'icon': 'spring_icon.png'},
            {'name': 'Django', 'icon': 'django_icon.png'},
            {'name': 'Node.js', 'icon': 'node_icon.png'},
            {'name': 'Kotlin', 'icon': 'kotlin_icon.png'},
            {'name': 'Swift', 'icon': 'swift_icon.png'},
            {'name': 'Flutter', 'icon': 'flutter_icon.png'},
            {'name': 'C#', 'icon': 'csharp_icon.png'},
            {'name': 'TypeScript', 'icon': 'ts_icon.png'},
        ]

        created_langs = 0
        for lang_data in languages_data:
            lang, created = ProgrammingLanguage.objects.get_or_create(
                name=lang_data['name'],
                defaults={'icon': lang_data['icon']}
            )
            if created:
                created_langs += 1
                print(f"✅ Created language: {lang.name}")

        print(f"📝 Total languages in DB: {ProgrammingLanguage.objects.count()}")

        # Crear usuarios de prueba si no existen
        test_users = [
            {
                'username': 'juandev',
                'email': 'juan@example.com',
                'first_name': 'Juan',
                'last_name': 'Pérez',
                'offers': ['Java', 'Python', 'Spring Boot'],
                'wants': ['React', 'Angular', 'JavaScript']
            },
            {
                'username': 'mariacode',
                'email': 'maria@example.com',
                'first_name': 'María',
                'last_name': 'García',
                'offers': ['React', 'JavaScript', 'TypeScript'],
                'wants': ['Python', 'Django', 'Java']
            },
            {
                'username': 'carlostech',
                'email': 'carlos@example.com',
                'first_name': 'Carlos',
                'last_name': 'López',
                'offers': ['Angular', 'Node.js', 'TypeScript'],
                'wants': ['Flutter', 'Kotlin', 'Swift']
            }
        ]

        for user_data in test_users:
            user, created = User.objects.get_or_create(
                username=user_data['username'],
                defaults={
                    'email': user_data['email'],
                    'first_name': user_data['first_name'],
                    'last_name': user_data['last_name']
                }
            )

            if created:
                user.set_password('testpass123')
                user.save()
                print(f"✅ Created user: {user.username}")

                # Añadir habilidades
                for skill_name in user_data['offers']:
                    try:
                        language = ProgrammingLanguage.objects.get(name=skill_name)
                        OfferedSkill.objects.get_or_create(
                            user=user,
                            language=language,
                            defaults={'level': random.randint(3, 5)}
                        )
                    except ProgrammingLanguage.DoesNotExist:
                        pass

                for skill_name in user_data['wants']:
                    try:
                        language = ProgrammingLanguage.objects.get(name=skill_name)
                        WantedSkill.objects.get_or_create(user=user, language=language)
                    except ProgrammingLanguage.DoesNotExist:
                        pass

        print("🎉 Initial data creation complete!")