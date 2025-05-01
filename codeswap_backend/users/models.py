from django.db import models
from django.contrib.auth.models import User
from skills.models import ProgrammingLanguage

class Profile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, related_name='profile')
    bio = models.TextField(blank=True, null=True)
    avatar_url = models.CharField(max_length=255, blank=True, null=True)
    rating = models.FloatField(default=0.0)

    def __str__(self):
        return f"Perfil de {self.user.username}"

class OfferedSkill(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='offered_skills')
    language = models.ForeignKey(ProgrammingLanguage, on_delete=models.CASCADE)
    level = models.IntegerField(default=1)  # Escala 1-5

    class Meta:
        unique_together = ('user', 'language')

    def __str__(self):
        return f"{self.user.username} ofrece {self.language.name}"

class WantedSkill(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='wanted_skills')
    language = models.ForeignKey(ProgrammingLanguage, on_delete=models.CASCADE)

    class Meta:
        unique_together = ('user', 'language')

    def __str__(self):
        return f"{self.user.username} busca {self.language.name}"