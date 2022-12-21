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

package io.zeroparadigm.liquid.media.controller;

import io.zeroparadigm.liquid.common.api.media.MinioService;
import javax.annotation.PostConstruct;

import io.zeroparadigm.liquid.common.constants.StorageConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Avatar interacted with minio.
 *
 * @author hezean
 */
@RestController("/avatar")
@Slf4j
public class AvatarController {

    @Autowired
    private MinioService minioService;

    @PostConstruct
    private void createBucket() {
        log.info("Creating bucket '{}'", StorageConsts.MINIO_AVATAR_BUCKET);
        minioService.createBucketIfNotExists(StorageConsts.MINIO_AVATAR_BUCKET);
    }

    @PostMapping("/{uid}/upload")
    public String upload(
            @PathVariable String uid,
            @RequestBody MultipartFile file) {
        return minioService.upload(file, uid, StorageConsts.MINIO_AVATAR_BUCKET);
    }

    @GetMapping("/{uid}")
    public byte[] hello(@PathVariable String uid) {
        return minioService.download(uid, StorageConsts.MINIO_AVATAR_BUCKET);
    }
}
