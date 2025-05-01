from rest_framework import serializers
from .models import Session
from users.serializers import ProfileSerializer
from users.models import Profile
from skills.serializers import ProgrammingLanguageSerializer

class SessionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Session
        fields = '__all__'

class SessionListSerializer(serializers.ModelSerializer):
    teacher_profile = serializers.SerializerMethodField()
    student_profile = serializers.SerializerMethodField()
    language_details = serializers.SerializerMethodField()

    class Meta:
        model = Session
        fields = ['id', 'status', 'date_time', 'duration_minutes', 'teacher_profile', 'student_profile', 'language_details']

    def get_teacher_profile(self, obj):
        profile = Profile.objects.get(user=obj.teacher)
        return ProfileSerializer(profile).data

    def get_student_profile(self, obj):
        profile = Profile.objects.get(user=obj.student)
        return ProfileSerializer(profile).data

    def get_language_details(self, obj):
        return ProgrammingLanguageSerializer(obj.language).data