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

package io.zeroparadigm.liquidms.controller;

import io.zeroparadigm.liquidms.service.MinioService;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Avatar interacted with minio.
 *
 * @author hezean
 */
@RestController
@Slf4j
public class AvatarController {

    @Autowired
    private MinioService minioService;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @PostConstruct
    private void createBucket() {
        log.info("Creating bucket '{}'", bucketName);
        minioService.createBucketIfNotExists(bucketName);
    }

    @PostMapping("/upload")
    public String upload(MultipartFile file) {
        return minioService.upload(file, file.getName(), bucketName);
    }
}
