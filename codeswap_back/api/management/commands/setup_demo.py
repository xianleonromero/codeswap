from django.core.management.base import BaseCommand
from django.core.management import call_command


class Command(BaseCommand):
    help = 'Setup complete demo environment'

    def handle(self, *args, **options):
        self.stdout.write("🚀 Setting up CodeSwap demo environment...")

        # Aplicar migraciones
        self.stdout.write("📦 Applying migrations...")
        call_command('migrate')

        # Poblar lenguajes
        self.stdout.write("🔤 Populating programming languages...")
        call_command('populate_languages')

        # Crear usuarios de prueba
        self.stdout.write("👥 Creating test users...")
        call_command('create_test_users')

        # Crear superusuario si no existe
        from django.contrib.auth.models import User
        if not User.objects.filter(username='admin').exists():
            self.stdout.write("👑 Creating admin user...")
            User.objects.create_superuser('admin', 'admin@example.com', 'admin123')
            self.stdout.write("Admin user created: admin/admin123")

        self.stdout.write(
            self.style.SUCCESS('✅ Demo environment setup complete!')
        )
        self.stdout.write("🔐 Test users available:")
        self.stdout.write("  - juandev / testpass123")
        self.stdout.write("  - mariacode / testpass123")
        self.stdout.write("  - carlostech / testpass123")
        self.stdout.write("  - anaprog / testpass123")
        self.stdout.write("🌐 Admin panel: /admin/ (admin/admin123)")