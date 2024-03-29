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

package io.zeroparadigm.liquid.common.api.git;

import io.zeroparadigm.liquid.common.bo.UserBO;
import java.io.IOException;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

/**
 * Basic git operations shared for liquid-core.
 *
 * @author hezean
 */
@Service
public interface GitBasicService {

    /**
     * Creates a repo.
     *
     * @param owner      repo owner
     * @param repo       repo name
     * @param initBranch initial branch
     * @throws IOException     if cannot create repo in FS
     * @throws GitAPIException if cannot init the repo
     */
    void createRepo(String owner, String repo, String initBranch) throws IOException, GitAPIException;

    void addReadMe(String owner, String repo, String desc);

    void addGitIgnore(String owner, String repo, String initBranch);

    void forkRepo(String fromOwner, String fromRepo, String toOwner, String toRepo) throws IOException, GitAPIException;

    void webCommit(String owner, String repo, String initBranch, List<String> addFiles,
                   UserBO committer);

    List<String> listBranches(String owner, String repo);

    void deleteRepo(String owner, String repo);

    void renameRepo(String owner, String repo, String newRepoName);

    void mergePR(String baseOwner, String baseRepo, String baseBranch, String headOwner,
                 String headRepo, String headBranch,
                 String PRTitle) throws IOException, GitAPIException;

}
