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
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceResourceServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceStub;
import org.wso2.carbon.registry.resource.stub.beans.xsd.CollectionContentBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.ContentBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.ContentDownloadBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.MetadataBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.PermissionBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.ResourceTreeEntryBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.VersionPath;
import org.wso2.carbon.registry.resource.stub.beans.xsd.VersionsBean;
import org.wso2.carbon.registry.resource.stub.common.xsd.ResourceData;
import org.wso2.ei.dataservices.integration.common.clients.utils.AuthenticateStubUtil;

import javax.activation.DataHandler;
import java.rmi.RemoteException;


public class ResourceAdminServiceClient {
    private static final Log log = LogFactory.getLog(ResourceAdminServiceClient.class);

    private final String serviceName = "ResourceAdminService";
    private ResourceAdminServiceStub resourceAdminServiceStub;

    private static final String MEDIA_TYPE_WSDL = "application/wsdl+xml";
    private static final String MEDIA_TYPE_WADL = "application/wadl+xml";
    private static final String MEDIA_TYPE_SCHEMA = "application/x-xsd+xml";
    private static final String MEDIA_TYPE_POLICY = "application/policy+xml";
    private static final String MEDIA_TYPE_GOVERNANCE_ARCHIVE = "application/vnd.wso2.governance-archive";

    public ResourceAdminServiceClient(String serviceUrl, String sessionCookie) throws AxisFault {
        String endPoint = serviceUrl + serviceName;
        resourceAdminServiceStub = new ResourceAdminServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(sessionCookie, resourceAdminServiceStub);
    }

