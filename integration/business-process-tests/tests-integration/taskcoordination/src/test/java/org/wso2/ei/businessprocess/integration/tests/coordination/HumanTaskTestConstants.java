/*
 * Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.ei.businessprocess.integration.tests.coordination;

/**
 *
 */
public final class HumanTaskTestConstants {

    //User Credentials
    public static final String CLERK1_USER = "clerk1";
    public static final String CLERK1_PASSWORD = "clerk1password";
    public static final String CLERK2_USER = "clerk2";
    public static final String CLERK2_PASSWORD = "clerk2password";
    public static final String MANAGER1_USER = "manager1";
    public static final String MANAGER1_PASSWORD = "manager1password";

    //Credentials for HT-Coordination Test suite
    public static final String HT_COORDINATOR_USER = "htcoor";
    public static final String HT_COORDINATOR_PASSWORD = "htcooradmin";

    // Roles
    public static final String REGIONAL_CLERKS_ROLE = "regionalClerksRole";
    public static final String REGIONAL_MANAGER_ROLE = "regionalManagerRole";
    public static final String HT_COORDINATOR_ROLE = "htcoordinator";

	//Artifact Location
	public final static String DIR_WS_COORDINATION = "wscoordination";


    // Package Names
    public static final String CLAIMS_APPROVAL_PACKAGE_NAME = "ClaimsApprovalTask";

    //Services and Operations
    public static final String CLAIM_APPROVAL_PROCESS_SERVICE = "ClaimsApprovalProcessService";
    public static final String CLAIM_APPROVAL_PROCESS_OPERATION = "claimsApprovalProcessOperation";

    public static final String CLAIM_SERVICE = "ClaimService";


    // Other constants
    public static final String CLAIM_APPROVAL_NAMESPACE = "{http://www.wso2.org/humantask/claimsapprovalprocess.bpel}ClaimsApprovalProcess";
    public static final String COMPLETED = "COMPLETED";
    public static final String TERMINATED = "TERMINATED";
    public static final String EXITED = "EXITED";
    public static final String FAILED = "FAILED";


    public static String createClaimApprovalProcessRequest(String custID, String firstName, String lastName, long amount) {
        return "<cla:ClaimApprovalProcessInput xmlns:cla=\"http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl\">\n" +
                "         <cla:custID>" + custID + "</cla:custID>\n" +
                "         <cla:custFName>" + firstName + "</cla:custFName>\n" +
                "         <cla:custLName>" + lastName + "</cla:custLName>\n" +
                "         <cla:amount>" + amount + "</cla:amount>\n" +
                "         <cla:region>LK</cla:region>\n" +
                "         <cla:priority>4</cla:priority>\n" +
                "      </cla:ClaimApprovalProcessInput>";
    }

    public static String createClaimTaskOutput(boolean approved) {
        return "<sch:ClaimApprovalResponse xmlns:sch=\"http://www.example.com/claims/schema\">\n" +
                "         <sch:approved>" + approved + "</sch:approved>\n" +
                "      </sch:ClaimApprovalResponse>";

    }

}
