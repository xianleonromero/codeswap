import json
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth.models import User
from django.utils import timezone
from .models import ProgrammingLanguage, UserProfile, OfferedSkill, WantedSkill, Match, Session
import random
from datetime import timedelta
from django.contrib.auth import authenticate
from django.db.models import Q
from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.response import Response
from django.forms.models import model_to_dict
from datetime import datetime


@api_view(['GET'])
@permission_classes([AllowAny])
def health_check(request):
    return Response({"lives": True})

@api_view(['POST'])
@permission_classes([AllowAny])
def login(request):
    username = request.data.get('username')
    password = request.data.get('password')
    
    if not username or not password:
        return Response({"error": "Please provide both username and password"}, status=400)
    
    user = authenticate(username=username, password=password)
    
    if not user:
        return Response({"error": "Invalid credentials"}, status=401)
    
    token, _ = Token.objects.get_or_create(user=user)
    
    return Response({
        "token": token.key,
        "user_id": user.id,
        "username": user.username
    })

@api_view(['POST'])
@permission_classes([AllowAny])
def register(request):
    username = request.data.get('username')
    email = request.data.get('email')
    password = request.data.get('password')
    
    if not username or not email or not password:
        return Response({"error": "Please provide username, email and password"}, status=400)
    
    if User.objects.filter(username=username).exists():
        return Response({"error": "Username already exists"}, status=400)
    
    if User.objects.filter(email=email).exists():
        return Response({"error": "Email already exists"}, status=400)
    
    user = User.objects.create_user(username=username, email=email, password=password)
    
    token, _ = Token.objects.get_or_create(user=user)
    
    return Response({
        "token": token.key,
        "user_id": user.id,
        "username": user.username
    }, status=201)

@api_view(['GET'])
@permission_classes([AllowAny])
def languages(request):
    languages = ProgrammingLanguage.objects.all()
    languages_data = []
    
    for lang in languages:
        languages_data.append({
            "id": lang.id,
            "name": lang.name,
            "icon": lang.icon
        })
    
    return Response(languages_data)

