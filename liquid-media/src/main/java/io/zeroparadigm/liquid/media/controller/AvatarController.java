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
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import java.util.Objects;
import javax.annotation.PostConstruct;

import io.zeroparadigm.liquid.common.constants.StorageConsts;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Avatar interacted with minio.
 *
 * @author hezean
 */
@RestController
@Slf4j
@CrossOrigin
public class AvatarController {

    @Autowired
    private MinioService minioService;

    @PostConstruct
    private void createBucket() {
        log.info("Creating bucket '{}'", StorageConsts.MINIO_AVATAR_BUCKET);
        minioService.createBucketIfNotExists(StorageConsts.MINIO_AVATAR_BUCKET);
        if (minioService.download("default1", StorageConsts.MINIO_AVATAR_BUCKET) == null) {
            log.info("Uploading default avatar");
            minioService.upload(getClass().getClassLoader().getResourceAsStream("default1.png"),
                    "default1", StorageConsts.MINIO_AVATAR_BUCKET);
        }
        if (minioService.download("default2", StorageConsts.MINIO_AVATAR_BUCKET) == null) {
            log.info("Uploading default avatar");
            minioService.upload(getClass().getClassLoader().getResourceAsStream("default2.png"),
                    "default2", StorageConsts.MINIO_AVATAR_BUCKET);
        }
    }

    @PostMapping("/upload")
    public String upload(
                         @RequestParam String uid,
                         @RequestBody MultipartFile file) {
        return minioService.upload(file, uid, StorageConsts.MINIO_AVATAR_BUCKET);
    }

    @GetMapping(path = "/get", produces = MediaType.IMAGE_JPEG_VALUE)
    @SneakyThrows
    @WrapsException(status = HttpStatus.NOT_FOUND)
    public byte[] hello(@RequestParam String uid) {
        var ava = minioService.download(uid, StorageConsts.MINIO_AVATAR_BUCKET);
        if (Objects.isNull(ava)) {
            if (uid.length() % 2 == 0) {
                return minioService.download("default1", StorageConsts.MINIO_AVATAR_BUCKET);
            } else {
                return minioService.download("default2", StorageConsts.MINIO_AVATAR_BUCKET);
            }
        }
        return ava;
    }
}
