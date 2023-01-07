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

package io.zeroparadigm.liquid.git.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.lang.Nullable;

/**
 * Info of the latest commit on a file/directory.
 *
 * @author hezean
 */
@Slf4j
@Data
@ToString
public class LatestCommitInfo {

    /**
     * Name of file / dir.
     */
    String name;

    /**
     * Whether the target is a directory.
     */
    Boolean isDir;

    /**
     * Latest commit sha.
     */
    String sha;

    /**
     * Latest commit timestamp.
     */
    Integer timestamp;

    /**
     * Latest commit short message.
     */
    String message;

    String user;

    /**
     * Fetches the latest commit on a file/dir.
     *
     * @param checkOuted the jGit object, should be correctly check-outed.
     * @param rel        relative path from repo root.
     */
    public LatestCommitInfo(Git checkOuted, @Nullable File rel) {
        File abs;
        if (rel == null) {
            abs = new File(checkOuted.getRepository().getDirectory().getParent());
        } else {
            abs = new File(checkOuted.getRepository().getDirectory().getParent(),
                String.valueOf(rel));
        }
        if (rel != null) {
            name = rel.getName();
        }
        isDir = abs.isDirectory();

        AtomicReference<RevCommit> latestCommitAtom = new AtomicReference<>();
        try {
            if (rel == null) {
                checkOuted.log()
                    .setMaxCount(1)
                    .call()
                    .forEach(latestCommitAtom::set);
            } else {
                checkOuted.log()
                    .addPath(rel.getPath())
                    .setMaxCount(1)
                    .call()
                    .forEach(latestCommitAtom::set);
            }
        } catch (GitAPIException e) {
            log.error("Cannot fetch commit info", e);
            return;
        }
        RevCommit latestCommit = latestCommitAtom.get();
        if (Objects.isNull(latestCommit)) {
            return;
        }

        sha = latestCommit.getId().getName();
        timestamp = latestCommit.getCommitTime();
        message = latestCommit.getShortMessage();
        user = latestCommit.getAuthorIdent().getName();
    }
}
