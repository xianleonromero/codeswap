from django.contrib import admin
from .models import Match

@admin.register(Match)
class MatchAdmin(admin.ModelAdmin):
    list_display = ('user1', 'user2', 'match_type', 'compatibility_score', 'created_at')
    list_filter = ('match_type',)
    search_fields = ('user1__username', 'user2__username')