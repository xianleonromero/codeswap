from django.db import models
from django.contrib.auth.models import User

class Match(models.Model):
    MATCH_TYPES = [
        ('POTENTIAL', 'Potencial'),
        ('NORMAL', 'Normal'),
    ]

    user1 = models.ForeignKey(User, on_delete=models.CASCADE, related_name='matches_as_user1')
    user2 = models.ForeignKey(User, on_delete=models.CASCADE, related_name='matches_as_user2')
    match_type = models.CharField(max_length=10, choices=MATCH_TYPES, default='POTENTIAL')
    created_at = models.DateTimeField(auto_now_add=True)
    compatibility_score = models.IntegerField(default=0)

    class Meta:
        unique_together = ('user1', 'user2')

    def __str__(self):
        return f"Match entre {self.user1.username} y {self.user2.username}"