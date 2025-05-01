from rest_framework import serializers
from .models import ProgrammingLanguage

class ProgrammingLanguageSerializer(serializers.ModelSerializer):
    class Meta:
        model = ProgrammingLanguage
        fields = '__all__'