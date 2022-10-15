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

package io.zeroparadigm.liquid.service.impl;

import io.zeroparadigm.liquid.service.MinioService;
import io.zeroparadigm.liquid.utils.SnowFlakeUtils;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    @Autowired
    MinioClient minioClient;

    @Autowired
    SnowFlakeUtils snowFlake;

    @Override
    public void createBucketIfNotExists(String bucketName) {
        try {
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                return;
            }
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("Cannot check/create bucket '{}'", bucketName, e);
        }
    }

    @Override
    public boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            return true;
        } catch (Exception e) {
            log.error("Cannot remove bucket '{}'", bucketName, e);
            return false;
        }
    }

    @Nullable
    @Override
    public String upload(MultipartFile file, @Nullable String specName, String bucketName) {
        String filename = specName;
        if (!StringUtils.hasText(filename)) {
            String[] fileExt = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
            StringBuilder nameBuilder = new StringBuilder();
            if (fileExt.length == 1) {
                nameBuilder.append(fileExt[0]).append('_').append(snowFlake.getNextId());
            } else {
                for (int i = 0; i < fileExt.length - 1; i++) {
                    nameBuilder.append(fileExt[i]).append('.');
                }
                nameBuilder.deleteCharAt(nameBuilder.length() - 1);
                nameBuilder
                        .append('_')
                        .append(snowFlake.getNextId())
                        .append('.')
                        .append(fileExt[fileExt.length - 1]);
            }
            filename = nameBuilder.toString();
        }
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(filename).stream(
                            is, is.available(), -1)
                            .contentType(file.getContentType())
                            .build());
            return filename;
        } catch (Exception e) {
            log.error(
                    "Cannot upload file (origin_name: '{}', identifier: '{}', bucket: '{}')",
                    file.getOriginalFilename(),
                    filename,
                    bucketName,
                    e);
            return null;
        }
    }

    @Nullable
    @Override
    public byte[] download(String filename, String bucketName) {
        try (
                InputStream is =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(filename)
                                        .build());
                ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            IOUtils.copy(is, os);
            return os.toByteArray();
        } catch (Exception e) {
            log.error(
                    "Cannot download file (identifier: '{}', bucket: '{}')",
                    filename,
                    bucketName,
                    e);
            return null;
        }
    }

    @Override
    public ResponseEntity<byte[]> httpDownload(String filename, String bucketName) {
        byte[] bytes = download(filename, bucketName);
        if (Objects.isNull(bytes)) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(bytes.length);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setAccessControlExposeHeaders(List.of("*"));
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @Override
    public boolean removeObject(String filename, String bucketName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(filename).build());
            return true;
        } catch (Exception e) {
            log.error(
                    "Cannot delete file (identifier: '{}', bucket: '{}')", filename, bucketName, e);
            return false;
        }
    }
}
