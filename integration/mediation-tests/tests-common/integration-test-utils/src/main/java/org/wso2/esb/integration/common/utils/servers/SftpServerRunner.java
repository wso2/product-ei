/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.esb.integration.common.utils.servers;

import org.apache.commons.lang3.StringUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystem;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SSHD based SFTP server implementation for EI testing.
 */
public class SftpServerRunner {

    private final SftpServer sftpServer;

    public SftpServerRunner(int port, String ftpFolderPath, String userName, String password) {
        this.sftpServer = new SftpServer(port, ftpFolderPath, userName, password);
    }

    public void start() {
        Thread thread = new Thread(sftpServer);
        thread.start();
    }

    public void stop(){
        sftpServer.stop();
    }

    private class SftpServer implements Runnable {

        private final Logger LOGGER = LoggerFactory.getLogger(SftpServer.class);
        private final SshServer sshd = SshServer.setUpDefaultServer();
        private final int port;
        private final String path;
        private final String ftpUser;
        private final String ftpPassword;

        SftpServer(int port, String path, String ftpUser, String ftpPassword) {
            this.port = port;
            this.path = path;
            this.ftpUser = ftpUser;
            this.ftpPassword = ftpPassword;
        }

        @Override
        public void run() {
            sshd.setPort(port);
            sshd.setSubsystemFactories(
                    Arrays.<NamedFactory<Command>>asList(new SftpSubsystemFactory()));
            sshd.setCommandFactory(new ScpCommandFactory());
            sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
            sshd.setFileSystemFactory(new VirtualFileSystemFactory(Paths.get(path)));
            sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
                @Override
                public boolean authenticate(final String username, final String password, final ServerSession session) {
                    return StringUtils.equals(username, ftpUser) && StringUtils.equals(password, ftpPassword);
                }
            });
            try {
                LOGGER.info("Starting SFTP server on port {}", port);
                sshd.start();
            } catch (IOException e) {
                LOGGER.error("Error starting SFTP server", e);
            }
        }

        private void stop() {
            try {
                sshd.stop();
            } catch (IOException e) {
                LOGGER.error("Error stopping SFTP server", e);
            }
        }
    }
}
