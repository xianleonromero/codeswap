from django.contrib import admin
from .models import Profile, OfferedSkill, WantedSkill

class OfferedSkillInline(admin.TabularInline):
    model = OfferedSkill
    extra = 1

class WantedSkillInline(admin.TabularInline):
    model = WantedSkill
    extra = 1

@admin.register(Profile)
class ProfileAdmin(admin.ModelAdmin):
    list_display = ('user', 'rating')
    search_fields = ('user__username', 'user__email')
    inlines = [OfferedSkillInline, WantedSkillInline]

admin.site.register(OfferedSkill)
admin.site.register(WantedSkill)