import os

GIT_STORE_DOCKER = "storage/git"
GIT_PATH = '/usr/bin/git'
PROJECTS_PATH = os.path.join(os.path.dirname(__file__), 'projects')

SECRET_KEY = "192b9bdd22ab9ed4d12e236c78afcb9a393ec15f71bbf5dc987d54727823bcbf"
SESSION_COOKIE_HTTPONLY = False
MAX_COOKIE_SIZE = 0

#dev
GIT_STORE = "../../liquid-git/storage/git"
AUTH_API = "http://localhost:8002/internal/v1/auth"

#docker
# GIT_STORE = "storage/git"
# AUTH_API = "http://localhost:8002/internal/v1/auth/passwd"

# USERNAME = 'admin'
# PASSWORD = '123456'