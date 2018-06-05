/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.core.util;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.micro.integrator.core.internal.CarbonCoreDataHolder;
import org.wso2.carbon.utils.FileManipulator;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

/**
 *  This task handles house keeping. Work such as cleaning up temp files etc. are handled here.
 */
public class HouseKeepingTask extends TimerTask {
    private static Log log = LogFactory.getLog(HouseKeepingTask.class);

    private String workDir;
    private BidiMap fileResourceMap;
    private int fileTimeoutMillis;

    public HouseKeepingTask(String workDir, BidiMap fileResourceMap) {
        this.workDir = workDir;
        this.fileResourceMap = fileResourceMap;
        fileTimeoutMillis =
                Integer.parseInt(CarbonCoreDataHolder.getInstance().getServerConfigurationService().
                        getFirstProperty("HouseKeeping.MaxTempFileLifetime")) * 60 * 1000;
    }

    public void run() {
        // This task is executed by two threads. Added this synchronized block to fix CARBON-14532
        synchronized (HouseKeepingTask.class) {
            if (log.isDebugEnabled()) {
                log.debug("Starting house-keeping task.");
            }
            try {
                File workDir = new File(this.workDir);
                if (workDir.exists()) {
                    List<String> deletedFiles = new ArrayList<String>();
                    clean(workDir, deletedFiles, false);
                    if (log.isDebugEnabled()) {
                        log.debug("Clearing filemap cache.");
                    }
                    if (fileResourceMap != null) {
                        for (Iterator iterator = deletedFiles.iterator(); iterator.hasNext(); ) {
                            fileResourceMap.removeValue(iterator.next());
                        }
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug("House-keeping complete.");
                }
            } catch (Throwable e) {
                log.error("Could not run HousekeepingTask", e);
            }
        }
    }

    private void clean(File file, List<String> deletedFiles, boolean deleteParent) {
        if(file == null){
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                clean(children[i], deletedFiles,true);
            }
            if (file.listFiles() == null || file.listFiles().length == 0) { // all children deleted?
                String absPath = file.getAbsolutePath();
                if (log.isDebugEnabled()) {
                    log.debug("Deleting directory " + absPath);
                }
                
                if(deleteParent){
                	deletedFiles.add(absPath);
                    FileManipulator.deleteDir(file);
                }                
            }
        } else {
            if (System.currentTimeMillis() -
                file.lastModified() >= fileTimeoutMillis) {
                String absPath = file.getAbsolutePath();
                if (file.isDirectory()) {
                	
                    if (log.isDebugEnabled()) {
                        log.debug("Deleting directory " + absPath);
                    }
                    if(deleteParent){
                    	deletedFiles.add(absPath);
                        FileManipulator.deleteDir(file);
                	}
                    
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Deleting file " + absPath);
                    }
                    deletedFiles.add(absPath);
                    if(!file.delete()){
                        log.warn("Could not delete file " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public void setFileResourceMap(BidiMap fileResourceMap) {
        this.fileResourceMap = fileResourceMap;
    }
}
