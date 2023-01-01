import os
import toml
import requests as req

from flask import Flask, request, send_file, make_response, Response
from flask_httpauth import HTTPBasicAuth

from git import git_command_with_input, git_command
from config import GIT_STORE

app = Flask(__name__)
app.config.from_object('config')

auth = HTTPBasicAuth()

# @auth.get_password
def password(username):
    return req.get(app.config['AUTH_API'] + '/passwd', params={'username': username}).text


@auth.verify_password
def verify_pw(username, password):
    return password == password(username)


@app.route('/<string:repo_name>/git-upload-pack', methods=['POST'])
@app.route('/<string:repo_name>.git/git-upload-pack', methods=['POST'])
def git_upload_pack(repo_name):
    # print(request.headers.get('Git-Protocol'))
    repo_name = repo_name + '.git'
    args = ['upload-pack', "--stateless-rpc", '.']
    res = git_command_with_input(repo_name, '', request.data, *args)

    return Response(res)


@app.route('/<string:repo_name>/git-receive-pack', methods=['POST'])
@app.route('/<string:repo_name>.git/git-receive-pack', methods=['POST'])
@auth.login_required
def git_receive_pack(repo_name):
    repo = repo_name + '.git'
    old_version = request.headers.get('Git-Protocol')
    args = ['receive-pack', "--stateless-rpc", '.']
    res = git_command_with_input(repo, '', request.data, *args)
    return Response(res)


@app.route('/<string:repo_name>/info/refs', methods=['GET'])
@app.route('/<string:repo_name>.git/info/refs', methods=['GET'])
def git_info_refs(repo_name):
    repo_name = repo_name + '.git'

    repo_path = os.path.join(GIT_STORE, repo_name)
    old_version = request.headers.get('Git-Protocol')
    version = request.headers.get('git/2.17.1')
    service = request.args.get('service')

    if service and 'git-' in service:
        service_name = service[4:]
    else:
        service_name = 'upload-pack'

    if service_name == 'receive-pack' and not auth.username():
        return auth.login_required(git_info_refs)(repo_name)

    args = [service_name, "--stateless-rpc", "--advertise-refs", "."]

    res = git_command(repo_name, version, *args)

    first_line = '# service=git-%s\n0000' % service_name
    first_line = ('%.4x' % len(first_line)) + first_line

    resp = make_response(first_line + res.decode())
    resp.headers['Content-Type'] = 'application/x-git-%s-advertisement' % service_name
    return resp


if __name__ == '__main__':
    print(app.config)
    app.run()
