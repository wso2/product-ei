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
package org.wso2.carbon.esb.samples.test.transport;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.clients.mediation.SynapseConfigAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.SqlDataSourceUtil;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * Sample 271: File Processing
 */
public class Sample271TestCase extends ESBIntegrationTest {

    private SqlDataSourceUtil sqlDataSourceUtil = null;
    private ServerConfigurationManager serverConfigurationManager = null;

    private File mysqlJar;
    private File smooksCsvJar;

    private File inFolder;
    private File failureFolder;
    private File originalFolder;
    private File outFolder;

    private String path2ResourceSample271;
    private String path2CarbonSample271;

    private static final String GMAIL_USER_NAME = "test.automation.dummy";
    private static final String GMAIL_PASSWORD = "automation.test";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

        path2ResourceSample271 =
            getESBResourceLocation() + File.separator + "sample_271" + File.separator;
        path2CarbonSample271 =
            ServerConfigurationManager.getCarbonHome() + File.separator + "sample_271" +
            File.separator;

        FileUtils.deleteDirectory(new File(path2CarbonSample271));

        // Create CARBON_HOME/sample_271 folder
        assertTrue(new File(path2CarbonSample271).mkdirs(), "file folder not created");

        // Create in, out, failure, original folders
        inFolder = new File(path2CarbonSample271 + "in" + File.separator);
        failureFolder = new File(path2CarbonSample271 + "failure" + File.separator);
        originalFolder = new File(path2CarbonSample271 + "original" + File.separator);
        outFolder = new File(path2CarbonSample271 + "out" + File.separator);

        assertTrue(inFolder.mkdirs(), "file folder not created");
        assertTrue(failureFolder.mkdirs(), "file folder not created");
        assertTrue(originalFolder.mkdirs(), "file folder not created");
        assertTrue(outFolder.mkdirs(), "file folder not created");

        // Copy synapse config folder to carbon_home/sample_271
        FileUtils.copyDirectory(new File(path2ResourceSample271 + "synapse-configs"),
                                new File(path2CarbonSample271 + "synapse-configs"));

        // Copy smooks-config.xml
        FileUtils.copyFileToDirectory(new File(path2ResourceSample271 + "smooks-config.xml"),
                                      new File(path2CarbonSample271));

        // Copy axis2.xml
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager
            .applyConfigurationWithoutRestart(new File(path2ResourceSample271 + "axis2.xml"));

        // copy jars
        mysqlJar = new File(path2ResourceSample271 + "lib" + File.separator +
                            "mysql-connector-java-5.1.10-bin.jar");
        smooksCsvJar = new File(
            path2ResourceSample271 + "lib" + File.separator + "milyn-smooks-csv-1.2.4.jar");
        serverConfigurationManager.copyToComponentLib(mysqlJar);
        serverConfigurationManager.copyToComponentLib(smooksCsvJar);

        serverConfigurationManager.restartGracefully();

        super.init();
        SynapseConfigAdminClient synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        String config = synapseConfigAdminClient.getConfiguration();

        // Set up the database
        File sqlFile = new File(path2ResourceSample271 + "create_table.sql");
        List<File> sqlFileList = new ArrayList<File>(0);
        sqlFileList.add(sqlFile);

        sqlDataSourceUtil = new SqlDataSourceUtil(getSessionCookie(), contextUrls.getBackEndUrl());
        sqlDataSourceUtil.createDataSource("WSO2_CARBON_DB", sqlFileList);

        config = config.replace("<password>wso2carbon</password>",
                                "<password>" + sqlDataSourceUtil.getDatabasePassword() +
                                "</password>"
        ).replace("<user>wso2carbon</user>",
                  "<user>" + sqlDataSourceUtil.getDatabaseUser() +
                  "</user>"
        ).replace("<url>jdbc:mysql://localhost:3306/test</url>",
                  "<url>" + sqlDataSourceUtil.getJdbcUrlForProxy() + "</url>")
                       .replace("<driver>com.mysql.jdbc.Driver</driver>",
                                "<driver>" + sqlDataSourceUtil.getDriver() + "</driver>");

        config = config.replace("/home/username/test/in", inFolder.getAbsolutePath())
                       .replace("/home/username/test/original", originalFolder.getAbsolutePath())
                       .replace("/home/username/test/failure", failureFolder.getAbsolutePath())
                       .replace("/home/username/test/out",
                                outFolder.getAbsolutePath() + File.separator + "out.txt");

