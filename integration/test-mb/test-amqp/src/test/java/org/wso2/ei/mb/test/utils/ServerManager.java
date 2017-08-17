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

import org.apache.commons.io.FilenameUtils;
import org.testng.log4testng.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;

/**
 * Server manager for integration test.
 */
public class ServerManager {

    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Logger log = Logger.getLogger(ServerManager.class);

    /**
     * Broker startup script name.
     */
    private static final String brokerStartupScriptName = "broker";

    private String carbonHome;
    private Process process;
    private ServerLogReader inputStreamHandler;
    private ServerLogReader errorStreamHandler;

    /**
     * Setup server home by extracting the distribution on temporary directory.
     *
     * @param serverZipFilePath distribution zip file path
     * @return temporary carbon home path after extraction
     * @throws IOException
     */
    public synchronized String setupServerHome(String serverZipFilePath) throws IOException {

        String fileSeparator = File.separator.equals("\\") ? "\\" : "/";
        if (fileSeparator.equals("\\")) {
            serverZipFilePath = serverZipFilePath.replace("/", "\\");
        }
        int indexOfZip = serverZipFilePath.lastIndexOf(".zip");

        String extractedCarbonDir = serverZipFilePath.substring(serverZipFilePath.lastIndexOf(fileSeparator) + 1,
                indexOfZip);
        FileManipulator.deleteDirectory(new File(extractedCarbonDir));
        String extractDir = "carbontmp" + System.currentTimeMillis();
        String baseDir = System.getProperty("basedir", ".") + File.separator + "target";
        log.info("Extracting carbon zip file.. ");
        (new ArchiveExtractor()).extractFile(serverZipFilePath, baseDir + File.separator + extractDir);
        this.carbonHome = (new File(baseDir)).
                getAbsolutePath() + File.separator + extractDir + File.separator + extractedCarbonDir;

        return carbonHome;
    }

    /**
     * Start server instance
     *
     * @param carbonHomePath carbon home path
     * @throws IOException
     */
    public synchronized void startServer(String carbonHomePath) throws IOException {

        File carbonHomeDirectory = new File(carbonHomePath);
        String scriptName = getStartupScriptFileName(carbonHomePath);
        String[] cmdArray;

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows")) {
            carbonHomeDirectory = new File(carbonHomePath + File.separator + "bin");
            cmdArray = new String[]{"cmd.exe", "/c", scriptName + ".bat"};
            process = Runtime.getRuntime().exec(cmdArray, (String[]) null, carbonHomeDirectory);
        } else {
            cmdArray = new String[]{"sh", "bin/" + scriptName + ".sh"};
            process = Runtime.getRuntime().exec(cmdArray, (String[]) null, carbonHomeDirectory);
        }

        //Adding shutdown hook to capture process termination
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    stopServer();
                } catch (IOException e1) {
                    log.error("Server did not shut down properly", e1);
                }
            }
        });

        errorStreamHandler = new ServerLogReader("errorStream", process.getErrorStream());
        inputStreamHandler = new ServerLogReader("inputStream", process.getInputStream());
        inputStreamHandler.start();
        errorStreamHandler.start();

        long startupWaitTimeOut = 60000L;
        long time = System.currentTimeMillis() + startupWaitTimeOut;

        while (true) {
            if (this.inputStreamHandler.getOutput().contains("WSO2 Message Broker is started.")) {
                log.info("Server started successfully.");
                break;
            } else if (System.currentTimeMillis() >= time) {
                log.error("Server did not started properly within " + startupWaitTimeOut + " milliseconds.");
                break;
            }
        }
    }

    /**
     * Stop server instance
     *
     * @throws IOException
     */
    public synchronized void stopServer() throws IOException {

        if (process != null) {
            long endTimeMillis = System.currentTimeMillis() + 10000;

            while (process.isAlive()) {
                process.destroy();
                if (System.currentTimeMillis() > endTimeMillis) {
                    process.destroyForcibly();
                    log.info("process destroyed forcibly");
                    break;
                }
            }

            String catPidCommand = "cat " + carbonHome + File.separator + "carbon.pid";
            String pid = executeCommand(catPidCommand);

            log.info("Server instance pid : " + pid);
            String killCommand = "kill -9 " + pid;
            executeCommand(killCommand);

            log.info("Destroyed server instance with command  : " + killCommand);

            process.destroyForcibly();

            process = null;
            log.info("Server stopped successfully...");
        }

        inputStreamHandler.stop();
        errorStreamHandler.stop();
    }

    /**
     * Return pid of server instance.
     *
     * @return pid of running server instance as in carbon.pid
     * @throws java.io.IOException
     */
    private String executeCommand(String commandString) throws java.io.IOException {
        String commandOutput = "";
        Process process = Runtime.getRuntime().exec(commandString);
        InputStream inputStream = process.getInputStream();
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        if (scanner.hasNext()) {
            commandOutput = scanner.next();
        }
        return commandOutput;
    }

    /**
     * This method will verify if required script exist in specified carbon home directory
     * under "bin" sub directory.
     *
     * @param carbonHome Carbon home directory file path.
     * @return server startup script name.
     * @throws FileNotFoundException
     */
    private String getStartupScriptFileName(String carbonHome) throws FileNotFoundException {

        String serverStartupScriptFileName = System.getProperty("server.startup.script.file.name");

        if (serverStartupScriptFileName.isEmpty()) {
            serverStartupScriptFileName = brokerStartupScriptName;
        }

        File[] scriptFiles = (new File(carbonHome + File.separator + "bin")).listFiles();
        String scriptName = null;
        if (scriptFiles == null) {
            throw new FileNotFoundException("Startup script not found at " + carbonHome + File.separator + "bin");
        } else {
            for (int i = 0; i < scriptFiles.length; ++i) {
                File scriptFileName = scriptFiles[i];
                if (scriptFileName.getName().contains(serverStartupScriptFileName)) {
                    scriptName = scriptFileName.getAbsoluteFile().getName();
                    break;
                }
            }
            scriptName = FilenameUtils.removeExtension(scriptName);

            return scriptName;
        }
    }
}
