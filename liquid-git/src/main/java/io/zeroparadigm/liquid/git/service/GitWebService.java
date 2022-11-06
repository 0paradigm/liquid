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

import java.io.IOException;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface GitWebService {

    String uploadFile(String owner, String repo, String fromBranch, MultipartFile file, String relPath,
                      String taskId) throws IOException, GitAPIException;

    void commit(String owner, String repo, String toBranch, String taskId,
                @Nullable List<String> addFiles, String message) throws IOException, GitAPIException;

    List<String> listFiles(String owner, String repo, String branchOrCommit,
                           String relPath) throws IOException, GitAPIException;
}
