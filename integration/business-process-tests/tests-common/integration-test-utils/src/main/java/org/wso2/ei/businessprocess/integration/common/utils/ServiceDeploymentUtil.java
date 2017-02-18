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

package org.wso2.ei.businessprocess.integration.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.ei.businessprocess.integration.common.clients.ServiceAdminClient;

import java.rmi.RemoteException;
import java.util.Calendar;

public class ServiceDeploymentUtil {
    private static int SERVICE_DEPLOYMENT_DELAY = 90 * 1000;
    private static Log log = LogFactory.getLog(ServiceDeploymentUtil.class);


    public static boolean isServiceDeployed(String backEndUrl, String sessionCookie,
                                            String serviceName)
            throws RemoteException {
        log.info("waiting " + SERVICE_DEPLOYMENT_DELAY + " millis for Service deployment " + serviceName);
        boolean isServiceDeployed = false;
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < SERVICE_DEPLOYMENT_DELAY) {
            if (adminServiceService.isServiceExists(serviceName)) {
                isServiceDeployed = true;
                log.info(serviceName + " Service Deployed in " + time + " millis");
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        return isServiceDeployed;
    }

    public static boolean isServiceDeleted(String backEndUrl, String sessionCookie, String serviceName)
            throws RemoteException {
        log.info("waiting " + SERVICE_DEPLOYMENT_DELAY + " millis for service un-deployment");
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        boolean isServiceDeleted = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < SERVICE_DEPLOYMENT_DELAY) {
            if (!adminServiceService.isServiceExists(serviceName)) {
                isServiceDeleted = true;
                log.info(serviceName + " Service un-deployed in " + time + " millis");
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
        }
        return isServiceDeleted;
    }

    public static boolean isServiceExist(String backEndUrl, String sessionCookie, String serviceName)
            throws RemoteException {
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        return adminServiceService.isServiceExists(serviceName);
    }

    public static boolean isFaultyService(String backEndUrl, String sessionCookie, String serviceName)
            throws RemoteException {
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        return adminServiceService.isServiceFaulty(serviceName);
    }

    public static boolean isServiceUnDeployed(String backEndUrl, String sessionCookie,
                                              String serviceName,
                                              int deploymentDelay)
            throws RemoteException {
        log.info("waiting " + deploymentDelay + " millis for Service undeployment");
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        boolean isServiceDeleted = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < deploymentDelay) {
            if (!adminServiceService.isServiceExists(serviceName)) {
                isServiceDeleted = true;
                log.info(serviceName + " Service undeployed in " + time + " millis");
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        return isServiceDeleted;
    }


}
