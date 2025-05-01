from django.contrib.auth.models import User
from django.db.models import Q
from users.models import OfferedSkill, WantedSkill
from matches.models import Match

def calculate_compatibility_score(user1, user2):
    """Calcula puntuación de compatibilidad entre dos usuarios"""
    score = 0

    # Verificar skills que user1 ofrece e user2 busca
    user1_offers = OfferedSkill.objects.filter(user=user1).values_list('language_id', flat=True)
    user2_wants = WantedSkill.objects.filter(user=user2).values_list('language_id', flat=True)

    # Coincidencias: o que user1 ofrece e user2 busca
    common_skills_1_to_2 = set(user1_offers).intersection(set(user2_wants))
    score += len(common_skills_1_to_2) * 10

    # Verificar skills que user2 ofrece e user1 busca
    user2_offers = OfferedSkill.objects.filter(user=user2).values_list('language_id', flat=True)
    user1_wants = WantedSkill.objects.filter(user=user1).values_list('language_id', flat=True)

    # Coincidencias: o que user2 ofrece e user1 busca
    common_skills_2_to_1 = set(user2_offers).intersection(set(user1_wants))
    score += len(common_skills_2_to_1) * 10

    # Bonus por match bidireccional (potencial)
    if common_skills_1_to_2 and common_skills_2_to_1:
        score += 20

    return score

def generate_potential_matches():
    #"""Genera matches potenciales entre usuarios basado en skills"""
    all_users = User.objects.filter(is_active=True).exclude(is_staff=True)
    matches_created = 0

    for i, user1 in enumerate(all_users):
        for user2 in all_users[i+1:]:  # Evitar duplicados e auto-matches
            # Verificar si xa existe un match entre estos usuarios
            existing_match = Match.objects.filter(
                (Q(user1=user1) & Q(user2=user2)) |
                (Q(user1=user2) & Q(user2=user1))
            ).exists()

            if not existing_match:
                score = calculate_compatibility_score(user1, user2)

                # Solo crear match si hai certa compatibilidad
                if score >= 10:
                    # Asegurar que user1 sempre ten menor ID que user2 para consistencia
                    if user1.id > user2.id:
                        user1, user2 = user2, user1

                    Match.objects.create(
                        user1=user1,
                        user2=user2,
                        match_type='POTENTIAL',
                        compatibility_score=score
                    )
                    matches_created += 1

    return matches_created