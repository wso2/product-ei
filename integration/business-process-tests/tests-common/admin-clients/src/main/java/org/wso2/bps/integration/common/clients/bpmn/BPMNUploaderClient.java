/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wso2.bps.integration.common.clients.bpmn;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.bps.integration.common.clients.AuthenticateStubUtil;
import org.wso2.carbon.bpmn.core.mgt.model.xsd.UploadedFileItem;
import org.wso2.carbon.bpmn.stub.BPMNUploaderServiceStub;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;
import java.rmi.RemoteException;

/**
 * Client handle the process archive uploading
 */
public class BPMNUploaderClient {
    private static final Log log = LogFactory.getLog(BPMNUploaderClient.class);

    private final static String serviceName = "BPMNUploaderService";
    private BPMNUploaderServiceStub bpmnUploaderServiceStub;


    public BPMNUploaderClient(String serviceEndPoint, String sessionCookie) throws AxisFault {
        final String uploaderServiceURL = serviceEndPoint + serviceName;
        bpmnUploaderServiceStub = new BPMNUploaderServiceStub(uploaderServiceURL);
        AuthenticateStubUtil.authenticateStub(sessionCookie, bpmnUploaderServiceStub);
    }

    public boolean deployBPMN(String packageName, String dirPath)
            throws RemoteException, InterruptedException {

        deployPackage(packageName, dirPath, bpmnUploaderServiceStub);
        return false;
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
                               BPMNUploaderServiceStub bpmnUploaderServiceStub)
            throws RemoteException, InterruptedException {

        String sampleArchiveName = packageName + ".bar";
        log.info(resourceDir + File.separator + sampleArchiveName);
        DataSource BPMNDataSource = new FileDataSource(resourceDir + File.separator + sampleArchiveName);
        UploadedFileItem[] uploadedFileItems = new UploadedFileItem[1];
        uploadedFileItems[0] = getUploadedFileItem(new DataHandler(BPMNDataSource),
                sampleArchiveName,
                "bar");
        log.info("Deploying " + sampleArchiveName);
        bpmnUploaderServiceStub.uploadService(uploadedFileItems);
    }
}
