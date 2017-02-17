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

import org.wso2.carbon.dataservices.samples.gspread_sample_service.GSpreadSampleStub;
import org.wso2.ws.dataservice.samples.gspread_sample_service.Customer;

public class GSpreadSample extends BaseSample {

	public static void main(String[] args) throws Exception {
		String epr = "http://" + HOST_IP + ":" + HOST_HTTP_PORT + "/services/samples/GSpreadSample";
		org.wso2.carbon.dataservices.samples.gspread_sample_service.GSpreadSample stub =
                new GSpreadSampleStub(epr);
		Customer[] customers = stub.getCustomers();
		System.out.println("EPR: " + epr + "\n");
		System.out.println("Products:-");
		for (Customer customer : customers) {
			System.out.println("\t-----------------------------");
			System.out.println("\tCustomer Name: " + customer.getCustomerName());
			System.out.println("\tCustomer Number: " + customer.getCustomerNumber());
			System.out.println("\tCity: " + customer.getCity());
		}
	}
	
}
