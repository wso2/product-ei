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
package org.wso2.bps.integration.common.clients.bpel;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.bps.integration.common.clients.AuthenticateStubUtil;
import org.wso2.carbon.bpel.stub.mgt.BPELPackageManagementServiceStub;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;
import org.wso2.carbon.bpel.stub.upload.BPELUploaderStub;
import org.wso2.carbon.bpel.stub.upload.types.UploadedFileItem;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class BpelUploaderClient {
    private static final Log log = LogFactory.getLog(BpelUploaderClient.class);
    private static final String serviceName = "BPELUploader";
    private BPELUploaderStub bpelUploaderStub;

    public BpelUploaderClient(String serviceEndPoint, String sessionCookie) throws AxisFault {
        String uploaderServiceURL = serviceEndPoint + serviceName;
        bpelUploaderStub = new BPELUploaderStub(uploaderServiceURL);
        AuthenticateStubUtil.authenticateStub(sessionCookie, bpelUploaderStub);
    }

    public BpelUploaderClient(String serviceEndPoint, String username, String password) throws AxisFault {
        String uploaderServiceURL = serviceEndPoint + serviceName;
        bpelUploaderStub = new BPELUploaderStub(uploaderServiceURL);
        AuthenticateStubUtil.authenticateStub(username, password, bpelUploaderStub);
    }

    public boolean deployBPEL(String packageName, String dirPath)
            throws RemoteException, InterruptedException, PackageManagementException {
        boolean success = false;
        deployPackage(packageName, dirPath, bpelUploaderStub);
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
                               BPELUploaderStub bpelUploaderStub)
            throws RemoteException, InterruptedException {

        String sampleArchiveName = packageName + ".zip";
        log.info(resourceDir + File.separator + sampleArchiveName);
        DataSource bpelDataSource = new FileDataSource(resourceDir + File.separator + sampleArchiveName);
        UploadedFileItem[] uploadedFileItems = new UploadedFileItem[1];
        uploadedFileItems[0] = getUploadedFileItem(new DataHandler(bpelDataSource),
                sampleArchiveName,
                "zip");
        log.info("Deploying " + sampleArchiveName);
        bpelUploaderStub.uploadService(uploadedFileItems);
    }
}
