package io.zeroparadigm.liquid.core.controller;

import io.swagger.annotations.Api;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.api.core.GitMetaService;
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
import java.util.Map;
import lombok.Builder;
import lombok.Data;
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
@SuppressWarnings("rawtypes")
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

    @Autowired
    GitMetaService gitMetaService;

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

    @GetMapping("/addcontributor/{owner}/{repo}/{contributor}")
    public Result addContributor(@PathVariable("owner") String owner,
                                 @PathVariable("repo") String repo,
                                 @PathVariable("contributor") String contributor) {
        gitMetaService.recordContributor(owner, repo, contributor);
        return Result.success();
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
                                  @RequestParam(value = "forkedId", required = false)
                                  String forkedStr,
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
        Repo fromRepo = null;
        try {
            if (forkedStr == null) {
                gitBasicService.createRepo(user.getLogin(), name, "master");
            } else {
                var ownerAndName = forkedStr.split("/");
                fromRepo = Objects.requireNonNull(
                    repoMapper.findByOwnerAndName(ownerAndName[0], ownerAndName[1]));
                gitBasicService.forkRepo(
                    Objects.requireNonNull(userMapper.findById(fromRepo.getOwner())).getLogin(),
                    fromRepo.getName(),
                    user.getLogin(),
                    name);
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
        if (description == null || description.isEmpty()) {
            description = fromRepo == null ? "" : fromRepo.getDescription();
        }
        if (language == null || language.isEmpty()) {
            language = fromRepo == null ? "" : fromRepo.getLanguage();
        }
        repoMapper.createRepo(userId, name, fromRepo == null ? null : fromRepo.getId(), description,
            language,
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
                User owner1 = userMapper.findById(repo1.getOwner());
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

    @GetMapping("/listcontributors/{owner}/{repo}")
    public Result<List<String>> listContributors(@PathVariable("owner") String owner,
                                                 @PathVariable("repo") String repoName) {
        Repo repo = repoMapper.findByOwnerAndName(owner, repoName);
        return Result.success(repoMapper.listContributors(repo.getId()));
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

    @GetMapping("/list_starers/{owner}/{repo}")
    public Result<List<String>> listStarers(@PathVariable("owner") String owner,
                                            @PathVariable("repo") String repoName) {
        Repo repo = repoMapper.findByOwnerAndName(owner, repoName);
        List<User> user = repoMapper.listStarers(repo.getId());
        var res = user.stream().map(User::getLogin).toList();
        return Result.success(res);
    }

    @GetMapping("/list_watchers/{owner}/{repo}")
    public Result<List<String>> listWatchers(@PathVariable("owner") String owner,
                                             @PathVariable("repo") String repoName) {
        Repo repo = repoMapper.findByOwnerAndName(owner, repoName);
        List<User> user = repoMapper.listWatchers(repo.getId());
        var res = user.stream().map(User::getLogin).toList();
        return Result.success(res);
    }

    @GetMapping("/toggle_watch/{owner}/{repo}")
    public Result toggleWatch(@RequestHeader("Authorization") String token,
                              @PathVariable("owner") String owner,
                              @PathVariable("repo") String repoName) {
        Integer userId = jwtService.getUserId(token);
        Repo repo = repoMapper.findByOwnerAndName(owner, repoName);
        var isWatching = repoMapper.listWatchers(repo.getId()).stream()
            .anyMatch(user -> user.getId().equals(userId));
        if (isWatching) {
            repoMapper.removeWatcher(repo.getId(), userId);
            return Result.success(Map.of(
                "msg", String.format("You unwatched %s/%s", owner, repoName),
                "cnt", repoMapper.countWatchers(repo.getId())
            ));
        } else {
            repoMapper.addWatcher(repo.getId(), userId);
            return Result.success(Map.of(
                "msg", String.format("Start watching %s/%s", owner, repoName),
                "cnt", repoMapper.countWatchers(repo.getId())
            ));
        }
    }

    @GetMapping("/toggle_star/{owner}/{repo}")
    public Result toggleStar(@RequestHeader("Authorization") String token,
                             @PathVariable("owner") String owner,
                             @PathVariable("repo") String repoName) {
        Integer userId = jwtService.getUserId(token);
        Repo repo = repoMapper.findByOwnerAndName(owner, repoName);
        var isStarring = repoMapper.listStarers(repo.getId()).stream()
            .anyMatch(user -> user.getId().equals(userId));
        if (isStarring) {
            repoMapper.deleteStarer(repo.getId(), userId);
            return Result.success(Map.of(
                "msg", String.format("You unstarred %s/%s", owner, repoName),
                "cnt", repoMapper.countStarers(repo.getId())
            ));
        } else {
            repoMapper.addStarer(repo.getId(), userId);
            return Result.success(Map.of(
                "msg", String.format("You starred %s/%s", owner, repoName),
                "cnt", repoMapper.countStarers(repo.getId())
            ));
        }
    }

    @Data
    @Builder
    public static class ListForkDTO {
        String owner;
        String name;
    }

    @GetMapping("/list_forks/{owner}/{repo}")
    public Result<List<ListForkDTO>> listForks(@PathVariable("owner") String owner,
                                               @PathVariable("repo") String repoName) {
        var repo = repoMapper.findByOwnerAndName(owner, repoName);
        List<Repo> repos = repoMapper.listForks(repo.getId());
        var dtos = repos.stream().map(r ->
            ListForkDTO.builder()
                .owner(userMapper.findById(r.getOwner()).getLogin())
                .name(r.getName())
                .build()
        ).toList();
        return Result.success(dtos);
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
