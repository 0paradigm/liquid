package io.zeroparadigm.liquid.core.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.api.git.GitBasicService;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.core.dao.UserDao;
import io.zeroparadigm.liquid.core.dao.entity.Repo;
import io.zeroparadigm.liquid.core.dao.entity.User;
import io.zeroparadigm.liquid.core.dao.mapper.RepoMapper;
import io.zeroparadigm.liquid.core.dao.mapper.UserMapper;
import io.zeroparadigm.liquid.core.dto.RepoDto;
import java.util.ArrayList;
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
@RequestMapping("/api/repo")
public class RepoController {

    @Autowired
    UserDao userDao;

    @DubboReference(parameters = {"unicast", "false"})
    JWTService jwtService;

    @DubboReference(parameters = {"unicast", "false"})
    GitBasicService gitBasicService;

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
    public Result<Boolean> auth(@RequestHeader("Authorization") String token,
                                @RequestParam("repoId") Integer repoId,
                                @RequestParam("read") Boolean read,
                                @RequestParam("manage") Boolean manage,
                                @RequestParam("push") Boolean push,
                                @RequestParam("settings") Boolean settings,
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
    public Result<Boolean> create(@RequestHeader("Authorization") String token,
                                  @RequestParam("name") String name,
                                  @RequestParam("forkedId") Integer forkedId,
                                  @RequestParam("description") String description,
                                  @RequestParam("language") String language,
                                  @RequestParam("private") Boolean privat) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        log.error("user " + user + " create ");
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        try {
            if (forkedId <= -1) {
                gitBasicService.createRepo(user.getLogin(), name, "master");
            } else {
                Repo fromRepo = Objects.requireNonNull(repoMapper.findById(forkedId));
                gitBasicService.forkRepo(
                    Objects.requireNonNull(userMapper.findById(fromRepo.getOwner())).getLogin(),
                    fromRepo.getName(),
                    user.getLogin(),
                    fromRepo.getName());
            }
        } catch (Exception e) {
            log.error("create repo error", e);
            return Result.error(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS, e.getMessage());
        }
        repoMapper.createRepo(userId, name, forkedId <= -1 ? null : forkedId, description, language,
            privat);
        return Result.success(true);
    }

    @GetMapping("/add_collaborator")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> addCollaborator(@RequestHeader("Authorization") String token,
                                           @RequestParam("repoId") Integer repoId,
                                           @RequestParam("userId") Integer userId) {
        Integer usrId = jwtService.getUserId(token);
        User usr = userMapper.findById(usrId);
        Repo repo = repoMapper.findById(repoId);
        User user = userMapper.findById(userId);
//        log.info("repo/add_collaborator: " + usr +  repo.getOwner());
        if (Objects.isNull(usr) || Objects.isNull(user) || Objects.isNull(repo) ||
            !repo.getOwner().equals(usrId)) {
            return Result.error(ServiceStatus.METHOD_NOT_ALLOWED);
        }
        repoMapper.addCollaborator(repoId, userId);
        return Result.success(true);
    }

    @GetMapping("/remove_collaborator")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> removeCollaborator(@RequestHeader("Authorization") String token,
                                              @RequestParam("repoId") Integer repoId,
                                              @RequestParam("userId") Integer userId) {
        Integer usrId = jwtService.getUserId(token);
        User usr = userMapper.findById(usrId);
        Repo repo = repoMapper.findById(repoId);
        User user = userMapper.findById(userId);
        if (Objects.isNull(usr) || Objects.isNull(user) || Objects.isNull(repo) ||
            !repo.getOwner().equals(usrId)) {
            return Result.error(ServiceStatus.METHOD_NOT_ALLOWED);
        }
        repoMapper.removeCollaborator(repoId, userId);
        return Result.success(true);
    }

