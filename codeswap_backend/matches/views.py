from rest_framework import viewsets, permissions, status
from rest_framework.decorators import action
from rest_framework.response import Response
from django.db.models import Q
from .models import Match
from .serializers import MatchSerializer, MatchListSerializer

class MatchViewSet(viewsets.ModelViewSet):
    serializer_class = MatchSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        user = self.request.user
        return Match.objects.filter(
            Q(user1=user) | Q(user2=user)
        ).order_by('-created_at')

    def get_serializer_class(self):
        if self.action == 'list':
            return MatchListSerializer
        return MatchSerializer

    @action(detail=True, methods=['post'])
    def accept(self, request, pk=None):
        match = self.get_object()
        if match.match_type == 'POTENTIAL' and (match.user1 == request.user or match.user2 == request.user):
            match.match_type = 'NORMAL'
            match.save()
            return Response({'status': 'match aceptado'})
        return Response({'error': 'No se pudo aceptar el match'}, status=status.HTTP_400_BAD_REQUEST)

    @action(detail=True, methods=['post'])
    def reject(self, request, pk=None):
        match = self.get_object()
        if match.match_type == 'POTENTIAL' and (match.user1 == request.user or match.user2 == request.user):
            match.delete()
            return Response({'status': 'match rechazado'})
        return Response({'error': 'No se pudo rechazar el match'}, status=status.HTTP_400_BAD_REQUEST)

    @staff_member_required
    @require_POST
    def generate_matches_admin(request):
        matches_created = generate_potential_matches()
        return JsonResponse({'success': True, 'matches_created': matches_created})
