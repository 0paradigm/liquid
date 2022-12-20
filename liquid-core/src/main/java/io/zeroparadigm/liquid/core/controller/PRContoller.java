package io.zeroparadigm.liquid.core.controller;

import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.core.dao.UserDao;
import io.zeroparadigm.liquid.core.dao.entity.PR;
import io.zeroparadigm.liquid.core.dao.entity.PRComment;
import io.zeroparadigm.liquid.core.dao.entity.Repo;
import io.zeroparadigm.liquid.core.dao.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Fetch and modify pr info.
 *
 * @author matthewleng
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api/pr")
public class PRContoller {

    @Autowired
    UserDao userDao;

    @DubboReference
    JWTService jwtService;

    @Autowired
    RepoMapper repoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PrMapper prMapper;

    @Autowired
    PRCommentMapper prCommentMapper;

    @GetMapping("/new")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> newPR(@RequestHeader("Authorization") String token, @RequestParam("display_id") Integer displayId,
                                 @RequestParam("repo_id") Integer repoId,
                                 @RequestParam("title") String title, @RequestParam("head") Integer head,
                                 @RequestParam("base") Integer base){
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        prMapper.createPr(displayId, repoId, userId, title, head, base, System.currentTimeMillis());
        return Result.success(true);
    }

    @GetMapping("/get")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<PR>> getPR(@RequestParam("repo_id") Integer repoId){
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        List<PR> prs = prMapper.findByRepoId(repoId);
        return Result.success(prs);
    }

    @GetMapping("/get_by_user")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<PR>> getPRByUser(@RequestHeader("Authorization") String token){
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        List<PR> prs = prMapper.findByUserId(userId);
        return Result.success(prs);
    }

    @GetMapping("/getByClosed")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<PR>> getPRByClosed(@RequestParam("repo_id") Integer repoId, @RequestParam("closed") Boolean closed){
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        List<PR> prs = prMapper.findByRepoIdAndClosed(repoId, closed);
        return Result.success(prs);
    }

    @GetMapping("/setClosed")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> setClosed(@RequestHeader("Authorization") String token, @RequestParam("pr_id") Integer prId, @RequestParam("closed") Boolean closed) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        PR pr = prMapper.findById(prId);
        Repo repo = repoMapper.findById(pr.getRepo());
        if (Objects.isNull(pr)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        if (!pr.getOpener().equals(userId) || !Objects.requireNonNull(repo).getOwner().equals(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        prMapper.setClosed(prId, closed);
        prMapper.setClosedAt(prId, System.currentTimeMillis());
        return Result.success(true);
    }

    @GetMapping("/get_comment")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<PRComment>> getPRComment(@RequestParam("pr_id") Integer prId){
        PR pr = prMapper.findById(prId);
        if (Objects.isNull(pr)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        List<PRComment> prComments = prCommentMapper.findByPRId(prId);
        return Result.success(prComments);
    }

    @GetMapping("/new_comment")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> newPRComment(@RequestHeader("Authorization") String token, @RequestParam("repo_id") Integer repoId,
                                        @RequestParam("pr_id") Integer prId, @RequestParam("content") String content) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        PR pr = prMapper.findById(prId);
        prCommentMapper.createPRComment(repoId, prId, userId, content, System.currentTimeMillis());
        return Result.success(true);
    }
}
