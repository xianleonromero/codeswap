from rest_framework import serializers
from .models import Match
from users.serializers import ProfileSerializer
from users.models import Profile

class MatchSerializer(serializers.ModelSerializer):
    class Meta:
        model = Match
        fields = '__all__'

class MatchListSerializer(serializers.ModelSerializer):
    matched_profile = serializers.SerializerMethodField()

    class Meta:
        model = Match
        fields = ['id', 'match_type', 'compatibility_score', 'created_at', 'matched_profile']

    def get_matched_profile(self, obj):
        request = self.context.get('request')
        if not request:
            return None

        current_user = request.user
        matched_user = obj.user2 if obj.user1 == current_user else obj.user1
        profile = Profile.objects.get(user=matched_user)
        return ProfileSerializer(profile).data