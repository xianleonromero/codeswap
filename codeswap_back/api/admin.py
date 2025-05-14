from django.contrib import admin
from .models import ProgrammingLanguage, UserProfile, OfferedSkill, WantedSkill, Match, Session

@admin.register(ProgrammingLanguage)
class ProgrammingLanguageAdmin(admin.ModelAdmin):
    list_display = ('name', 'icon')
    search_fields = ('name',)

@admin.register(UserProfile)
class UserProfileAdmin(admin.ModelAdmin):
    list_display = ('user', 'rating')
    search_fields = ('user__username', 'user__email')

@admin.register(OfferedSkill)
class OfferedSkillAdmin(admin.ModelAdmin):
    list_display = ('user', 'language', 'level')
    list_filter = ('language', 'level')
    search_fields = ('user__username', 'language__name')

@admin.register(WantedSkill)
class WantedSkillAdmin(admin.ModelAdmin):
    list_display = ('user', 'language')
    list_filter = ('language',)
    search_fields = ('user__username', 'language__name')

@admin.register(Match)
class MatchAdmin(admin.ModelAdmin):
    list_display = ('user1', 'user2', 'match_type', 'compatibility_score', 'created_at')
    list_filter = ('match_type',)
    search_fields = ('user1__username', 'user2__username')

@admin.register(Session)
class SessionAdmin(admin.ModelAdmin):
    list_display = ('teacher', 'student', 'language', 'status', 'date_time', 'duration_minutes')
    list_filter = ('status', 'language')
    search_fields = ('teacher__username', 'student__username', 'language__name')