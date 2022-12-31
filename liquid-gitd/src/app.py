import os
import toml
import requests as req

from flask import Flask, request, send_file
from flask_httpauth import HTTPBasicAuth

app = Flask(__name__)
app.config.from_file(f'config-{os.environ.get("GITD_ENV", "dev")}.toml', load=toml.load)

auth = HTTPBasicAuth()


@auth.get_password
def password(username):
    return req.get(app.config['AUTH_API'] + '/passwd', params={'username': username}).text


@app.route('/<path:repo>', methods=['GET'])
@auth.login_required
def pull(repo):
    # os.system(f'''
    # cd ../tmp
    # rm -rfi {repo}
    # git clone {app.config['GIT_STORE']}/{repo}
    # cd {repo}
    # git update-server-info
    # ''')
    repo = repo.replace('.git', '/.git', 1)
    return send_file('../tmp/' + repo)


if __name__ == '__main__':
    print(app.config)
    app.run()
