/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.esb.integration.common.clients.logging;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.esb.integration.common.clients.client.utils.AuthenticateStub;

import java.rmi.RemoteException;

/**
 * This class can be used as the client to obtaining Log4J information about the system and also used for managing the
 * system Log4J configuration.
 */
public class LoggingAdminClient {
    private static final Log log = LogFactory.getLog(LoggingAdminClient.class);
    private final String serviceName = "LoggingAdmin";
    private String endpoint = null;
    // private LoggingAdminStub loggingAdminStub;

    public enum LogLevel {OFF, TRACE, DEBUG, INFO, WARN, ERROR, FATAL}

    public LoggingAdminClient(String backEndUrl, String sessionCookie) throws AxisFault {
        this.endpoint = backEndUrl + serviceName;
        // todo add proper handler to handle loggers
        // loggingAdminStub = new LoggingAdminStub(this.endpoint);
        // AuthenticateStub.authenticateStub(sessionCookie, loggingAdminStub);
    }

    /**
     * Updates the loggerdata in the {@link LoggingAdminStub}.
     *
     * @param loggerName the logger name
     * @param logLevel   the log level
     * @param additivity whether additivity is true or false
     * @param persist    whether to persist or not
     * @return whether the update was successful or not
     * @throws Exception exception from the {@link LoggingAdminStub}
     */
    public boolean updateLoggerData(String loggerName, String logLevel,
                                    boolean additivity, boolean persist) throws Exception {

        // loggingAdminStub.updateLoggerData(loggerName, logLevel, additivity, persist);
        return true;
    }
}
