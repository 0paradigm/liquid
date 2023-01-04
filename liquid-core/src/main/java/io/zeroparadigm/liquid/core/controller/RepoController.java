package io.zeroparadigm.liquid.core.controller;

import io.swagger.annotations.Api;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.api.git.GitBasicService;
import io.zeroparadigm.liquid.common.bo.UserBO;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
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

    @GetMapping("/isprivate/{owner}/{repo}")
    public Result<Boolean> isPrivate(@PathVariable("owner") String owner,
                                     @PathVariable("repo") String repo) {
        Repo repoEntity = repoMapper.findByOwnerAndName(owner, repo);
        return Result.success(repoEntity.getPrivated());
    }

    @PostMapping("/setprivate/{owner}/{repo}")
    public Result setPrivate(@PathVariable("owner") String owner,
                             @PathVariable("repo") String repo,
                             @RequestParam Boolean privated) {
        Repo repoEntity = repoMapper.findByOwnerAndName(owner, repo);
        repoEntity.setPrivated(privated);
        repoMapper.updateById(repoEntity);
        return Result.success();
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

    @PostMapping("/rename")
    @WrapsException(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS)
    public Result rename(@RequestParam String owner,
                         @RequestParam String oldName,
                         @RequestParam String newName) {
        gitBasicService.renameRepo(owner, oldName, newName);
        repoMapper.updateNameFindByOwnerAndName(owner, oldName, newName);
        return Result.success();
    }

    @GetMapping("/create")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> create(@RequestHeader("Authorization") String token,
                                  @RequestParam("name") String name,
                                  @RequestParam("forkedId") Integer forkedId,
                                  @RequestParam("description") String description,
                                  @RequestParam("language") String language,
                                  @RequestParam("private") Boolean privat,
                                  @RequestParam("readme") Boolean readme) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        log.error("user " + user + " create " + name);
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
            List<String> addFiles = new ArrayList<>(2);
            if (readme) {
                gitBasicService.addReadMe(user.getLogin(), name, description);
                addFiles.add("README.md");
            }
            if (!StringUtils.isEmpty(language)) {
                gitBasicService.addGitIgnore(user.getLogin(), name, language);
                addFiles.add(".gitignore");
            }
            if (readme || !StringUtils.isEmpty(language)) {
                UserBO userBO = new UserBO();
                userBO.setLogin(user.getLogin());
                userBO.setEmail(user.getEmail());
                gitBasicService.webCommit(user.getLogin(), name, "master", addFiles, userBO);
            }
        } catch (Exception e) {
            log.error("create repo error", e);
            return Result.error(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS, e.getMessage());
        }
        repoMapper.createRepo(userId, name, forkedId <= -1 ? null : forkedId, description, language,
            privat);
        return Result.success(true);
    }

    @GetMapping("/add_collaborator/{owner}/{repo}")
    @WrapsException(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS)
    public Result<Boolean> addCollaborator(@PathVariable("owner") String owner,
                                           @PathVariable("repo") String repoName,
                                           @RequestParam("colab") String colabName) {
        Repo repo = repoMapper.findByOwnerAndName(owner, repoName);
        User user = userMapper.findByNameOrMail(colabName);
        repoMapper.addCollaborator(repo.getId(), user.getId());
        return Result.success(true);
    }

    @DeleteMapping("/delete/{owner}/{repo}")
    public Result deleteRepo(@PathVariable String owner,
                             @PathVariable String repo) {

        var id = repoMapper.findByOwnerAndName(owner, repo);
        repoMapper.deleteById(id);
        gitBasicService.deleteRepo(owner, repo);
        return Result.success();
    }

    @GetMapping("/remove_collaborator/{owner}/{repo}")
    @WrapsException(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS)
    public Result<Boolean> removeCollaborator(@PathVariable("owner") String owner,
                                              @PathVariable("repo") String repoName,
                                              @RequestParam("colab") String colabName) {
        Repo repo = repoMapper.findByOwnerAndName(owner, repoName);
        User user = userMapper.findByNameOrMail(colabName);
        repoMapper.removeCollaborator(repo.getId(), user.getId());
        return Result.success(true);
    }

    @GetMapping("/addable_collaborator/{owner}/{repo}")
    @WrapsException(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS)
    public Result<List<String>> getAddableCollaborator(@PathVariable("owner") String owner,
                                                       @PathVariable("repo") String repoName) {
        Repo repo = repoMapper.findByOwnerAndName(owner, repoName);
        List<String> collaborators = repoMapper.listCollaborators(repo.getId()).stream()
            .map(User::getLogin).toList();
        List<String> allUsers = userMapper.listAll().stream()
            .map(User::getLogin).toList();
        allUsers = new ArrayList<>(allUsers);
        allUsers.removeAll(collaborators);
        allUsers.remove(owner);
        return Result.success(allUsers);
    }

    @GetMapping("/get_collaborator/{owner}/{repo}")
    @WrapsException(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS)
    public Result<List<String>> getCollaborators(@PathVariable("owner") String owner,
                                                 @PathVariable("repo") String repoName) {
        var repo = repoMapper.findByOwnerAndName(owner, repoName);
        List<User> collaborators = repoMapper.listCollaborators(repo.getId());
        return Result.success(collaborators.stream().map(User::getLogin).toList());
    }

    @GetMapping("/is_collaborator/{owner}/{repo}")
    @WrapsException(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS)
    public Result<Boolean> isCollaborator(@PathVariable("owner") String owner,
                                          @PathVariable("repo") String repoName,
                                          @RequestParam("colab") String colab) {
        if (owner.equals(colab)) {
            return Result.success(true);
        }
        var repo = repoMapper.findByOwnerAndName(owner, repoName);
        List<User> collaborators = repoMapper.listCollaborators(repo.getId());
        return Result.success(
            collaborators.stream().anyMatch(user -> user.getLogin().equals(colab)));
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
    @GetMapping("/meta/{owner}/{repo}")
    @WrapsException(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS)
    public Result<RepoDto> findRepoByName(@PathVariable("owner") String owner,
                                                @PathVariable("repo") String name) {
        Repo repo = repoMapper.findByOwnerAndName(owner, name);
        String forkedFrom = null;
        if (repo.getForkedFrom() != null) {
            Repo repo1 = repoMapper.findById(repo.getForkedFrom());
            if (!Objects.isNull(repo1)) {
                User owner1 = userMapper.findById(repo.getOwner());
                forkedFrom = owner1.getLogin() + "/" + repo1.getName();
            }
        }
        Integer star = repoMapper.countStarers(repo.getId());
        Integer fork = repoMapper.countForks(repo.getId());
        Integer watch = repoMapper.countWatchers(repo.getId());
        var dto = new RepoDto(repo.getId(), owner, repo.getName(),
                repo.getDescription(), repo.getLanguage(), forkedFrom,
                repo.getPrivated(), star, fork, watch);
        return Result.success(dto);
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
