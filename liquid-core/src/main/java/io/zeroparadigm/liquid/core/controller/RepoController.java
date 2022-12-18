package io.zeroparadigm.liquid.core.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.core.dao.UserDao;
import io.zeroparadigm.liquid.core.dao.entity.Repo;
import io.zeroparadigm.liquid.core.dao.entity.User;
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
 * Fetch and modify repo info.
 *
 * @author matthewleng
 */
@Api
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/repo")
public class RepoController {

    @Autowired
    UserDao userDao;

    @DubboReference
    JWTService jwtService;

    @Autowired
    RepoMapper repoMapper;

    @Autowired
    UserMapper userMapper;

    @Deprecated
    public void changeAuth(Integer userId, Integer repoId, Boolean read, Boolean manage,
                              Boolean push, Boolean settings,
                              Boolean admin) {
        repoMapper.setAuth(repoId, userId, read, manage, push, settings, admin);
    }

    @Deprecated
    @GetMapping("/auth")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> auth(@RequestHeader("Authorization") String token, @RequestParam("repoId") Integer repoId,
                                @RequestParam("read") Boolean read, @RequestParam("manage") Boolean manage,
                                @RequestParam("push") Boolean push, @RequestParam("settings") Boolean settings,
                                @RequestParam("admin") Boolean admin) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        repoMapper.setAuth(repoId, userId, read, manage, push, settings, admin);
        return Result.success(true);
    }

    @GetMapping("/create")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> create(@RequestHeader("Authorization") String token, @RequestParam("name") String name,
                                  @RequestParam("forkedId") Integer forkedId, @RequestParam("private") Boolean privat) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        repoMapper.createRepo(userId, name, forkedId==-1?null:forkedId, privat);
        return Result.success(true);
    }

    @GetMapping("/add_collaborator")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> addCollaborator(@RequestHeader("Authorization") String token, @RequestParam("repoId") Integer repoId,
                                           @RequestParam("userId") Integer userId) {
        Integer usrId = jwtService.getUserId(token);
        User usr = userMapper.findById(usrId);
        Repo repo = repoMapper.findById(repoId);
        User user = userMapper.findById(userId);
        if (Objects.isNull(usr) || Objects.isNull(user) || Objects.isNull(repo) || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.METHOD_NOT_ALLOWED);
        }
        repoMapper.addCollaborator(repoId, userId);
        return Result.success(true);
    }

    @GetMapping("/remove_collaborator")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> removeCollaborator(@RequestHeader("Authorization") String token, @RequestParam("repoId") Integer repoId,
                                           @RequestParam("userId") Integer userId) {
        Integer usrId = jwtService.getUserId(token);
        User usr = userMapper.findById(usrId);
        Repo repo = repoMapper.findById(repoId);
        User user = userMapper.findById(userId);
        if (Objects.isNull(usr) || Objects.isNull(user) || Objects.isNull(repo) || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.METHOD_NOT_ALLOWED);
        }
        repoMapper.removeCollaborator(repoId, userId);
        return Result.success(true);
    }

    @GetMapping("/get_collaborators")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<List<User>> getCollaborators(@RequestHeader("Authorization") String token, @RequestParam("repoId") Integer repoId) {
        Integer usrId = jwtService.getUserId(token);
        User user = userMapper.findById(usrId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        List<User> collaborators = repoMapper.listCollaborators(repoId);
        return Result.success(collaborators);
    }

    @GetMapping("/set_public")
    @WrapsException(ServiceStatus.METHOD_NOT_ALLOWED)
    public Result<Boolean> setPublic(@RequestHeader("Authorization") String token, @RequestParam("repoId") Integer repoId) {
        Integer usrId = jwtService.getUserId(token);
        User user = userMapper.findById(usrId);
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(user) || Objects.isNull(repo) || !repo.getOwner().equals(usrId)) {
            return Result.error(ServiceStatus.METHOD_NOT_ALLOWED);
        }
        repoMapper.setPublic(repoId);
        return Result.success(true);
    }

    @GetMapping("/set_private")
    @WrapsException(ServiceStatus.METHOD_NOT_ALLOWED)
    public Result<Boolean> setPrivate(@RequestHeader("Authorization") String token, @RequestParam("repoId") Integer repoId) {
        Integer usrId = jwtService.getUserId(token);
        User user = userMapper.findById(usrId);
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(user) || Objects.isNull(repo) || !repo.getOwner().equals(usrId)) {
            return Result.error(ServiceStatus.METHOD_NOT_ALLOWED);
        }
        repoMapper.setPrivate(repoId);
        return Result.success(true);
    }


    @GetMapping("/find")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Repo> findRepoByOwnerIdAndName(@RequestHeader("Authorization") String token, @RequestParam("name") String name) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Repo repo = repoMapper.findByOwnerIdAndName(userId,name);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return Result.success(repo);
    }

    @GetMapping("/search_useless")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Repo> findRepoByOwnerAndName(@RequestParam("owner") String owner, @RequestParam("name") String name){
        Repo repo = repoMapper.findByOwnerAndName(owner,name);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(repo);
    }

    @GetMapping("/search")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<Repo>> findRepoByName(@RequestHeader("Authorization") String token, @RequestParam("name") String name){
        Integer userId = jwtService.getUserId(token);
        User usr = userMapper.findById(userId);
        if (Objects.isNull(usr)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        List<Repo> repo = repoMapper.findByName(userId,name);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(repo);
    }

    @GetMapping("count_star")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Integer> countStar(@RequestParam("repoId")Integer repoId){
        Integer count = repoMapper.countStarers(repoId);
        if (Objects.isNull(count)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(count);
    }

    @GetMapping("count_fork")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Integer> countFork(@RequestParam("repoId")Integer repoId){
        Integer count = repoMapper.countForks(repoId);
        if (Objects.isNull(count)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(count);
    }

    @GetMapping("count_watch")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Integer> countWatch(@RequestParam("repoId")Integer repoId){
        Integer count = repoMapper.countWatchers(repoId);
        if (Objects.isNull(count)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(count);
    }

    @GetMapping("/list_starers")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<User>> listStarers(@RequestParam("repoId")Integer repoId){
        List<User> user = repoMapper.listStarers(repoId);
        return Result.success(user);
    }

    @GetMapping("/list_watchers")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<User>> listWatchers(@RequestParam("repoId")Integer repoId){
        List<User> user = repoMapper.listWatchers(repoId);
        return Result.success(user);
    }

    @GetMapping("/list_forks")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<Repo>> listForks(@RequestParam("repoId")Integer repoId){
        List<Repo> repos = repoMapper.listForks(repoId);
        return Result.success(repos);
    }

    @GetMapping("/delete")
    @WrapsException(ServiceStatus.METHOD_NOT_ALLOWED)
    public Result<Boolean> deleteRepo(@RequestHeader("Authorization") String token, @RequestParam("repoId")Integer repoId){
        Integer userId = jwtService.getUserId(token);
        User usr = userMapper.findById(userId);
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(usr) || Objects.isNull(repo) || !repo.getOwner().equals(userId)) {
            return Result.error(ServiceStatus.METHOD_NOT_ALLOWED);
        }
        repoMapper.deleteById(repoId);
        return Result.success(true);
    }
}
