/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.mb.test.utils;

import org.testng.log4testng.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Server instance log reader
 */
public class ServerLogReader implements Runnable {

    private static Logger log = Logger.getLogger(ServerLogReader.class);

    private String streamType;
    private InputStream inputStream;
    private StringBuilder stringBuilder;

    private final String inputStreamType = "inputStream";
    private final String errorStreamType = "errorStream";
    private final Object lock = new Object();

    private volatile boolean running = true;
    private Thread thread;

    public ServerLogReader(String name, InputStream inputStream) {
        this.streamType = name;
        this.inputStream = inputStream;
        this.stringBuilder = new StringBuilder();
    }

    /**
     * Start server instance log reader thread.
     */
    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Stop server instance log reader thread.
     */
    public void stop() {
        running = false;
    }

    public void run() {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            inputStreamReader = new InputStreamReader(inputStream, Charset.defaultCharset());
            bufferedReader = new BufferedReader(inputStreamReader);

            while (running) {
                if (bufferedReader.ready()) {
                    String readLine = bufferedReader.readLine();
                    stringBuilder.setLength(0);
                    if (readLine == null) {
                        break;
                    }

                    if (inputStreamType.equals(streamType)) {
                        stringBuilder.append(readLine).append("\n");
                        log.info(readLine);
                    } else if (errorStreamType.equals(streamType)) {
                        stringBuilder.append(readLine).append("\n");
                        log.error(readLine);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Problem reading the [" + streamType + "] due to: " + e.getMessage(), e);
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStream.close();
                    inputStreamReader.close();
                } catch (IOException e) {
                    log.error("Error occurred while closing the server log stream: " + e.getMessage(), e);
                }
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log.error("Error occurred while closing the server log stream: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * output server log line as string
     * @return stringBuilder as string
     */
    public String getOutput() {
        synchronized (lock) {
            return stringBuilder.toString();
        }
    }


}
