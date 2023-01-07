import time
import os
import zipfile

import requests as req
from flask import Flask, request, make_response, Response, send_file
from flask_httpauth import HTTPBasicAuth

from git import git_command_with_input, git_command
from apscheduler.schedulers.background import BackgroundScheduler

app = Flask(__name__)
app.config.from_object('config')

auth = HTTPBasicAuth()

_sched = BackgroundScheduler()
_sched.start()


@auth.get_password
def verify(username):
    return req.get(app.config['AUTH_API'] + '/passwd', params={'username': username}).text


def sync_repo(full):
    def wrapper(name):
        try:
            for i in range(10):
                resp = req.post(f"{app.config['SYNC_API']}/{name}")
                time.sleep(0.5)
                print(i, name, resp.text or 'success')
        except Exception as e:
            print(e)

    _sched.add_job(
        wrapper,
        args=(full,)
    )


def add_contrib(to):
    print(f"{app.config['CONTRIB_API']}/{to}/{auth.current_user()}")
    print(req.get(f"{app.config['CONTRIB_API']}/{to}/{auth.current_user()}").text)


@app.route('/<string:owner>/<string:repo_name>/git-upload-pack', methods=['POST'])
@app.route('/<string:owner>/<string:repo_name>.git/git-upload-pack', methods=['POST'])
@auth.login_required
def git_upload_pack(owner, repo_name):
    repo_name = owner + '/' + repo_name.rstrip('.git')
    args = ['upload-pack', "--stateless-rpc", '.']
    res = git_command_with_input(repo_name, '', request.data, *args)
    sync_repo(repo_name)
    return Response(res)


@app.route('/<string:owner>/<string:repo_name>/git-receive-pack', methods=['POST'])
@app.route('/<string:owner>/<string:repo_name>.git/git-receive-pack', methods=['POST'])
@auth.login_required
def git_receive_pack(owner, repo_name):
    repo = owner + '/' + repo_name.rstrip('.git')
    args = ['receive-pack', "--stateless-rpc", '.']
    res = git_command_with_input(repo, '', request.data, *args)
    sync_repo(repo)
    return Response(res)


@app.route('/<string:owner>/<string:repo_name>/info/refs', methods=['GET'])
@app.route('/<string:owner>/<string:repo_name>.git/info/refs', methods=['GET'])
@auth.login_required
def git_info_refs(owner, repo_name):
    repo_name = owner + '/' + repo_name.rstrip('.git')

    version = request.headers.get('git/2.17.1')
    service = request.args.get('service')

    if service and 'git-' in service:
        service_name = service[4:]
    else:
        service_name = 'upload-pack'

    args = [service_name, "--stateless-rpc", "--advertise-refs", "."]
    res = git_command(repo_name, version, *args)

    first_line = '# service=git-%s\n0000' % service_name
    first_line = ('%.4x' % len(first_line)) + first_line

    resp = make_response(first_line + res.decode())
    resp.headers['Content-Type'] = 'application/x-git-%s-advertisement' % service_name
    sync_repo(repo_name)
    add_contrib(repo_name)
    return resp


@app.route('/file/<string:owner>/<string:repo_name>/<path:path>', methods=['GET'])
def download_file(owner, repo_name, path):
    return send_file(f"{app.config['GIT_CACHE_STORE']}/{owner}/{repo_name}-1/{path}")


def zipDir(dirpath, outFullName):
    if os.path.exists(outFullName):
        os.remove(outFullName)
    zip = zipfile.ZipFile(outFullName, "w", zipfile.ZIP_DEFLATED)
    for path, dirnames, filenames in os.walk(dirpath):
        fpath = path.replace(dirpath, '')
        for filename in filenames:
            zip.write(os.path.join(path, filename), os.path.join(fpath, filename))
    zip.close()


@app.route('/zip/<string:owner>/<string:repo_name>', methods=['GET'])
def download_zip(owner, repo_name):
    repo_name = owner + '/' + repo_name.rstrip('.git')
    zipDir(f"{app.config['GIT_CACHE_STORE']}/{repo_name}-1", f"{app.config['GIT_CACHE_STORE']}/{repo_name}.zip")
    return send_file(f"{app.config['GIT_CACHE_STORE']}/{repo_name}.zip")


if __name__ == '__main__':
    app.run(port=5001, host='0.0.0.0')
