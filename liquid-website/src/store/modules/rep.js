import router from "@/router";
import api from "@/api";
import {Message} from '@arco-design/web-vue';

export default {
    namespaced: true,
    state: {
        owner: '',
        rep: '',
        branch: '',
        relPath: '',
        fileList: '',
        description: '',
        isLoading: false,
        readme: false,
        readmeValue: '',
        privateRepo: true,
        watchCount: 0,
        forkCount: 0,
        starCount: 0,
        forkedFrom: '',
    },

    getters: {
        getPrivateRepo(state){
            return state.privateRepo
        },
        getDescription(state) {
            return state.description || 'No description, website, or topics provided.'
        },
        getWatchCount(state){
            return state.watchCount
        },
        getForkCount(state){
            return state.forkCount;
        },
        getStarCount(state){
            return state.starCount
        },
        getFileList(state) {
            return state.fileList
        },
        getRep(state) {
            return {
                owner: state.owner,
                rep: state.rep,
                branch: state.branch,
                relPath: state.relPath,
            }
        },
        getForkedFrom(state) {
            return !!state.forkedFrom ? state.forkedFrom : ''
        },
        getForkedUser(state){
            return !!state.forkedFrom ? state.forkedFrom.split('/')[0] : ''
        },
        getForkedRepo(state){
            return !!state.forkedFrom ? state.forkedFrom.split('/')[1] : ''
        },
        isLoading(state) {
            return state.isLoading
        },
        readme(state) {
            return state.readme
        },
        getReadmeValue(state) {
            return state.readmeValue
        }
    },

    mutations: {
        setPrivateRepo(state, val){
            state.privateRepo = val;
        },
        setForkedFrom(state, val) {
            state.forkedFrom = val
        },
        setWatchCount(state, watchCount){
            state.watchCount = watchCount;
        },
        setDescription(state, desc) {
            state.description = desc
        },
        setForkCount(state, forkCount){
            state.forkCount = forkCount;
        },
        setStarCount(state, starCount){
            state.starCount = starCount;
        },
        setFileList(state, fileList) {
            state.fileList = fileList
            state.readme = false
            fileList.forEach(item => {
                if (item.name === 'README.md') {
                    state.readme = true;
                }
            })
        },
        setRep(state, owner, rep, branch, relPath) {
            state.owner = owner;
            state.rep = rep;
            state.branch = branch;
            state.relPath = relPath
        },
        setIsLoading(state, isLoading) {
            state.isLoading = isLoading
        },
        setReadmeValue(state, readmeValue) {
            state.readmeValue = readmeValue
        }

    },

    actions: {
        commitUpload({commit}, [owner, rep, branch, data]) {
            commit('setIsLoading', true)
            api.commitUploadRep(owner, rep, branch, data).then(res => {
                Message.success(res.data.msg);
                router.go(-1);
                commit('setIsLoading', false)
            }).catch(res => {
                    Message.error(res.response.data.msg)
                    console.log(res.response)
                    commit('setIsLoading', false)
                }
            )
        },

        initFileList({commit, state}, [owner, rep, branch, relPath]) {
            commit('setIsLoading', true)
            var str = ""
            if (relPath !== undefined && relPath !== '') {
                relPath.forEach(dir => str += dir + '/')
            }
            api.getFileList(owner, rep, branch, str).then(res => {
                    commit('setFileList', res.data.data);
                    if (state.readme) {
                        var str = ""
                        if (relPath !== undefined && relPath !== '') {
                            relPath.forEach(dir => str += dir + '/')
                        }
                        str += 'README.md';
                        api.getFile(owner, rep, branch, str).then(res => {
                                var data = JSON.parse(res.data.data)
                                commit('setReadmeValue', data.content)
                                commit('setIsLoading', false)
                            }
                        )
                    } else {
                        commit('setIsLoading', false)
                    }

                }
            )
        },

        getFile({commit}, [owner, rep, branch, relPath]) {
            console.log(relPath)
            var str = ""
            if (relPath !== undefined && relPath !== '') {
                relPath.forEach(dir => str += dir + '/')
            }
            return api.getFile(owner, rep, branch, str)
        }

    }
};