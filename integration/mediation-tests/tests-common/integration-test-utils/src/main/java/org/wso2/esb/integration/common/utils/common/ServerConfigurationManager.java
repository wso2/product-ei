/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.esb.integration.common.utils.common;

import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.ServerAdminClient;
import org.wso2.carbon.integration.common.utils.ClientConnectionUtil;
import org.wso2.carbon.integration.common.utils.FileManager;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.server.admin.stub.ServerAdminException;
import org.wso2.carbon.utils.ServerConstants;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class can be used to replace configuration files at carbon server
 */
public class ServerConfigurationManager {

    private static final long TIME_OUT = 600000;
    private File originalConfig;
    private File backUpConfig;
    private int port;
    private String hostname;
    private String backEndUrl;
    private AutomationContext autoCtx;
    private String sessionCookie;
    private LoginLogoutClient loginLogoutClient;
    private List<ConfigData> configDatas = new ArrayList<ConfigData>();

    /**
     * Create a ServerConfigurationManager
     *
     * @param productGroup product group name
     * @param userMode     user mode
     */
    public ServerConfigurationManager(String productGroup, TestUserMode userMode)
            throws AutomationUtilException, XPathExpressionException, MalformedURLException {
        this.autoCtx = new AutomationContext(productGroup, userMode);
        this.loginLogoutClient = new LoginLogoutClient(autoCtx);
        this.backEndUrl = autoCtx.getContextUrls().getBackEndUrl();
        this.port = new URL(backEndUrl).getPort();
        this.hostname = new URL(backEndUrl).getHost();
    }

    /**
     * Create a ServerConfigurationManager
     *
     * @param autoCtx automation context
     * @throws XPathExpressionException
     */
    public ServerConfigurationManager(AutomationContext autoCtx)
            throws AutomationUtilException, XPathExpressionException, MalformedURLException {
        this.loginLogoutClient = new LoginLogoutClient(autoCtx);
        this.autoCtx = autoCtx;
        this.backEndUrl = autoCtx.getContextUrls().getBackEndUrl();
        this.port = new URL(backEndUrl).getPort();
        this.hostname = new URL(backEndUrl).getHost();
    }


    /**
     * backup the current server configuration file
     *
     * @param fileName file name
     * @throws IOException
     */
    private void backupConfiguration(String fileName) throws IOException {
        //restore backup configuration
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        String confDir = Paths.get(carbonHome,"conf").toString();
        String AXIS2_XML = "axis2";
        if (fileName.contains(AXIS2_XML)) {
            confDir = Paths.get(confDir, "axis2").toString();
        }
        originalConfig = Paths.get(confDir,fileName).toFile();
        backUpConfig = Paths.get(confDir , fileName + ".backup").toFile();

        Files.move(originalConfig.toPath(), backUpConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);

        if (originalConfig.exists()) {
            throw new IOException("Failed to rename file from " + originalConfig.getName() + "to" +
                    backUpConfig.getName());
        }

        configDatas.add(new ConfigData(backUpConfig, originalConfig));
    }

    /**
     * Backup a file residing in a cabron server.
     *
     * @param file file residing in server to backup.
     * @throws IOException
     */
    private void backupConfiguration(File file) throws IOException {
        //restore backup configuration
        originalConfig = file;
        backUpConfig = new File(file.getAbsolutePath() + ".backup");

        Files.move(originalConfig.toPath(), backUpConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);

        if (originalConfig.exists()) {
            throw new IOException("Failed to rename file from " + originalConfig.getName() + "to" +
                    backUpConfig.getName());
        }

        configDatas.add(new ConfigData(backUpConfig, originalConfig));
    }

    /**
     * @return will return the carbon home. the location of the server instance
     */
    public static String getCarbonHome() {
        return System.getProperty(ServerConstants.CARBON_HOME);
    }

