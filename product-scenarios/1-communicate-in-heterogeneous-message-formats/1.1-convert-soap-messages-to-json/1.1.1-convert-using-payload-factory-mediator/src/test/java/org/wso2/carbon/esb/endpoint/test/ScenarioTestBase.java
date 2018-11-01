package org.wso2.carbon.esb.endpoint.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ScenarioTestBase {

    private static final String INPUTS_LOCATION = System.getenv("DATA_BUCKET_LOCATION");
    private static final String INFRASTRUCTURE_PROPERTIES = "infrastructure.properties";
    private static final String DEPLOYMENT_PROPERTIES = "deployment.properties";
    private static final String JOB_PROPERTIES = "testplan-props.properties";

    private static final Log log = LogFactory.getLog(ScenarioTestBase.class);

    public static final String MGT_CONSOLE_URL = "MgtConsoleUrl";
    public static final String CARBON_SERVER_URL = "CarbonServerUrl";
    public static final String ESB_HTTP_URL = "ESBHttpUrl";
    public static final String ESB_HTTPS_URL = "ESBHttpsUrl";

    protected static final int ARTIFACT_DEPLOYMENT_WAIT_TIME_MS = 120000;
    protected static final String resourceLocation = System.getProperty("test.resources.dir");

    /**
     * This is a utility method to load the deployment details.
     * The deployment details are available as key-value pairs in {@link #INFRASTRUCTURE_PROPERTIES},
     * {@link #DEPLOYMENT_PROPERTIES}, and {@link #JOB_PROPERTIES} under the
     * {@link #INPUTS_LOCATION}.
     *
     * This method loads these files into one single properties, and return it.
     *
     * @return properties the deployment properties
     */
    public static Properties getDeploymentProperties() {
        Path infraPropsFile = Paths.get(INPUTS_LOCATION + File.separator + INFRASTRUCTURE_PROPERTIES);
        Path deployPropsFile = Paths.get(INPUTS_LOCATION + File.separator + DEPLOYMENT_PROPERTIES);
        Path jobPropsFile = Paths.get(INPUTS_LOCATION + File.separator + JOB_PROPERTIES);

        Properties props = new Properties();
        loadProperties(infraPropsFile, props);
        loadProperties(deployPropsFile, props);
        loadProperties(jobPropsFile, props);
        return props;
    }

    private static void loadProperties(Path propsFile, Properties props) {
        String msg = "Deployment property file not found: ";
        if (!Files.exists(propsFile)) {
            log.warn(msg + propsFile);
            return;
        }

        try (InputStream propsIS = Files.newInputStream(propsFile)) {
            props.load(propsIS);
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }

}
