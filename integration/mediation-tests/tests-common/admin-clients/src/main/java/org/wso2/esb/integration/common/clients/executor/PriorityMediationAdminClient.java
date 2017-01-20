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

package org.wso2.esb.integration.common.clients.executor;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.priority.executors.stub.PriorityMediationAdminStub;
import org.wso2.esb.integration.common.clients.client.utils.AuthenticateStub;

import javax.activation.DataHandler;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.rmi.RemoteException;

public class PriorityMediationAdminClient {

    private static final Log log = LogFactory.getLog(PriorityMediationAdminClient.class);
    private String serviceName ="PriorityMediationAdmin";

    String backendUrl = null;
    String SessionCookie = null;
    PriorityMediationAdminStub priorityMediationAdmin;

    public PriorityMediationAdminClient(String backendUrl, String sessionCookie) throws AxisFault {
        this.backendUrl = backendUrl;
        this.SessionCookie = sessionCookie;
        String targetEndpoint = backendUrl+serviceName;
        priorityMediationAdmin = new PriorityMediationAdminStub(targetEndpoint);
        AuthenticateStub.authenticateStub(SessionCookie, priorityMediationAdmin);
    }

    public void addPriorityMediator(String name, DataHandler dh)
            throws IOException, XMLStreamException {
        XMLStreamReader parser =
                XMLInputFactory.newInstance().createXMLStreamReader(dh.getInputStream());
        StAXOMBuilder builder = new StAXOMBuilder(parser);
        OMElement messageProcessorElem = builder.getDocumentElement();
        priorityMediationAdmin.add(name, messageProcessorElem);
    }

    public void addPriorityMediator(String name, OMElement priorityExecutor) throws RemoteException {
        priorityMediationAdmin.add(name, priorityExecutor);
    }

    public String[] getExecutorList() throws RemoteException {
        return priorityMediationAdmin.getExecutorList();
    }

    public void remove(String executorName) throws RemoteException {
        priorityMediationAdmin.remove(executorName);
    }

}
