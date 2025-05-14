from django.core.management.base import BaseCommand
from django.contrib.auth.models import User
from api.models import ProgrammingLanguage, UserProfile, OfferedSkill, WantedSkill, Match, Session
from django.utils import timezone
import random
from datetime import timedelta

class Command(BaseCommand):
    help = 'Populate the database with sample data'

    def handle(self, *args, **kwargs):
        self.stdout.write('Creating programming languages...')
        
        # Crear lenguajes de programación
        languages = [
            {'name': 'Java', 'icon': 'java_icon'},
            {'name': 'Python', 'icon': 'python_icon'},
            {'name': 'JavaScript', 'icon': 'js_icon'},
            {'name': 'Kotlin', 'icon': 'kotlin_icon'},
            {'name': 'Swift', 'icon': 'swift_icon'},
            {'name': 'C#', 'icon': 'csharp_icon'},
            {'name': 'C++', 'icon': 'cpp_icon'},
            {'name': 'PHP', 'icon': 'php_icon'},
            {'name': 'Ruby', 'icon': 'ruby_icon'},
            {'name': 'Go', 'icon': 'go_icon'},
            {'name': 'React', 'icon': 'react_icon'},
            {'name': 'Angular', 'icon': 'angular_icon'},
            {'name': 'Vue.js', 'icon': 'vue_icon'},
            {'name': 'Flutter', 'icon': 'flutter_icon'},
            {'name': 'Spring Boot', 'icon': 'spring_icon'},
        ]
        
        for lang_data in languages:
            ProgrammingLanguage.objects.get_or_create(
                name=lang_data['name'],
                defaults={'icon': lang_data['icon']}
            )
        
        # Crear usuarios de prueba
        self.stdout.write('Creating test users...')
        users_data = [
            {'username': 'JuanDev', 'email': 'juan@example.com', 'password': 'password123', 'first_name': 'Juan', 'last_name': 'Pérez'},
            {'username': 'MariaCoder', 'email': 'maria@example.com', 'password': 'password123', 'first_name': 'María', 'last_name': 'García'},
            {'username': 'PedroPython', 'email': 'pedro@example.com', 'password': 'password123', 'first_name': 'Pedro', 'last_name': 'López'},
            {'username': 'AnaJava', 'email': 'ana@example.com', 'password': 'password123', 'first_name': 'Ana', 'last_name': 'Martínez'},
            {'username': 'CarlosJS', 'email': 'carlos@example.com', 'password': 'password123', 'first_name': 'Carlos', 'last_name': 'Rodríguez'},
        ]
        
        created_users = []
        for user_data in users_data:
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
                created_users.append(user)
            
            # Actualizar perfil
            profile = UserProfile.objects.get(user=user)
            profile.bio = f"Desarrollador con experiencia en varios lenguajes. Me gusta {user_data['first_name']}."
            profile.rating = round(random.uniform(3.5, 5.0), 1)
            profile.save()
        
        # Asignar habilidades aleatorias a los usuarios
        self.stdout.write('Assigning skills to users...')
        all_languages = list(ProgrammingLanguage.objects.all())
        
        for user in User.objects.all():
            # Limpiar habilidades existentes para evitar duplicados en reinicios
            OfferedSkill.objects.filter(user=user).delete()
            WantedSkill.objects.filter(user=user).delete()
            
            # Asignar 2-4 habilidades ofrecidas
            offered_count = random.randint(2, 4)
            offered_languages = random.sample(all_languages, offered_count)
            
            for lang in offered_languages:
                OfferedSkill.objects.create(
                    user=user,
                    language=lang,
                    level=random.randint(1, 5)
                )
            
            # Asignar 2-3 habilidades deseadas (que no ofrezca)
            wanted_languages = [lang for lang in all_languages if lang not in offered_languages]
            wanted_count = min(random.randint(2, 3), len(wanted_languages))
            wanted_languages = random.sample(wanted_languages, wanted_count)
            
            for lang in wanted_languages:
                WantedSkill.objects.create(
                    user=user,
                    language=lang
                )
        
        # Crear matches
        self.stdout.write('Creating matches...')
        
        all_users = list(User.objects.all())
        
        for user1 in all_users:
            # Conseguir lo que este usuario ofrece
            user1_offers = set(skill.language for skill in OfferedSkill.objects.filter(user=user1))
            user1_wants = set(skill.language for skill in WantedSkill.objects.filter(user=user1))
            
            for user2 in all_users:
                if user1 != user2:
                    # Conseguir lo que el otro usuario ofrece/quiere
                    user2_offers = set(skill.language for skill in OfferedSkill.objects.filter(user=user2))
                    user2_wants = set(skill.language for skill in WantedSkill.objects.filter(user=user2))
                    
                    # Ver si hay match (user1 ofrece algo que user2 quiere Y user2 ofrece algo que user1 quiere)
                    if user1_offers.intersection(user2_wants) and user2_offers.intersection(user1_wants):
                        # Decidir si es un match normal o potencial
                        match_type = random.choice([Match.TYPE_NORMAL, Match.TYPE_POTENTIAL])
                        
                        # Calcular compatibilidad
                        compatibility = random.uniform(65.0, 98.0)
                        
                        # Crear match si no existe
                        Match.objects.get_or_create(
                            user1=user1,
                            user2=user2,
                            defaults={
                                'match_type': match_type,
                                'compatibility_score': compatibility
                            }
                        )
        # Crear sesiones
        self.stdout.write('Creating sessions...')

        # Crear algunas sesiones futuras
        now = timezone.now()

        for i in range(10):
            # Seleccionar dos usuarios aleatorios donde hay un match
            match = random.choice(list(Match.objects.all()))
            teacher = match.user1
            student = match.user2

            # Seleccionar un lenguaje que el teacher ofrece y el student quiere
            teacher_offers = OfferedSkill.objects.filter(user=teacher)
            student_wants = WantedSkill.objects.filter(user=student)

            # Encontrar una intersección
            matching_languages = []
            for offered in teacher_offers:
                if student_wants.filter(language=offered.language).exists():
                    matching_languages.append(offered.language)

            if matching_languages:
                language = random.choice(matching_languages)

                # Asignar fecha en el futuro cercano
                future_days = random.randint(1, 30)
                session_date = now + timedelta(days=future_days)
                session_hour = random.randint(9, 17)
                session_date = session_date.replace(hour=session_hour, minute=0, second=0, microsecond=0)

                # Duración aleatoria
                duration = random.choice([30, 45, 60, 90])

                # Asignar un estado aleatorio para la sesión
                status = random.choices(
                    [Session.STATUS_PENDING, Session.STATUS_CONFIRMED],
                    weights=[0.3, 0.7]
                )[0]

                Session.objects.create(
                    teacher=teacher,
                    student=student,
                    language=language,
                    status=status,
                    date_time=session_date,
                    duration_minutes=duration
                )

        # Crear algunas sesiones pasadas
        for i in range(5):
            # Seleccionar dos usuarios aleatorios donde hay un match
            match = random.choice(list(Match.objects.filter(match_type=Match.TYPE_NORMAL)))
            teacher = match.user1
            student = match.user2

            # Seleccionar un lenguaje
            teacher_offers = OfferedSkill.objects.filter(user=teacher)
            student_wants = WantedSkill.objects.filter(user=student)

            # Encontrar una intersección
            matching_languages = []
            for offered in teacher_offers:
                if student_wants.filter(language=offered.language).exists():
                    matching_languages.append(offered.language)

            if matching_languages:
                language = random.choice(matching_languages)

                # Asignar fecha en el pasado cercano
                past_days = random.randint(1, 30)
                session_date = now - timedelta(days=past_days)
                session_hour = random.randint(9, 17)
                session_date = session_date.replace(hour=session_hour, minute=0, second=0, microsecond=0)

                # Duración aleatoria
                duration = random.choice([30, 45, 60, 90])

                # Asignar un estado completado o cancelado para la sesión
                status = random.choices(
                    [Session.STATUS_COMPLETED, Session.STATUS_CANCELLED],
                    weights=[0.8, 0.2]
                )[0]

                Session.objects.create(
                    teacher=teacher,
                    student=student,
                    language=language,
                    status=status,
                    date_time=session_date,
                    duration_minutes=duration
                )

        self.stdout.write(self.style.SUCCESS('Successfully populated the database'))