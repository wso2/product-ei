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
package org.wso2.ei.dataservices.integration.common.clients;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.dataservices.ui.stub.DataServiceAdminStub;
import org.wso2.carbon.dataservices.ui.stub.admin.core.xsd.PaginatedTableInfo;
import org.wso2.ei.dataservices.integration.common.clients.utils.AuthenticateStubUtil;

import java.rmi.RemoteException;


public class DataServiceAdminClient {
    private static final Log log = LogFactory.getLog(DataServiceAdminClient.class);

    private final String serviceName = "DataServiceAdmin";
    private DataServiceAdminStub dataServiceAdminStub;

    public DataServiceAdminClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        dataServiceAdminStub = new DataServiceAdminStub(endPoint);
        AuthenticateStubUtil.authenticateStub(sessionCookie, dataServiceAdminStub);
    }

    public DataServiceAdminClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        dataServiceAdminStub = new DataServiceAdminStub(endPoint);
        AuthenticateStubUtil.authenticateStub(userName, password, dataServiceAdminStub);
    }

    public String[] getCarbonDataSources() throws RemoteException {
        return dataServiceAdminStub.getCarbonDataSourceNames();
    }

    public void editDataService(String serviceName, String serviceHierachy,
                                String dataServiceContent) throws RemoteException {
        dataServiceAdminStub.saveDataService(serviceName, serviceHierachy, dataServiceContent);
    }

    public String getDataServiceContent(String serviceName)
            throws RemoteException {
        String content;
        content = dataServiceAdminStub.getDataServiceContentAsString(serviceName);
        return content;
    }


    public void saveDataService(String serviceName, String serviceGroup, String serviceContents)
            throws AxisFault {
        try {
            dataServiceAdminStub.saveDataService(serviceName, serviceGroup, serviceContents);
        } catch (RemoteException e) {
            log.error("Error occurred while saving dataservice : " + serviceName, e);
            throw new AxisFault("Saving " + serviceName + " failed.", e);
        }
    }

    /**
     * @param driverClass   JDBC driver class name
     * @param jdbcURL       JDBC Url
     * @param username      username
     * @param password      password
     * @param passwordAlias password alias
     * @return a string representing success or the failure of the JDBC connection
     * @throws org.apache.axis2.AxisFault axisFault
     */
    public String testJDBCConnection(String driverClass, String jdbcURL, String username,
                                     String password, String passwordAlias) throws AxisFault {
        String response = "";
        try {
            response = dataServiceAdminStub.testJDBCConnection(driverClass, jdbcURL, username,
                                                               password, passwordAlias);
        } catch (RemoteException e) {
            throw new AxisFault("Error connecting to " + jdbcURL + ". Message from the service is : ", e);
        }
        return response;
    }

    public String testGSpreadConnection(String userName, String password, String visibility,
                                        String documentURL, String passwordAlias) throws AxisFault {
        String response = "";
        try {
            response =
                    dataServiceAdminStub.testGSpreadConnection(userName, password, visibility, documentURL,
                                                               passwordAlias);
        } catch (RemoteException e) {
            throw new AxisFault("Error connecting to " + documentURL + ". Message from the service is : ", e);
        }
        return response;
    }

    public String[] getOutputColumnNames(String query) throws Exception {
        return dataServiceAdminStub.getOutputColumnNames(query);
    }

    public String[] getInputMappingNames(String query) throws Exception {
        return dataServiceAdminStub.getInputMappingNames(query);
    }

    public PaginatedTableInfo getPaginatedTableInfo(int pageNumber,
                                                    String datasourceId, String dbName,
                                                    String[] schemas) throws Exception {
        return dataServiceAdminStub.getPaginatedTableInfo(pageNumber, datasourceId, dbName, schemas);
    }

    public PaginatedTableInfo getPaginatedSchemaInfo(int pageNumber,
                                                     String datasourceId) throws Exception {
        return dataServiceAdminStub.getPaginatedSchemaInfo(pageNumber, datasourceId);
    }

    public String[] getTableInfo(String datasourceId, String dbName, String[] schemas)
            throws Exception {
        return dataServiceAdminStub.getTableList(datasourceId, dbName, schemas);
    }

    public String[] getdbSchemaList(String datasourceId) throws Exception {
        return dataServiceAdminStub.getdbSchemaList(datasourceId);
    }


    public String[] getDSServiceList(String dataSourceId, String dbName, String[] schemas,
                                     String[] tableNames, String serviceNamespace) {
        try {
            return dataServiceAdminStub.getDSServiceList(dataSourceId, dbName, schemas, tableNames,
                                                         false, serviceNamespace);
        } catch (Exception e) {
            return null;
        }
    }

    public String getDSService(String dataSourceId, String dbName, String[] schemas,
                               String[] tableNames, String serviceName, String serviceNamespace) {
        try {
            return dataServiceAdminStub.getDSService(dataSourceId, dbName, schemas, tableNames, true, serviceName,
                                                     serviceNamespace);
        } catch (Exception e) {
            return null;
        }
    }


}
