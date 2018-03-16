/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.ei.migration.service.migrator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.ei.migration.MigrationClientException;
import org.wso2.carbon.ei.migration.service.Migrator;
import org.wso2.carbon.ei.migration.service.dao.ServerProfileDAO;
import org.wso2.carbon.ei.migration.util.Constant;
import org.wso2.carbon.ei.migration.util.Utility;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * Password transformation class for Server Profile.
 */
public class ServerProfileMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(ServerProfileMigrator.class);

    @Override
    public void migrate() {
        transformPasswordInAllServerProfiles();
    }

    /**
     * This method will transform the server profile password encrypted with old encryption algorithm to new encryption
     * algorithm.
     *
     */
    private void transformPasswordInAllServerProfiles() {
        log.info(Constant.MIGRATION_LOG + "Migration starting on Server Profiles.");
        updateSuperTenantConfigs();
        updateTenantConfigs();
    }

    private void updateSuperTenantConfigs() {
        String carbonHome = System.getProperty(Constant.CARBON_HOME);
        try {
            String zipPath = Paths.get(carbonHome,
                    new String[]{"wso2", "business-process", "repository", "deployment", "server", "bpel"}).toString();
            File[] spZipFiles = new File(zipPath).listFiles();
            if (spZipFiles != null) {
                for (File zipFile : spZipFiles) {
                    if (zipFile.getName().toLowerCase().endsWith(".zip")) {
                        String extractedFolderPath = zipPath + "/Extracted_" + zipFile.getName().replace(".zip", "");
                        Utility.unZipIt(zipFile.getAbsolutePath(), extractedFolderPath);
                        String sourceFolderPath = extractedFolderPath + File.separator
                                + zipFile.getName().replace(".zip", "");
                        ServerProfileDAO.getInstance().modifyInsideExtractedFolder(sourceFolderPath);
                        if (ServerProfileDAO.getInstance().isModified) {
                            Utility.delete(zipFile);
                            List<String> files = Utility.generateFileList(sourceFolderPath);
                            Utility.zipIt(sourceFolderPath, zipFile.getAbsolutePath(), files);
                        }
                        Utility.delete(new File(extractedFolderPath));
                    }
                }
            }

            File[] spFolders = new File(Paths.get(carbonHome,
                    new String[]{"wso2", "business-process", "repository", "bpel", Constant.SUPER_TENANT_ID + ""})
                    .toString()).listFiles();
            processSPFiles(spFolders);
        } catch (Exception e) {
            log.error("Error while updating mediator password for super tenant", e);
        }
    }


    private void updateTenantConfigs() {
        String carbonHome = System.getProperty(Constant.CARBON_HOME);
        try {
            String tenantsPath = Paths.get(carbonHome,
                    new String[]{"wso2", "business-process", "repository", "tenants"}).toString();
            File[] tenantFolders = new File(tenantsPath).listFiles();
            if (tenantFolders != null) {
                for (File tenantFolder : tenantFolders) {
                    String zipPath = Paths.get(tenantFolder.getAbsolutePath(),
                            new String[]{"bpel"}).toString();
                    File[] spZipFiles = new File(zipPath).listFiles();
                    if (spZipFiles != null) {
                        for (File zipFile : spZipFiles) {
                            if (zipFile.getName().toLowerCase().endsWith(".zip")) {
                                String extractedFolderPath = zipPath + "/Extracted_"
                                        + zipFile.getName().replace(".zip", "");
                                Utility.unZipIt(zipFile.getAbsolutePath(), extractedFolderPath);
                                String sourceFolderPath = extractedFolderPath + File.separator
                                        + zipFile.getName().replace(".zip", "");
                                ServerProfileDAO.getInstance().modifyInsideExtractedFolder(sourceFolderPath);
                                if (ServerProfileDAO.getInstance().isModified) {
                                    Utility.delete(zipFile);
                                    List<String> files = Utility.generateFileList(sourceFolderPath);
                                    Utility.zipIt(sourceFolderPath, zipFile.getAbsolutePath(), files);
                                }
                                Utility.delete(new File(extractedFolderPath));
                            }
                        }
                    }

                    File[] spFolders = new File(Paths.get(carbonHome,
                            new String[]{"wso2", "business-process", "repository", "bpel", tenantFolder.getName() + ""})
                            .toString()).listFiles();
                    processSPFiles(spFolders);
                }
            }
        } catch (MigrationClientException e) {
            log.error("Error while updating server profile mediator password for tenant", e);
        }
    }

    private void processSPFiles(File[] spFolders) throws MigrationClientException {
        if (spFolders != null) {
            for (File folder : spFolders) {
                File[] spFiles = folder.listFiles();
                if (spFiles != null) {
                    for (File spFile : spFiles) {
                        if (spFile.isFile() && spFile.getName().toLowerCase().endsWith(".xml")) {
                            ServerProfileDAO.getInstance().transformSPPassword(spFile.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }
}
