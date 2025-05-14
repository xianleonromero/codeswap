from django.db import models
from django.contrib.auth.models import User
from django.dispatch import receiver
from django.db.models.signals import post_save

class ProgrammingLanguage(models.Model):
    name = models.CharField(max_length=100)
    icon = models.CharField(max_length=255, blank=True, null=True)

    def __str__(self):
        return self.name

class UserProfile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, related_name='profile')
    bio = models.TextField(blank=True, null=True)
    avatar_url = models.CharField(max_length=255, blank=True, null=True)
    rating = models.FloatField(default=0.0)

    def __str__(self):
        return self.user.username

@receiver(post_save, sender=User)
def create_user_profile(sender, instance, created, **kwargs):
    if created:
        UserProfile.objects.create(user=instance)

@receiver(post_save, sender=User)
def save_user_profile(sender, instance, **kwargs):
    instance.profile.save()

class OfferedSkill(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='offered_skills')
    language = models.ForeignKey(ProgrammingLanguage, on_delete=models.CASCADE)
    level = models.IntegerField(default=1)  # 1-5 como en el modelo front

    class Meta:
        unique_together = ('user', 'language')

    def __str__(self):
        return f"{self.user.username} offers {self.language.name}"

class WantedSkill(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='wanted_skills')
    language = models.ForeignKey(ProgrammingLanguage, on_delete=models.CASCADE)

    class Meta:
        unique_together = ('user', 'language')

    def __str__(self):
        return f"{self.user.username} wants {self.language.name}"

class Match(models.Model):
    TYPE_POTENTIAL = 'POTENTIAL'
    TYPE_NORMAL = 'NORMAL'

    MATCH_TYPES = [
        (TYPE_POTENTIAL, 'Potential Match'),
        (TYPE_NORMAL, 'Normal Match'),
    ]

    user1 = models.ForeignKey(User, on_delete=models.CASCADE, related_name='matches_as_user1')
    user2 = models.ForeignKey(User, on_delete=models.CASCADE, related_name='matches_as_user2')
    match_type = models.CharField(max_length=20, choices=MATCH_TYPES, default=TYPE_POTENTIAL)
    compatibility_score = models.FloatField(default=0.0)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        unique_together = ('user1', 'user2')

    def __str__(self):
        return f"{self.user1.username} - {self.user2.username} ({self.match_type})"

    def is_potential_match(self):
        return self.match_type == self.TYPE_POTENTIAL

class Session(models.Model):
    STATUS_PENDING = 'PENDING'
    STATUS_CONFIRMED = 'CONFIRMED'
    STATUS_COMPLETED = 'COMPLETED'
    STATUS_CANCELLED = 'CANCELLED'

    STATUS_CHOICES = [
        (STATUS_PENDING, 'Pending'),
        (STATUS_CONFIRMED, 'Confirmed'),
        (STATUS_COMPLETED, 'Completed'),
        (STATUS_CANCELLED, 'Cancelled'),
    ]

    teacher = models.ForeignKey(User, on_delete=models.CASCADE, related_name='sessions_as_teacher')
    student = models.ForeignKey(User, on_delete=models.CASCADE, related_name='sessions_as_student')
    language = models.ForeignKey(ProgrammingLanguage, on_delete=models.CASCADE)
    status = models.CharField(max_length=20, choices=STATUS_CHOICES, default=STATUS_PENDING)
    date_time = models.DateTimeField()
    duration_minutes = models.IntegerField(default=60)
    google_calendar_event_id = models.CharField(max_length=255, blank=True, null=True)

    def __str__(self):
        return f"{self.teacher.username} teaching {self.student.username} - {self.language.name}"

    def is_pending(self):
        return self.status == self.STATUS_PENDING

    def is_confirmed(self):
        return self.status == self.STATUS_CONFIRMED

    def is_completed(self):
        return self.status == self.STATUS_COMPLETED

    def is_cancelled(self):
        return self.status == self.STATUS_CANCELLED