package org.wso2.bps.samples.client;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.user.mgt.stub.UserAdminStub;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;


public class SamplesInvoker {
    private static final Log log = LogFactory.getLog(SamplesInvoker.class);

    public static final String ACTION = "action";
    public static final String REQ_MSG = "requestMsg";
    public static final String SVC_NAME = "serviceName";

    private static OMElement requestPayload = null;
    private static String action = null;
    private static String serviceName = null;

    private static UserAdminStub userAdminStub = null;
    private static AuthenticationAdminStub authenticationAdminStub = null;

    final static String USER_MANAGEMENT_SERVICE_URL = "https://" + "localhost" +
                                                      ":" + "9443" +
                                                      "/services/UserAdmin";
    final static String AUTHENTICATION_SERVICE_URL = "https://" + "localhost" +
                                                          ":" + "9443" +
                                                          "/services/AuthenticationAdmin";


    private static String getProperty(String propertyName, String defaultValue) {
        String result = System.getProperty(propertyName);
        if (result == null || result.length() == 0) {
            result = defaultValue;
        }
        return result;
    }

    public static void main(String args[]) {
        try {
            executeClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadServiceProperties(String propFilePath, String operation) {
        File f = new File(propFilePath);
        Properties properties = new Properties();
        FileInputStream fin = null;
        ByteArrayInputStream bin = null;
        String xmlStr = null;
        try {
            fin = new FileInputStream(f);
            properties.load(fin);
            serviceName = properties.getProperty(SVC_NAME).trim();

            if (operation != null && !operation.equals("")) {

                action = properties.getProperty(operation + "." + ACTION).trim();
                xmlStr = properties.getProperty(operation + "." + REQ_MSG).trim();

            } else {

                action = properties.getProperty(ACTION).trim();
                xmlStr = properties.getProperty(REQ_MSG).trim();

            }
            bin = new ByteArrayInputStream(xmlStr.getBytes());
            StAXOMBuilder builder = new StAXOMBuilder(bin);
            requestPayload = builder.getDocumentElement();
        } catch (IOException e) {
            System.out.println("Exception in reading properties file" + e.getMessage());
            System.exit(0);
        } catch (XMLStreamException xe) {
            System.out.println("Error in the xml request msg " + xe.getMessage());
            System.exit(0);
        } finally {
            try {
                fin.close();
            } catch (IOException e) {
            }
            try {
                bin.close();
            } catch (IOException e) {
            }
        }

    }

    /**
     * Check whether a property file exist for the given sample name
     *
     * @param sampleName
     * @param sampleDir
     * @return
     */

    private static String getPropertyFile(String sampleName, String sampleDir) {
        // Check whether the specified sample exists in the repository/samples/bpel directory
        String samplePath = sampleDir + File.separator + "resources" + File.separator + "bpel" +
                            File.separator + sampleName + ".properties";
        File sample = new File(samplePath);
        if (sample.exists()) {
            return samplePath;
        }

        samplePath = sampleDir + File.separator + "resources" + File.separator + "humantask" +
                     File.separator + sampleName + ".properties";

        sample = new File(samplePath);
        if (sample.exists()) {
            return samplePath;
        }
        return null;
    }

    private static void printResult(OMElement element) throws Exception {
        System.out.println("Received response from the service");
        System.out.println(element.toStringWithConsume());
        System.exit(0);
    }

    public static void executeClient() throws Exception {

        String soapVer = getProperty("soapver", "soap11");
        String addUrl = getProperty("addurl", null);
        String trpUrl = getProperty("trpurl", null);
        String prxUrl = getProperty("prxurl", null);
        String repository = getProperty("repository", "client_repo");
        String sampleName = getProperty("sample", "CreditRating");
        String sampleDir = getProperty("sampleDir", ".");
        String operation = getProperty("operation", null);
        String createUsers = getProperty("createUsers", "false");

        if ("true".equals(createUsers)) {
            createUsers();
        }


        String propertyFile = getPropertyFile(sampleName, sampleDir);
        if (propertyFile != null) {
            loadServiceProperties(propertyFile, operation);
        } else {
            System.out.println("Matching properties file not found for the specified sample");
            System.exit(0);
        }

        ConfigurationContext configContext = null;

        ServiceClient serviceClient;

        if (repository != null && !"null".equals(repository)) {
            configContext =
                    ConfigurationContextFactory.
                            createConfigurationContextFromFileSystem(repository,
                                                                     repository + File.separator + "conf" + File.separator + "axis2.xml");
            serviceClient = new ServiceClient(configContext, null);
        } else {
            serviceClient = new ServiceClient();
        }

        Options options = new Options();

        if (action != null && !action.equals("")) {

            options.setAction(action);

        } else {

            serviceClient.disengageModule("addressing");
            options.setProperty(AddressingConstants.DISABLE_ADDRESSING_FOR_OUT_MESSAGES, true);
        }

        if (addUrl != null && !"null".equals(addUrl + serviceName)) {
            options.setTo(new EndpointReference(addUrl));
        }
        if (trpUrl != null && !"null".equals(trpUrl)) {
            options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl + serviceName);
        }
        if (prxUrl != null && !"null".equals(prxUrl)) {
            HttpTransportProperties.ProxyProperties proxyProperties =
                    new HttpTransportProperties.ProxyProperties();
            URL url = new URL(prxUrl);
            proxyProperties.setProxyName(url.getHost());
            proxyProperties.setProxyPort(url.getPort());
            proxyProperties.setUserName("");
            proxyProperties.setPassWord("");
            proxyProperties.setDomain("");
            options.setProperty(HTTPConstants.PROXY, proxyProperties);
        }

        if ("soap12".equals(soapVer)) {
            options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        }
        serviceClient.setOptions(options);
        OMElement response = serviceClient.sendReceive(requestPayload);
        printResult(response);
    }

    //Creates a list of sample users and roles for the human task sample.
    private static void createUsers() {

        try {
            initUserAdminStub();
            addRoles();
            addUsers();

        } catch (Exception ex) {
        }
    }

    private static void initUserAdminStub() throws Exception {
        userAdminStub = new UserAdminStub(USER_MANAGEMENT_SERVICE_URL);

        ServiceClient serviceClient = userAdminStub._getServiceClient();
        Options serviceClientOptions = serviceClient.getOptions();
        serviceClientOptions.setManageSession(true);
        CarbonUtils.setBasicAccessSecurityHeaders("admin", "admin", serviceClient);
    }

    private static void iniAuthenticationAdminStub() throws Exception {

        authenticationAdminStub = new AuthenticationAdminStub(AUTHENTICATION_SERVICE_URL);

        ServiceClient client = authenticationAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
    }


    private static void addRoles() throws Exception {
        userAdminStub.addRole("regionalClerksRole", null, new String[]{"/permission/admin/login",
                                                                       "/permission/admin/manage/humantask/viewtasks"},false);
        userAdminStub.addRole("regionalManagerRole", null, new String[]{"/permission/admin/login",
                                                                        "/permission/admin/manage/humantask/viewtasks"},false);
    }


    private static void addUsers()
            throws Exception {
        userAdminStub.addUser("clerk1", "clerk1password",
                              new String[]{"regionalClerksRole"}, null, null);
        userAdminStub.addUser("clerk2", "clerk2password",
                              new String[]{"regionalClerksRole"}, null, null);

        userAdminStub.addUser("manager", "managerpassword",
                              new String[]{"regionalManagerRole"}, null, null);
    }
}
