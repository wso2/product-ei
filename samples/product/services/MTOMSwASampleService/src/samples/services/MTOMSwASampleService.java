/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package samples.services;

import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.attachments.Attachments;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import java.io.*;

public class MTOMSwASampleService {

    private static final int BUFFER = 2048;

    public OMElement uploadFileUsingMTOM(OMElement request) throws Exception {

        OMText binaryNode = (OMText) request.
            getFirstChildWithName(new QName("http://services.samples", "request")).
            getFirstChildWithName(new QName("http://services.samples", "image")).
            getFirstOMChild();
        DataHandler dataHandler = (DataHandler) binaryNode.getDataHandler();
        InputStream is = dataHandler.getInputStream();

        File tempFile = File.createTempFile("mtom-", ".gif");
        FileOutputStream fos = new FileOutputStream(tempFile);
        BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

        byte data[] = new byte[BUFFER];
        int count;
        while ((count = is.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, count);
        }

        dest.flush();
        dest.close();
        System.out.println("Wrote MTOM content to temp file : " + tempFile.getAbsolutePath());

        OMFactory factory = request.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");
        OMElement payload  = factory.createOMElement("uploadFileUsingMTOMResponse", ns);
        OMElement response = factory.createOMElement("response", ns);
        OMElement image    = factory.createOMElement("image", ns);

        FileDataSource fileDataSource = new FileDataSource(tempFile);
        dataHandler = new DataHandler(fileDataSource);
        OMText textData = factory.createOMText(dataHandler, true);
        image.addChild(textData);
        response.addChild(image);
        payload.addChild(response);

        MessageContext outMsgCtx = MessageContext.getCurrentMessageContext()
            .getOperationContext().getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
        outMsgCtx.setProperty(
            org.apache.axis2.Constants.Configuration.ENABLE_MTOM,
            org.apache.axis2.Constants.VALUE_TRUE);

        return payload;
    }

    public OMElement uploadFileUsingSwA(OMElement request) throws Exception {

        String imageContentId = request.
            getFirstChildWithName(new QName("http://services.samples", "request")).
            getFirstChildWithName(new QName("http://services.samples", "imageId")).
            getText();

        MessageContext msgCtx   = MessageContext.getCurrentMessageContext();
        Attachments attachment  = msgCtx.getAttachmentMap();
        DataHandler dataHandler = attachment.getDataHandler(imageContentId);
        File tempFile = File.createTempFile("swa-", ".gif");
        FileOutputStream fos = new FileOutputStream(tempFile);
        dataHandler.writeTo(fos);
		fos.flush();
		fos.close();
        System.out.println("Wrote SwA attachment to temp file : " + tempFile.getAbsolutePath());

        MessageContext outMsgCtx = msgCtx.getOperationContext().
            getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
        outMsgCtx.setProperty(
            org.apache.axis2.Constants.Configuration.ENABLE_SWA,
            org.apache.axis2.Constants.VALUE_TRUE);

        OMFactory factory = request.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");
        OMElement payload  = factory.createOMElement("uploadFileUsingSwAResponse", ns);
        OMElement response = factory.createOMElement("response", ns);
        OMElement imageId  = factory.createOMElement("imageId", ns);

        FileDataSource fileDataSource = new FileDataSource(tempFile);
        dataHandler = new DataHandler(fileDataSource);
        imageContentId = outMsgCtx.addAttachment(dataHandler);
        imageId.setText(imageContentId);
        response.addChild(imageId);
        payload.addChild(response);

        return payload;
    }

    public void oneWayUploadUsingMTOM(OMElement element) throws Exception {

        OMText binaryNode = (OMText) element.getFirstOMChild();
        DataHandler dataHandler = (DataHandler) binaryNode.getDataHandler();
        InputStream is = dataHandler.getInputStream();

        File tempFile = File.createTempFile("mtom-", ".gif");
        FileOutputStream fos = new FileOutputStream(tempFile);
        BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

        byte data[] = new byte[BUFFER];
        int count;
        while ((count = is.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, count);
        }

        dest.flush();
        dest.close();
        System.out.println("Wrote to file : " + tempFile.getAbsolutePath());
    }
}
