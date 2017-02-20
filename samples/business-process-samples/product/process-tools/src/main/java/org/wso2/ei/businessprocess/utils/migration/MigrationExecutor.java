/*
 * Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * License under the Apache License, Version 2.0 (the "License");
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
package org.wso2.ei.businessprocess.utils.migration;


import org.apache.xerces.dom.DeferredElementImpl;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.ei.businessprocess.utils.migration.db.DBQuery;
import org.wso2.ei.businessprocess.utils.migration.deployment.ArchiveBasedHumanTaskDeploymentUnitBuilder;
import org.wso2.ei.businessprocess.utils.migration.utils.MigrationToolUtil;
import org.wso2.carbon.humantask.TTask;
import org.wso2.carbon.humantask.core.HumanTaskConstants;
import org.wso2.carbon.humantask.core.deployment.HumanTaskDeploymentUnit;
import org.wso2.carbon.humantask.core.utils.HumanTaskStoreUtils;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Date;
import java.util.TimeZone;


/**
 * Manages the migration process of BPS server.
 * Changes to repository packages
 * Append QName with version for existing tasks .
 */
public class MigrationExecutor {
    private static final int SUPER_TENANT = -1234;
    //DB query builder according to DB type
    private static DBQuery query;
    private static String BPS_HOME;
    static String databaseURL = null;

    //Main method
    public static void main(String[] args) {
        try {
            if (System.getProperty("carbon.components.dir.path") != null) {
                addJarFileUrls(new File(System.getProperty("carbon.components.dir.path")));
            }
            System.out.println("Initialize Migration...");
            System.out.println("==========================================");
            if (System.getProperty("carbon.home") != null) {
                System.out.println("Using carbon home directory : " + System.getProperty("carbon.home"));
            } else {
                System.out.println("Carbon Home not set, please check the migration tool script !!!!");
            }

            //Get BPS HOME from bin directory
            BPS_HOME = System.getProperty("carbon.home");
            //Load properties
            initializeDBConnection();
            //Set time zone for oracle
            TimeZone.setDefault(TimeZone.getTimeZone(System.getProperty("user.timezone")));

            //Repository Paths
            String superTenantRepoPath = BPS_HOME + File.separator + "repository" + File.separator + "deployment" +
                    File.separator + "server" + File.separator + "humantasks";
            String tenantsRepoPath = BPS_HOME + File.separator + "repository" + File.separator + "tenants";
            System.out.println("SUPER TENANT REPOSITORY PATH:" + superTenantRepoPath);
            System.out.println("TENANTS REPOSITORY PATH:" + tenantsRepoPath);

            query = new DBQuery(databaseURL);

            //Check DB schemas Exist
            if (!versionDBSchemasExists()) {
                System.out.println("DB Tables are not updated for BPS 3.2.0. Please run the sql script from " +
                        "dbscripts/migration directory and Try Again!!!!");
                return;
            }

            //Migration for super tenant.
            migrateSuperTenantHTPacks(superTenantRepoPath);

            //Migration for  tenants.
            migrateTenantsHTPacks(tenantsRepoPath);
            System.out.println("Migration Success!!!!");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR:Migration failed.Try Again!!!!");
        }
    }

