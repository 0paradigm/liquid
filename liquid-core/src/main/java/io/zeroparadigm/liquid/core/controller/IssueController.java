/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.zeroparadigm.liquid.core.controller;

import io.swagger.annotations.Api;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.core.dao.UserDao;
import io.zeroparadigm.liquid.core.dao.entity.*;
import io.zeroparadigm.liquid.core.dao.mapper.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewIssueDTO {

        String title;
        List<TagDTO> tags;
        String submitStr;
    }

    @Data
    public static class TagDTO {

        String content;
        String color;
    }

    @PostMapping("/new/{owner}/{repo}")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> newIssue(
                                    @RequestHeader(value = "Authorization", required = false) String token,
                                    @PathVariable("owner") String owner,
                                    @PathVariable("repo") String repoName,
                                    @RequestBody NewIssueDTO dto) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        var repo = repoMapper.findByOwnerAndName(owner, repoName);
        var issueId = issueMapper.findByRepoId(repo.getId()).size() + 1;
        log.info("{}", dto);
        log.info("issueId: {}", issueId);
        issueMapper.createIssue(issueId, repo.getId(), userId, dto.getTitle(),
                System.currentTimeMillis(), false);
        for (var tag : dto.getTags()) {
            issueMapper.addLabel(repo.getId(), issueId, tag.getContent(), tag.getColor());
        }
        issueCommentMapper.createIssueComment(repo.getId(), issueId, userId, dto.getSubmitStr(),
                System.currentTimeMillis());
        return Result.success(true);
    }

    @Data
    public static class AddCmtDTO {

        String ctx;
    }

    @PostMapping("/comment/{owner}/{repo}/{issueId}")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> commentIssue(
                                        @RequestHeader(value = "Authorization", required = false) String token,
                                        @PathVariable("owner") String owner,
                                        @PathVariable("repo") String repoName,
                                        @PathVariable("issueId") Integer issueId,
                                        @RequestBody AddCmtDTO dto) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        var repo = repoMapper.findByOwnerAndName(owner, repoName);
        issueCommentMapper.createIssueComment(repo.getId(), issueId, userId, dto.getCtx(),
                System.currentTimeMillis());
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
    public Result<Boolean> deleteIssueComment(
                                              @RequestHeader(value = "Authorization", required = false) String token,
                                              @RequestParam("comment_id") Integer commentId) {
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
    public Result<List<Issue>> listIssueByOnwerId(
                                                  @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = jwtService.getUserId(token);
        List<Issue> issues = issueMapper.findByOwnerId(userId);
        if (Objects.isNull(issues)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        return Result.success(issues);
    }

    @Data
    @Builder
    public static class IssueDetailDTO {

        String title;
        Boolean isOpening;
        String openBy;
        Long openAt;
        List<IssueEventDTO> events;
        List<String> participants;
    }

    @Data
    @Builder
    public static class IssueEventDTO {

        String ctx;
        String author;
        String cred;
        Long time;
    }

    @GetMapping("/details/{owner}/{repo}/{displayId}")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<IssueDetailDTO> findByIds(@PathVariable("owner") String owner,
                                            @PathVariable("repo") String repoName,
                                            @PathVariable("displayId") Integer displayId) {
        var repo = repoMapper.findByOwnerAndName(owner, repoName);
        var issue = issueMapper.findByDisplayedIdandRepoId(displayId, repo.getId());
        var events = issueCommentMapper.findByRepoIdAndIssueDisplayId(repo.getId(), displayId);
        List<IssueEventDTO> eventDTOs = events.stream()
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
                    return IssueEventDTO.builder()
                            .author(author.getLogin())
                            .ctx(event.getComment())
                            .cred(isOwner ? "Owner" : isColab ? "Collaborator" : isContributor ? "Contributor" : "")
                            .time(Long.parseLong(event.getCreatedAt()))
                            .build();
                })
                .toList();
        List<String> parts = events.stream()
                .map(IssueComment::getAuthor)
                .distinct()
                .map(id -> userMapper.findById(id).getLogin())
                .toList();
        IssueDetailDTO dto = IssueDetailDTO.builder()
                .title(issue.getTitle())
                .isOpening(!issue.getClosed())
                .openBy(userMapper.findById(issue.getOpener()).getLogin())
                .openAt(issue.getCreatedAt())
                .events(eventDTOs)
                .participants(parts)
                .build();
        return Result.success(dto);
    }

    @Data
    @Builder
    public static class IssueListDTO {

        Integer id;
        String title;
        Integer cmtCnt;
        Long openAt;
        String openBy;
        List<IssueTagDTO> tags;
    }

    @Data
    @Builder
    public static class IssueTagDTO {

        String content;
        String color;
    }

    @Data
    @Builder
    public static class WatchIssueDTO {

        String userName;
        String repoOwnerName;
        String repoName;
        Long time;
        Integer issueId;
        Boolean issueIsClose;
        List<IssueTagDTO> issueTags;
        String issueTitle;
        Integer issueCmtCnt;
    }

    @GetMapping("/watchingrepos")
    public Result<List<WatchIssueDTO>> watchingIssues(
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
                    var dto = listIssueByRepoId(ownerName, repo.getName());
                    List<IssueListDTO> opens = dto.getData().get("opens");
                    List<WatchIssueDTO> openDtos = opens.stream()
                            .map(raw -> WatchIssueDTO.builder()
                                    .userName(raw.openBy)
                                    .repoOwnerName(ownerName)
                                    .repoName(repo.getName())
                                    .time(raw.openAt)
                                    .issueId(raw.id)
                                    .issueIsClose(false)
                                    .issueTags(raw.tags)
                                    .issueTitle(raw.title)
                                    .issueCmtCnt(raw.cmtCnt)
                                    .build())
                            .filter(dto1 -> !dto1.getUserName().equals(userName))
                            .toList();
                    List<IssueListDTO> closes = dto.getData().get("closes");
                    List<WatchIssueDTO> closeDtos = closes.stream()
                            .map(raw -> WatchIssueDTO.builder()
                                    .userName(raw.openBy)
                                    .repoOwnerName(ownerName)
                                    .repoName(repo.getName())
                                    .time(raw.openAt)
                                    .issueId(raw.id)
                                    .issueIsClose(true)
                                    .issueTags(raw.tags)
                                    .issueTitle(raw.title)
                                    .issueCmtCnt(raw.cmtCnt)
                                    .build())
                            .filter(dto1 -> !dto1.getUserName().equals(userName))
                            .toList();
                    var ret = new ArrayList<>(openDtos);
                    ret.addAll(closeDtos);
                    return ret;
                })
                .flatMap(List::stream)
                .sorted(Comparator.comparing(WatchIssueDTO::getTime))
                .toList();
        return Result.success(res);
    }

    @GetMapping("/issuelist/{owner}/{repo}")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Map<String, List<IssueListDTO>>> listIssueByRepoId(@PathVariable String owner,
                                                                     @PathVariable String repo) {
        var id = repoMapper.findByOwnerAndName(owner, repo).getId();
        List<Issue> issues = issueMapper.findByRepoId(id);

        List<IssueListDTO> opens = new LinkedList<>();
        List<IssueListDTO> closes = new LinkedList<>();
        for (Issue issue : issues) {
            IssueListDTO dto = IssueListDTO.builder()
                    .id(issue.getDisplayId())
                    .title(issue.getTitle())
                    .cmtCnt(issueCommentMapper.cntByRepoAndIssueId(id, issue.getDisplayId()))
                    .openAt(issue.getCreatedAt())
                    .openBy(userMapper.findById(issue.getOpener()).getLogin())
                    .tags(issueLabelMapper.listAllLabelsOfIssue(id, issue.getDisplayId()).stream()
                            .map(label -> IssueTagDTO.builder()
                                    .content(label.getLabel())
                                    .color(label.getColor())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
            if (issue.getClosed()) {
                closes.add(dto);
            } else {
                opens.add(dto);
            }
        }
        return Result.success(Map.of("opens", opens, "closes", closes));
    }

    @GetMapping("/repo_user_list")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<Issue>> listIssueByRepoIdAndUserId(@RequestParam("repoId") Integer repoId,
                                                          @RequestParam("userId") Integer userId) {
        List<Issue> issues = issueMapper.findByRepoIdandOwnerId(repoId, userId);
        if (Objects.isNull(issues)) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        return Result.success(issues);
    }

    @GetMapping("/repo_label_list")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<Issue>> listIssueByRepoIdAndLabel(@RequestParam("repoId") Integer repoId,
                                                         @RequestParam("label") String label) {
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
    public Result<List<Issue>> listIssueByUserIdAndClose(
                                                         @RequestHeader(value = "Authorization", required = false) String token,
                                                         @RequestParam("closed") Boolean closed) {
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
    public Result<Boolean> assignIssue(
                                       @RequestHeader(value = "Authorization", required = false) String token,
                                       @RequestParam("issueId") Integer issueId,
                                       @RequestParam("assigneeId") Integer assigneeId) {
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
        // if (!issue.getOwnerId().equals(userId)) {
        // return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        // }
        Boolean auth = repoMapper.verifyAuth(userId, issue.getRepo());
        Repo repo = repoMapper.findById(issue.getRepo());
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.assignIssue(issueId, assigneeId);
        return Result.success(true);
    }

    @GetMapping("/unassign")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> unassignIssue(
                                         @RequestHeader(value = "Authorization", required = false) String token,
                                         @RequestParam("issueId") Integer issueId,
                                         @RequestParam("assigneeId") Integer assigneeId) {
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
        Boolean flag = false;
        for (int i = 0; i < assignee.size(); i++) {
            if (assignee.get(i).getId().equals(assigneeId)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        Boolean auth = repoMapper.verifyAuth(userId, issue.getRepo());
        Repo repo = repoMapper.findById(issue.getRepo());

        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.removeAssignee(issueId, assigneeId);
        return Result.success(true);
    }

    @GetMapping("/assign_label")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> assignLabel(
                                       @RequestHeader(value = "Authorization", required = false) String token,
                                       @RequestParam("issueId") Integer issueId,
                                       @RequestParam("labelId") Integer labelId) {
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
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.assignLabelById(issueId, labelId);
        return Result.success(true);
    }

    @GetMapping("/unassign_label")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> unassignLabel(
                                         @RequestHeader(value = "Authorization", required = false) String token,
                                         @RequestParam("issueId") Integer issueId,
                                         @RequestParam("labelId") Integer labelId) {
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
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.removeLabelById(issueId, labelId);
        return Result.success(true);
    }

    @GetMapping("/assign_milestone")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> assignMilestone(
                                           @RequestHeader(value = "Authorization", required = false) String token,
                                           @RequestParam("issueId") Integer issueId,
                                           @RequestParam("milestoneId") Integer milestoneId) {
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
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.assignMilestone(issueId, milestoneId);
        return Result.success(true);
    }

    @GetMapping("/unassign_milestone")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> unassignMilestone(
                                             @RequestHeader(value = "Authorization", required = false) String token,
                                             @RequestParam("issueId") Integer issueId,
                                             @RequestParam("milestoneId") Integer milestoneId) {
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
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueMapper.removeMilestone(issueId, milestoneId);
        return Result.success(true);
    }

    @GetMapping("/setclose/{owner}/{repo}/{issue}")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Boolean> closeIssue(
                                      @RequestHeader(value = "Authorization", required = false) String token,
                                      @PathVariable("owner") String owner,
                                      @PathVariable("repo") String repoName,
                                      @PathVariable("issue") Integer issueDisplayId,
                                      @RequestParam("close") Boolean close) {
        Integer userId = jwtService.getUserId(token);
        var repo = repoMapper.findByOwnerAndName(owner, repoName);
        Issue issue = issueMapper.findByDisplayedIdandRepoId(issueDisplayId, repo.getId());
        issueMapper.closeIssue(issue.getId(), close);
        issueCommentMapper.createIssueComment(repo.getId(), issueDisplayId, userId,
                close ? "[[[close]]]" : "[[[reopen]]]", System.currentTimeMillis());
        return Result.success(true);
    }
}
