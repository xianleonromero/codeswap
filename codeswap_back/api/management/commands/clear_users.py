from django.core.management.base import BaseCommand
from django.contrib.auth.models import User
from api.models import Match, Session, OfferedSkill, WantedSkill, UserProfile


class Command(BaseCommand):
    help = 'Clear all users and related data except admin'

    def handle(self, *args, **options):
        # Eliminar todos los datos relacionados
        self.stdout.write("🗑️ Deleting sessions...")
        Session.objects.all().delete()

        self.stdout.write("🗑️ Deleting matches...")
        Match.objects.all().delete()

        self.stdout.write("🗑️ Deleting skills...")
        OfferedSkill.objects.all().delete()
        WantedSkill.objects.all().delete()

        self.stdout.write("🗑️ Deleting user profiles...")
        UserProfile.objects.exclude(user__username='admin').delete()

        # Eliminar usuarios excepto admin
        self.stdout.write("🗑️ Deleting users...")
        deleted_count = User.objects.exclude(username='admin').count()
        User.objects.exclude(username='admin').delete()

        self.stdout.write(
            self.style.SUCCESS(f'✅ Successfully deleted {deleted_count} users and all related data')
        )
        self.stdout.write("👑 Admin user preserved")