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
package org.wso2.ei.businessprocess.integration.common.clients.bpel;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.wso2.ei.businessprocess.integration.common.clients.AuthenticateStubUtil;
import org.wso2.carbon.bpel.stub.mgt.InstanceManagementException;
import org.wso2.carbon.bpel.stub.mgt.InstanceManagementServiceStub;
import org.wso2.carbon.bpel.stub.mgt.types.InstanceInfoType;
import org.wso2.carbon.bpel.stub.mgt.types.LimitedInstanceInfoType;
import org.wso2.carbon.bpel.stub.mgt.types.PaginatedInstanceList;
import org.wso2.carbon.bpel.stub.mgt.types.VariableInfoType;
import org.wso2.carbon.bpel.stub.mgt.types.Variables_type0;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public class BpelInstanceManagementClient {
    private static final Log log = LogFactory.getLog(BpelInstanceManagementClient.class);

    private static final String EMPTY_STRING = "";
    private static final String INSTANCE_MANAGEMENT_SERVICE = "InstanceManagementService";
    private InstanceManagementServiceStub instanceManagementServiceStub = null;

    /**
     * Registers the text to display in a tool tip.   The text displays when the cursor lingers over the component.
     *
     * @param serviceEndPoint the string to display.  If the text is null, the tool tip is turned off for this
     *                        component.
     */
    public BpelInstanceManagementClient(String serviceEndPoint, String sessionCookie) throws AxisFault {
        final String packageMgtServiceUrl = serviceEndPoint + INSTANCE_MANAGEMENT_SERVICE;
        instanceManagementServiceStub = new InstanceManagementServiceStub(packageMgtServiceUrl);
        AuthenticateStubUtil.authenticateStub(sessionCookie, instanceManagementServiceStub);
    }

    public BpelInstanceManagementClient(String serviceEndPoint, String username, String password) throws AxisFault {
        final String packageMgtServiceUrl = serviceEndPoint + INSTANCE_MANAGEMENT_SERVICE;
        instanceManagementServiceStub = new InstanceManagementServiceStub(packageMgtServiceUrl);
        AuthenticateStubUtil.authenticateStub(username, password, instanceManagementServiceStub);
    }

    public PaginatedInstanceList listAllInstances()
            throws InstanceManagementException, RemoteException {

        PaginatedInstanceList paginatedInstanceList = null;
        /** The filter set on the is not filtering appropriate services as the filter is ment . it is require to filer the service manually**/
        paginatedInstanceList = instanceManagementServiceStub.getPaginatedInstanceList(EMPTY_STRING, EMPTY_STRING, 300, 0);
        return paginatedInstanceList;
    }

    public PaginatedInstanceList filterPageInstances(String processId)
            throws InstanceManagementException, RemoteException {

        PaginatedInstanceList paginatedInstanceList = null;
        PaginatedInstanceList filteredInstanceList = null;
        /** The filter set on the is not filtering appropriate services as the filter is ment . it is require to filer the service manually**/
        paginatedInstanceList = instanceManagementServiceStub.getPaginatedInstanceList(EMPTY_STRING, EMPTY_STRING, 300, 0);
        filteredInstanceList = new PaginatedInstanceList();
        if (paginatedInstanceList.isInstanceSpecified()) {
            for (LimitedInstanceInfoType instance : paginatedInstanceList.getInstance()) {
                if (instance.getPid().toString().contains(processId)) {
                    filteredInstanceList.addInstance(instance);
                }
            }
        }
        return filteredInstanceList;
    }

    //Return all instances
    public List<String> listInstances() throws InstanceManagementException, RemoteException {
        return listInstances(null);
    }

    public List<String> listInstances(int expectedInstanceCount) throws InstanceManagementException, RemoteException {
        return listInstances(null, expectedInstanceCount);
    }

    //Return all instance which are related to given process ID
    public List<String> listInstances(String processId)
            throws RemoteException, InstanceManagementException {
        List<String> instanceIds = new ArrayList<String>();
        PaginatedInstanceList paginatedInstanceList = getPaginatedInstanceList(processId, EMPTY_STRING, 300, 0);
        if (paginatedInstanceList.isInstanceSpecified()) {
            for (LimitedInstanceInfoType instance : paginatedInstanceList.getInstance()) {
                instanceIds.add(instance.getIid());
                log.info("ProcessId: " + instance.getPid() +
                        "\nInstanceId: " + instance.getIid() +
                        "\nStarted: " + instance.getDateStarted().getTime() +
                        "\nState: " + instance.getStatus() +
                        "\nLast-Active: " + instance.getDateLastActive().getTime() + "\n");
            }
        }
        return instanceIds;
    }

    public List<String> listInstances(String processId, int expectedInstanceCount) throws InstanceManagementException, RemoteException {
        List<String> instanceIds = new ArrayList<String>();
        PaginatedInstanceList paginatedInstanceList = getPaginatedInstanceList(processId, EMPTY_STRING, 300, 0);
        boolean isExpectInstanceFound = false;
        if (paginatedInstanceList.isInstanceSpecified() &&
                paginatedInstanceList.getInstance().length == expectedInstanceCount) {
            isExpectInstanceFound = true;
            for (LimitedInstanceInfoType instance : paginatedInstanceList.getInstance()) {
                instanceIds.add(instance.getIid());
                log.info("ProcessId: " + instance.getPid() +
                        "\nInstanceId: " + instance.getIid() +
                        "\nStarted: " + instance.getDateStarted().getTime() +
                        "\nState: " + instance.getStatus() +
                        "\nLast-Active: " + instance.getDateLastActive().getTime() + "\n");
            }
        } else if (!paginatedInstanceList.isInstanceSpecified() && expectedInstanceCount == 0) {
            log.info("No instances found as expected");
            isExpectInstanceFound = true;
        }
        Assert.assertFalse(!isExpectInstanceFound, "Expected instance count " + expectedInstanceCount +
                " is not there in the server");
        return instanceIds;
    }

    private PaginatedInstanceList getPaginatedInstanceList(String processId, String order, int limit, int page)
            throws InstanceManagementException, RemoteException {
        String filter;
        if (processId == null) {
            filter = EMPTY_STRING;
        } else {
            filter = "pid=" + processId;
        }

        //TODO Check this comment ?
        /** The filter set on the is not filtering appropriate services as the filter is ment . it is require to filer the service manually**/
        return instanceManagementServiceStub.getPaginatedInstanceList(filter, order, limit, page);
    }

    public int deleteInstance(String instanceId)
            throws org.wso2.carbon.bpel.stub.mgt.InstanceManagementException, RemoteException {
        String instanceFilter = "IID=" + instanceId;
        log.info("Deleting instance " + instanceId);
        return instanceManagementServiceStub.deleteInstances(instanceFilter, true);
    }

    public void deleteAllInstances() throws InstanceManagementException, RemoteException {
        log.info("Deleting all the instances");
        instanceManagementServiceStub.deleteInstances(" ", true);
        listInstances(0);
    }

    public void clearInstancesOfProcess(String processId)
            throws InstanceManagementException, RemoteException {
        PaginatedInstanceList instanceList = filterPageInstances(processId);
        for (LimitedInstanceInfoType instanceInfo : instanceList.getInstance()) {
            deleteInstance(instanceInfo.getIid());
        }
    }

    public InstanceInfoType getInstanceInfo(String instanceId)
            throws RemoteException, InstanceManagementException {
        InstanceInfoType instanceInfo = null;
        instanceInfo = instanceManagementServiceStub.
                getInstanceInfo(Long.parseLong(instanceId));
        return instanceInfo;
    }

    public void assertStatus(String status, List<String> instanceIds) throws InstanceManagementException, RemoteException {
        for (String iid : instanceIds) {
            InstanceInfoType instanceInfo = instanceManagementServiceStub.getInstanceInfo(Long.parseLong(iid));
            if (status != null) {
                log.info("Validating instance status, expected: " + status +
                        " actual: " + instanceInfo.getStatus());
                Assert.assertFalse(!instanceInfo.getStatus().getValue().equals(status.toUpperCase()), "Status of instance " + iid + " is not equal to " + status +
                        " but " + instanceInfo.getStatus().getValue());
            }
        }
    }

    public void assertVariable(String variableName, String expectedVarValue, List<String> instanceIds) throws InstanceManagementException, RemoteException {
        if (variableName != null && instanceIds != null) {
            for (String iid : instanceIds) {
                boolean isVariableFound = false;
                InstanceInfoType instanceInfo = instanceManagementServiceStub.getInstanceInfo(Long.parseLong(iid));
                Variables_type0 variables = instanceInfo.getRootScope().getVariables();
                VariableInfoType[] variableList = variables.getVariableInfo();
                for (VariableInfoType variable : variableList) {
                    String varName = variable.getSelf().getName();
                    if (varName.equals(variableName)) {
                        isVariableFound = true;
                        StringBuffer tempValueBuffer = new StringBuffer();
                        for (OMElement varElement : variable.getValue().getExtraElement()) {
                            tempValueBuffer.append(varElement.toString());
                        }
                        String varValue = tempValueBuffer.toString();
                        if (!varValue.contains(expectedVarValue)) {
                            Assert.fail("Incorrect Test Result: " + varValue +
                                    " Expected" + expectedVarValue + "in the result");
                        }
                    }
                }
                Assert.assertFalse(!isVariableFound, variableName + " variable not found in instance " + iid);
            }
        } else {
            Assert.fail("Test parameters can't be Null");
        }
    }

    public boolean assertInstanceInfo(String status, String variableName, String expectedVarValue,
                                      List<String> instanceIds)
            throws RemoteException, InstanceManagementException {
        boolean variableFound = false;
        for (String iid : instanceIds) {
            InstanceInfoType instanceInfo = instanceManagementServiceStub.
                    getInstanceInfo(Long.parseLong(iid));
            if (status != null) {
                log.info("Validating instance status, expected: " + status +
                        " actual: " + instanceInfo.getStatus());
            }
            if (variableName == null) {
                variableFound = true;
            } else {
                Variables_type0 variables = instanceInfo.getRootScope().getVariables();
                VariableInfoType[] variableList = variables.getVariableInfo();
                for (VariableInfoType variable : variableList) {
                    String varName = variable.getSelf().getName();
                    String varValue = null;
                    for (OMElement varElement : variable.getValue().getExtraElement()) {
                        if (varValue == null) {
                            varValue = varElement.toString();
                        } else {
                            varValue += varElement.toString();
                        }

                        if (expectedVarValue != null) {
                            if (varName.equals(variableName)) {
                                if (varValue.contains(expectedVarValue)) {
                                    variableFound = true;
                                } else {
                                    log.info("Incorrect Test Result: " + varValue +
                                            " Expected" + expectedVarValue + "in the result");
                                }
                            }
                        } else {
                            variableFound = true;
                        }
                        log.info("Variable name: " + varName + "\nVariable Value: " +
                                varValue);
                    }
                }
            }
        }
        return variableFound;
    }


    public void performAction(String instanceId, InstanceOperation operation)
            throws RemoteException, InstanceManagementException {
        switch (operation) {
            case SUSPEND:
                instanceManagementServiceStub.suspendInstance(Long.parseLong(instanceId));
                break;
            case RESUME:
                instanceManagementServiceStub.resumeInstance(Long.parseLong(instanceId));
                break;
            case TERMINATE:
                instanceManagementServiceStub.terminateInstance(Long.parseLong(instanceId));
                break;
        }
    }

    public static enum InstanceOperation {
        SUSPEND,
        RESUME,
        TERMINATE
    }

}
