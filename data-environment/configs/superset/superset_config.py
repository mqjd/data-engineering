ROW_LIMIT = 5000
SECRET_KEY = 'XqnGHRu2y8jbHcYQukB/2D8WQx/9tDbTtZXwOSoKJlhxXjJLAKPtTWX9'
SQLALCHEMY_DATABASE_URI = 'sqlite:////var/bigdata/configs/superset/superset.db?check_same_thread=false'
# Flask-WTF flag for CSRF
ENABLE_PROXY_FIX = False
WTF_CSRF_ENABLED = False
TALISMAN_ENABLED = False
# Set this API key to enable Mapbox visualizations
MAPBOX_API_KEY = ''

LANGUAGES = {
    'zh': {'flag': 'cn', 'name': 'Chinese'},
    'en': {'flag': 'us', 'name': 'English'},
}

BABEL_DEFAULT_LOCALE = "zh"