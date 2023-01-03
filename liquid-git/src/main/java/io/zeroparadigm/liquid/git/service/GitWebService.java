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

package io.zeroparadigm.liquid.git.service;

import com.alibaba.fastjson.JSON;
import io.zeroparadigm.liquid.common.bo.UserBO;
import java.io.IOException;
import java.util.List;

import io.zeroparadigm.liquid.git.pojo.LatestCommitInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface GitWebService {

    String uploadFile(String owner, String repo, String fromBranch, MultipartFile file, @Nullable String relPath,
                      String taskId) throws IOException, GitAPIException;

    void commit(String owner, String repo, @Nullable String toBranch, String taskId,
                @Nullable List<String> addFiles, String message, @Nullable UserBO committer) throws IOException, GitAPIException;

    /**
     * Lists the file/dir names with git info.
     *
     * @param owner          owner login name
     * @param repo           repo name
     * @param branchOrCommit target branch name, or commit sha
     * @param relPath        relative path from repo root, empty string or null for root itself
     * @return list of file/dir in this path, with their latest commit info
     * @throws IOException     if the repo or relPath doesn't exist
     * @throws GitAPIException if it cannot check out to the target branch or commit
     */
    List<LatestCommitInfo> listFiles(String owner, String repo, String branchOrCommit,
                                     @Nullable String relPath) throws IOException, GitAPIException;

    String getFile(String owner, String repo, String branchOrCommit,
                 @Nullable String filePath) throws IOException, GitAPIException;

    LatestCommitDTO latestCommitOfCurrentRepo(String owner, String repo, String branchOrCommit,
                                        @Nullable String relPath) throws IOException, GitAPIException;

    void updateCaches(@PathVariable String owner, @PathVariable String repo);


    @Data
    @SuperBuilder
    @NoArgsConstructor
    class LatestCommitDTO {
        LatestCommitInfo latest;
        Integer cnt;
    }
}
