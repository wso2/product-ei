/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.esb.integration.common.clients.inbound.endpoint;


import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.inbound.stub.InboundAdminInboundManagementException;
import org.wso2.carbon.inbound.stub.InboundAdminStub;
import org.wso2.carbon.inbound.stub.types.carbon.InboundEndpointDTO;
import org.wso2.carbon.inbound.stub.types.carbon.ParameterDTO;
import org.wso2.esb.integration.common.clients.client.utils.AuthenticateStub;

import java.rmi.RemoteException;
import java.util.Map;

public class InboundAdminClient {

	private static final Log log = LogFactory.getLog(InboundAdminClient.class);

	private final String serviceName = "InboundAdmin";
	private InboundAdminStub endpointAdminStub;

	/**
	 * @param backEndUrl    BackEnd URL
	 * @param sessionCookie Session cookie
	 * @throws org.apache.axis2.AxisFault
	 */
	public InboundAdminClient(String backEndUrl, String sessionCookie) throws AxisFault {
		String endPoint = backEndUrl + serviceName;
		endpointAdminStub = new InboundAdminStub(endPoint);
		AuthenticateStub.authenticateStub(sessionCookie, endpointAdminStub);
	}

	/**
	 * @param backEndUrl BackEnd URL
	 * @param userName   Username
	 * @param password   Password
	 * @throws org.apache.axis2.AxisFault
	 */
	public InboundAdminClient(String backEndUrl, String userName, String password) throws AxisFault {
		String endPoint = backEndUrl + serviceName;
		endpointAdminStub = new InboundAdminStub(endPoint);
		AuthenticateStub.authenticateStub(userName, password, endpointAdminStub);
	}


    /**
     * Return all Inbound Endpoint Names
     * @return InboundEndpointNames
     * @throws RemoteException
     * @throws InboundAdminInboundManagementException
     */
    public InboundEndpointDTO[] getAllInboundEndpointNames() throws RemoteException, InboundAdminInboundManagementException {
        try {
            return endpointAdminStub.getAllInboundEndpointNames();
        } catch (RemoteException e) {
            throw new RemoteException("Exception occurred when get endpoint names in InboundAdminClient", e);
        } catch (InboundAdminInboundManagementException e) {
            throw new InboundAdminInboundManagementException
                    ("Exception occurred when get endpoint names in InboundAdminClient", e);
        }
    }


	/**
	 * Return InboundEndpointDTO for specific InboundEndpoint
	 * @param endointName Inbound Endpoint Name
	 * @return InboundEndpointDTO
	 * @throws java.rmi.RemoteException
	 * @throws org.wso2.carbon.inbound.stub.InboundAdminInboundManagementException
	 */
	public InboundEndpointDTO getInboundEndpointbyName(String endointName) throws RemoteException,
	                                                                              InboundAdminInboundManagementException {
		try {
			return endpointAdminStub.getInboundEndpointbyName(endointName);
		} catch (RemoteException e) {
			throw new RemoteException("Exception occurred when get endpoint names in InboundAdminClient", e);
		} catch (InboundAdminInboundManagementException e) {
			throw new InboundAdminInboundManagementException("InboundAdminInboundManagementException occurred when getting " +
			                                                 "Inbound Endpoint by name", e);
		}
	}


    /**
     * Adding Inbound Endpoint to the underlying stub.
     * @param name Inbound Name
     * @param sequence Injecting sequence
     * @param onError Injecting sequence when error occurred
     * @param protocol Running protocol
     * @param classImpl Class for custom Inbounds
     * @throws RemoteException
     * @throws InboundAdminInboundManagementException
     */
    public void addInboundEndpoint(String name, String sequence,
                                   String onError, String protocol, String classImpl,
                                   Map<String, String> paramsMap) throws RemoteException, InboundAdminInboundManagementException {
        try {
            ParameterDTO[] parameterDTOs =null;
            if(paramsMap != null) {
                parameterDTOs = new ParameterDTO[paramsMap.size()];
                int count=0;
                for(String key:paramsMap.keySet()){
                    parameterDTOs[count] = new ParameterDTO();
                    parameterDTOs[count].setName(key);
                    parameterDTOs[count].setValue(paramsMap.get(key));
                    count++;
                }
            }
            endpointAdminStub.addInboundEndpoint(name, sequence, onError, protocol, classImpl,"false", parameterDTOs);
        } catch (RemoteException e) {
            throw new RemoteException("Remote Exception occurred when addInboundEndpoint" + name, e);
        } catch (InboundAdminInboundManagementException e) {
            throw new InboundAdminInboundManagementException("InboundAdminInboundManagementException  when add inbound " +
                                                             "endpoint " + name + " InboundAdmin Client", e);
        }
    }


	/**
	 * Adding Inbound Endpoint to the underlying stub from XMLString
	 * @param element String value of element
	 * @throws java.rmi.RemoteException
	 * @throws org.wso2.carbon.inbound.stub.InboundAdminInboundManagementException
	 */
	public void addInboundEndpoint(String element) throws RemoteException, InboundAdminInboundManagementException {
		try {
			endpointAdminStub.addInboundEndpointFromXMLString(element);
		} catch (RemoteException e) {
			throw new RemoteException("RemoteException when add inbound endpoint InboundAdmin Client", e);
		}
	}


    /**
     * Update InboundEndpoint with given details for InboundEndpoint with given name
     * @param name Inbound Name
     * @param sequence Injecting sequence
     * @param onError Injecting sequence when error occurred
     * @param protocol Running protocol
     * @param classImpl Class for custom Inbounds
     * @throws RemoteException
     * @throws InboundAdminInboundManagementException
     */
    public void updateInboundEndpoint(String name, String sequence,
                                      String onError, String protocol, String classImpl,
                                      Map<String, String> paramsMap) throws RemoteException, InboundAdminInboundManagementException {
        try {
            ParameterDTO[] parameterDTOs =null;
            if(paramsMap != null) {
                parameterDTOs = new ParameterDTO[paramsMap.size()];
                int count = 0;
                for (String key : paramsMap.keySet()) {
                    parameterDTOs[count] = new ParameterDTO();
                    parameterDTOs[count].setName(key);
                    parameterDTOs[count].setValue(paramsMap.get(key));
                    count++;
                }
            }
            endpointAdminStub.updateInboundEndpoint(name, sequence, onError, protocol, classImpl,"false", parameterDTOs);
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException occurred when update inbound endpoint " + name + " InboundAdmin Client", e);
        } catch (InboundAdminInboundManagementException e) {
            throw new InboundAdminInboundManagementException("InboundAdminInboundManagementException when update inbound " +
                                                             "endpoint InboundAdmin Client", e);
        }
    }


	/**
	 * Delete InboundEndpoint with given name
	 * @param name Inbound Name
	 * @throws java.rmi.RemoteException
	 * @throws org.wso2.carbon.inbound.stub.InboundAdminInboundManagementException
	 */
	public void removeInboundEndpoint(String name) throws RemoteException, InboundAdminInboundManagementException {
		try {
			endpointAdminStub.removeInboundEndpoint(name);
		} catch (RemoteException e) {
			throw new RemoteException("RemoteException when removing inbound endpoint " + name + " InboundAdmin Client", e);
		} catch (InboundAdminInboundManagementException e) {
			throw new InboundAdminInboundManagementException("InboundAdminInboundManagementException when removing " +
			                                                 "inbound endpoint " + name + " InboundAdmin Client", e);
		}
	}
}
