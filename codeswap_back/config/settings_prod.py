from .settings import *
import os
import dj_database_url

DEBUG = False
ALLOWED_HOSTS = ['codeswap-68w3.onrender.com', '.onrender.com', '*']

# Base de datos - usar variable de entorno de Render
DATABASE_URL = os.environ.get('DATABASE_URL')
if DATABASE_URL:
    DATABASES = {
        'default': dj_database_url.config(
            default=DATABASE_URL,
            conn_max_age=600,
            ssl_require=True
        )
    }

# CORS
CORS_ALLOWED_ORIGINS = [
    "https://codeswap-68w3.onrender.com",
]
CORS_ALLOW_ALL_ORIGINS = True
CORS_ALLOW_CREDENTIALS = True

# Archivos estáticos
STATIC_ROOT = BASE_DIR / 'staticfiles'
STATICFILES_STORAGE = 'whitenoise.storage.CompressedManifestStaticFilesStorage'

# Añadir WhiteNoise al middleware
MIDDLEWARE.insert(1, 'whitenoise.middleware.WhiteNoiseMiddleware')

# Secret key desde variable de entorno
SECRET_KEY = os.environ.get('SECRET_KEY', 'fallback-secret-key-for-development')