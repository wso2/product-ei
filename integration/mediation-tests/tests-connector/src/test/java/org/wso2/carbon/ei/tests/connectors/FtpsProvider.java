/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ei.tests.connectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.ftps.FtpsFileProvider;
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FtpsProvider {
    private static int SocketPort;
    private static String ConnectionUri;
    private static FtpServer Server;
    static FileSystemOptions fileSystemOptions;
    private static final String TEST_URI = "test.ftps.uri";
    private static final String USER_PROPS_RES = "org.apache.ftpsserver/users.properties";
    private static final String SERVER_JKS_RES = "org.apache.ftpsserver/wso2carbon.jks";

    static String getConnectionUri() {
        return ConnectionUri;
    }

    static int getSocketPort() {
        return SocketPort;
    }

    static String getSystemTestUriOverride() {
        return System.getProperty(TEST_URI);
    }

    static void init() throws IOException {
        SocketPort = FtpsUtil.findFreeLocalPort();
        ConnectionUri = "ftps://test:test@127.0.0.1:" + SocketPort;
    }

    /**
     * Creates and starts an embedded Apache FTPS Server (MINA).
     *
     * @throws FtpException
     * @throws IOException
     */
    static void startFtpsServer() throws FtpException, IOException {
        if (Server != null) {
            return;
        }
        init();
        final FtpServerFactory serverFactory = new FtpServerFactory();
        final PropertiesUserManagerFactory propertiesUserManagerFactory = new PropertiesUserManagerFactory();
        final URL userPropsResource = ClassLoader.getSystemClassLoader().getResource(USER_PROPS_RES);
        Assert.assertNotNull(USER_PROPS_RES, userPropsResource);
        propertiesUserManagerFactory.setUrl(userPropsResource);
        final UserManager userManager = propertiesUserManagerFactory.createUserManager();
        serverFactory.setUserManager(userManager);
        final ListenerFactory factory = new ListenerFactory();
        // set the port of the listener
        factory.setPort(SocketPort);

        // define SSL configuration
        final URL serverJksResource = ClassLoader.getSystemClassLoader().getResource(SERVER_JKS_RES);
        Assert.assertNotNull(SERVER_JKS_RES, serverJksResource);
        final SslConfigurationFactory ssl = new SslConfigurationFactory();
        final File keyStoreFile = FileUtils.toFile(serverJksResource);
        Assert.assertTrue(keyStoreFile.toString(), keyStoreFile.exists());
        ssl.setKeystoreFile(keyStoreFile);
        ssl.setKeystorePassword(Constants.KEYSTORE_PASSWORD);
        factory.setSslConfiguration(ssl.createSslConfiguration());

        serverFactory.addListener("default", factory.createListener());

        // start the server
        Server = serverFactory.createServer();
        Server.start();
    }

    /**
     * Stops the embedded Apache FTP Server (MINA).
     */
    static void shutdownFtpsServer() {
        if (Server != null) {
            Server.stop();
            Server = null;
        }
    }
}
