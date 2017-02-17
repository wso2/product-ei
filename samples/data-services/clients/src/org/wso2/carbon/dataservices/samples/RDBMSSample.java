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

import org.wso2.carbon.dataservices.samples.rdbms_sample.RDBMSSampleStub;
import org.wso2.ws.dataservice.samples.rdbms_sample.Customer;
import org.wso2.ws.dataservice.samples.rdbms_sample.Employee;

public class RDBMSSample extends BaseSample {

	public static void main(String[] args) throws Exception {
		String epr = "http://" + HOST_IP + ":" + HOST_HTTP_PORT + "/services/samples/RDBMSSample";
		org.wso2.carbon.dataservices.samples.rdbms_sample.RDBMSSample stub =
                new RDBMSSampleStub(epr);
		Customer[] customers = stub.customersInBoston();
		System.out.println("EPR: " + epr + "\n");
		System.out.println("Customers:-");
		for (Customer customer : customers) {
			System.out.println("\t-----------------------------");
			System.out.println("\tContact Name: " + customer.getContactFirstName() + " " +
                               customer.getContactLastName());
			System.out.println("\tCity: " + customer.getCity());
			System.out.println("\tCountry: " + customer.getCountry());
			System.out.println("\tCustomer Name: " + customer.getCustomerName());
			System.out.println("\tPhone: " + customer.getPhone());
		}
		
		System.out.println("\nRetrieving Employee info with Employee ID 1002:-");
		System.out.println("\t-----------------------------");
		Employee empl = stub.employeesByNumber(1002)[0];		
		System.out.println("\tName: " + empl.getFirstName() + " " + empl.getLastName());
		System.out.println("\tEmployee Number: " + empl.getEmail());
	}
	
}