        synapseConfigAdminClient.updateConfiguration(config);

    }

    @Test(groups = { "wso2.esb" }, description = "File Processing", enabled = false)
    public void testFileProcessing() throws Exception {
        String feedURL = "https://mail.google.com/mail/feed/atom";
        int beforeMaiilCount = getMailCount(feedURL);

        File inputFile = new File(path2ResourceSample271 + "input.txt");
        File outfile = new File(outFolder.getAbsolutePath() + File.separator + "out.txt");

        FileUtils.copyFileToDirectory(inputFile, inFolder);

        boolean isInputFileNotExistInFolder =
            isFileNotExist(inputFile.getAbsolutePath(), "input.txt");
        Assert.assertFalse(isInputFileNotExistInFolder, "input.txt file is still exist");

        boolean isInputFileExistInOriginalFolder = isFileExist(originalFolder.getAbsolutePath(),
                                                               "input.txt");
        Assert.assertTrue(isInputFileExistInOriginalFolder, "out.txt file not found");

        boolean isOutFileExistInOutFolder = isFileExist(outFolder.getAbsolutePath(), "out.txt");
        Assert.assertTrue(isOutFileExistInOutFolder, "out.txt file not found");

        String vfsOut = FileUtils.readFileToString(outfile);
        Assert.assertTrue(vfsOut.contains("Don") && vfsOut.contains("John"),
                          "Don & John not found");

        Assert.assertEquals(getMailCount(feedURL), beforeMaiilCount + 1, "Mail count mismatch");

        // todo : database record insertion
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();

        // Remove the database
        File sqlFile = new File(path2ResourceSample271 + "drop_table.sql");
        List<File> sqlFileList = new ArrayList<File>(0);
        sqlFileList.add(sqlFile);

        sqlDataSourceUtil = new SqlDataSourceUtil(getSessionCookie(), contextUrls.getBackEndUrl());
        sqlDataSourceUtil.createDataSource("WSO2_CARBON_DB", sqlFileList);

        // Delete carbon_home/sample_271
        FileUtils.deleteDirectory(new File(path2CarbonSample271));

        // revert axis2.xml
        serverConfigurationManager.restoreToLastConfiguration(false);

        // remove jars
        serverConfigurationManager.removeFromComponentLib(mysqlJar.getAbsolutePath());
        serverConfigurationManager.removeFromComponentLib(smooksCsvJar.getAbsolutePath());

        // restart server
        serverConfigurationManager.restartGracefully();
    }

    private boolean isFileNotExist(String filePath, String fileName) throws Exception {
        File file = new File(filePath + File.separator + fileName);
        long startTime = System.currentTimeMillis();
        boolean isFileNotExist = true;
        while (((System.currentTimeMillis() - startTime) < 180000) && isFileNotExist) {
            log.info("Waiting for input.txt file to be disappear ....");
            isFileNotExist = file.exists();
            Thread.sleep(3000);
        }

        return isFileNotExist;
    }

    private boolean isFileExist(String filePath, String fileName) throws Exception {
        File file = new File(filePath + File.separator + fileName);
        long startTime = System.currentTimeMillis();
        boolean isFileExist = false;
        while (((System.currentTimeMillis() - startTime) < 180000) && !isFileExist) {
            log.info("Waiting for out.txt file....");
            isFileExist = file.exists();
            Thread.sleep(3000);
        }

        return isFileExist;
    }

    private int getMailCount(String feedURL) throws XMLStreamException, IOException {
        OMElement mailFeed = getAtomFeedContent(feedURL);
        Iterator itr = mailFeed.getChildrenWithName(new QName("fullcount"));
        int count = 0;
        if (itr.hasNext()) {
            OMElement countOm = (OMElement) itr.next();
            return Integer.parseInt(countOm.getText());
        }
        return count;
    }

    private static OMElement getAtomFeedContent(String atomURL) throws IOException,
                                                                       XMLStreamException {
        StringBuilder sb;
        InputStream inputStream = null;
        URL url = new URL(atomURL);
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            String userPassword = GMAIL_USER_NAME + ":" + GMAIL_PASSWORD;
            String encodedAuthorization = Base64Utils.encode(userPassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic " +
                                                           encodedAuthorization);
            connection.connect();

            inputStream = connection.getInputStream();
            sb = new StringBuilder();
            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            assert inputStream != null;
            inputStream.close();
        }

        return AXIOMUtil.stringToOM(sb.toString());

    }
}