@api_view(['GET', 'PUT'])
@permission_classes([IsAuthenticated])
def profile(request):
    if request.method == 'GET':
        user = request.user
        profile = UserProfile.objects.get(user=user)

        offered_skills = []
        for skill in OfferedSkill.objects.filter(user=user):
            offered_skills.append({
                "id": skill.id,
                "language": {
                    "id": skill.language.id,
                    "name": skill.language.name,
                    "icon": skill.language.icon
                },
                "level": skill.level
            })
        
        wanted_skills = []
        for skill in WantedSkill.objects.filter(user=user):
            wanted_skills.append({
                "id": skill.id,
                "language": {
                    "id": skill.language.id,
                    "name": skill.language.name,
                    "icon": skill.language.icon
                }
            })
        
        return Response({
            "user": {
                "id": user.id,
                "username": user.username,
                "email": user.email,
                "first_name": user.first_name,
                "last_name": user.last_name
            },
            "bio": profile.bio,
            "avatar_url": profile.avatar_url,
            "rating": profile.rating,
            "offered_skills": offered_skills,
            "wanted_skills": wanted_skills
        })
    
    elif request.method == 'PUT':
        user = request.user
        profile = UserProfile.objects.get(user=user)

        if 'username' in request.data:
            user.username = request.data['username']
        if 'email' in request.data:
            user.email = request.data['email']
        if 'first_name' in request.data:
            user.first_name = request.data['first_name']
        if 'last_name' in request.data:
            user.last_name = request.data['last_name']
        user.save()

        if 'bio' in request.data:
            profile.bio = request.data['bio']
        if 'avatar_url' in request.data:
            profile.avatar_url = request.data['avatar_url']
        profile.save()

        if 'offered_skills' in request.data:
            OfferedSkill.objects.filter(user=user).delete()
            for skill_data in request.data['offered_skills']:
                language = ProgrammingLanguage.objects.get(id=skill_data['language_id'])
                level = skill_data.get('level', 1)
                OfferedSkill.objects.create(user=user, language=language, level=level)

        if 'wanted_skills' in request.data:
            WantedSkill.objects.filter(user=user).delete()
            for skill_data in request.data['wanted_skills']:
                language = ProgrammingLanguage.objects.get(id=skill_data['language_id'])
                WantedSkill.objects.create(user=user, language=language)
        
        return Response({"message": "Profile updated successfully"})


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def potential_matches(request):
    # Buscar matches potenciales donde el usuario sea user1 O user2
    matches = Match.objects.filter(
        Q(user1=request.user) | Q(user2=request.user),
        match_type=Match.TYPE_POTENTIAL
    )

    matches_data = []
    for match in matches:
        # Siempre estructurar la respuesta con el usuario actual como user1
        if match.user1 == request.user:
            current_user = match.user1
            other_user = match.user2
        else:
            current_user = match.user2
            other_user = match.user1

        # Lo que el usuario actual ofrece y el otro usuario quiere
        current_offers = OfferedSkill.objects.filter(user=current_user)
        other_wants = WantedSkill.objects.filter(user=other_user)

        user1_offers = []
        for offered in current_offers:
            if other_wants.filter(language=offered.language).exists():
                user1_offers.append({
                    "id": offered.language.id,
                    "name": offered.language.name,
                    "icon": offered.language.icon
                })

        # Lo que el otro usuario ofrece y el usuario actual quiere
        other_offers = OfferedSkill.objects.filter(user=other_user)
        current_wants = WantedSkill.objects.filter(user=current_user)

        user2_wants = []
        for wanted in current_wants:
            if other_offers.filter(language=wanted.language).exists():
                user2_wants.append({
                    "id": wanted.language.id,
                    "name": wanted.language.name,
                    "icon": wanted.language.icon
                })

        matches_data.append({
            "id": match.id,
            "user1": {  # Siempre el usuario actual
                "id": current_user.id,
                "username": current_user.username,
                "first_name": current_user.first_name,
                "last_name": current_user.last_name
            },
            "user2": {  # Siempre el otro usuario
                "id": other_user.id,
                "username": other_user.username,
                "first_name": other_user.first_name,
                "last_name": other_user.last_name
            },
            "match_type": match.match_type,
            "compatibility_score": match.compatibility_score,
            "created_at": match.created_at,
            "user1_offers": user1_offers,  # Lo que el usuario actual ofrece
            "user2_wants": user2_wants  # Lo que el usuario actual quiere aprender
        })

    return Response(matches_data)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def normal_matches(request):
    # Buscar matches normales donde el usuario sea user1 O user2
    matches = Match.objects.filter(
        Q(user1=request.user) | Q(user2=request.user),
        match_type=Match.TYPE_NORMAL
    )

    matches_data = []
    for match in matches:
        # Siempre estructurar la respuesta con el usuario actual como user1
        if match.user1 == request.user:
            current_user = match.user1
            other_user = match.user2
        else:
            current_user = match.user2
            other_user = match.user1

        # Lo que el usuario actual ofrece y el otro usuario quiere
        current_offers = OfferedSkill.objects.filter(user=current_user)
        other_wants = WantedSkill.objects.filter(user=other_user)

        user1_offers = []
        for offered in current_offers:
            if other_wants.filter(language=offered.language).exists():
                user1_offers.append({
                    "id": offered.language.id,
                    "name": offered.language.name,
                    "icon": offered.language.icon
                })

        # Lo que el otro usuario ofrece y el usuario actual quiere
        other_offers = OfferedSkill.objects.filter(user=other_user)
        current_wants = WantedSkill.objects.filter(user=current_user)

        user2_wants = []
        for wanted in current_wants:
            if other_offers.filter(language=wanted.language).exists():
                user2_wants.append({
                    "id": wanted.language.id,
                    "name": wanted.language.name,
                    "icon": wanted.language.icon
                })

        matches_data.append({
            "id": match.id,
            "user1": {  # Siempre el usuario actual
                "id": current_user.id,
                "username": current_user.username,
                "first_name": current_user.first_name,
                "last_name": current_user.last_name
            },
            "user2": {  # Siempre el otro usuario
                "id": other_user.id,
                "username": other_user.username,
                "first_name": other_user.first_name,
                "last_name": other_user.last_name
            },
            "match_type": match.match_type,
            "compatibility_score": match.compatibility_score,
            "created_at": match.created_at,
            "user1_offers": user1_offers,  # Lo que el usuario actual ofrece
            "user2_wants": user2_wants  # Lo que el usuario actual quiere aprender
        })

    return Response(matches_data)


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def refresh_matches(request):
    # Limpiar matches potenciales anteriores del usuario
    Match.objects.filter(user1=request.user, match_type=Match.TYPE_POTENTIAL).delete()

    user_offered = OfferedSkill.objects.filter(user=request.user)
    user_wanted = WantedSkill.objects.filter(user=request.user)

    potential_matches = []
    normal_matches = []

    # Obtener mis habilidades como sets para facilitar las comparaciones
    my_offers = set(user_offered.values_list('language_id', flat=True))
    my_wants = set(user_wanted.values_list('language_id', flat=True))

    # Buscar todos los otros usuarios
    other_users = User.objects.exclude(id=request.user.id)

    for other_user in other_users:
        # Obtener habilidades del otro usuario
        other_offers = set(OfferedSkill.objects.filter(user=other_user).values_list('language_id', flat=True))
        other_wants = set(WantedSkill.objects.filter(user=other_user).values_list('language_id', flat=True))

        # Verificar si ya existe un match (en cualquier dirección)
        existing_match = Match.objects.filter(
            Q(user1=request.user, user2=other_user) |
            Q(user1=other_user, user2=request.user)
        ).exists()

        if existing_match:
            continue

        # Verificar intersecciones
        i_can_teach = my_offers.intersection(other_wants)  # Lo que yo ofrezco y él quiere
        he_can_teach = other_offers.intersection(my_wants)  # Lo que él ofrece y yo quiero

        # Determinar tipo de match
        if i_can_teach and he_can_teach:
            # MATCH POTENCIAL: Bidireccional - ambos pueden enseñarse algo
            potential_matches.append(other_user)
        elif i_can_teach or he_can_teach:
            # MATCH NORMAL: Unidireccional - solo uno puede enseñar al otro
            normal_matches.append(other_user)

    # Crear matches potenciales
    matches_created = 0
    for other_user in potential_matches:
        # Calcular compatibilidad para potenciales (más alta porque es bidireccional)
        my_offers = set(user_offered.values_list('language_id', flat=True))
        my_wants = set(user_wanted.values_list('language_id', flat=True))
        other_offers = set(OfferedSkill.objects.filter(user=other_user).values_list('language_id', flat=True))
        other_wants = set(WantedSkill.objects.filter(user=other_user).values_list('language_id', flat=True))

        mutual_teaching_potential = len(my_offers.intersection(other_wants)) + len(other_offers.intersection(my_wants))
        compatibility_score = min(95.0, 75.0 + (mutual_teaching_potential * 5) + random.uniform(-5, 10))

        Match.objects.create(
            user1=request.user,
            user2=other_user,
            match_type=Match.TYPE_POTENTIAL,
            compatibility_score=compatibility_score
        )
        matches_created += 1

    # Crear matches normales
    for other_user in normal_matches:
        # Calcular compatibilidad para normales (más baja porque es unidireccional)
        my_offers = set(user_offered.values_list('language_id', flat=True))
        my_wants = set(user_wanted.values_list('language_id', flat=True))
        other_offers = set(OfferedSkill.objects.filter(user=other_user).values_list('language_id', flat=True))
        other_wants = set(WantedSkill.objects.filter(user=other_user).values_list('language_id', flat=True))

        single_direction_potential = max(
            len(my_offers.intersection(other_wants)),
            len(other_offers.intersection(my_wants))
        )
        compatibility_score = min(85.0, 60.0 + (single_direction_potential * 8) + random.uniform(-3, 8))

        Match.objects.create(
            user1=request.user,
            user2=other_user,
            match_type=Match.TYPE_NORMAL,
            compatibility_score=compatibility_score
        )
        matches_created += 1

    return Response({
        "message": "Matches refreshed successfully",
        "matches_found": matches_created,
        "potential_matches": len(potential_matches),
        "normal_matches": len(normal_matches)
    })
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def upcoming_sessions(request):
    now = timezone.now()
    sessions = Session.objects.filter(
        Q(teacher=request.user) | Q(student=request.user),
        date_time__gte=now
    ).order_by('date_time')
    
    sessions_data = []
    for session in sessions:
        sessions_data.append({
            "id": session.id,
            "teacher": {
                "id": session.teacher.id,
                "username": session.teacher.username,
                "first_name": session.teacher.first_name,
                "last_name": session.teacher.last_name
            },
            "student": {
                "id": session.student.id,
                "username": session.student.username,
                "first_name": session.student.first_name,
                "last_name": session.student.last_name
            },
            "language": {
                "id": session.language.id,
                "name": session.language.name,
                "icon": session.language.icon
            },
            "status": session.status,
            "date_time": session.date_time,
            "duration_minutes": session.duration_minutes,
            "google_calendar_event_id": session.google_calendar_event_id
        })
    
    return Response(sessions_data)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def past_sessions(request):
    now = timezone.now()
    sessions = Session.objects.filter(
        Q(teacher=request.user) | Q(student=request.user),
        date_time__lt=now
    ).order_by('-date_time')
    
    sessions_data = []
    for session in sessions:
        sessions_data.append({
            "id": session.id,
            "teacher": {
                "id": session.teacher.id,
                "username": session.teacher.username,
                "first_name": session.teacher.first_name,
                "last_name": session.teacher.last_name
            },
            "student": {
                "id": session.student.id,
                "username": session.student.username,
                "first_name": session.student.first_name,
                "last_name": session.student.last_name
            },
            "language": {
                "id": session.language.id,
                "name": session.language.name,
                "icon": session.language.icon
            },
            "status": session.status,
            "date_time": session.date_time,
            "duration_minutes": session.duration_minutes,
            "google_calendar_event_id": session.google_calendar_event_id
        })
    
    return Response(sessions_data)


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def create_session(request):
    if 'student_id' not in request.data or 'language_id' not in request.data or 'date_time' not in request.data:
        return Response({"error": "Missing required fields: student_id, language_id, date_time"}, status=400)

    try:
        student = User.objects.get(id=request.data['student_id'])
        language = ProgrammingLanguage.objects.get(id=request.data['language_id'])
    except (User.DoesNotExist, ProgrammingLanguage.DoesNotExist):
        return Response({"error": "Invalid student_id or language_id"}, status=400)

    # Verificar que no sea el mismo usuario
    if request.user == student:
        return Response({"error": "Cannot create session with yourself"}, status=400)

    session = Session.objects.create(
        teacher=request.user,
        student=student,
        language=language,
        date_time=timezone.datetime.fromisoformat(request.data['date_time'].replace('Z', '+00:00')),
        duration_minutes=request.data.get('duration_minutes', 60),
        status=Session.STATUS_PENDING
    )

    # Actualizar match a normal si existe
    Match.objects.filter(
        Q(user1=request.user, user2=student) | Q(user1=student, user2=request.user),
        match_type=Match.TYPE_POTENTIAL
    ).update(match_type=Match.TYPE_NORMAL)

    return Response({
        "id": session.id,
        "teacher": {
            "id": session.teacher.id,
            "username": session.teacher.username,
            "first_name": session.teacher.first_name,
            "last_name": session.teacher.last_name
        },
        "student": {
            "id": session.student.id,
            "username": session.student.username,
            "first_name": session.student.first_name,
            "last_name": session.student.last_name
        },
        "language": {
            "id": session.language.id,
            "name": session.language.name,
            "icon": session.language.icon
        },
        "status": session.status,
        "date_time": session.date_time,
        "duration_minutes": session.duration_minutes,
        "google_calendar_event_id": session.google_calendar_event_id,
        "message": f"Sesión solicitada a {student.first_name}. Aparecerá en sus sesiones pendientes."
    }, status=201)

