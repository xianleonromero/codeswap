from django.db.models.signals import post_save, post_delete
from django.dispatch import receiver
from django.contrib.auth.models import User
from .models import Profile, OfferedSkill, WantedSkill
from matches.utils import calculate_compatibility_score, generate_potential_matches

@receiver(post_save, sender=User)
def create_user_profile(sender, instance, created, **kwargs):
    """Crea un perfil automáticamente cuando se crea un usuario"""
    if created:
        Profile.objects.create(user=instance)

@receiver(post_save, sender=OfferedSkill)
@receiver(post_save, sender=WantedSkill)
@receiver(post_delete, sender=OfferedSkill)
@receiver(post_delete, sender=WantedSkill)
def update_potential_matches(sender, instance, **kwargs):
    """Actualiza los matches potenciales cuando cambian las habilidades de un usuario"""
    # Para grandes volúmenes de usuarios, considera realizar esto como unha tarea programada
    # en lugar de "en tempo real"
    user = instance.user

    # Xenerar matches para este usuario específico
    all_users = User.objects.filter(is_active=True).exclude(id=user.id).exclude(is_staff=True)

    for other_user in all_users:
        score = calculate_compatibility_score(user, other_user)

        # Solo crear match si hai certa compatibilidad
        if score >= 10:
            # Ordenar users por ID para consistencia
            if user.id > other_user.id:
                user1, user2 = other_user, user
            else:
                user1, user2 = user, other_user

            Match.objects.update_or_create(
                user1=user1,
                user2=user2,
                defaults={
                    'match_type': 'POTENTIAL',
                    'compatibility_score': score
                }
            )
        else:
            # Si non hai suficiente compatibilidad, eliminar match potencial si existe
            Match.objects.filter(
                (Q(user1=user) & Q(user2=other_user)) |
                (Q(user1=other_user) & Q(user2=user)),
                match_type='POTENTIAL'
            ).delete()