    /**
     * Create DB connection
     * @return Connection
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static Connection initializeDBConnection() throws ParserConfigurationException, IOException, SAXException,
            ClassNotFoundException, SQLException {
        String databaseUsername = null;
        String databasePassword = null;
        String databaseDriver = null;
        boolean dbConfigFound = false;
        String configPath = System.getProperty("carbon.home")  + File.separator + "conf" +
                    File.separator + "datasources" + File.separator + "bps-datasources.xml";
        System.out.println("Using datasource config file at :" + configPath);
        File elementXmlFile = new File(configPath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setIgnoringComments(true);
        dbFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(elementXmlFile);
        document.getDocumentElement().normalize();
        NodeList datasourceList = document.getDocumentElement().getElementsByTagName("datasource");
        for (int i = 0; i < datasourceList.getLength(); i++) {
            Node datasource = datasourceList.item(i);
            String dbName = ((DeferredElementImpl) datasource).getElementsByTagName("name").item(0).getTextContent();
            if(dbName.equals("BPS_DS")){
                databaseURL = document.getDocumentElement().getElementsByTagName("url").item(i).getTextContent().split(";")[0];
                databaseDriver = document.getDocumentElement().getElementsByTagName("driverClassName").item(i).getTextContent();
                databaseUsername = document.getDocumentElement().getElementsByTagName("username").item(i).getTextContent();
                databasePassword = document.getDocumentElement().getElementsByTagName("password").item(i).getTextContent();

                dbConfigFound = true;
                break;
            }
        }
        if(!dbConfigFound){
            System.out.println("DB configurations not found or invalid!");
            System.exit(0);
        }
        Class.forName(databaseDriver);
        return DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
    }

    //Check DB changes were exist
    private static boolean versionDBSchemasExists() throws Exception {
        try {
            Connection conn = initializeDBConnection();
            conn.setAutoCommit(false);
            ResultSet resultList = null;
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                String sql = query.getVERSION();
                resultList = stmt.executeQuery(sql);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
                if (resultList != null) {
                    resultList.close();
                }

                if (conn != null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    //Get user inputs
    private static boolean canRunScripts() throws Exception {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println();
            System.out.println("ERROR:DB schemas were not found. Do you want to run scripts using tool?(Y/N)");
            String input = br.readLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            }
            return false;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;

        } finally {
            br.close();
        }

    }

    /**
     * Alteration to current DB schemas
     *
     * @throws Exception
     */
    private static boolean migrateDB() throws Exception {
        System.out.println("Alteration to current DB schemas...");
        System.out.println("==========================================");
        Connection conn = initializeDBConnection();
        conn.setAutoCommit(false);
        try {

            conn.createStatement().execute(query.getHT_DEPLOYMENT_UNIT());
            System.out.println("Deployment Unit Table Created.");
            conn.createStatement().execute(query.getHT_VERSIONS());
            System.out.println("Version Table Created.");
            conn.createStatement().execute(query.getALTER_PACKAGE_NAME());
            conn.createStatement().execute(query.getALTER_TASK_DEF_NAME());
            conn.createStatement().execute(query.getALTER_TASK_VERSION());
            System.out.println("Table Columns Altered.");

            conn.commit();
            System.out.println("Database Alteration Success!!");
            return true;
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
            throw new Exception("Database Alteration Unsuccessful!! (DB error)");

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Store extracted packages with version in super tenant repository.
     *
     * @param repoPath
     * @throws Exception
     */
    private static void migrateSuperTenantHTPacks(String repoPath) throws Exception {
        System.out.println("Migration for Super Tenant...");
        System.out.println("==========================================");
        File repoDirectory = new File(repoPath);
        File[] deployedHumanTaskPacks = repoDirectory.listFiles();
        if (deployedHumanTaskPacks != null) {
            System.out.println("HumanTask Package\t|\tVersion");
            for (int i = 0; i < deployedHumanTaskPacks.length; i++) {
                if (deployedHumanTaskPacks[i].isFile() && FilenameUtils.getExtension(deployedHumanTaskPacks[i].getName()).trim().equals("zip")) {
                    migrateHumanTasks(deployedHumanTaskPacks[i], SUPER_TENANT);
                }
            }
        } else {
            System.out.println("No HumanTask Packages were found.");
        }
    }

    /**
     * Store extracted packages with version in tenants repositories.
     *
     * @param repoPath
     * @throws Exception
     */
    private static void migrateTenantsHTPacks(String repoPath) throws Exception {
        System.out.println("Migration for  Tenants...");
        System.out.println("==========================================");
        File tenantsDirectory = new File(repoPath);
        File[] listTenantDirs = tenantsDirectory.listFiles();
        if (listTenantDirs != null) {
            for (int i = 0; i < listTenantDirs.length; i++) {
                if (listTenantDirs[i].isDirectory() && MigrationToolUtil.isInteger(listTenantDirs[i].getName())) {
                    System.out.println("TenantID: " + listTenantDirs[i].getName());
                    File[] deployedHumanTaskPacks = new File(listTenantDirs[i].getAbsolutePath() + File.separator +
                            "humantasks").listFiles();
                    if (deployedHumanTaskPacks != null) {
                        System.out.println("HumanTask Package\t|\tVersion");
                        for (int j = 0; j < deployedHumanTaskPacks.length; j++) {
                            if (deployedHumanTaskPacks[j].isFile() && FilenameUtils.getExtension(deployedHumanTaskPacks[j]
                                    .getName()).trim().equals("zip")) {
                                migrateHumanTasks(deployedHumanTaskPacks[j], Integer.parseInt(listTenantDirs[i].getName()));
                            }
                        }
                    } else {
                        System.out.println("No HumanTask Packages were found.");
                    }

                }
            }
        } else {
            System.out.println("No Tenant Repositories were found.");
        }

    }

    /**
     * Store extracted packages with version.
     * Change QName for current tasks appending with version
     *
     * @param archiveFile
     * @param tenantId
     * @throws Exception
     */
    private static void migrateHumanTasks(File archiveFile, int tenantId) throws Exception {

        Connection conn = initializeDBConnection();
        conn.setAutoCommit(false);
        String md5sum = HumanTaskStoreUtils.getMD5Checksum(archiveFile);
        String packageName = FilenameUtils.removeExtension(archiveFile.getName());
        Long version = getNextVersion();
        String deployPath = "repository" + File.separator +
                HumanTaskConstants.HUMANTASK_REPO_DIRECTORY + File.separator +
                tenantId + File.separator + packageName + "-" + version;

        //Create Human Task Deployment Unit
        HumanTaskDeploymentUnit humanTaskDU = createHumanTaskDeploymentUnit(archiveFile, tenantId, md5sum,
                version);

        try {
            long deploymentID = getNextDeploymentID();
            Date date = new Date();
            //Entry to HT_DEPLOYMENT_UNIT table
            conn.createStatement().execute(query.getINSERT_DEPLOYMENT_UNIT(deploymentID,
                    md5sum, date, deployPath, packageName + "-" + version, packageName,
                    tenantId, version));

            TTask[] tasks = humanTaskDU.getTasks();
            //Update QName of existing tasks.
            if (tasks != null) {

                for (TTask task : tasks) {
                    QName taskQName = new QName(humanTaskDU.getNamespace(), task.getName());
                    conn.createStatement().execute(query.getUPDATE_TASKS(taskQName.toString(), tenantId, packageName,
                            version));
                }
            }
            //Set Next Version
            setVersion();
            conn.commit();
            System.out.println(FilenameUtils.removeExtension(archiveFile.getName()) + "\t|\t" + version);
        } catch (Exception ex) {
            ex.printStackTrace();
            conn.rollback();
            throw new Exception(ex);

        } finally {
            if (conn != null) {
                conn.close();
            }
        }

    }

    /**
     * Get the Human task deployment unit
     *
     * @param archiveFile
     * @param md5sum
     * @param tenantId
     * @param version
     * @throws Exception
     */
    private static HumanTaskDeploymentUnit createHumanTaskDeploymentUnit(File archiveFile, int tenantId,
                                                                         String md5sum, long version) throws Exception {
        return new ArchiveBasedHumanTaskDeploymentUnitBuilder(BPS_HOME, archiveFile, tenantId, version,
                md5sum).createNewHumanTaskDeploymentUnit();
    }

    /**
     * Get the version
     *
     * @throws Exception
     */
    private static long getNextVersion() throws Exception {
        Connection conn = initializeDBConnection();
        conn.setAutoCommit(false);
        ResultSet resultList = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = query.getVERSION();
            resultList = stmt.executeQuery(sql);
            String s_version = null;
            while (resultList.next()) {
                s_version = resultList.getString("TASK_VERSION");
            }
            if (s_version == null) {
                conn.createStatement().execute(query.getINSERT_VERSION());
                conn.commit();

                return 1;
            }
            Long version = Long.parseLong(s_version) + 1;
            return version;
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (resultList != null) {
                resultList.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    private static long getNextDeploymentID() throws Exception {
        Connection conn = initializeDBConnection();
        conn.setAutoCommit(false);
        ResultSet resultList = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = query.getDEPLOYMENT_UNIT_ID();
            resultList = stmt.executeQuery(sql);
            String s_version = null;
            while (resultList.next()) {
                s_version = resultList.getString("id");
            }
            if (s_version == null) {

                return 0;
            }
            Long id = Long.parseLong(s_version) + 1;
            return id;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        } finally {
            if(stmt != null) {
                stmt.close();
            }
            if(resultList != null) {
                resultList.close();
            }
            if(conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Increment the next version
     *
     * @throws Exception
     */
    private static void setVersion() throws Exception {

        Connection conn = initializeDBConnection();
        conn.setAutoCommit(false);
        try {
            conn.createStatement().execute(query.getUPDATE_VERSION());
            conn.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            conn.rollback();
            throw new Exception(ex);
        } finally {
            if(conn != null) {
            conn.close();
            }
        }
    }

    /**
     * Add JAR files found in the given directory to the Classpath. This fix is done due to terminal's argument character limitation.
     *
     * @param root the directory to recursively search for JAR files.
     * @throws java.net.MalformedURLException If a provided JAR file URL is malformed
     */
    private static void addJarFileUrls(File root) throws Exception {
        File[] children = root.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (child.isFile() && child.canRead() &&
                    child.getName().toLowerCase().endsWith(".jar") &&
                    !child.getName().toLowerCase().startsWith("org.apache.synapse.module") &&
                    !child.getName().toLowerCase().startsWith("wss4j")) {
                addPath(child.getPath());
            }
        }
    }

    private static void addPath(String s) throws Exception {
        File f = new File(s);
        URL u = f.toURL();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, u);
    }

}