@api_view(['PUT'])
@permission_classes([IsAuthenticated])
def update_session_status(request, session_id):
    try:
        session = Session.objects.get(id=session_id)
    except Session.DoesNotExist:
        return Response({"error": "Session not found"}, status=404)

    if request.user != session.teacher and request.user != session.student:
        return Response({"error": "You don't have permission to update this session"}, status=403)
    
    if 'status' not in request.data:
        return Response({"error": "Status field is required"}, status=400)
    
    new_status = request.data['status']
    if new_status not in [s[0] for s in Session.STATUS_CHOICES]:
        return Response({"error": "Invalid status value"}, status=400)
    
    session.status = new_status
    session.save()
    
    return Response({
        "id": session.id,
        "teacher": {
            "id": session.teacher.id,
            "username": session.teacher.username,
            "first_name": session.teacher.first_name,
            "last_name": session.teacher.last_name
        },
        "student": {
            "id": session.student.id,
            "username": session.student.username,
            "first_name": session.student.first_name,
            "last_name": session.student.last_name
        },
        "language": {
            "id": session.language.id,
            "name": session.language.name,
            "icon": session.language.icon
        },
        "status": session.status,
        "date_time": session.date_time,
        "duration_minutes": session.duration_minutes,
        "google_calendar_event_id": session.google_calendar_event_id
    })