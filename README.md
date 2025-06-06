# CodeSwap

Plataforma de intercambio de conocimientos de programación entre desarrolladores. Sistema de matching bidireccional con chat en tiempo real.

**Stack:** Android Java + Django REST API

## Instalación Rápida

### Backend

```bash
git clone https://github.com/xianleon310/codeswap.git
cd codeswap_back
pip install django djangorestframework django-cors-headers psycopg2-binary
python manage.py migrate
python manage.py setup_demo
python manage.py runserver
```

### Android

1. Abrir `codeswap_front` en Android Studio
2. Cambiar URL en `ApiClient.java`: `http://10.0.2.2:8000/api/`
3. Ejecutar en dispositivo/emulador

## Usuarios de Prueba

* `juandev` / `testpass123` - Java, Python → React, Angular
* `mariacode` / `testpass123` - React, JavaScript → Python, Django
* `carlostech` / `testpass123` - Angular, Node.js → Flutter, Kotlin

## API Principal

```
POST /api/auth/login/              # Autenticación
GET  /api/matches/potential/       # Matches bidireccionales
GET  /api/matches/normal/          # Matches unidireccionales
POST /api/matches/refresh/         # Buscar nuevos matches
GET  /api/conversations/           # Lista de chats
POST /api/session-requests/        # Solicitar sesión
```

## Funcionalidades

* **Matching inteligente** basado en intersección de habilidades ofrecidas/buscadas
* **Chat tiempo real** con auto-refresh cada 3 segundos
* **Gestión de sesiones** (PENDING → CONFIRMED → COMPLETED)
* **Sistema de notificaciones** para solicitudes pendientes

## Comandos Útiles

```bash
python manage.py create_test_users  # Crear usuarios demo
python manage.py populate_languages # Poblar lenguajes
python manage.py clear_users        # Limpiar datos
```

## Despliegue

**Producción:** https://codeswap-68w3.onrender.com

**Variables:** `SECRET_KEY`, `DATABASE_URL`, `RUN_MIGRATIONS=true`

---

**Xián León Romero** - DAM 2024/2025
