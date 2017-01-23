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

package samples.userguide;

import org.apache.axiom.om.*;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.attachments.Attachments;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;

import javax.activation.FileDataSource;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.io.*;

public class MTOMSwAClient {

    private static final int BUFFER = 2048;

    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0) {
            result = def;
        }
        return result;
    }

    private static ServiceClient createServiceClient() throws AxisFault {
        String repo = getProperty("repository", "client_repo");
        if (repo != null && !"null".equals(repo)) {
            ConfigurationContext configContext =
                    ConfigurationContextFactory.
                            createConfigurationContextFromFileSystem(repo,
                                    repo + File.separator + "conf" + File.separator + "axis2.xml");
            return new ServiceClient(configContext, null);
        } else {
            return new ServiceClient();
        }
    }

    public static void main(String[] args) throws Exception {

        String targetEPR = getProperty("opt_url", "http://localhost:8280/services/MTOMSwASampleService");
        String fileName = getProperty("opt_file", "./../../repository/samples/resources/mtom/asf-logo.gif");
        String mode = getProperty("opt_mode", "mtom");

        if (args.length > 0) mode = args[0];
        if (args.length > 1) targetEPR = args[1];
        if (args.length > 2) fileName = args[2];

        if ("mtom".equals(mode)) {
            sendUsingMTOM(fileName, targetEPR);
        } else if ("swa".equals(mode)) {
            sendUsingSwA(fileName, targetEPR);
        }

        // let the server read the stream before exit
        Thread.sleep(1000);
        System.exit(0);
    }

    public static MessageContext sendUsingSwA(String fileName, String targetEPR) throws IOException {

        Options options = new Options();
        options.setTo(new EndpointReference(targetEPR));
        options.setAction("urn:uploadFileUsingSwA");
        options.setProperty(Constants.Configuration.ENABLE_SWA, Constants.VALUE_TRUE);

        ServiceClient sender = createServiceClient();
        sender.setOptions(options);
        OperationClient mepClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);

        MessageContext mc = new MessageContext();

        System.out.println("Sending file : " + fileName + " as SwA");
        FileDataSource fileDataSource = new FileDataSource(new File(fileName));
        DataHandler dataHandler = new DataHandler(fileDataSource);
        String attachmentID = mc.addAttachment(dataHandler);


        SOAPFactory factory = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope env = factory.getDefaultEnvelope();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");
        OMElement payload = factory.createOMElement("uploadFileUsingSwA", ns);
        OMElement request = factory.createOMElement("request", ns);
        OMElement imageId = factory.createOMElement("imageId", ns);
        imageId.setText(attachmentID);
        request.addChild(imageId);
        payload.addChild(request);
        env.getBody().addChild(payload);
        mc.setEnvelope(env);

        mepClient.addMessageContext(mc);
        mepClient.execute(true);
        MessageContext response = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

        SOAPBody body = response.getEnvelope().getBody();
        String imageContentId = body.
                getFirstChildWithName(new QName("http://services.samples", "uploadFileUsingSwAResponse")).
                getFirstChildWithName(new QName("http://services.samples", "response")).
                getFirstChildWithName(new QName("http://services.samples", "imageId")).
                getText();

        Attachments attachment = response.getAttachmentMap();
        dataHandler = attachment.getDataHandler(imageContentId);
        File tempFile = File.createTempFile("swa-", ".gif");
        FileOutputStream fos = new FileOutputStream(tempFile);
        dataHandler.writeTo(fos);
        fos.flush();
        fos.close();

        System.out.println("Saved response to file : " + tempFile.getAbsolutePath());

        return response;
    }

    public static OMElement sendUsingMTOM(String fileName, String targetEPR) throws IOException {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");
        OMElement payload = factory.createOMElement("uploadFileUsingMTOM", ns);
        OMElement request = factory.createOMElement("request", ns);
        OMElement image = factory.createOMElement("image", ns);

        System.out.println("Sending file : " + fileName + " as MTOM");
        FileDataSource fileDataSource = new FileDataSource(new File(fileName));
        DataHandler dataHandler = new DataHandler(fileDataSource);
        OMText textData = factory.createOMText(dataHandler, true);
        image.addChild(textData);
        request.addChild(image);
        payload.addChild(request);

        ServiceClient serviceClient = createServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(targetEPR));
        options.setAction("urn:uploadFileUsingMTOM");
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);

        serviceClient.setOptions(options);
        OMElement response = serviceClient.sendReceive(payload);

        OMText binaryNode = (OMText) response.
                getFirstChildWithName(new QName("http://services.samples", "response")).
                getFirstChildWithName(new QName("http://services.samples", "image")).
                getFirstOMChild();
        dataHandler = (DataHandler) binaryNode.getDataHandler();
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
        System.out.println("Saved response to file : " + tempFile.getAbsolutePath());
        return response;
    }
}
