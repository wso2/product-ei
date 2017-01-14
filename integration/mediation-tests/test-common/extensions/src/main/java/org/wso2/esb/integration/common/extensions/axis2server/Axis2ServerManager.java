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

package org.wso2.esb.integration.common.extensions.axis2server;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.engine.ListenerManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.extensions.ExtensionUtils;
import org.wso2.carbon.automation.extensions.servers.axis2server.BackendServer;
import org.wso2.carbon.utils.ServerConstants;

import java.io.*;

public class Axis2ServerManager implements BackendServer {
    private static final Log log = LogFactory.getLog(org.wso2.carbon.automation.extensions.servers.axis2server.Axis2ServerManager.class);
    private ConfigurationContext cfgCtx;
    private ListenerManager listenerManager;
    private boolean started;
    String repositoryPath = null;

    public Axis2ServerManager() {
        this("test_axis2_server_9000.xml");
        repositoryPath = System.getProperty(ServerConstants.CARBON_HOME) + File.separator +
                "samples" + File.separator + "axis2Server" + File.separator + "repository";
    }

    public Axis2ServerManager(String axis2xmlFile) {
        repositoryPath = System.getProperty(ServerConstants.CARBON_HOME) + File.separator +
                "samples" + File.separator + "axis2Server" + File.separator + "repository";
        File repository = new File(repositoryPath);
        log.info("Using the Axis2 repository path: " + repository.getAbsolutePath());
        try {
            File axis2xml = copyResourceToFileSystem(axis2xmlFile, "axis2.xml");
            if (!axis2xml.exists()) {
                log.error("Error while copying the test axis2.xml to the file system");
                return;
            }
            log.info("Loading axis2.xml from: " + axis2xml.getAbsolutePath());
            cfgCtx = ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                    repository.getAbsolutePath(), axis2xml.getAbsolutePath());
        } catch (Exception e) {
            log.error("Error while initializing the configuration context", e);
        }
    }

    public void start() throws IOException {
        log.info("Starting sample Axis2 server");
        listenerManager = new ListenerManager();
        listenerManager.init(cfgCtx);
        listenerManager.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        started = true;
    }

    public void stop() {
        log.info("Stopping sample Axis2 server");
        try {
            listenerManager.stop();
            listenerManager.destroy();
            cfgCtx.cleanupContexts();
        } catch (AxisFault axisFault) {
            log.error("Error while shutting down the listener managers", axisFault);
        }
        started = false;
    }

    public boolean isStarted() {
        return !listenerManager.isStopped();
    }

    public void hotDeployArtifact(String artifact) throws IOException {
        File fOrig = new File(artifact);
        File fDest = new File(repositoryPath + File.separator + "services" + File.separator);
        FileUtils.copyFile(fOrig, fDest);
    }

    public void hotUndeployArtifact(String artifact) {
        File fOrig = new File(artifact);
        FileUtils.deleteQuietly(fOrig);
    }

    public void deployService(Object service) throws IOException {
        String artifactName = service + ".aar";
        File file = copyResourceToFileSystem(artifactName, artifactName);
        AxisServiceGroup serviceGroup = DeploymentEngine.loadServiceGroup(file, cfgCtx);
        cfgCtx.getAxisConfiguration().addServiceGroup(serviceGroup);
    }

    private File copyResourceToFileSystem(String resourceName, String fileName) throws IOException {
        File file = new File(System.getProperty("basedir") + File.separator + "target" +
                File.separator + fileName);
        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }
        FileUtils.touch(file);
        OutputStream os = FileUtils.openOutputStream(file);
        InputStream is;
        if (resourceName.contains(".aar")) {
            is = new FileInputStream(ExtensionUtils.getSystemResourceLocation() +
                    File.separator + "artifacts" + File.separator + "AXIS2" +
                    File.separator + "aar" +
                    File.separator + resourceName);
        } else {
            is = new FileInputStream(ExtensionUtils.getSystemResourceLocation() +
                    File.separator + "artifacts" + File.separator + "AXIS2" +
                    File.separator + "config" +
                    File.separator + resourceName);
        }
        if (is != null) {
            byte[] data = new byte[1024];
            int len;
            while ((len = is.read(data)) != -1) {
                os.write(data, 0, len);
            }
        }
        os.flush();
        os.close();
        is.close();
        return file;
    }

    private File copyServiceToFileSystem(String resourceName, String fileName) throws IOException {
        File file = new File(System.getProperty("basedir") + File.separator + "target" +
                File.separator + fileName);
        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }
        FileUtils.touch(file);
        OutputStream os = FileUtils.openOutputStream(file);
        InputStream is = new FileInputStream(ExtensionUtils.getSystemResourceLocation() +
                File.separator + "artifacts" + File.separator + "AXIS2" +
                File.separator + "config" +
                File.separator + resourceName);
        if (is != null) {
            byte[] data = new byte[1024];
            int len;
            while ((len = is.read(data)) != -1) {
                os.write(data, 0, len);
            }
            os.flush();
            os.close();
            is.close();
        }
        return file;
    }
}

