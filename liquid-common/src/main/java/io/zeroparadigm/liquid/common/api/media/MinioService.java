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

package io.zeroparadigm.liquid.common.api.media;

import java.io.InputStream;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Minio utilities.
 *
 * @author hezean
 */
@Service
public interface MinioService {

    void createBucketIfNotExists(String bucketName);

    boolean removeBucket(String bucketName);

    @Nullable
    String upload(MultipartFile file, @Nullable String specName, String bucketName);

    @Nullable
    String upload(InputStream is, String specName, String bucketName);

    @Nullable
    byte[] download(String filename, String bucketName);

    ResponseEntity<byte[]> httpDownload(String filename, String bucketName);

    boolean removeObject(String filename, String bucketName);
}