    @GetMapping("/get_collaborator")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<List<User>> getCollaborators(@RequestHeader("Authorization") String token,
                                               @RequestParam("repoId") Integer repoId) {
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
    public Result<Boolean> setPublic(@RequestHeader("Authorization") String token,
                                     @RequestParam("repoId") Integer repoId) {
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
    public Result<Boolean> setPrivate(@RequestHeader("Authorization") String token,
                                      @RequestParam("repoId") Integer repoId) {
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
    public Result<Repo> findRepoByOwnerIdAndName(@RequestHeader("Authorization") String token,
                                                 @RequestParam("name") String name) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Repo repo = repoMapper.findByOwnerIdAndName(userId, name);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return Result.success(repo);
    }

    @GetMapping("/search_useless")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Repo> findRepoByOwnerAndName(@RequestParam("owner") String owner,
                                               @RequestParam("name") String name) {
        Repo repo = repoMapper.findByOwnerAndName(owner, name);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(repo);
    }

    // star, forked name, fork, watch      description language
    @GetMapping("/search")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<RepoDto>> findRepoByName(@RequestHeader("Authorization") String token,
                                                @RequestParam("name") String name) {
        Integer userId = jwtService.getUserId(token);
        User usr = userMapper.findById(userId);
//        log.info("testing search ..... user " + usr);
        if (Objects.isNull(usr)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        List<Repo> repos = repoMapper.findByName(userId, name);
//        log.info("testing search ..... repos " + repos);
        if (Objects.isNull(repos)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        List<RepoDto> repoDtos = new ArrayList<>();
        for (int i = 0; i < repos.size(); i++) {
            String forkedFrom = null;
            if (repos.get(i).getForkedFrom() != null) {
                Repo repo = repoMapper.findById(repos.get(i).getForkedFrom());
                if (!Objects.isNull(repo)) {
                    User owner = userMapper.findById(repo.getOwner());
                    forkedFrom = owner.getLogin() + "/" + repo.getName();
                }
            }
            Integer star = repoMapper.countStarers(repos.get(i).getId());
            Integer fork = repoMapper.countForks(repos.get(i).getId());
            Integer watch = repoMapper.countWatchers(repos.get(i).getId());
            repoDtos.add(new RepoDto(repos.get(i).getId(), usr.getLogin(), repos.get(i).getName(),
                repos.get(i).getDescription(), repos.get(i).getLanguage(), forkedFrom,
                repos.get(i).getPrivated(),
                star, fork, watch));
        }
        return Result.success(repoDtos);
    }

    @GetMapping("count_star")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Integer> countStar(@RequestParam("repoId") Integer repoId) {
        Integer count = repoMapper.countStarers(repoId);
        if (Objects.isNull(count)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(count);
    }

    @GetMapping("count_fork")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Integer> countFork(@RequestParam("repoId") Integer repoId) {
        Integer count = repoMapper.countForks(repoId);
        if (Objects.isNull(count)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(count);
    }

    @GetMapping("count_watch")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Integer> countWatch(@RequestParam("repoId") Integer repoId) {
        Integer count = repoMapper.countWatchers(repoId);
        if (Objects.isNull(count)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(count);
    }

    @GetMapping("/list_starers")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<User>> listStarers(@RequestParam("repoId") Integer repoId) {
        List<User> user = repoMapper.listStarers(repoId);
        return Result.success(user);
    }

    @GetMapping("/list_watchers")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<User>> listWatchers(@RequestParam("repoId") Integer repoId) {
        List<User> user = repoMapper.listWatchers(repoId);
        return Result.success(user);
    }

    @GetMapping("/list_forks")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<RepoDto>> listForks(@RequestParam("repoId") Integer repoId) {
        List<Repo> repos = repoMapper.listForks(repoId);
//        log.info("testing list forks ..... repos " + repos);
        List<RepoDto> repoDtos = new ArrayList<>();
        for (int i = 0; i < repos.size(); i++) {
            String forkedFrom = null;
            if (repos.get(i).getForkedFrom() != null) {
                Repo repo = repoMapper.findById(repos.get(i).getForkedFrom());
                if (!Objects.isNull(repo)) {
                    User owner = userMapper.findById(repo.getOwner());
                    forkedFrom = owner.getLogin() + "/" + repo.getName();
                }
            }
            Integer star = repoMapper.countStarers(repos.get(i).getId());
            Integer fork = repoMapper.countForks(repos.get(i).getId());
            Integer watch = repoMapper.countWatchers(repos.get(i).getId());
            String owner = userMapper.findById(repos.get(i).getOwner()).getLogin();
            repoDtos.add(new RepoDto(repos.get(i).getId(), owner, repos.get(i).getName(),
                repos.get(i).getDescription(), repos.get(i).getLanguage(), forkedFrom,
                repos.get(i).getPrivated(),
                star, fork, watch));
        }
        return Result.success(repoDtos);
    }

    @GetMapping("/delete")
    @WrapsException(ServiceStatus.METHOD_NOT_ALLOWED)
    public Result<Boolean> deleteRepo(@RequestHeader("Authorization") String token,
                                      @RequestParam("repoId") Integer repoId) {
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
