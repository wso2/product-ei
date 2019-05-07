/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ei.migration.migrate;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.mediation.security.vault.MediationSecurityAdminService;

/**
 *  Encrypt passwords.
 */
public class PasswordEncryptor {

    /**
     * Encrypt plain text
     *
     * @param plainText plain text string
     * @return encrypted string
     */
    public String encrypt(String plainText) {
        MediationSecurityAdminService mediationSecurityAdminService = new MediationSecurityAdminService();
        try {
            return mediationSecurityAdminService.doEncrypt(plainText);
        } catch (AxisFault a) {
            throw new MigrationClientException("Error occurred while encrypting password", a);
        }
    }
}
