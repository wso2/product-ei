/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.micro.integrator.security.callback;


/*
This interface is defined to provide plain text password for username token scenarios where user sends the digested
password. In default callback handler class uses this class. Usually, this interface is implemented by custom userstore managers.
 */
public interface UserCredentialRetriever {
    /**
     * Provide the password based on user store implementation.
     *
     * @param username - Domain less username; eg; fooUser but not Domain/fooUser
     * @return - plain text password of username
     * @throws Exception - throws if failed to provide plain text password.
     */
    String getPassword(String username) throws Exception;
}
