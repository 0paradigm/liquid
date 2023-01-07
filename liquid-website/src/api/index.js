import request from '../utils/request'
import auth from "@/api/modules/auth";
import core from "@/api/modules/core";
import git from "@/api/modules/git";
import media from "@/api/modules/media";
import {BACKEND_URL} from "@/utils/config";

const path = {
    auth: '/auth',
    git: '/git',
    core: '/core/api',
    // user: '/auth',
    login: '/login',
}

const api = {
    auth,
    core,
    git,
    media,

    login(user) {
        return request.post(BACKEND_URL + '/core/api/user' + path.login, user)
    },

    getSelfRep() {
        return request.get(BACKEND_URL + path.core + '/user/repo')
    },

    createRep(owner, rep, branch) {
        return request.post(BACKEND_URL + path.git + '/web/create/' + owner + '/' + rep, {initBranch: branch})
    },

    uploadRepUrl(owner, rep, branch) {
        return BACKEND_URL + path.git + '/web/upload/' + owner + '/' + rep + '/' + branch
    },

    //data:{addFiles:[], message:'', taskId:''}
    commitUploadRep(owner, rep, branch, data) {
        return request.post(BACKEND_URL + path.git + '/web/upload/' + owner + '/' + rep + '/' + branch + '/commit', data)
    },

    getFileList(owner, rep, branch, relPath) {
        return request.get(BACKEND_URL + path.git + '/web/list/' + owner + '/' + rep + '/' + branch, {
            params: {
                relPath: relPath
            }
        })
    },

    getFile(owner, rep, branch, relPath) {
        return request.get(BACKEND_URL + path.git + '/web/file/' + owner + '/' + rep + '/' + branch, {
            params: {
                filePath: relPath
            },
        })
    }
}

export default api