from django.core.management.base import BaseCommand
from api.models import ProgrammingLanguage


class Command(BaseCommand):
    help = 'Populate programming languages in the database'

    def handle(self, *args, **options):
        languages = [
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
            {'name': 'C++', 'icon': 'cpp_icon.png'},
            {'name': 'Go', 'icon': 'go_icon.png'},
            {'name': 'Rust', 'icon': 'rust_icon.png'},
            {'name': 'PHP', 'icon': 'php_icon.png'},
            {'name': 'Ruby', 'icon': 'ruby_icon.png'},
            {'name': 'TypeScript', 'icon': 'ts_icon.png'},
        ]

        created_count = 0
        for lang_data in languages:
            language, created = ProgrammingLanguage.objects.get_or_create(
                name=lang_data['name'],
                defaults={'icon': lang_data['icon']}
            )
            if created:
                created_count += 1
                self.stdout.write(f"Created: {language.name}")
            else:
                self.stdout.write(f"Already exists: {language.name}")

        self.stdout.write(
            self.style.SUCCESS(f'Successfully processed {len(languages)} languages. Created: {created_count}')
        )