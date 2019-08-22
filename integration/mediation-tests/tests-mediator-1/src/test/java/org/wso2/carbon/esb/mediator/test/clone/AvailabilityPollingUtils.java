/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.clone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;


public class AvailabilityPollingUtils {

    private static Log log = LogFactory.getLog(AvailabilityPollingUtils.class);

    /**
     * Check if message received from server logs.
     *
     * @param logViewerClient logViewerClient instance of LogViewerClient class
     * @return Whether REQUEST PARAM VALUE is contained in the log message or not
     */
    public static Callable<Boolean> isMessageRecived(LogViewerClient logViewerClient) {
        final LogViewerClient logViewer = logViewerClient;
        return new Callable<Boolean>() {
            @Override
            public Boolean call() {
                LogEvent[] getLogsInfo;
                boolean assertValue = false;
                try {
                    getLogsInfo = logViewer.getAllRemoteSystemLogs();
                    for (LogEvent event : getLogsInfo) {
                        log.info("Read Log: " + event.getMessage());
                        if (event.getMessage().contains("REQUEST PARAM VALUE")) {
                            assertValue = true;
                            log.info("Test success for CloneFunctionalContextTestCase");
                        }
                    }
                } catch (RemoteException e) {
                    log.error("Error while reading log files ", e);
                }
                return assertValue;
            }
        };
    }
}
