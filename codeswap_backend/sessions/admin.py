from django.contrib import admin
from .models import Session

@admin.register(Session)
class SessionAdmin(admin.ModelAdmin):
    list_display = ('teacher', 'student', 'language', 'status', 'date_time')
    list_filter = ('status', 'language')
    search_fields = ('teacher__username', 'student__username')