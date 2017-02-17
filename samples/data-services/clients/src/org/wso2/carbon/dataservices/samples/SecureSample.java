/*
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.dataservices.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.wso2.carbon.dataservices.samples.secure_dataservice.SecureDataServiceStub;
import org.wso2.ws.dataservice.samples.secure_dataservice.Office;

public class SecureSample extends BaseSample {
	
    private static Policy loadPolicy(String path) throws Exception {
        InputStream resource = new FileInputStream(path);
        StAXOMBuilder builder = new StAXOMBuilder(resource);
        return PolicyEngine.getPolicy(builder.getDocumentElement());
    }
	
	public static void main(String[] args) throws Exception {
		String epr = "https://" + HOST_IP + ":" + HOST_HTTPS_PORT + "/services/samples/SecureDataService";
		System.setProperty("javax.net.ssl.trustStore", (new File(CLIENT_JKS_PATH)).getAbsolutePath());
		ConfigurationContext ctx = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem(null, null);
                SecureDataServiceStub stub = new SecureDataServiceStub(ctx, epr);
		ServiceClient client = stub._getServiceClient();
		Options options = client.getOptions();
		client.engageModule("rampart");		
		options.setUserName("admin");
		options.setPassword("admin");

		options.setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy(SECURITY_POLICY_PATH));
		Office[] offices = stub.showAllOffices();
		for (Office office : offices) {
			System.out.println("\t-----------------------------");
			System.out.println("\tOffice Code: " + office.getOfficeCode());
			System.out.println("\tPhone: " + office.getPhone());
			System.out.println("\tAddress Line 1: " + office.getAddressLine1());
			System.out.println("\tAddress Line 2: " + office.getAddressLine2());
			System.out.println("\tCity: " + office.getCity());			
			System.out.println("\tState: " + office.getState());
			System.out.println("\tPostal Code: " + office.getPostalCode());
			System.out.println("\tCountry: " + office.getCountry());
		}
	}
	
}
