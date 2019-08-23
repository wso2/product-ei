/*
*Copyright (c) 2005, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.esb.integration.common.utils;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Utils {

    private static Log log = LogFactory.getLog(Utils.class);

    public static OMElement getSimpleQuoteRequest(String symbol) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement omGetQuote = fac.createOMElement("getSimpleQuote", omNs);
        OMElement value1 = fac.createOMElement("symbol", omNs);

        value1.addChild(fac.createOMText(omGetQuote, symbol));
        omGetQuote.addChild(value1);

        return omGetQuote;
    }

    public static OMElement getCustomQuoteRequest(String symbol) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "ns");
        OMElement chkPrice = factory.createOMElement("CheckPriceRequest", ns);
        OMElement code = factory.createOMElement("Code", ns);
        chkPrice.addChild(code);
        code.setText(symbol);
        return chkPrice;
    }

    public static OMElement getStockQuoteRequest(String symbol) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);
        OMElement value1 = fac.createOMElement("request", omNs);
        OMElement value2 = fac.createOMElement("symbol", omNs);

        value2.addChild(fac.createOMText(value1, symbol));
        value1.addChild(value2);
        method.addChild(value1);

        return method;
    }

	public static OMElement getIncorrectRequest(String stringValue) {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://echo.services.core.carbon.wso2.org", "echo");
		OMElement method = fac.createOMElement("echoInt", omNs);
		OMElement value1 = fac.createOMElement("in", omNs);
		value1.setText(stringValue);
		method.addChild(value1);
		return method;
	}

    public static OMElement getCustomPayload(String symbol) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement payload = fac.createOMElement("getQuote", omNs);
        OMElement request = fac.createOMElement("request", omNs);
        OMElement code = fac.createOMElement("Code", omNs);
        code.setText(symbol);

        request.addChild(code);
        payload.addChild(request);
        return payload;
    }

    /**
     * method to kill existing servers which are bind to the given port
     *
     * @param port
     */
    public static void shutdownFailsafe(int port) throws IOException {
        try {
            Process p = Runtime.getRuntime().exec("lsof -Pi tcp:" + port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            reader.readLine();
            line = reader.readLine();
            if (line != null) {
                line = line.trim();
                String processId = line.split(" +")[1];
                if (processId != null) {
                    String killStr = "kill -9 " + processId;
                    Runtime.getRuntime().exec(killStr);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error killing the process which uses the port " + port, e);
        }
    }

    /**
     * Check if the given queue does not contain any messages
     *
     * @param queueName queue to be checked
     * @return true in queue is empty, false otherwise
     * @throws Exception if error while checking
     */
    public static boolean isQueueEmpty(String queueName) throws Exception {

        String poppedMessage;
        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(
                JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            consumer.connect(queueName);
            poppedMessage = consumer.popMessage();
        } finally {
            consumer.disconnect();
        }

        return poppedMessage == null;
    }

    /**
     * Get set of logs with expected string and given priority
     *
     * @param logViewerClient LogViewerClient object
     * @param priority        priority level
     * @param expected        expected string
     * @return set of logs with expected string and given priority
     * @throws RemoteException
     */
    public static ArrayList<LogEvent> getLogsWithExpectedValue(LogViewerClient logViewerClient, String priority,
                                                      String expected) throws RemoteException {
        LogEvent[] allLogs;
        allLogs = logViewerClient.getAllRemoteSystemLogs();
        ArrayList<LogEvent> systemLogs = new ArrayList<>();
        int i = 0;
        if (allLogs != null) {
            for (LogEvent logEvent : allLogs) {
                if (logEvent == null) {
                    continue;
                }
                if (logEvent.getPriority().equals(priority) && logEvent.getMessage().contains(expected)) {
                    systemLogs.add(logEvent);
                }
            }
        }
        return systemLogs;
    }

    /**
     * Check if the system log contains the expected string. The search will be done for maximum 10 seconds.
     *
     * @param logViewerClient log viewer used for test
     * @param expected        expected string
     * @return true if a match found, false otherwise
     * @throws RemoteException             due to a logviewer error
     */
    public static boolean assertIfSystemLogContains(LogViewerClient logViewerClient, String expected)
            throws RemoteException {
        boolean matchFound = false;
        long startTime = System.currentTimeMillis();
        LogEvent[] systemLogs;
        while (!matchFound && (System.currentTimeMillis() - startTime) < 10000) {
            matchFound = assertIfLogExists(logViewerClient, expected);
        }
        return matchFound;
    }

    private static boolean assertIfLogExists(LogViewerClient logViewerClient, String expected)
            throws RemoteException {

        LogEvent[] systemLogs;
        systemLogs = logViewerClient.getAllRemoteSystemLogs();
        boolean matchFound = false;
        if (systemLogs != null) {
            for (LogEvent logEvent : systemLogs) {
                if (logEvent == null) {
                    continue;
                }
                if (logEvent.getMessage().contains(expected)) {
                    matchFound = true;
                    break;
                }
            }
        }
        return matchFound;
    }

    /**
     * Check whether a log found with expected string of given priority
     *
     * @param logViewerClient LogViewerClient object
     * @param priority        priority level
     * @param expected        expected string
     * @return true if a log found with expected string of given priority, false otherwise
     * @throws RemoteException
     */
    private static boolean assertIfLogExistsWithGivenPriority(LogViewerClient logViewerClient, String priority, String expected)
            throws RemoteException {

        LogEvent[] systemLogs;
        systemLogs = logViewerClient.getAllRemoteSystemLogs();
        boolean matchFound = false;
        if (systemLogs != null) {
            for (LogEvent logEvent : systemLogs) {
                if (logEvent == null) {
                    continue;
                }
                if (logEvent.getPriority().equals(priority) && logEvent.getMessage().contains(expected)) {
                    matchFound = true;
                    break;
                }
            }
        }
        return matchFound;
    }

    /**
     * Check for the existence of the given log message. The polling will happen in one second intervals.
     *
     * @param logViewerClient log viewer used for test
     * @param expected        expected log string
     * @param timeout         max time to do polling in seconds
     * @return true if the log is found with given timeout, false otherwise
     * @throws InterruptedException        if interrupted while sleeping
     * @throws RemoteException             due to a logviewer error
     */
    public static boolean checkForLog(LogViewerClient logViewerClient, String expected, int timeout) throws
            InterruptedException, RemoteException {
        boolean logExists = false;
        for (int i = 0; i < timeout; i++) {
            TimeUnit.SECONDS.sleep(1);
            if (assertIfLogExists(logViewerClient, expected)) {
                logExists = true;
                break;
            }
        }
        return logExists;
    }

    /**
     * Check for the existence of a given log message of given priority within the given timeout
     *
     * @param logViewerClient LogViewerClient object
     * @param priority        priority level
     * @param expected        expected string to search in logs
     * @param timeout         timeout value in seconds
     * @return true if a log found with the given priority and content within the given timeout, false otherwise
     * @throws InterruptedException
     * @throws RemoteException
     */
    public static boolean checkForLogsWithPriority(LogViewerClient logViewerClient, String priority,  String expected, int timeout)
            throws InterruptedException, RemoteException {
        boolean logExists = false;
        for (int i = 0; i < timeout; i++) {
            TimeUnit.SECONDS.sleep(1);
            if (assertIfLogExistsWithGivenPriority(logViewerClient, priority, expected)) {
                logExists = true;
                break;
            }
        }
        return logExists;
    }

    /**
     * Wait for expected message count found in the message store until a defined timeout
     * @param messageStoreName Message store name
     * @param expectedCount    Expected message count
     * @param timeout          Timeout to wait in Milliseconds
     * @return true if the expected message count found, false otherwise
     */
    public static boolean waitForMessageCount(MessageStoreAdminClient messageStoreAdminClient, String messageStoreName, int expectedCount, long timeout) throws InterruptedException, RemoteException {
        long elapsedTime = 0;
        boolean messageCountFound = false;
        while(elapsedTime < timeout && !messageCountFound) {
            Thread.sleep(500);
            messageCountFound = messageStoreAdminClient.getMessageCount(messageStoreName) == expectedCount;
            elapsedTime += 500;
        }
        return messageCountFound;
    }

    /**
     * Util function to check whether specified car file is deployed
     *
     * @param carFileName - Name of the car file to deploy
     * @param applicationAdminClient - Application admin client
     * @param timeout - timeout for car deployment
     * @return true if the car file deployed successfully else, false
     */
    public static boolean isCarFileDeployed(String carFileName, ApplicationAdminClient applicationAdminClient,
            int timeout) throws Exception {

        log.info("waiting " + timeout + " millis for car deployment " + carFileName);
        boolean isCarFileDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;

        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < timeout) {
            String[] applicationList = applicationAdminClient.listAllApplications();
            if (applicationList != null) {
                if (ArrayUtils.contains(applicationList, carFileName)) {
                    isCarFileDeployed = true;
                    log.info("car file deployed in " + time + " mills");
                    return isCarFileDeployed;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ignore
            }

        }
        return isCarFileDeployed;
    }
}