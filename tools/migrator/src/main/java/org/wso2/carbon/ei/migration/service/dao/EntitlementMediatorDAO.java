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
package org.wso2.carbon.ei.migration.service.dao;

import org.wso2.carbon.ei.migration.util.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Data Access layer for entitlement mediator
 */
public class EntitlementMediatorDAO {

    private static EntitlementMediatorDAO instance = new EntitlementMediatorDAO();

    private EntitlementMediatorDAO() {

    }

    public static EntitlementMediatorDAO getInstance() {
        return instance;
    }

    /**
     * Return the path of the synapse configurations where we can have the entitlement mediator
     *
     * @param carbonHome
     * @param emPathFolders
     * @param folder
     * @return path of the configuration
     */
    public String buildPathFromFolders(String carbonHome, String[] emPathFolders, String folder) {
        String[] folders;
        final int l = emPathFolders.length;
        folders = Arrays.copyOf(emPathFolders, l + 1);
        folders[l] = folder;
        return Paths.get(carbonHome, folders).toString();
    }

    /**
     * Put all paths of the synapse configurations.
     *
     * @param tenantId
     * @return HashMap
     * @throws FileNotFoundException
     */
    public HashMap<String, File[]> getEMConfigFiles(int tenantId) throws FileNotFoundException {
        String carbonHome = System.getProperty(Constant.CARBON_HOME);
        HashMap<String, File[]> filesMap = new HashMap<>();
        String emPath;
        String[] containerFolders = {"api", "proxy-services", "sequences", "templates"};
        String[] emPathFolders;
        if (tenantId == Constant.SUPER_TENANT_ID) {
            emPathFolders = new String[]{"repository", "deployment", "server", "synapse-configs", "default"};
        } else {
            emPathFolders = new String[]{"repository", "tenants", String.valueOf(tenantId), "synapse-configs",
                    "default"};
        }
        File[] files;
        for (String folder : containerFolders) {
            emPath = EntitlementMediatorDAO.getInstance().buildPathFromFolders(carbonHome, emPathFolders, folder);
            files = new File(emPath).listFiles();
            files = files != null ? files : new File[0];
            filesMap.put(folder, files);
        }
        return filesMap;
    }
}
