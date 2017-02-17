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

import org.wso2.carbon.dataservices.samples.batch_request_sample.BatchRequestSampleStub;
import org.wso2.ws.dataservice.samples.batch_request_sample.AddEmployee_batch_req;
import org.wso2.ws.dataservice.samples.batch_request_sample.AddEmployee_type0;
import org.wso2.ws.dataservice.samples.batch_request_sample.DeleteEmployee;
import org.wso2.ws.dataservice.samples.batch_request_sample.DeleteEmployee_type0;
import org.wso2.ws.dataservice.samples.batch_request_sample.EmployeeExists;

public class BatchRequestSample extends BaseSample {

	private static AddEmployee_type0 createEmployee(int id, String email) {
		AddEmployee_type0 val = new AddEmployee_type0();
		val.setEmployeeNumber(id);
		val.setEmail(email);
		printEmployeeInfo(id, email);
		return val;
	}

	private static void deleteEmployee(BatchRequestSampleStub stub, int id) throws Exception {
		DeleteEmployee de = new DeleteEmployee();
		DeleteEmployee_type0 det = new DeleteEmployee_type0();
		det.setEmployeeNumber(id);
		de.setDeleteEmployee(det);
		stub.deleteEmployee(de);
	}

	private static void printEmployeeInfo(int id, String email) {
		System.out.println("Creating - Employee Id: " + id + " Email: " + email);
	}

	private static void printEmployeeExists(BatchRequestSampleStub stub, int id) throws Exception {
		EmployeeExists ee = new EmployeeExists();
		ee.setEmployeeNumber(id);
		String res = stub.employeeExists(ee).getEmployees().getEmployee()[0].getExists();
		System.out.println("Employee with Id: " + id + " Exists: "
				+ (res.equals("1") ? "TRUE" : "FALSE"));
	}

	private static void cleanup(BatchRequestSampleStub stub) throws Exception {
		deleteEmployee(stub, 180000);
		deleteEmployee(stub, 180001);
		deleteEmployee(stub, 180002);
		deleteEmployee(stub, 180003);
		deleteEmployee(stub, 180004);
		deleteEmployee(stub, 180005);
		deleteEmployee(stub, 180006);
		deleteEmployee(stub, 180007);
		deleteEmployee(stub, 180008);
		deleteEmployee(stub, 180009);
		deleteEmployee(stub, 180010);
		deleteEmployee(stub, 180011);
	}

	public static void main(String[] args) throws Exception {
		String epr = "http://" + HOST_IP + ":" + HOST_HTTP_PORT + "/services/samples/BatchRequestSample";
		BatchRequestSampleStub stub = new BatchRequestSampleStub(epr);
		try {
			AddEmployee_batch_req vals1 = new AddEmployee_batch_req();
			/* successful transaction */
			vals1.addAddEmployee(createEmployee(180000, "afaoi@dde.com"));
			vals1.addAddEmployee(createEmployee(180001, "xa@cxs.com"));
			vals1.addAddEmployee(createEmployee(180002, "a@gmail.com"));
			try {
				System.out.println("Executing Add Employees..");
				stub.addEmployee_batch_req(vals1);
			} catch (Exception e) {
				System.out.println("Error in Add Employees!");
			}
			printEmployeeExists(stub, 180000);
			printEmployeeExists(stub, 180001);
			printEmployeeExists(stub, 180002);

			/* unsuccessful transaction */
			AddEmployee_batch_req vals2 = new AddEmployee_batch_req();
			vals2.addAddEmployee(createEmployee(180003, "afaoi@dde.com"));
			vals2.addAddEmployee(createEmployee(180004, "xaxs.com"));
			vals2.addAddEmployee(createEmployee(180005, "a@gmail.com"));
			try {
				System.out.println("Executing Add Employees..");
				stub.addEmployee_batch_req(vals2);
			} catch (Exception e) {
				System.out.println("Error in Add Employees!");
			}
			printEmployeeExists(stub, 180003);
			printEmployeeExists(stub, 180004);
			printEmployeeExists(stub, 180005);

			/* unsuccessful transaction */
			AddEmployee_batch_req vals3 = new AddEmployee_batch_req();
			vals3.addAddEmployee(createEmployee(180006, "afaoi@dde.com"));
			vals3.addAddEmployee(createEmployee(180007, "xa@cxs.com"));
			vals3.addAddEmployee(createEmployee(180008, ""));
			try {
				System.out.println("Executing Add Employees..");
				stub.addEmployee_batch_req(vals3);
			} catch (Exception e) {
				System.out.println("Error in Add Employees!");
			}
			printEmployeeExists(stub, 180006);
			printEmployeeExists(stub, 180007);
			printEmployeeExists(stub, 180008);

			/* successful transaction */
			AddEmployee_batch_req vals4 = new AddEmployee_batch_req();
			vals4.addAddEmployee(createEmployee(180009, "la@ge.com"));
			vals4.addAddEmployee(createEmployee(180010, "g@ge.org"));
			vals4.addAddEmployee(createEmployee(180011, "x@x.net"));
			try {
				System.out.println("Executing Add Employees..");
				stub.addEmployee_batch_req(vals4);
			} catch (Exception e) {
				System.out.println("Error in Add Employees!");
			}
			printEmployeeExists(stub, 180009);
			printEmployeeExists(stub, 180010);
			printEmployeeExists(stub, 180011);
		} catch (Exception e) {
			throw e;
		} finally {
			cleanup(stub);
			System.out.println("Cleanup.");
		}
	}
}
