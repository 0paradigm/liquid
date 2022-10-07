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

package edu.sustc.liquid.service;

import edu.sustc.liquid.dto.LoginCredentials;
import edu.sustc.liquid.exceptions.InvalidCredentialFieldException;
import org.apache.shiro.ShiroException;
import org.apache.shiro.subject.Subject;

/**
 * Authorization service.
 *
 * @author hezean
 */
public interface AuthService {

    /**
     * Logs in using shiro.
     *
     * @param credentials includes login method and related credentials
     * @return subject if successfully logged in
     * @throws ShiroException if not logged in
     * @throws InvalidCredentialFieldException if the credential is invalid
     */
    Subject login(LoginCredentials credentials)
            throws ShiroException, InvalidCredentialFieldException;
}