    /**
     * Apply configuration from source file to a target file without restarting.
     *
     * @param sourceFile Source file to copy.
     * @param targetFile Target file that is to be backed up and replaced.
     * @param backup     boolean value, set this to true if you want to backup the original file.
     * @throws IOException - throws if apply configuration fails
     */
    public void applyConfigurationWithoutRestart(File sourceFile, File targetFile, boolean backup)
            throws IOException {
        // Using InputStreams to copy bytes instead of Readers that copy chars.
        // Otherwise things like JKS files get corrupted during copy.
        FileChannel source = null;
        FileChannel destination = null;
        if (backup) {
            backupConfiguration(targetFile);
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(originalConfig).getChannel();
        } else {
            if (!targetFile.exists()) {
                if (!targetFile.createNewFile()) {
                    throw new IOException("File " + targetFile + "creation fails");
                }
            }
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(targetFile).getChannel();
        }
        destination.transferFrom(source, 0, source.size());
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    /**
     * @param sourceFile       file  of the new configuration file
     * @param targetFile       configuration file required to replace in the server. File must be created
     *                         with the absolute path.
     * @param backupConfigFile require to back the existing file
     * @param restartServer    require to restart the server after replacing the config file
     */
    public void applyConfiguration(File sourceFile, File targetFile, boolean backupConfigFile,
                                   boolean restartServer)
            throws AutomationUtilException, IOException {

        // Using InputStreams to copy bytes instead of Readers that copy chars.
        // Otherwise things like JKS files get corrupted during copy.
        FileChannel source = null;
        FileChannel destination = null;
        try {
            if (backupConfigFile) {
                backupConfiguration(targetFile);
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(originalConfig).getChannel();
            } else {
                if (!targetFile.exists()) {
                    if (!targetFile.createNewFile()) {
                        throw new IOException("File " + targetFile + "creation fails");
                    }
                }
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(targetFile).getChannel();
            }
            destination.transferFrom(source, 0, source.size());
            if (restartServer) {
                restartGracefully();
            }
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    //ignored
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException e) {
                    //ignored
                }
            }
        }
    }

    /**
     * restore to a last configuration and restart the server
     */
    public void restoreToLastConfiguration() throws IOException, AutomationUtilException {
        restoreToLastConfiguration(true);
    }

    /**
     * restore all files to last configuration and restart the server
     *
     * @throws AutomationUtilException - throws if restore to last configuration fails
     * @throws IOException             - throws if restore to last configuration fails
     */
    public void restoreToLastConfiguration(boolean isRestartRequired) throws AutomationUtilException,
                                                                      IOException {
        for (ConfigData data : configDatas) {
            Files.move(data.getBackupConfig().toPath(), data.getOriginalConfig().toPath(),
                       StandardCopyOption.REPLACE_EXISTING);

            if (data.getBackupConfig().exists()) {
                throw new IOException("File rename from " + data.getBackupConfig() + "to " +
                                      data.getOriginalConfig() + "fails");
            }
        }
        if (isRestartRequired) {
            restartGracefully();
        }
    }

    /**
     * apply configuration file and restart server to take effect the configuration
     *
     * @param newConfig configuration file
     * @throws AutomationUtilException - throws if apply configuration fails
     * @throws IOException             - throws if apply configuration fails
     */
    public void applyConfiguration(File newConfig) throws AutomationUtilException, IOException {
        //to backup existing configuration
        backupConfiguration(newConfig.getName());
        InputStreamReader in = new InputStreamReader(new FileInputStream(newConfig), StandardCharsets.UTF_8);
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(originalConfig), StandardCharsets.UTF_8);
        try {
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //ignore
                }

            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        restartGracefully();
    }

    /**
     * apply configuration file and restart server to take effect the configuration
     *
     * @param newConfig configuration file
     * @throws IOException - throws if apply configuration fails
     */
    public void applyConfigurationWithoutRestart(File newConfig) throws IOException {
        //to backup existing configuration
        backupConfiguration(newConfig.getName());
        InputStreamReader in = new InputStreamReader(new FileInputStream(newConfig), StandardCharsets.UTF_8);
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(originalConfig), StandardCharsets.UTF_8);
        try {
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //ignore
                }

            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }

    /**
     * Methods to replace configuration files in products.
     *
     * @param sourceFile - configuration file to be copied for your local machine or carbon server it self.
     * @param targetFile - configuration file in carbon server. e.g - path to axis2.xml in config directory
     */
    public void applyConfiguration(File sourceFile, File targetFile) throws AutomationUtilException,
                                                                     IOException {
        //to backup existing configuration
        backupConfiguration(targetFile.getName());
        InputStreamReader in = new InputStreamReader(new FileInputStream(sourceFile), StandardCharsets.UTF_8);
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(originalConfig), StandardCharsets.UTF_8);

        try {
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //ignore
                }

            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        restartGracefully();
    }

    /**
     * Restart Server Gracefully  from admin user
     *
     * @throws AutomationUtilException - throws if server restart fails
     */
    public void restartGracefully() throws AutomationUtilException {
        restartGracefully(TIME_OUT);
    }

    /**
     * Restart Server Gracefully  from admin user
     *
     * @param timeout - Server startup waiting time
     * @throws AutomationUtilException - throws if server restart fails
     */
    public void restartGracefully(long timeout) throws AutomationUtilException {
        try {
            sessionCookie = loginLogoutClient.login();
            ServerAdminClient serverAdmin = new ServerAdminClient(backEndUrl, sessionCookie);
            serverAdmin.restartGracefully();
            try {
                Thread.sleep(25000); //force wait until server gracefully shutdown
                ClientConnectionUtil.waitForPort(port, timeout, true, hostname);
                Thread.sleep(5000); //forceful wait until server is ready to be served
            } catch (InterruptedException e) {
                /* ignored */
            }
            ClientConnectionUtil.waitForLogin(autoCtx);

        } catch (RemoteException|ServerAdminException|MalformedURLException|LoginAuthenticationExceptionException e) {
            throw new AutomationUtilException("Error while gracefully restarting the server ", e);
        }
    }

    /**
     * Restart server gracefully from current user session
     *
     * @param sessionCookie session cookie
     * @throws AutomationUtilException - throws if server restart fails
     */
    public void restartGracefully(String sessionCookie) throws AutomationUtilException {
        try {
            ServerAdminClient serverAdmin = new ServerAdminClient(backEndUrl, sessionCookie);
            serverAdmin.restartGracefully();
            try {
                Thread.sleep(20000); //force wait until server gracefully restarts
                ClientConnectionUtil.waitForPort(port, TIME_OUT, true, hostname);
                Thread.sleep(5000); //forceful wait until server is ready to be served
            } catch (InterruptedException e) {
                //ignored
            }
            ClientConnectionUtil.waitForLogin(autoCtx);
        } catch (RemoteException e) {
            throw new AutomationUtilException("Error while gracefully restarting the server ", e);
        } catch (ServerAdminException e) {
            throw new AutomationUtilException("Error while gracefully restarting the server ", e);
        } catch (MalformedURLException e) {
            throw new AutomationUtilException("Error while gracefully restarting the server ", e);
        } catch (LoginAuthenticationExceptionException e) {
            throw new AutomationUtilException("Error while gracefully restarting the server ", e);
        }
    }

    /**
     * Restart Server forcefully from admin user
     *
     * @throws AutomationUtilException - throws if forceful restart fails
     */
    public void restartForcefully() throws AutomationUtilException {
        try {
            sessionCookie = loginLogoutClient.login();
            ServerAdminClient serverAdmin = new ServerAdminClient(backEndUrl, sessionCookie);
            serverAdmin.restart();
            try {
                Thread.sleep(20000); //force wait until server gracefully restarts
                ClientConnectionUtil.waitForPort(port, TIME_OUT, true, hostname);
                Thread.sleep(5000); //forceful wait until server is ready to be served
            } catch (InterruptedException e) {
                //ignored
            }
            ClientConnectionUtil.waitForLogin(autoCtx);
        } catch (RemoteException e) {
            throw new AutomationUtilException("Error while forcefully restarting the server ", e);
        } catch (ServerAdminException e) {
            throw new AutomationUtilException("Error while forcefully restarting the server ", e);
        } catch (MalformedURLException e) {
            throw new AutomationUtilException("Error while forcefully restarting the server ", e);
        } catch (LoginAuthenticationExceptionException e) {
            throw new AutomationUtilException("Error while forcefully restarting the server ", e);
        }
    }

    /**
     * Copy Jar file to server component/lib
     *
     * @param jar jar file
     * @throws IOException
     * @throws URISyntaxException
     */
    public void copyToComponentLib(File jar) throws IOException, URISyntaxException {
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        String lib = Paths.get(carbonHome, "lib").toString();
        FileManager.copyJarFile(jar, lib);
    }

    /**
     * @param fileName file name
     * @throws IOException
     * @throws URISyntaxException
     */
    public void removeFromComponentLib(String fileName) throws IOException, URISyntaxException {
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        String filePath = Paths.get(carbonHome ,"lib" , fileName).toString();
        FileManager.deleteFile(filePath);
//      removing osgi bundle from dropins; OSGI bundle versioning starts with _1.0.0
        fileName = fileName.replace("-", "_");
        fileName = fileName.replace(".jar", "_1.0.0.jar");
        removeFromComponentDropins(fileName);
    }

    /**
     * /**
     * Copy Jar file to server component/dropins
     *
     * @param jar jar file
     * @throws IOException
     * @throws URISyntaxException
     */
    public void copyToComponentDropins(File jar) throws IOException, URISyntaxException {
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        String lib = Paths.get(carbonHome, "dropins").toString();
        FileManager.copyJarFile(jar, lib);
    }

    /**
     * @param fileName file name
     * @throws IOException
     * @throws URISyntaxException
     */
    public void removeFromComponentDropins(String fileName) throws IOException, URISyntaxException {
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        URI filePath = Paths.get(carbonHome, "dropins", fileName).toUri();
        File file = Paths.get(filePath).toFile();

        if (file.exists()) {
            FileManager.deleteFile(file.getAbsolutePath());
        }
    }

    public void reInitializeConfigData() {
        configDatas = new ArrayList<ConfigData>();
    }

    /**
     * Private class to hold config data
     */
    private static class ConfigData {

        private File backupConfig;
        private File originalConfig;

        public ConfigData(File backupConfig, File originalConfig) {
            this.backupConfig = backupConfig;
            this.originalConfig = originalConfig;
        }

        public File getBackupConfig() {
            return backupConfig;
        }

        public File getOriginalConfig() {
            return originalConfig;
        }
    }
}

