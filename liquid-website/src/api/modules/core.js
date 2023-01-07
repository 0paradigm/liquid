import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import jsCookie from "js-cookie";

export default {
    issue:{
        assignIssue(assignId, issueId){
            request.get(BACKEND_URL + '/core/api/issue/assign',
                {
                    params:{
                        assignId: assignId,
                        issueId: issueId
                    }
                })
        },
        assignLabel(labelId, issueId){
            request.get(BACKEND_URL + '/core/api/issue/assign_label',
                {
                    params:{
                        labelId: labelId,
                        issueId: issueId
                    }
                })
        },
        assignMileStone(milestoneId, issueId){
            request.get(BACKEND_URL + '/core/api/issue/assign_milestone',
                {
                    params:{
                        milestoneId: milestoneId,
                        issueId: issueId
                    }
                })
        },
        listIssueByAssignee(id){
            request.get(BACKEND_URL + '/core/api/issue/assignee',
                {
                    params:{
                        id:id
                    }
                })
        },
        closeIssue(issueId){
            request.get(BACKEND_URL + '/core/api/issue/close',
                {
                    params:{
                        issueId: issueId
                    }
                })
        },
        commentIssue(content, issue_id, repo_id){
            request.get(BACKEND_URL + '/core/api/issue/comment',
                {
                    params:{
                        content: content,
                        issue_id: issue_id,
                        repo_id: repo_id
                    }
                })
        },
        deleteIssueComment(comment_id){
            request.get(BACKEND_URL + '/core/api/issue/delete_comment',
                {
                    params:{
                        comment_id: comment_id
                    }
                })
        },
        findIssueById(id){
            request.get(BACKEND_URL + '/core/api/issue/find',
                {
                    params:{
                        id:id
                    }
                })
        },
        getIssueComment(issue_id){
            request.get(BACKEND_URL + '/core/api/issue/get_comment',
                {
                    params:{
                        issue_id: issue_id
                    }
                })
        },
        newIssue(closed, display_id, repo_id, title){
            request.get(BACKEND_URL + '/core/api/issue/new',
                {
                    params:{
                        closed: closed,
                        display_id: display_id,
                        repo_id: repo_id,
                        title: title
                    }
                })
        },
        listIssueByRepoIdAndLabel(label, repoId){
            request.get(BACKEND_URL + '/core/api/issue/repo_label_list',
                {
                    params:{
                        label:label,
                        repoId: repoId
                    }
                })
        },
        listIssueByRepoId(repoId){
            request.get(BACKEND_URL + '/core/api/issue/repo_list',
                {
                    params:{
                        repoId: repoId
                    }
                })
        },
        listIssueByRepoIdAndUserId(repoId, userId){
            request.get(BACKEND_URL + '/core/api/issue/repo_user_list',
                {
                    params:{
                        repoId: repoId,
                        userId: userId
                    }
                })
        },
        unassignIssue(assigneeId, issueId){
            request.get(BACKEND_URL + '/core/api/issue/unassign',
                {
                    params:{
                        assigneeId: assigneeId,
                        issueId: issueId
                    }
                })
        },
        unassignLabel(labelId, issueId){
            request.get(BACKEND_URL + '/core/api/issue/unassign_label',
                {
                    params:{
                        labelId: labelId,
                        issueId: issueId
                    }
                })
        },
        unassignMilestone(milestoneId, issueId){
            request.get(BACKEND_URL + '/core/api/issue/unassign_milestone',
                {
                    params:{
                        milestoneId: milestoneId,
                        issueId: issueId
                    }
                })
        },
        listIssueByUserIdAndClose(closed){
            request.get(BACKEND_URL + '/core/api/issue/user_close_list',
                {
                    params:{
                        closed: closed
                    }
                })
        },
        listIssueByOwnerId(){
            request.get(BACKEND_URL + '/core/api/issue/user_list')
        }

    },

    issueLabel:{
        deleteIssueLabelById(id){
            request.get(BACKEND_URL + '/core/api/issuelabel/delete',
                {
                    params:{
                        id: id
                    }
                })
        },
        deleteIssueLabelByRepoAndName(name, repoId){
            request.get(BACKEND_URL + '/core/api/issuelabel/delete_repo_name',
                {
                    params:{
                        name: name,
                        repoId: repoId
                    }
                })
        },
        findIssueLabelById(id){
            request.get(BACKEND_URL + '/core/api/issuelabel/find',
                {
                    params:{
                        id: id
                    }
                })
        },
        findIssueLabelByRepoAndName(name, repoId){
            request.get(BACKEND_URL + '/core/api/issuelabel/find_repo_name',
                {
                    params:{
                        name: name,
                        repoId: repoId
                    }
                })
        },
        newIssueLabel(color, description, name, repoId){
            request.get(BACKEND_URL + '/core/api/issuelabel/new',
                {
                    params:{
                        color: color,
                        description: description,
                        name: name,
                        repoId: repoId
                    }
                })
        },
        findIssueLabelByRepoId(repoId){
            request.get(BACKEND_URL + '/core/api/issuelabel/repo',
                {
                    params:{
                        repoId: repoId
                    }
                })
        }
    },

    milestone:{
        deleteMileStone(milestoneId){
            request.get(BACKEND_URL + '/core/api/milestone/delete',
                {
                    params:{
                        milestoneId: milestoneId
                    }
                })
        },
        findMileStone(milestoneId){
            request.get(BACKEND_URL + '/core/api/milestone/find',
                {
                    params:{
                        milestoneId: milestoneId
                    }
                })
        },
        newMileStone(closed, description, dueDate, name, repoId){
            request.get(BACKEND_URL + '/core/api/milestone/new',
                {
                    params:{
                        closed: closed,
                        description: description,
                        dueDate: dueDate,
                        name: name,
                        repoId: repoId
                    }
                })
        },
        findMileStoneByRepo(repoId){
            request.get(BACKEND_URL + '/core/api/milestone/repo',
                {
                    params:{
                        repoId: repoId
                    }
                })
        },
        updateMileStoneDue(due, milestoneId){
            request.get(BACKEND_URL + '/core/api/milestone/update_due',
                {
                    params:{
                        due: due,
                        milestoneId: milestoneId
                    }
                })
        },
    },

    pr:{
        getPR(repo_id){
            request.get(BACKEND_URL + '/core/api/pr/get',
                {
                    params:{
                        repo_id: repo_id
                    }
                })
        },
        getPRByClosed(repo_id, closed){
            request.get(BACKEND_URL + '/core/api/pr/getByClosed',
                {
                    params:{
                        repo_id: repo_id,
                        closed: closed
                    }
                })
        },
        getPRByUser(){
            request.get(BACKEND_URL + '/core/api/pr/get_by_user')
        },
        getPRComment(pr_id){
            request.get(BACKEND_URL + '/core/api/pr/get_comment',
                {
                    params:{
                       pr_id: pr_id
                    }
                })
        },
        newPR(base, display_id, head, repo_id, title){
            request.get(BACKEND_URL + '/core/api/pr/new',
                {
                    params:{
                        base: base,
                        display_id: display_id,
                        head: head,
                        repo_id: repo_id,
                        title: title
                    }
                })
        },
        newPRComment(content, pr_id, repo_id){
            request.get(BACKEND_URL + '/core/api/pr/new_comment',
                {
                    params:{
                        content: content,
                        pr_id: pr_id,
                        repo_id: repo_id
                    }
                })
        },
        setClosed(pr_id, closed){
            request.get(BACKEND_URL + '/core/api/pr/setClosed',
                {
                    params:{
                        pr_id: pr_id,
                        closed: closed
                    }
                })
        }
    },

    repo:{
        addCollaborator(repoId, userId){
            request.get(BACKEND_URL + '/core/api/repo/add_collaborator',
                {
                    params:{
                        repoId: repoId,
                        userId: userId
                    }
                })
        },

        ////auth?

        countFork(repoId){
            request.get(BACKEND_URL + '/core/api/repo/count_fork',
                {
                    params:{
                        repoId: repoId,
                    }
                })
        },

        countStar(repoId){
            request.get(BACKEND_URL + '/core/api/repo/count_star',
                {
                    params:{
                        repoId: repoId,
                    }
                })
        },

        countWatch(repoId){
            request.get(BACKEND_URL + '/core/api/repo/count_watch',
                {
                    params:{
                        repoId: repoId,
                    }
                })
        },

        deleteRepo(repoId){
            request.get(BACKEND_URL + '/core/api/repo/delete',
                {
                    params:{
                       repoId: repoId
                    }
                })
        },

        findRepoByOwnerIdAndName(name){
            request.get(BACKEND_URL + '/core/api/repo/find',
                {
                    params:{
                        name: name
                    }
                })
        },

        getCollaborators(repoId){
            request.get(BACKEND_URL + '/core/api/repo/get_collaborators',
                {
                    params:{
                        repoId: repoId
                    }
                })
        },

        listForks(repoId){
            request.get(BACKEND_URL + '/core/api/repo/list_forks',
                {
                    params:{
                        repoId: repoId
                    }
                })
        },

        listStarers(repoId){
            request.get(BACKEND_URL + '/core/api/repo/list_starers',
                {
                    params:{
                        repoId: repoId
                    }
                })
        },

        listWatchers(repoId){
            request.get(BACKEND_URL + '/core/api/repo/list_watchers',
                {
                    params:{
                        repoId: repoId
                    }
                })
        },

        removeCollaborator(repoId, userId){
            request.get(BACKEND_URL + '/core/api/repo/remove_collaborator',
                {
                    params:{
                        repoId: repoId,
                        userId: userId
                    }
                })
        },

        findRepoByName(name){
            request.get(BACKEND_URL + '/core/api/repo/search',
                {
                    params:{
                        name: name
                    }
                })
        },
        findRepoByOwnerAndName(name, owner){
            request.get(BACKEND_URL + '/core/api/repo/search_useless',
                {
                    params:{
                        name: name,
                        owner: owner
                    }
                })
        },
    },

    user:{
        findUserByNameOrMail(usr){
            return request.get(BACKEND_URL + '/core/api/user/find',
                {
                    params:{
                        usr: usr
                    }
                })
        },
        getUserInfo(){
            return request.get(BACKEND_URL + '/core/api/user/info', {
                headers: {
                    'Authorization': jsCookie.get('Authorization')
                }
            })
        },
        star(id){
            request.get(BACKEND_URL + '/core/api/user/star',
                {
                    params:{
                        id: id
                    }
                })
        },
        unstar(id){
            request.get(BACKEND_URL + '/core/api/user/unstar',
                {
                    params:{
                        id: id
                    }
                })
        },
        unwatch(id){
            request.get(BACKEND_URL + '/core/api/user/unwatch',
                {
                    params:{
                        id: id
                    }
                })
        },
        watch(id, alerts, discuss, issue, particip, pull, release){
            request.get(BACKEND_URL + '/core/api/user/watch',
                {
                    params:{
                        id: id,
                        alerts: alerts,
                        discuss: discuss,
                        issue: issue,
                        particip: particip,
                        pull: pull,
                        release: release
                    }
                })
        }

        ////getUserNamedAs

        ////getUserIdViaJWT

        ////hello
    }

}