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

package org.wso2.esb.integration.common.utils.servers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.esb.integration.common.utils.exception.RabbitMQTransportException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * RabbitMQ server management class
 */
public class RabbitMQServer {
    private static final Log log = LogFactory.getLog(org.wso2.esb.integration.common.utils.servers.RabbitMQServer.class);
    private boolean started;
    private File rabbitMQHome;

    public RabbitMQServer(String rabbitmqHome) throws RabbitMQTransportException {
        rabbitMQHome = new File(rabbitmqHome);
        if (rabbitMQHome.exists() && isValidRabbitMQHome()) {
            log.info("Using the RabbitMQ_HOME : " + rabbitMQHome.getAbsolutePath());
        } else {
            rabbitMQHome = null;
            log.error("Invalid RabbitMQ_HOME. RabbitMQ Broker will not connect as expected");
            throw new RabbitMQTransportException("Invalid RabbitMQ_HOME. RabbitMQ Broker will not connect as expected");
        }
    }

    private boolean isValidRabbitMQHome() {
        boolean rabbitmqctlFound = false;
        boolean rabbitmqserverFound = false;
        File[] files = rabbitMQHome.listFiles();
        if (files == null) {
            return false;
        }
        for (File f : files) {
            if (f.getName().contains("rabbitmqctl")) {
                rabbitmqctlFound = true;
            } else if (f.getName().contains("rabbitmq-server")) {
                rabbitmqserverFound = true;
            }
        }
        return (rabbitmqctlFound && rabbitmqserverFound);
    }

    public void start() {
        log.info("Starting RabbitMQ Broker");
        execute("sh rabbitmq-server -detached");
        started = true;

    }

    public void stop() {
        log.info("Stopping RabbitMQ Broker");
        execute("sh rabbitmqctl stop");
        started = false;

    }

    public void initialize() {
        log.info("Initializing RabbitMQ Broker");
        execute("sh rabbitmqctl stop_app");
        execute("sh rabbitmqctl reset");
        execute("sh rabbitmqctl start_app");

    }

    public boolean isStarted() {
        return started;
    }

    /**
     * Check whether rabbitMQ server started, this method will have a timeout, so if server won't get started within
     * the given timeout, this will return false
     *
     * @param timeout in seconds
     * @return
     */
    public boolean isRabbitMQStarted(long timeout) {
        for (int i = 1; i * 5 < timeout; i++) {
            Reader reader = null;
            Writer writer = null;
            InputStream instream = null;
            try {
                Process process = Runtime.getRuntime().exec("sh rabbitmqctl status", null, rabbitMQHome);
                instream = process.getInputStream();
                writer = new StringWriter();
                char[] buffer = new char[1024];
                reader = new BufferedReader(new InputStreamReader(instream));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
                reader.close();
                instream.close();
                String message = writer.toString();
                log.info(message);
                if (message.contains("{pid,")) {
                    log.info("RabbitMQ server is running");
                    return true;
                }
                Thread.sleep(300); //wait 5 seconds before trying again
            } catch (IOException e) {
                log.error("Error getting rabbitmq server status - " + e.getMessage(), e);
                return false;
            } catch (InterruptedException e) {
                log.error("Error waiting - " + e.getMessage(), e);
                return false;
            } finally {
                try {
                    reader.close();
                    instream.close();
                    writer.close();
                } catch (IOException e) {
                    log.error("Error closing streams - " + e.getMessage(), e);
                }
            }
        }
        return false;
    }

    private void execute(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command, null, rabbitMQHome);
            InputStream instream = process.getInputStream();
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            Reader reader = new BufferedReader(new InputStreamReader(instream));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            reader.close();
            instream.close();
            log.info(writer.toString());
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
