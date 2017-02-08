/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.bps.humantask.sample.clients;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.databinding.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.bps.humantask.sample.util.HumanTaskSampleConstants;
import org.wso2.carbon.humantask.stub.ui.task.client.api.HumanTaskClientAPIAdminStub;
import org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalAccessFault;
import org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalArgumentFault;
import org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalOperationFault;
import org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalStateFault;
import org.wso2.carbon.humantask.stub.ui.task.client.api.types.TSimpleQueryInput;
import org.wso2.carbon.humantask.stub.ui.task.client.api.types.TTaskAbstract;
import org.wso2.carbon.humantask.stub.ui.task.client.api.types.TTaskSimpleQueryResultSet;

import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

public class HumanTaskClientAPIServiceClient {

    private static Log log = LogFactory.getLog(HumanTaskClientAPIServiceClient.class);
    private HumanTaskClientAPIAdminStub stub;

    public HumanTaskClientAPIServiceClient(String cookie, String backendServerURL, ConfigurationContext configContext)
            throws AxisFault {
        String serviceURL = backendServerURL + HumanTaskSampleConstants.TASK_OPERATIONS_SERVICE;
        stub = new HumanTaskClientAPIAdminStub(configContext, serviceURL);
        ServiceClient client = stub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
    }

    public TTaskSimpleQueryResultSet taskListQuery(TSimpleQueryInput queryInput)
            throws RemoteException, IllegalArgumentFault, IllegalStateFault, IllegalOperationFault, IllegalAccessFault {
        String errMsg = "Error occurred while performing taskListQuery operation";
        try {
            return stub.simpleQuery(queryInput);
        } catch (Exception ex) {
            handleException(errMsg, ex);
    }
        return null;
    }

    /**
     *
     * Load task data for the give task id.
     *
     * @param taskId :
     * @return :
     * @throws java.rmi.RemoteException    :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalAccessFault :
     */
    public TTaskAbstract loadTask(URI taskId)
            throws RemoteException, IllegalAccessFault, IllegalOperationFault, IllegalArgumentFault, IllegalStateFault {
        String errMsg = "Error occurred while performing loadTask operation";
        try {
            return stub.loadTask(taskId);
        } catch (Exception ex) {
            handleException(errMsg, ex);
        }
        return null;
    }


    /**
     * Task complete operation.
     *
     * @param taskId  : The task id to be completed.
     * @param payLoad : The payload.
     * @throws java.rmi.RemoteException       :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalAccessFault    :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalArgumentFault  :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalStateFault     :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalOperationFault :
     * @throws javax.xml.stream.XMLStreamException    :
     */
    public void complete(URI taskId, String payLoad)
            throws RemoteException, IllegalAccessFault, IllegalArgumentFault, IllegalStateFault, IllegalOperationFault,
                   XMLStreamException {
        String errMsg = "Error occurred while performing complete operation";
        try {
            String decodedPayload = decodeHTML(payLoad);
            stub.complete(taskId, decodedPayload);
        } catch (Exception ex) {
            handleException(errMsg, ex);
        }
    }

    /**
     * Loads the task input.
     *
     * @param taskId : The id of the task/.
     * @return : The task input OMElement.
     * @throws java.rmi.RemoteException                     :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalStateFault                   :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalOperationFault               :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalAccessFault                  :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalArgumentFault                :
     * @throws javax.xml.stream.XMLStreamException :
     */
    public OMElement loadTaskInput(URI taskId)
            throws RemoteException, IllegalStateFault, IllegalOperationFault, IllegalAccessFault, IllegalArgumentFault,
                   XMLStreamException {
        String errMsg = "Error occurred while performing loadTaskInput operation";
        try {
            String input = (String) stub.getInput(taskId, null);
            return AXIOMUtil.stringToOM(input);
        } catch (Exception ex) {
            handleException(errMsg, ex);
        }
        return null;
    }