    public ResourceAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        resourceAdminServiceStub = new ResourceAdminServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(userName, password, resourceAdminServiceStub);
    }

    public boolean addResource(String destinationPath, String mediaType,
                               String description, DataHandler dh)
            throws ResourceAdminServiceExceptionException, RemoteException {

        if (log.isDebugEnabled()) {
            log.debug("Destination Path :" + destinationPath);
            log.debug("Media Type :" + mediaType);
        }
        return resourceAdminServiceStub.addResource(destinationPath, mediaType, description, dh, null, null);
    }

    public ResourceData[] getResource(String destinationPath)
            throws ResourceAdminServiceExceptionException, RemoteException {
        ResourceData[] rs;
        rs = resourceAdminServiceStub.getResourceData(new String[]{destinationPath});
        return rs;
    }

    public CollectionContentBean getCollectionContent(String destinationPath)
            throws RemoteException, ResourceAdminServiceExceptionException {
        CollectionContentBean collectionContentBean;
        collectionContentBean = resourceAdminServiceStub.getCollectionContent(destinationPath);

        return collectionContentBean;
    }

    public boolean deleteResource(String destinationPath)
            throws ResourceAdminServiceExceptionException, RemoteException {

        return resourceAdminServiceStub.delete(destinationPath);
    }

    public void addWSDL(String description, DataHandler dh)
            throws ResourceAdminServiceExceptionException, RemoteException {

        String fileName;
        fileName = dh.getName().substring(dh.getName().lastIndexOf('/') + 1);
        log.debug(fileName);
        resourceAdminServiceStub.addResource("/" + fileName, MEDIA_TYPE_WSDL, description, dh, null, null);
    }

    public void addWADL(String description, DataHandler dh)
            throws ResourceAdminServiceExceptionException, RemoteException {

        String fileName;
        fileName = dh.getName().substring(dh.getName().lastIndexOf('/') + 1);
        log.debug(fileName);
        resourceAdminServiceStub.addResource("/" + fileName, MEDIA_TYPE_WADL, description, dh, null, null);
    }

    public void addWSDL(String resourceName, String description,
                        String fetchURL)
            throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminServiceStub.importResource("/", resourceName, MEDIA_TYPE_WSDL,
                                                description, fetchURL, null, null);
    }

    public void addWADL(String resourceName, String description,
                        String fetchURL)
            throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminServiceStub.importResource("/", resourceName, MEDIA_TYPE_WADL,
                                                description, fetchURL, null, null);
    }

    public void addSchema(String description, DataHandler dh)
            throws ResourceAdminServiceExceptionException, RemoteException {
        String fileName;
        fileName = dh.getName().substring(dh.getName().lastIndexOf('/') + 1);
        resourceAdminServiceStub.addResource("/" + fileName, MEDIA_TYPE_SCHEMA,
                                             description, dh, null, null);
    }

    public void addSchema(String resourceName, String description,
                          String fetchURL) throws ResourceAdminServiceExceptionException,
                                                  RemoteException {

        resourceAdminServiceStub.importResource("/", resourceName, MEDIA_TYPE_SCHEMA,
                                                description, fetchURL, null, null);

    }

    public void addPolicy(String description, DataHandler dh)
            throws ResourceAdminServiceExceptionException, RemoteException {
        String fileName;
        fileName = dh.getName().substring(dh.getName().lastIndexOf('/') + 1);
        resourceAdminServiceStub.addResource("/" + fileName, MEDIA_TYPE_POLICY,
                                             description, dh, null, null);
    }

    public void addPolicy(String resourceName, String description,
                          String fetchURL)
            throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminServiceStub.importResource("/", resourceName, MEDIA_TYPE_POLICY,
                                                description, fetchURL, null, null);
    }

    public void uploadArtifact(String description, DataHandler dh)
            throws ResourceAdminServiceExceptionException, RemoteException {
        String fileName;
        fileName = dh.getName().substring(dh.getName().lastIndexOf('/') + 1);
        resourceAdminServiceStub.addResource("/" + fileName, MEDIA_TYPE_GOVERNANCE_ARCHIVE,
                                             description, dh, null, null);
    }

    public String addCollection(String parentPath, String collectionName,
                                String mediaType, String description)
            throws ResourceAdminServiceExceptionException, RemoteException {
        return resourceAdminServiceStub.addCollection(parentPath, collectionName, mediaType, description);
    }


    public void addSymbolicLink(String parentPath, String name,
                                String targetPath)
            throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminServiceStub.addSymbolicLink(parentPath, name, targetPath);
    }

    public void addTextResource(String parentPath, String fileName,
                                String mediaType, String description, String content)
            throws RemoteException, ResourceAdminServiceExceptionException {

        resourceAdminServiceStub.addTextResource(parentPath, fileName, mediaType,
                                                 description, content);
    }

    public void addResourcePermission(String pathToAuthorize,
                                      String roleToAuthorize,
                                      String actionToAuthorize, String permissionType)
            throws RemoteException, ResourceAdminServiceResourceServiceExceptionException {

        resourceAdminServiceStub.addRolePermission(pathToAuthorize, roleToAuthorize,
                                                   actionToAuthorize, permissionType);

    }

    public String getProperty(String resourcePath, String key)
            throws RemoteException, ResourceAdminServiceExceptionException {

        return resourceAdminServiceStub.getProperty(resourcePath, key);

    }

    public MetadataBean getMetadata(String resourcePath)
            throws RemoteException, ResourceAdminServiceExceptionException {

        return resourceAdminServiceStub.getMetadata(resourcePath);
    }

    public ContentBean getResourceContent(String resourcePath)
            throws RemoteException, ResourceAdminServiceExceptionException {

        return resourceAdminServiceStub.getContentBean(resourcePath);

    }

    public ResourceData[] getResourceData(String resourcePath)
            throws RemoteException, ResourceAdminServiceExceptionException {
        String[] resourceArray = {resourcePath};

        return resourceAdminServiceStub.getResourceData(resourceArray);


    }

    public String getHumanReadableMediaTypes() throws Exception {
        try {
            return resourceAdminServiceStub.getHumanReadableMediaTypes();
        } catch (Exception e) {
            String msg = "get human readable media type error ";
            throw new Exception(msg, e);
        }
    }

    public String getMimeTypeFromHuman(String mediaType) throws Exception {

        try {
            return resourceAdminServiceStub.getMimeTypeFromHuman(mediaType);
        } catch (Exception e) {
            String msg = "get human readable media type error ";
            throw new Exception(msg, e);

        }
    }

    public void updateTextContent(String path, String content)
            throws RemoteException, ResourceAdminServiceExceptionException {
        resourceAdminServiceStub.updateTextContent(path, content);
    }

    public void copyResource(String parentPath, String oldResourcePath, String destinationPath,
                             String targetName)
            throws RemoteException, ResourceAdminServiceExceptionException {
        resourceAdminServiceStub.copyResource(parentPath, oldResourcePath, destinationPath, targetName);
    }

    public void moveResource(String parentPath, String oldResourcePath, String destinationPath,
                             String targetName)
            throws RemoteException, ResourceAdminServiceExceptionException {
        resourceAdminServiceStub.moveResource(parentPath, oldResourcePath, destinationPath, targetName);
    }


    public VersionPath[] getVersionPaths(String path)
            throws RemoteException, ResourceAdminServiceExceptionException {
        VersionPath[] versionPaths = null;
        VersionsBean vb = resourceAdminServiceStub.getVersionsBean(path);
        versionPaths = vb.getVersionPaths();
        return versionPaths;
    }

    public VersionsBean getVersionsBean(String path)
            throws RemoteException, ResourceAdminServiceExceptionException {

        return resourceAdminServiceStub.getVersionsBean(path);
    }

    public void createVersion(String resourcePath)
            throws RemoteException, ResourceAdminServiceExceptionException {
        resourceAdminServiceStub.createVersion(resourcePath);
    }


    public void deleteVersionHistory(String path, String snapshotID)
            throws RemoteException, ResourceAdminServiceExceptionException {
        resourceAdminServiceStub.deleteVersionHistory(path, snapshotID);
    }


    public boolean restoreVersion(String path)
            throws RemoteException, ResourceAdminServiceExceptionException {
        boolean status = false;

        status = resourceAdminServiceStub.restoreVersion(path);
        return status;
    }

    public String getTextContent(String path)
            throws RemoteException, ResourceAdminServiceExceptionException {
        String content = null;
        content = resourceAdminServiceStub.getTextContent(path);
        return content;
    }

    public PermissionBean getPermission(String path) throws Exception {
        return resourceAdminServiceStub.getPermissions(path);
    }


    public void renameResource(String parentPath, String oldResourcePath, String newResourceName)
            throws RemoteException, ResourceAdminServiceExceptionException {
        resourceAdminServiceStub.renameResource(parentPath, oldResourcePath, newResourceName);
    }

    public boolean addExtension(String name, DataHandler content)
            throws RemoteException, ResourceAdminServiceExceptionException {
        return resourceAdminServiceStub.addExtension(name, content);
    }

    public boolean removeExtension(String name)
            throws RemoteException, ResourceAdminServiceExceptionException {
        return resourceAdminServiceStub.removeExtension(name);
    }

    public String[] listExtensions()
            throws RemoteException, ResourceAdminServiceExceptionException {
        return resourceAdminServiceStub.listExtensions();
    }


    public void setDescription(String path, String description)
            throws RemoteException, ResourceAdminServiceExceptionException {
        resourceAdminServiceStub.setDescription(path, description);
    }


    public ContentDownloadBean getContentDownloadBean(String path)
            throws RemoteException, ResourceAdminServiceExceptionException {
        return resourceAdminServiceStub.getContentDownloadBean(path);
    }


    public boolean importResource(String parentPath, String resourceName, String mediaType,
                                  String description, String fetchURL, String symLink)
            throws RemoteException, ResourceAdminServiceExceptionException {
        return resourceAdminServiceStub.importResource(parentPath, resourceName, mediaType, description, fetchURL, symLink, null);
    }

    public ResourceTreeEntryBean getResourceTreeEntryBean(String resourcePath)
            throws RemoteException, ResourceAdminServiceExceptionException {
        return resourceAdminServiceStub.getResourceTreeEntry(resourcePath);
    }

}
