/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.dataservice.integration.common.utils.SqlDataSourceUtil;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservice.integration.test.util.LockHolder;
import org.wso2.ei.dataservice.integration.test.util.ParallelRequestHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This test is to verify the fix for https://wso2.org/jira/browse/DS-1083
 * where doing multiple operations parallely within begin boxcar and end boxcar won't get
 * persisted to the database correctly In the test, there will be parallel insert requests within single
 * boxcarring session, Note that this is a intermittent issue and sometimes issue won't occur in
 * earlier setup as well. Best we can do is test with heavy parallel load.
 */
public class DS1083BoxcarringParallelInsertTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(DS1083BoxcarringParallelInsertTestCase.class);

    private final String serviceName = "BoxcarringParalleAccess";

    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");

    private String serviceEndPoint;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        List<File> sqlFileList = new ArrayList<File>();
        sqlFileList.add(selectSqlFile("BoxcarringParallelAccess.sql"));
        SqlDataSourceUtil dataSource = new SqlDataSourceUtil(sessionCookie
                , dssContext.getContextUrls().getBackEndUrl());
        dataSource.createDataSource(sqlFileList);
        Assert.assertTrue(isServiceDeployed(serviceName));
        serviceEndPoint = getServiceUrlHttp(serviceName);
    }


    @Test(groups = {"wso2.dss"}, description = "Send parallel boxcarring requests to the server and check whether they were added successfully", alwaysRun = true)
    public void boxcarringParallelInsertsTest() throws Exception {
        int requestCount = 50;
        LockHolder.getInstance(requestCount, 30);

        OMElement beginBoxcarPayload = fac.createOMElement("begin_boxcar", omNs);

        ParallelRequestHelper beginBoxcarHelper = new ParallelRequestHelper(null, "begin_boxcar", beginBoxcarPayload, serviceEndPoint);

        String sessionCookie = beginBoxcarHelper.beginBoxcarReturningSession();

        List<ParallelRequestHelper> insertRequestsHelpers = new ArrayList<ParallelRequestHelper>(requestCount);
        for (int i = 0; i < requestCount; i++) {
            OMElement insertPayload = fac.createOMElement("insert_operation", omNs);

            OMElement groupId = fac.createOMElement("IDSTRING", omNs);
            groupId.setText("input_" + i);
            insertPayload.addChild(groupId);

            OMElement name = fac.createOMElement("SAMPLEINPUT", omNs);
            name.setText("sample input " + i);
            insertPayload.addChild(name);
            ParallelRequestHelper insertRequestHelper = new ParallelRequestHelper(sessionCookie, "insert_operation", insertPayload, serviceEndPoint);
            insertRequestsHelpers.add(insertRequestHelper);
        }

        for (ParallelRequestHelper helper : insertRequestsHelpers) {
            helper.start();
        }

        int requestsSent = LockHolder.getInstance().waitForComplete();

        Assert.assertEquals(requestsSent, requestCount, "All the requests are not sent to the back end within given time limit");


        OMElement selectCountPayload = fac.createOMElement("select_count_operation", omNs);

        ParallelRequestHelper selectCountHelper = new ParallelRequestHelper(null, "select_count_operation", selectCountPayload, serviceEndPoint);

        OMElement countResult = selectCountHelper.sendRequestAndReceiveResult();

        Assert.assertNotNull(countResult, "Response null");
        System.out.println("Result "+countResult.toString());
        Assert.assertTrue(countResult.toString().contains("<ROW_COUNT>0</ROW_COUNT>"), "Expected result not found");

        OMElement endBoxcarPayload = fac.createOMElement("end_boxcar", omNs);

        ParallelRequestHelper endBoxcarHelper = new ParallelRequestHelper(sessionCookie, "end_boxcar", endBoxcarPayload, serviceEndPoint);

        endBoxcarHelper.sendRequestAndReceiveResult();

        OMElement finalCountResult = selectCountHelper.sendRequestAndReceiveResult();

        Assert.assertNotNull(finalCountResult, "Response null");
        System.out.println("Result "+finalCountResult.toString());
        Assert.assertTrue(finalCountResult.toString().contains("<ROW_COUNT>" + requestCount + "</ROW_COUNT>"), "Expected result not found");
    }

    @AfterClass
    public void clean() throws Exception {
        cleanup();
    }
}
