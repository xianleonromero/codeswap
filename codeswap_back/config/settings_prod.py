from .settings import *
import os

DEBUG = False
ALLOWED_HOSTS = ['codeswap-68w3.onrender.com', '.onrender.com', '*']

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql',
        'NAME': 'codeswap_db',
        'USER': 'tu_usuario_db',
        'PASSWORD': 'tu_contraseña_db',
        'HOST': 'localhost',
        'PORT': '5432',
    }
}


CORS_ALLOWED_ORIGINS = [
    "https://codeswap-68w3.onrender.com",
    "https://tu-app-android.com"
]
CORS_ALLOW_ALL_ORIGINS = True  


STATIC_ROOT = BASE_DIR / 'staticfiles'
MIDDLEWARE.insert(1, 'whitenoise.middleware.WhiteNoiseMiddleware')
STATICFILES_STORAGE = 'whitenoise.storage.CompressedManifestStaticFilesStorage'


import dj_database_url
DATABASE_URL = os.environ.get('DATABASE_URL')
if DATABASE_URL:
    DATABASES['default'] = dj_database_url.config(
        default=DATABASE_URL,
        conn_max_age=600,
        ssl_require=True
    )