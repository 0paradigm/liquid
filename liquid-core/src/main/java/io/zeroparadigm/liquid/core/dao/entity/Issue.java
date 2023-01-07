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

package io.zeroparadigm.liquid.core.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serializable;

/**
 * Issue entity.
 *
 * @author matthewleng
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@TableName("t_ds_issue")
public class Issue implements Serializable {

    /**
     * system generated primary key.
     */
    @TableId(value = "id", type = IdType.AUTO)
    @NonNull
    private Integer id;

    /**
     * display id
     */
    @TableField("display_id")
    @NonNull
    private Integer displayId;

    /**
     * repo id, fk.
     */
    @TableField("repo")
    @NonNull
    private Integer repo;

    /**
     * issue opener.
     */
    @TableField("opener")
    @NonNull
    private Integer opener;

    /**
     * issue title.
     */
    @TableField("title")
    @NonNull
    private String title;

    /**
     * issue created time.
     */
    @TableField("created_at")
    @NonNull
    private Long createdAt;

    /**
     * issue closed
     */
    @TableField("closed")
    @NonNull
    private Boolean closed;
}