    /**
     * Loads the task output.
     *
     * @param taskId : The id of the task/.
     * @return : The task input OMElement.
     * @throws java.rmi.RemoteException                     :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalStateFault                   :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalOperationFault               :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalAccessFault                  :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalArgumentFault                :
     * @throws javax.xml.stream.XMLStreamException :
     */
    public OMElement loadTaskOutput(URI taskId)
            throws RemoteException, IllegalStateFault, IllegalOperationFault, IllegalAccessFault, IllegalArgumentFault,
                   XMLStreamException {
        String errMsg = "Error occurred while performing loadTaskOutput operation";
        try {
            String output = (String) stub.getOutput(taskId, null);
            return AXIOMUtil.stringToOM(output);
        } catch (Exception ex) {
            handleException(errMsg, ex);
        }
        return null;
    }

    /**
     * Claim task operation.
     *
     * @param taskId : The ID of the task to be claimed.
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalArgumentFault  :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalAccessFault    :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalStateFault     :
     * @throws java.rmi.RemoteException       :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalOperationFault :
     */
    public void claim(URI taskId)
            throws IllegalArgumentFault, IllegalAccessFault, IllegalStateFault, RemoteException, IllegalOperationFault {
        String errMsg = "Error occurred while performing claim operation";
        try {
            stub.claim(taskId);
        } catch (Exception ex) {
            handleException(errMsg, ex);
        }
    }

    public void start(URI taskId)
            throws RemoteException, IllegalStateFault, IllegalOperationFault, IllegalArgumentFault, IllegalAccessFault {
        String errMsg = "Error occurred while performing start operation";
        try {
            stub.start(taskId);
        } catch (Exception ex) {
            handleException(errMsg, ex);
        }
    }

    /**
     * Stop task.
     *
     * @param taskId : The task Id.
     * @throws java.rmi.RemoteException       :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalStateFault     :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalOperationFault :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalArgumentFault  :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalAccessFault    :
     */
    public void stop(URI taskId)
            throws RemoteException, IllegalStateFault, IllegalOperationFault, IllegalArgumentFault, IllegalAccessFault {
        String errMsg = "Error occurred while performing stop operation";
        try {
            stub.stop(taskId);
        } catch (Exception ex) {
            handleException(errMsg, ex);
        }
    }

    /**
     * Task remove operation. Note: applicable for notifications only.
     *
     * @param taskId : The id of the task to be removed.
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalArgumentFault  :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalOperationFault :
     * @throws org.wso2.carbon.humantask.stub.ui.task.client.api.IllegalAccessFault    :
     * @throws java.rmi.RemoteException       :
     */
    public void remove(URI taskId)
            throws IllegalArgumentFault, IllegalOperationFault, IllegalAccessFault, RemoteException, IllegalStateFault {
        String errMsg = "Error occurred while performing resume operation";
        try {
            stub.remove(taskId);
        } catch (Exception ex) {
            handleException(errMsg, ex);
        }
    }

    public static String decodeHTML(String encodedString) {
        // Replace "&lt;" &gt; and &amp; with <,>,& in input String.
        return encodedString.replace("&lt;", "<").replace("&gt;", ">")
                .replace("&amp;", "&");

    }

    private void handleException(String errMsg, Exception ex)
            throws IllegalStateFault, IllegalOperationFault, IllegalArgumentFault,
                   IllegalAccessFault {
        if (ex instanceof IllegalAccessFault) {
            throw new IllegalAccessFault(errMsg, ex);
        } else if (ex instanceof IllegalArgumentFault) {
            throw new IllegalArgumentFault(errMsg, ex);
        } else if (ex instanceof IllegalOperationFault) {
            throw new IllegalOperationFault(errMsg, ex);
        } else if (ex instanceof IllegalStateFault) {
            throw new IllegalStateFault(errMsg, ex);
        } else {
            throw new IllegalStateFault(errMsg, ex);
        }
    }
}
