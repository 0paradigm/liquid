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

package io.zeroparadigm.liquid.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.zeroparadigm.liquid.dao.annotation.Unique;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Demo.
 *
 * @author hezean
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@TableName("t_ds_user")
public class User implements Serializable {

    /** system generated primary key. */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** account name aka. username. */
    @Unique
    @TableField("login")
    private String login;

    /** nickname, default to none, and display large @code{User.login} in profile page. */
    @TableField("name")
    private String name;

    /** only one, need to check regex both in frontend and backend. */
    @Unique
    @TableField("email")
    private String email;

    @TableField("twitter_username")
    private String twitterUsername;

    @TableField("bio")
    private String biography;

    @TableField("location")
    private String location;

    /** supports '@apache' representations, need to split and find fuzzily. */
    @TableField("company")
    private String company;

    /** System.currentTimeMillis() */
    @TableField("created_at")
    private Long createdAt;

    /** System.currentTimeMillis() */
    @TableField("updated_at")
    private Long updatedAt;

    @TableField(exist = false)
    private Integer following;

    @TableField(exist = false)
    private Integer followers;

    @TableField(exist = false)
    private Integer publicRepos;

    @TableField("password")
    private String password;
}
