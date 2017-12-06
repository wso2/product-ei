/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.test.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Utility class for Tests.
 */
public class TestUtils {

    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    /**
     * Starts the EI server with given balx file.
     *
     * @param balxFile path to balx file is relative to the project root
     * @return true if the start stared successfully, false otherwise
     */
    public static boolean startServer(String balxFile) {
        String eiHome = "target/test-integration/wso2ei-" + System.getProperty("product.ei.version");
        boolean isStarted = false;

        String pathRelatedToTestModule = ".." + File.separator + ".." + File.separator + balxFile;
        File relatedPathFile = new File(pathRelatedToTestModule);
        File parentDirectory = new File(relatedPathFile.getParent());
        File fullPath = new File(parentDirectory, pathRelatedToTestModule);

        try {
            String canonicalPath = fullPath.getCanonicalPath();
            String[] startCmd;
            if (System.getProperty("os.name").toLowerCase(Locale.getDefault()).contains("windows")) {
                startCmd = new String[] { "cmd.exe", "/c", "integrator.bat", canonicalPath };
            } else {
                startCmd = new String[] { "bash", "integrator.sh", canonicalPath, "&" };
            }
            ProcessBuilder processBuilder = new ProcessBuilder(startCmd);
            processBuilder.directory(new File(eiHome + File.separator + "bin"));
            processBuilder.start();

            Socket socket;
            int safeCount = 0;
            while (!isStarted && safeCount < 5) {
                try {
                    log.debug("Waiting till the server starts properly [" + safeCount + "]");
                    socket = new Socket(InetAddress.getLocalHost(), 9090);
                    isStarted = socket.isConnected();
                } catch (ConnectException ignore) {
                    log.debug("Sever not started. Waiting for sometime");
                    Thread.sleep(2000);
                    safeCount++;
                }
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error while stating the sever ", e);
        }
        return isStarted;
    }

    /**
     * Stops the EI server.
     *
     * @return true if stopped successfully, false otherwise
     */
    public static boolean stopServer() {
        BufferedReader bufferedReader = null;
        try {
            if (System.getProperty("os.name").toLowerCase(Locale.getDefault()).contains("windows")) {
                //todo later

            } else {
                String[] netstatCmd = new String[] { "netstat", "-lntp" };
                ProcessBuilder processBuilder = new ProcessBuilder(netstatCmd);

                Process process = processBuilder.start();
                bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));
                String line;
                String pidLine = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains(":9090")) {
                        pidLine = line;
                        break;
                    }
                }
                process.waitFor();

                if (pidLine != null) {
                    String pidStr = pidLine.split("LISTEN")[1].split("/java")[0].trim();
                    String[] killCmd = new String[] { "kill", "-9", pidStr };
                    ProcessBuilder killPB = new ProcessBuilder(killCmd);
                    killPB.start();
                } else {
                    log.warn("EI server is not running");
                    return false;
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error stopping the server ", e);
            return false;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log.debug("Error while closing buffered reader ", e);
                }
            }
        }
        return true;
    }

}
