package io.zeroparadigm.liquid.core.controller;

import io.swagger.annotations.Api;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.api.git.GitBasicService;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.core.dao.UserDao;
import io.zeroparadigm.liquid.core.dao.entity.IssueComment;
import io.zeroparadigm.liquid.core.dao.entity.PR;
import io.zeroparadigm.liquid.core.dao.entity.PRComment;
import io.zeroparadigm.liquid.core.dao.entity.Repo;
import io.zeroparadigm.liquid.core.dao.entity.User;
import io.zeroparadigm.liquid.core.dao.mapper.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.eclipse.jgit.api.errors.GitAPIException;
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
public class PRController {

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
    PrMapper prMapper;

    @Autowired
    PRCommentMapper prCommentMapper;


    @GetMapping("/new/{owner}/{repo}")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Integer> newPR(
        @RequestHeader(value = "Authorization", required = false) String token,
        @PathVariable("owner") String owner,
        @PathVariable("repo") String repoName,
        @RequestParam("title") String title,
        @RequestParam("head_owner") String headOwner,
        @RequestParam("head_repo") String headRepo,
        @RequestParam("head_branch") String headBranch,
        @RequestParam("base_branch") String baseBranch,
        @RequestParam("cmt") String cmt) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        var repo = repoMapper.findByOwnerAndName(owner, repoName);
        var head = repoMapper.findByOwnerAndName(headOwner, headRepo);
        var prs = prMapper.findByRepoId(repo.getId());
        var displayId = prs.size() + 1;
        prMapper.createPr(displayId, repo.getId(), userId, title, head.getId(), repo.getId(), headBranch,
            baseBranch, System.currentTimeMillis());
        if (Objects.nonNull(cmt) && !cmt.isEmpty()) {
            prCommentMapper.createPRComment(
                repo.getId(), displayId, userId,
                cmt, System.currentTimeMillis()
            );
        }
        return Result.success(displayId);
    }

    @Data
    @Builder
    public static class PrListDTO {
        Integer id;
        Boolean isClosed;
        String title;
        String openBy;
        Long openAt;
        Integer cmtCnt;
    }


    @Data
    @Builder
    public static class PrDetailDTO {
        String title;
        Boolean isOpening;
        Boolean isMerged;
        String openBy;
        Long openAt;
        List<PrEventDTO> events;
        List<String> participants;

        String fromRepoBranch;
        String toRepoBranch;
        String chooseBranch;
        String chooseCmpRep;
        String chooseCmpBranch;
    }

    @Data
    @Builder
    public static class PrEventDTO {
        String ctx;
        String author;
        String cred;
        Long time;
    }

    @GetMapping("/details/{owner}/{repo}/{displayId}")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<PrDetailDTO> findByIds(@PathVariable("owner") String owner,
                                         @PathVariable("repo") String repoName,
                                         @PathVariable("displayId") Integer displayId) {
        var repo = repoMapper.findByOwnerAndName(owner, repoName);
        var pr = prMapper.findByRepoIdAndDisplayedId(repo.getId(), displayId);
        var events = prCommentMapper.findByRepoAndPrDisp(repo.getId(), displayId);
        List<PrEventDTO> eventDTOs = events.stream()
            .map(event -> {
                    var author = userMapper.findById(event.getAuthor());
                    var isOwner = owner.equals(author.getLogin());
                    var isColab = false;
                    var isContributor = false;
                    if (!isOwner) {
                        List<User> collaborators = repoMapper.listCollaborators(repo.getId());
                        isColab = collaborators.stream()
                            .anyMatch(user -> user.getId().equals(author.getId()));
                    }
                    if (!isOwner && !isColab) {
                        List<String> conts = repoMapper.listContributors(repo.getId());
                        isContributor = conts.stream().anyMatch(user -> user.equals(author.getLogin()));
                    }
                    return PrEventDTO.builder()
                        .author(author.getLogin())
                        .ctx(event.getComment())
                        .cred(isOwner ? "Owner" :
                            isColab ? "Collaborator" : isContributor ? "Contributor" : "")
                        .time(Long.parseLong(event.getCreatedAt()))
                        .build();
                }
            )
            .toList();
        List<String> parts = events.stream()
            .map(PRComment::getAuthor)
            .distinct()
            .map(id -> userMapper.findById(id).getLogin())
            .toList();
        var headRepo = repoMapper.findById(pr.getHead());
        PrDetailDTO dto = PrDetailDTO.builder()
            .title(pr.getTitle())
            .isOpening(!pr.getClosed())
            .openBy(userMapper.findById(pr.getOpener()).getLogin())
            .openAt(pr.getCreatedAt())
            .isMerged(eventDTOs.stream().anyMatch(e -> "[[[merge]]]".equals(e.getCtx())))
            .events(eventDTOs)
            .participants(parts)
            .fromRepoBranch(headRepo.getName() + ":" + pr.getHeadBranch())
            .toRepoBranch(repoName + ":" + pr.getBaseBranch())
            .chooseBranch(pr.getBaseBranch())
            .chooseCmpRep(headRepo.getName())
            .chooseCmpBranch(pr.getHeadBranch())
            .build();
        return Result.success(dto);
    }

    @Data
    @Builder
    public static class WatchPrDTO {
        String userName;
        String repoOwnerName;
        String repoName;
        Long time;
        Integer prId;
        Boolean prIsClose;
        String prTitle;
        Integer prCmtCnt;
    }

    @GetMapping("/listwatching")
    public Result<List<WatchPrDTO>> listWatching(
        @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.success(List.of());
        }
        final String userName = userMapper.findById(userId).getLogin();

        List<Repo> repos = userMapper.listWatchingRepos(userId);
        var res = repos.stream()
            .map(repo -> {
                var ownerName = userMapper.findById(repo.getOwner()).getLogin();
                var dto = listPr(ownerName, repo.getName());
                List<PrListDTO> opens = dto.getData().get("opens");
                List<PrListDTO> closes = dto.getData().get("closes");
                List<WatchPrDTO> openDtos = opens.stream()
                    .map(raw -> WatchPrDTO.builder()
                        .userName(raw.openBy)
                        .repoOwnerName(ownerName)
                        .repoName(repo.getName())
                        .time(raw.openAt)
                        .prId(raw.id)
                        .prIsClose(false)
                        .prTitle(raw.title)
                        .prCmtCnt(raw.cmtCnt)
                        .build()
                    )
                    .filter(dto1 -> !dto1.getUserName().equals(userName))
                    .toList();
                List<WatchPrDTO> closeDtos = closes.stream()
                    .map(raw -> WatchPrDTO.builder()
                        .userName(raw.openBy)
                        .repoOwnerName(ownerName)
                        .repoName(repo.getName())
                        .time(raw.openAt)
                        .prId(raw.id)
                        .prIsClose(true)
                        .prTitle(raw.title)
                        .prCmtCnt(raw.cmtCnt)
                        .build()
                    )
                    .filter(dto1 -> !dto1.getUserName().equals(userName))
                    .toList();
                var ret = new ArrayList<>(openDtos);
                ret.addAll(closeDtos);
                return ret;
            })
            .flatMap(List::stream)
            .sorted(Comparator.comparing(WatchPrDTO::getTime))
            .toList();
        return Result.success(res);
    }

    @GetMapping("/list/{owner}/{repo}")
    public Result<Map<String, List<PrListDTO>>> listPr(@PathVariable("owner") String owner,
                                                       @PathVariable("repo") String repo) {
        Repo repoE = repoMapper.findByOwnerAndName(owner, repo);
        if (Objects.isNull(repoE)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        var prs = prMapper.findByRepoId(repoE.getId());
        var res = prs.stream()
            .map(pr -> PrListDTO.builder()
                .id(pr.getDisplayId())
                .isClosed(pr.getClosed())
                .title(pr.getTitle())
                .openBy(userMapper.findById(pr.getOpener()).getLogin())
                .openAt(pr.getCreatedAt())
                .cmtCnt(prCommentMapper.cntByRepoAndPr(repoE.getId(), pr.getDisplayId()))
                .build()
            ).toList();
        var opens = res.stream()
            .filter(pr -> !pr.getIsClosed())
            .toList();
        var closes = res.stream()
            .filter(pr -> pr.getIsClosed())
            .toList();
        return Result.success(Map.of("opens", opens, "closes", closes));
    }


    @GetMapping("/get")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)

    public Result<List<PR>> getPR(@RequestParam("repo_id") Integer repoId) {
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        List<PR> prs = prMapper.findByRepoId(repoId);
        return Result.success(prs);
    }

    @GetMapping("/get_by_user")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<PR>> getPRByUser(
        @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        List<PR> prs = prMapper.findByUserId(userId);
        return Result.success(prs);
    }

    @GetMapping("/getByClosed")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<PR>> getPRByClosed(@RequestParam("repo_id") Integer repoId,
                                          @RequestParam("closed") Boolean closed) {
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        List<PR> prs = prMapper.findByRepoIdAndClosed(repoId, closed);
        return Result.success(prs);
    }

    @GetMapping("/setClosed/{owner}/{repo}/{displayId}")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> setClosed(
        @RequestHeader(value = "Authorization", required = false) String token,
        @PathVariable("owner") String owner,
        @PathVariable("repo") String repoName,
        @PathVariable("displayId") Integer displayId,
        @RequestParam("close") Boolean closed) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Repo repo = repoMapper.findByOwnerAndName(owner, repoName);
        PR pr = prMapper.findByRepoIdAndDisplayedId(repo.getId(), displayId);
        prMapper.setClosed(pr.getId(), closed);
        prCommentMapper.createPRComment(
            repo.getId(), pr.getDisplayId(), userId,
            closed ? "[[[close]]]" : "[[[reopen]]]",
            System.currentTimeMillis()
        );
        return Result.success(true);
    }

    @Data
    public static class AddCmtDTO {
        String ctx;
    }

    @PostMapping("/new_comment/{owner}/{repo}/{displayId}")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> newPRComment(
        @RequestHeader(value = "Authorization", required = false) String token,
        @PathVariable("owner") String owner,
        @PathVariable("repo") String repoName,
        @PathVariable("displayId") Integer displayId,
        @RequestBody AddCmtDTO dto) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        var repo = repoMapper.findByOwnerAndName(owner, repoName);
        PR pr = prMapper.findByRepoIdAndDisplayedId(repo.getId(), displayId);
        prCommentMapper.createPRComment(repo.getId(), pr.getDisplayId(), userId, dto.getCtx(),
            System.currentTimeMillis());
        return Result.success(true);
    }


    @GetMapping("/get_comment/{owner}/{repo}/{displayId}")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result merge(@PathVariable("owner") String owner,
                        @PathVariable("repo") String repoName,
                        @PathVariable("displayId") Integer displayId,
                        @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = jwtService.getUserId(token);
        var baseRepo = repoMapper.findByOwnerAndName(owner, repoName);
        PR pr = prMapper.findByRepoIdAndDisplayedId(baseRepo.getId(), displayId);
        var headRepo = repoMapper.findById(pr.getHead());
        var user = userMapper.findById(headRepo.getOwner());

        try {
            gitBasicService.mergePR(owner, repoName, user.getName(), headRepo.getName(),
                pr.getTitle());
        } catch (Exception e) {
            prMapper.setClosed(pr.getId(), true);
            return Result.error(ServiceStatus.GIT_REPO_NOT_FOUND);
        }

        prMapper.setClosed(pr.getId(), true);
        prCommentMapper.createPRComment(
            baseRepo.getId(), pr.getDisplayId(), userId,
            "[[[merge]]]",
            System.currentTimeMillis()
        );
        return Result.success();
    }

}
