from django.db import models
from django.contrib.auth.models import User
from skills.models import ProgrammingLanguage

class Session(models.Model):
    STATUS_CHOICES = [
        ('PENDING', 'Pendiente'),
        ('CONFIRMED', 'Confirmada'),
        ('COMPLETED', 'Completada'),
        ('CANCELLED', 'Cancelada'),
    ]

    teacher = models.ForeignKey(User, on_delete=models.CASCADE, related_name='sessions_as_teacher')
    student = models.ForeignKey(User, on_delete=models.CASCADE, related_name='sessions_as_student')
    language = models.ForeignKey(ProgrammingLanguage, on_delete=models.CASCADE)
    status = models.CharField(max_length=10, choices=STATUS_CHOICES, default='PENDING')
    date_time = models.DateTimeField()
    duration_minutes = models.IntegerField(default=60)
    google_calendar_event_id = models.CharField(max_length=255, blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Sesión: {self.teacher.username} enseñando {self.language.name} a {self.student.username}"