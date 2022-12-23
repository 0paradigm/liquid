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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Fetch and modify milestone info.
 *
 * @author matthewleng
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api/milestone")
public class MileStoneController {

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
    MileStoneMapper milestoneMapper;

    @GetMapping("/new")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> newMileStone(@RequestHeader("Authorization") String token,
                                        @RequestParam("repoId") Integer repoId,
                                        @RequestParam("name") String name,
                                        @RequestParam("description") String description,
                                        @RequestParam("dueDate") Long dueDate,
                                        @RequestParam("closed") Boolean closed) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Boolean auth = repoMapper.verifyAuth(userId, repoId);
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        milestoneMapper.createMileStone(repoId, name, description, dueDate, closed);
        return Result.success(true);
    }

    @GetMapping("/find")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<MileStone> findMileStone(@RequestParam("milestoneId") Integer milestoneId) {
        MileStone mileStone = milestoneMapper.findById(milestoneId);
        if (Objects.isNull(mileStone)) {
            return Result.error(ServiceStatus.NOT_FOUND);
        }
        return Result.success(mileStone);
    }

    @GetMapping("/repo")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<List<MileStone>> findMileStoneByRepo(@RequestParam("repoId") Integer repoId) {
        List<MileStone> mileStones = milestoneMapper.findByRepoId(repoId);
//        log.info("milestones: {}", mileStones);
        if (Objects.isNull(mileStones)) {
            return Result.error(ServiceStatus.NOT_FOUND);
        }
        return Result.success(mileStones);
    }


    // FIXME: authorization?
    @GetMapping("/update_due")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> updateMileStoneDue(@RequestHeader("Authorization") String token, @RequestParam("milestoneId") Integer milestoneId,
                                     @RequestParam("due") Long due) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        MileStone mileStone = milestoneMapper.findById(milestoneId);
        if (Objects.isNull(mileStone)) {
            return Result.error(ServiceStatus.NOT_FOUND);
        }
        Boolean auth = repoMapper.verifyAuth(userId, mileStone.getRepo());
        Repo repo = repoMapper.findById(mileStone.getRepo());
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        milestoneMapper.updateDueById(milestoneId, due);
        return Result.success(true);
    }

    @GetMapping("/delete")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> deleteMileStone(@RequestHeader("Authorization") String token, @RequestParam("milestoneId") Integer milestoneId) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        MileStone mileStone = milestoneMapper.findById(milestoneId);
        if (Objects.isNull(mileStone)) {
            return Result.error(ServiceStatus.NOT_FOUND);
        }
        Boolean auth = repoMapper.verifyAuth(userId, mileStone.getRepo());
        Repo repo = repoMapper.findById(mileStone.getRepo());
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        milestoneMapper.deleteById(milestoneId);
        return Result.success(true);
    }
}
