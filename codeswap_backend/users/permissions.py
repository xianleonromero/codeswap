from rest_framework import permissions

class IsOwnerOrReadOnly(permissions.BasePermission):
    """
    Permiso para permitir que solo os propietarios poidan editar os seeus propios recursos.
    """
    def has_object_permission(self, request, view, obj):
        # Permitir solicitudes de solo lectura para calqueira solicitud
        if request.method in permissions.SAFE_METHODS:
            return True

        # Verificar si o obxeto ten un atributo user ou un atributo relacionado
        # que podemos usar para comparar co usuario que fai a solicitud
        if hasattr(obj, 'user'):
            return obj.user == request.user
        elif hasattr(obj, 'teacher'):
            return obj.teacher == request.user
        elif hasattr(obj, 'student'):
            return obj.student == request.user

        # Por defecto, denegar permiso
        return False