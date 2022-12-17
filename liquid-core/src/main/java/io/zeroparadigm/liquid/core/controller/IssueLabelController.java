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
import io.zeroparadigm.liquid.core.dao.entity.Issue;
import io.zeroparadigm.liquid.core.dao.entity.IssueLabel;
import io.zeroparadigm.liquid.core.dao.entity.Repo;
import io.zeroparadigm.liquid.core.dao.entity.User;
import io.zeroparadigm.liquid.core.dao.mapper.IssueLabelMapper;
import io.zeroparadigm.liquid.core.dao.mapper.IssueMapper;
import io.zeroparadigm.liquid.core.dao.mapper.RepoMapper;
import io.zeroparadigm.liquid.core.dao.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Fetch and modify issue label info.
 *
 * @author matthewleng
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api/issuelabel")
public class IssueLabelController {

    @Autowired
    UserDao userDao;

    @DubboReference
    JWTService jwtService;

    @Autowired
    RepoMapper repoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    IssueMapper issueMapper;

    @Autowired
    IssueLabelMapper issueLabelMapper;

    @GetMapping("/new")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> newIssueLabel(@RequestHeader("Authorization") String token, @RequestParam("repoId") Integer repoId,
                                            @RequestParam("name") String labelName,
                                            @RequestParam("color") String labelColor,
                                         @RequestParam("description") String labelDescription) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Boolean auth = repoMapper.verifyAuth(userId, repoId);
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(repo) || !auth || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueLabelMapper.createIssueLabel(repoId, labelName, labelColor, labelDescription);
        return Result.success(true);
    }

    @GetMapping("/find")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<IssueLabel> findIssueLabelById(@RequestParam("id") Integer id) {
        IssueLabel issueLabel = issueLabelMapper.findById(id);
        if (Objects.isNull(issueLabel)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return Result.success(issueLabel);
    }

    // FIXME: authorization?
    @GetMapping("/delete")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> deleteIssueLabelById(@RequestHeader("Authorization") String token, @RequestParam("id") Integer id) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        IssueLabel issueLabel = issueLabelMapper.findById(id);
        if (Objects.isNull(issueLabel)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Boolean auth = repoMapper.verifyAuth(userId, issueLabel.getRepo());
        Repo repo = repoMapper.findById(issueLabel.getRepo());
        if (Objects.isNull(repo) || !auth || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueLabelMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/repo")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<List<IssueLabel>> findIssueLabelByRepoId(@RequestParam("repoId") Integer repoId) {
        List<IssueLabel> issueLabels = issueLabelMapper.findByRepoId(repoId);
        if (Objects.isNull(issueLabels)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return Result.success(issueLabels);
    }

    // FIXME: authorization?
    @GetMapping("/delete_repo_name")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> deleteIssueLabelByRepoAndName(@RequestHeader("Authorization") String token, @RequestParam("repoId") Integer repoId,
                                                         @RequestParam("name") String name) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Boolean auth = repoMapper.verifyAuth(userId, repoId);
        if (Objects.isNull(repo) || !auth || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueLabelMapper.deleteIssueLabel(repoId, name);
        return Result.success();
    }

    @GetMapping("/find_repo_name")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<IssueLabel> findIssueLabelByRepoAndName(@RequestParam("repoId") Integer repoId, @RequestParam("name") String name) {
        IssueLabel issueLabel = issueLabelMapper.findByRepoIdAndName(repoId, name);
        if (Objects.isNull(issueLabel)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return Result.success(issueLabel);
    }
}
