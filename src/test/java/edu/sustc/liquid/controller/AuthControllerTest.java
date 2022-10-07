/*******************************************************************************
 *    $$\      $$\                     $$\       $$\
 *    $$ |     \__|                    \__|      $$ |
 *    $$ |     $$\  $$$$$$\  $$\   $$\ $$\  $$$$$$$ |
 *    $$ |     $$ |$$  __$$\ $$ |  $$ |$$ |$$  __$$ |
 *    $$$$$$$$\$$ |\$$$$$$$ |\$$$$$$  |$$ |\$$$$$$$ |
 *    \________\__| \____$$ | \______/ \__| \_______|
 *                       $$ |
 *                       \__|  :: Liquid ::  (c) 2022
 *
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
 *******************************************************************************/

package edu.sustc.liquid.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sustc.liquid.base.constants.ServiceStatus;
import edu.sustc.liquid.dao.entity.User;
import edu.sustc.liquid.dao.mapper.UserMapper;
import edu.sustc.liquid.dto.LoginCredentials;
import edu.sustc.liquid.dto.Result;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    private static final ObjectMapper mapper = new ObjectMapper();

    @MockBean private UserMapper userMapper;

    private static final User testUser1__liquid_sa =
            new User(
                    1,
                    "liquid_sa",
                    "Liquid",
                    "test@liquid.com",
                    null,
                    null,
                    "CA",
                    "@liquid",
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    10,
                    20,
                    4,
                    "liquidSaPassword");

    @Test
    void testPasswordLoginSuccessfully() throws Exception {
        Mockito.when(userMapper.findByNameOrMail("test@liquid.com"))
                .thenReturn(testUser1__liquid_sa);

        String cred =
                mapper.writeValueAsString(
                        new LoginCredentials().password("test@liquid.com", "liquidSaPassword"));
        MvcResult res =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(cred))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn();

        assertThat(mapper.readValue(res.getResponse().getContentAsString(), Result.class))
                .extracting("code")
                .isEqualTo(ServiceStatus.SUCCESS.getCode());
        assertThat(mapper.readValue(res.getResponse().getContentAsString(), Result.class))
                .extracting("data")
                .extracting("token")
                .isNotNull();
    }

    @Test
    void testPasswordLoginWrongPassword() throws Exception {
        Mockito.when(userMapper.findByNameOrMail("test@liquid.com"))
                .thenReturn(testUser1__liquid_sa);

        String cred =
                mapper.writeValueAsString(
                        new LoginCredentials().password("test@liquid.com", "pwd"));
        MvcResult res =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(cred))
                        .andExpect(status().isNotAcceptable())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn();

        assertThat(mapper.readValue(res.getResponse().getContentAsString(), Result.class))
                .extracting("code")
                .isEqualTo(ServiceStatus.INCORRECT_CREDENTIAL.getCode());
    }

    @Test
    void testPasswordLoginAccountNotFound() throws Exception {
        Mockito.when(userMapper.findByNameOrMail("test@liquid.com"))
                .thenReturn(testUser1__liquid_sa);

        String cred =
                mapper.writeValueAsString(
                        new LoginCredentials().password("test11@liquid.com", "pwd"));
        MvcResult res =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(cred))
                        .andExpect(status().isNotAcceptable())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn();

        assertThat(mapper.readValue(res.getResponse().getContentAsString(), Result.class))
                .extracting("code")
                .isEqualTo(ServiceStatus.ACCOUNT_NOT_FOUND.getCode());
    }

    @Test
    void testPasswordLoginInvalidPassword() throws Exception {
        Mockito.when(userMapper.findByNameOrMail("test@liquid.com"))
                .thenReturn(testUser1__liquid_sa);

        String cred = mapper.writeValueAsString(new LoginCredentials().password(null, "p"));
        MvcResult res =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(cred))
                        .andExpect(status().isNotAcceptable())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn();

        assertThat(mapper.readValue(res.getResponse().getContentAsString(), Result.class))
                .extracting("code")
                .isEqualTo(ServiceStatus.MISSING_CREDENTIAL.getCode());
    }

    @Test
    void testAccessingPublicResourcesWithoutLogin() throws Exception {
        MvcResult res =
                mockMvc.perform(post("/pub/test1nf"))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn();

        assertThat(mapper.readValue(res.getResponse().getContentAsString(), Result.class))
                .extracting("code")
                .isEqualTo(ServiceStatus.NOT_FOUND.getCode());
    }

    @Test
    void testLogoutWithoutToken() throws Exception {
        MvcResult res =
                mockMvc.perform(post("/auth/logout"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn();

        assertThat(mapper.readValue(res.getResponse().getContentAsString(), Result.class))
                .extracting("code")
                .isEqualTo(ServiceStatus.SUCCESS.getCode());
    }

    @Test
    void testLogout() throws Exception {
        Mockito.when(userMapper.findByNameOrMail("test@liquid.com"))
                .thenReturn(testUser1__liquid_sa);

        String cred =
                mapper.writeValueAsString(
                        new LoginCredentials().password("test@liquid.com", "liquidSaPassword"));

        mockMvc.perform(post("/api/test1nf")).andExpect(status().isForbidden());

        MvcResult res =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(cred))
                        .andExpect(status().isOk())
                        .andReturn();

        String token =
                ((Map<String, String>)
                                mapper.readValue(
                                                res.getResponse().getContentAsString(),
                                                Result.class)
                                        .getData())
                        .get("token");
        Cookie cookie = new Cookie("JSESSIONID", token);

        mockMvc.perform(post("/api/test1nf").cookie(cookie)).andExpect(status().isNotFound());
        mockMvc.perform(post("/auth/logout").cookie(cookie)).andExpect(status().isOk());
        mockMvc.perform(post("/api/test1nf").cookie(cookie)).andExpect(status().isForbidden());
    }
}
