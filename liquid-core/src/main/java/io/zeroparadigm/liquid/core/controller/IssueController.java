package io.zeroparadigm.liquid.core.controller;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.core.dao.UserDao;
import io.zeroparadigm.liquid.core.dao.entity.*;
import io.zeroparadigm.liquid.core.dao.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Fetch and modify issue info.
 *
 * @author matthewleng
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api/issue")
public class IssueController {

    @Autowired
    UserDao userDao;

    @DubboReference(parameters = {"unicast", "false"})
    JWTService jwtService;

    @Autowired
    RepoMapper repoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    IssueMapper issueMapper;

    @Autowired
    IssueLabelMapper issueLabelMapper;

    @Autowired
    IssueCommentMapper issueCommentMapper;

    @GetMapping("/new")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> newIssue(@RequestHeader("Authorization") String token, @RequestParam("display_id") Integer displayId, @RequestParam("repo_id") Integer repoId,
                                    @RequestParam("title") String title, @RequestParam("closed") Boolean closed) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        issueMapper.createIssue(displayId, repoId, userId, title, System.currentTimeMillis(), closed);
        return Result.success(true);
    }

    @GetMapping("/comment")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> commentIssue(@RequestHeader("Authorization") String token, @RequestParam("repo_id") Integer repoId,
                                        @RequestParam("issue_id") Integer issueId, @RequestParam("content") String content) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        issueCommentMapper.createIssueComment(repoId, issueId, userId, content, System.currentTimeMillis());
        return Result.success(true);
    }

    @GetMapping("/get_comment")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<IssueComment>> getIssueComment(@RequestParam("issue_id") Integer issueId) {
        Issue issue = issueMapper.findById(issueId);
        if (Objects.isNull(issue)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        List<IssueComment> issueComments = issueCommentMapper.findByIssueId(issueId);
        return Result.success(issueComments);
    }

    @GetMapping("/delete_comment")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> deleteIssueComment(@RequestHeader("Authorization") String token, @RequestParam("comment_id") Integer commentId) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        IssueComment issueComment = issueCommentMapper.findById(commentId);
        if (Objects.isNull(issueComment)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Repo repo = repoMapper.findById(issueComment.getRepo());
        if (!issueComment.getAuthor().equals(userId) || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        issueCommentMapper.deleteById(commentId);
        return Result.success(true);
    }

    @GetMapping("/find")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Issue> findIssueById(@RequestParam("id") Integer id) {
        Issue issue = issueMapper.findById(id);
        if (Objects.isNull(issue)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        return Result.success(issue);
    }

    @GetMapping("/user_list")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<Issue>> listIssueByOnwerId(@RequestHeader("Authorization") String token) {
        Integer userId = jwtService.getUserId(token);
        List<Issue> issues = issueMapper.findByOwnerId(userId);
        if (Objects.isNull(issues)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        return Result.success(issues);
    }

    @GetMapping("/search_by_ids")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Issue> findByIds(@RequestParam("displayId") Integer displayedId, @RequestParam("repoId") Integer repoId) {
        Issue issues = issueMapper.findByDisplayedIdandRepoId(displayedId, repoId);
        if (Objects.isNull(issues)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        return Result.success(issues);
    }

    @GetMapping("/repo_list")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<Issue>> listIssueByRepoId(@RequestParam("repoId") Integer repoId) {
        List<Issue> issues = issueMapper.findByRepoId(repoId);
        if (Objects.isNull(issues)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        return Result.success(issues);
    }

    @GetMapping("/repo_user_list")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<Issue>> listIssueByRepoIdAndUserId(@RequestParam("repoId") Integer repoId, @RequestParam("userId") Integer userId) {
        List<Issue> issues = issueMapper.findByRepoIdandOwnerId(repoId, userId);
        if (Objects.isNull(issues)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        return Result.success(issues);
    }

    @GetMapping("/repo_label_list")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<Issue>> listIssueByRepoIdAndLabel(@RequestParam("repoId") Integer repoId, @RequestParam("label") String label) {
        IssueLabel issueLabel = issueLabelMapper.findByRepoIdAndName(repoId, label);
        if (Objects.isNull(issueLabel)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        List<Issue> issues = issueMapper.findByRepoIdandLabel(repoId, issueLabel);
        if (Objects.isNull(issues)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        return Result.success(issues);
    }

    @GetMapping("/user_close_list")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<Issue>> listIssueByUserIdAndClose(@RequestHeader("Authorization") String token, @RequestParam("closed") Boolean closed) {
        Integer userId = jwtService.getUserId(token);
        List<Issue> issues = issueMapper.findByUserIdandClosed(userId, closed);
        if (Objects.isNull(issues)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        return Result.success(issues);
    }

    @GetMapping("/assignee")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<User>> listIssueByAssignee(@RequestParam("id") Integer id) {
        List<User> assignee = issueMapper.findAssigneeById(id);
        if (Objects.isNull(assignee)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        return Result.success(assignee);
    }

    // FIXME: authorization?
    @GetMapping("/assign")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> assignIssue(@RequestHeader("Authorization") String token, @RequestParam("issueId") Integer issueId, @RequestParam("assigneeId") Integer assigneeId) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Issue issue = issueMapper.findById(issueId);
        if (Objects.isNull(issue)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        // FIXME: authorization?
//        if (!issue.getOwnerId().equals(userId)) {
//            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
//        }
        Boolean auth = repoMapper.verifyAuth(userId, issue.getRepo());
        Repo repo = repoMapper.findById(issue.getRepo());
        if (Objects.isNull(repo) || !auth || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.assignIssue(issueId, assigneeId);
        return Result.success(true);
    }

    @GetMapping("/unassign")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> unassignIssue(@RequestHeader("Authorization") String token, @RequestParam("issueId") Integer issueId, @RequestParam("assigneeId") Integer assigneeId) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Issue issue = issueMapper.findById(issueId);
        if (Objects.isNull(issue)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        List<User> assignee = issueMapper.findAssigneeById(issueId);
        if (!assignee.contains(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Boolean auth = repoMapper.verifyAuth(userId, issue.getRepo());
        Repo repo = repoMapper.findById(issue.getRepo());
        if (Objects.isNull(repo) || !auth || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.removeAssignee(issueId, assigneeId);
        return Result.success(true);
    }

    @GetMapping("/assign_label")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> assignLabel(@RequestHeader("Authorization") String token, @RequestParam("issueId") Integer issueId, @RequestParam("labelId") Integer labelId) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Issue issue = issueMapper.findById(issueId);
        if (Objects.isNull(issue)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Boolean auth = repoMapper.verifyAuth(userId, issue.getRepo());
        Repo repo = repoMapper.findById(issue.getRepo());
        if (Objects.isNull(repo) || !auth || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.assignLabelById(issueId, labelId);
        return Result.success(true);
    }

    @GetMapping("/unassign_label")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> unassignLabel(@RequestHeader("Authorization") String token, @RequestParam("issueId") Integer issueId, @RequestParam("labelId") Integer labelId) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Issue issue = issueMapper.findById(issueId);
        if (Objects.isNull(issue)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Boolean auth = repoMapper.verifyAuth(userId, issue.getRepo());
        Repo repo = repoMapper.findById(issue.getRepo());
        if (Objects.isNull(repo) || !auth || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.removeLabelById(issueId, labelId);
        return Result.success(true);
    }

    @GetMapping("/assign_milestone")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> assignMilestone(@RequestHeader("Authorization") String token, @RequestParam("issueId") Integer issueId, @RequestParam("milestoneId") Integer milestoneId) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Issue issue = issueMapper.findById(issueId);
        if (Objects.isNull(issue)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Boolean auth = repoMapper.verifyAuth(userId, issue.getRepo());
        Repo repo = repoMapper.findById(issue.getRepo());
        if (Objects.isNull(repo) || !auth || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.assignMilestone(issueId, milestoneId);
        return Result.success(true);
    }

    @GetMapping("/unassign_milestone")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> unassignMilestone(@RequestHeader("Authorization") String token, @RequestParam("issueId") Integer issueId, @RequestParam("milestoneId") Integer milestoneId) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Issue issue = issueMapper.findById(issueId);
        if (Objects.isNull(issue)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Boolean auth = repoMapper.verifyAuth(userId, issue.getRepo());
        Repo repo = repoMapper.findById(issue.getRepo());
        if (Objects.isNull(repo) || !auth || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.removeMilestone(issueId, milestoneId);
        return Result.success(true);
    }

    @GetMapping("/close")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> closeIssue(@RequestHeader("Authorization") String token, @RequestParam("issueId") Integer issueId) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Issue issue = issueMapper.findById(issueId);
        if (Objects.isNull(issue)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Boolean auth = repoMapper.verifyAuth(userId, issue.getRepo());
        Repo repo = repoMapper.findById(issue.getRepo());
        if (Objects.isNull(repo) || !auth || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.closeIssue(issueId);
        return Result.success(true);
    }

}
