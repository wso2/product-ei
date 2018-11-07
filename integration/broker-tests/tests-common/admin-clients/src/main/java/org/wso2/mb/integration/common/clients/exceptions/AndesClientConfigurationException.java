/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.mb.integration.common.clients.exceptions;

/**
 * Exception class for andes client configuration.
 */
public class AndesClientConfigurationException extends Exception {

    /**
     * Error message for exception
     */
    public String errorMessage;

    /**
     * Creates Andes configuration exception
     */
    public AndesClientConfigurationException() {
    }

    /**
     * Creates Andes configuration exception with error message
     *
     * @param message Error message
     */
    public AndesClientConfigurationException(String message) {
        super(message);
        errorMessage = message;
    }

    /**
     * Creates Andes configuration exception with error message and throwable
     *
     * @param message Error message
     * @param cause   The throwable
     */
    public AndesClientConfigurationException(String message, Throwable cause) {
        super(message, cause);
        errorMessage = message;
    }

    /**
     * Creates Andes configuration exception with throwable.
     *
     * @param cause The throwable
     */
    public AndesClientConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return errorMessage;
    }
}