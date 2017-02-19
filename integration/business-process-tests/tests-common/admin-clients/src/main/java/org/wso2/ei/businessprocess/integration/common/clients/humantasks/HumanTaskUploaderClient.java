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


package org.wso2.ei.businessprocess.integration.common.clients.humantasks;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.ei.businessprocess.integration.common.clients.AuthenticateStubUtil;
import org.wso2.carbon.humantask.stub.mgt.PackageManagementException;
import org.wso2.carbon.humantask.stub.upload.HumanTaskUploaderStub;
import org.wso2.carbon.humantask.stub.upload.types.UploadedFileItem;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;
import java.rmi.RemoteException;

public class HumanTaskUploaderClient {
    private static final Log log = LogFactory.getLog(HumanTaskUploaderClient.class);

    private final static String serviceName = "HumanTaskUploader";
    private HumanTaskUploaderStub htUploaderStub;

    public HumanTaskUploaderClient(String serviceEndPoint, String sessionCookie) throws AxisFault {
        final String uploaderServiceURL = serviceEndPoint + serviceName;
        htUploaderStub = new HumanTaskUploaderStub(uploaderServiceURL);
        AuthenticateStubUtil.authenticateStub(sessionCookie, htUploaderStub);
    }

    public HumanTaskUploaderClient(String serviceEndPoint, String username, String password) throws AxisFault {
        final String uploaderServiceURL = serviceEndPoint + serviceName;
        htUploaderStub = new HumanTaskUploaderStub(uploaderServiceURL);
        AuthenticateStubUtil.authenticateStub(username, password, htUploaderStub);
    }

    public boolean deployHumantask(String packageName, String dirPath)
            throws RemoteException, InterruptedException, PackageManagementException {
        boolean success = false;
        deployPackage(packageName, dirPath, htUploaderStub);
        return success;
    }

    private UploadedFileItem getUploadedFileItem(DataHandler dataHandler, String fileName,
                                                 String fileType) {
        UploadedFileItem uploadedFileItem = new UploadedFileItem();
        uploadedFileItem.setDataHandler(dataHandler);
        uploadedFileItem.setFileName(fileName);
        uploadedFileItem.setFileType(fileType);

        return uploadedFileItem;
    }

    private void deployPackage(String packageName, String resourceDir,
                               HumanTaskUploaderStub htUploaderStub)
            throws RemoteException, InterruptedException {

        String sampleArchiveName = packageName + ".zip";
        log.info(resourceDir + File.separator + sampleArchiveName);
        DataSource humantaskDataSource = new FileDataSource(resourceDir + File.separator + sampleArchiveName);
        UploadedFileItem[] uploadedFileItems = new UploadedFileItem[1];
        uploadedFileItems[0] = getUploadedFileItem(new DataHandler(humantaskDataSource),
                sampleArchiveName,
                "zip");
        log.info("Deploying " + sampleArchiveName);
        htUploaderStub.uploadHumanTask(uploadedFileItems);
    }
}
