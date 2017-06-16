/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.deployer.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class DirectoryListner {

    private static Logger logger = LoggerFactory.getLogger(DirectoryListner.class);

    private WatchService watchService;
    private Path targetDirectory;
    private WatchKey watchKey;
    private EventCallback eventCallback;

    public DirectoryListner() throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
    }

    public void registerTargetDir(Path targetDirPath, EventCallback callback) throws IOException {
        //TODO Add map to watch multiple directories
        targetDirectory = targetDirPath;
        eventCallback = callback;
        watchKey = targetDirPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public void startListening () {
        for (;;) {
            WatchKey key = null;
            try {
                key = watchService.take();
                eventCallback.exec(key);
            } catch (InterruptedException e) {
                logger.error("Error occurred while waiting for directory change events", e);
            }
        }
    }
}
