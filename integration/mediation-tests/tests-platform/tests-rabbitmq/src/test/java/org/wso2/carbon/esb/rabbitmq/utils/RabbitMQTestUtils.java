/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.rabbitmq.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.esb.integration.common.utils.exception.RabbitMQTransportException;
import org.wso2.esb.integration.common.utils.servers.RabbitMQServer;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RabbitMQTestUtils {

    private static final Log log = LogFactory.getLog(RabbitMQTestUtils.class);

    private static final String RABBITMQ_HOME_XPATH = "//rabbitmq/rabbitmqhome";

    public static RabbitMQServer getRabbitMQServerInstance() throws XPathExpressionException, RabbitMQTransportException {
        AutomationContext automationContext = new AutomationContext();
        String rabbitMQHome = automationContext.getConfigurationValue(RABBITMQ_HOME_XPATH);
        return new RabbitMQServer(rabbitMQHome);
    }

    /**
     * This is a helper method to copy config files before starting rabbitMQ server
     *
     * @param source
     * @param destination
     * @throws java.io.IOException
     */
    public static void copyConfig(File source, File destination) throws IOException {
        log.info("Copying config file - " + source.getPath() + " to location " + destination.getPath());
        InputStream inStream = null;
        OutputStream outStream = null;
        if (destination.exists() && !destination.isDirectory()) {
            if (destination.exists() && !destination.isDirectory()) {
                String backupPath = destination.getPath() + ".backup";
                if (destination.renameTo(new File(backupPath))) {
                    log.info("Original file backed up successfully, backupFile - " + backupPath);
                } else {
                    log.error("Failed to back up original config file - " + destination.getPath());
                    throw new IOException("Failed to back up original config file - " + destination.getPath());
                }
            }
        }
        inStream = new FileInputStream(source);
        outStream = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int length;
        //copy the file content
        while ((length = inStream.read(buffer)) > 0) {
            outStream.write(buffer, 0, length);
        }
        inStream.close();
        outStream.close();
        log.info("Original config file backed up (backupName - " + destination.getPath() + ".backup) and new file copied successfully");
    }

    /**
     * This is the helper method to revert the config files to the backups and clear copied files
     *
     * @param configFile
     * @throws IOException
     */
    public static void restoreConfigs(File configFile) throws IOException{
        log.info("Restoring config file to the original, config file - " + configFile.getPath());
        File backupFile = new File(configFile.getPath() + ".backup");

        if (backupFile.exists() && !backupFile.isDirectory()) {
            if (configFile.exists() && !configFile.isDirectory()) {
                configFile.delete();
                log.info("Copied config file deleted, file - " + configFile.getPath());
            }
            if (backupFile.renameTo(new File(backupFile.getPath().substring(0, backupFile.getPath().length() - 7)))) {
                log.info("Config file restored successfully, file - " + backupFile.getPath());
            } else {
                log.error("Failed to restore backup file - " + configFile.getPath() + ".backup");
                throw new IOException("Failed to restore backup file - " + configFile.getPath() + ".backup");
            }
        } else {
            log.info("Backup file does not exist for config - " + configFile.getPath());
        }
    }

    /**
     * Helper method to get rabbitMQ home path
     *
     * @return rabbitMQHome
     * @throws XPathExpressionException
     * @throws RabbitMQTransportException
     */
    public static String getRabbitmqHomePath() throws XPathExpressionException, RabbitMQTransportException {
        AutomationContext automationContext = new AutomationContext();
        String rabbitMQHome = automationContext.getConfigurationValue(RABBITMQ_HOME_XPATH);
        return rabbitMQHome;
    }
}
