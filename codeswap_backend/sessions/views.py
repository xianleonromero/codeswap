from rest_framework import viewsets, permissions, status
from rest_framework.decorators import action
from rest_framework.response import Response
from django.db.models import Q
from .models import Session
from .serializers import SessionSerializer, SessionListSerializer

class SessionViewSet(viewsets.ModelViewSet):
    serializer_class = SessionSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        user = self.request.user
        return Session.objects.filter(
            Q(teacher=user) | Q(student=user)
        ).order_by('-date_time')

    def get_serializer_class(self):
        if self.action == 'list':
            return SessionListSerializer
        return SessionSerializer

    @action(detail=True, methods=['post'])
    def confirm(self, request, pk=None):
        session = self.get_object()
        if session.status == 'PENDING' and session.teacher == request.user:
            session.status = 'CONFIRMED'
            session.save()
            return Response({'status': 'sesión confirmada'})
        return Response({'error': 'No se pudo confirmar la sesión'}, status=status.HTTP_400_BAD_REQUEST)

    @action(detail=True, methods=['post'])
    def complete(self, request, pk=None):
        session = self.get_object()
        if session.status == 'CONFIRMED' and (session.teacher == request.user or session.student == request.user):
            session.status = 'COMPLETED'
            session.save()
            return Response({'status': 'sesión completada'})
        return Response({'error': 'No se pudo completar la sesión'}, status=status.HTTP_400_BAD_REQUEST)

    @action(detail=True, methods=['post'])
    def cancel(self, request, pk=None):
        session = self.get_object()
        if session.status in ['PENDING', 'CONFIRMED'] and (session.teacher == request.user or session.student == request.user):
            session.status = 'CANCELLED'
            session.save()
            return Response({'status': 'sesión cancelada'})
        return Response({'error': 'No se pudo cancelar la sesión'}, status=status.HTTP_400_BAD_REQUEST)