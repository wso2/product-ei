/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.micro.integrator.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.wso2.carbon.server.ChildFirstURLClassLoader;
import org.wso2.carbon.server.LauncherConstants;
import org.wso2.carbon.server.extensions.DefaultBundleCreator;
import org.wso2.carbon.server.extensions.DropinsBundleDeployer;
import org.wso2.carbon.server.extensions.EclipseIniRewriter;
import org.wso2.carbon.server.extensions.LibraryFragmentBundleCreator;
import org.wso2.carbon.server.extensions.Log4jPropFileFragmentBundleCreator;
import org.wso2.carbon.server.extensions.PatchInstaller;
import org.wso2.carbon.server.extensions.SystemBundleExtensionCreator;
import org.wso2.carbon.server.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class Main {

    private static Log log = LogFactory.getLog(org.wso2.carbon.server.Main.class);

   static File platformDirectory;
    protected static final String FRAMEWORK_BUNDLE_NAME = "org.eclipse.osgi";
    protected static final String STARTER =
            "org.eclipse.core.runtime.adaptor.EclipseStarter";
    protected static final String FRAMEWORKPROPERTIES =
            "org.eclipse.osgi.framework.internal.core.FrameworkProperties";
    protected static final String NULL_IDENTIFIER = "@null";
    protected static final String OSGI_FRAMEWORK = "osgi.framework";
    protected static final String OSGI_INSTANCE_AREA = "osgi.instance.area";
    protected static final String OSGI_CONFIGURATION_AREA = "osgi.configuration.area";
    protected static final String OSGI_INSTALL_AREA = "osgi.install.area";
    protected static final String P2_DATA_AREA = "eclipse.p2.data.area";

    public static void main(String[] args) {
        System.out.println("88***************************************");
        //Setting Carbon Home
        if (System.getProperty(LauncherConstants.CARBON_HOME) == null) {
            System.setProperty(LauncherConstants.CARBON_HOME, ".");
        }
        System.setProperty(LauncherConstants.AXIS2_HOME, System.getProperty(LauncherConstants.CARBON_HOME));

        //To keep track of the time taken to start the Carbon server.
        System.setProperty("wso2carbon.start.time", System.currentTimeMillis() + "");
        if (System.getProperty("carbon.instance.name") == null) {
            InetAddress addr;
            String ipAddr;
            String hostName;
            try {
                addr = InetAddress.getLocalHost();
                ipAddr = addr.getHostAddress();
                hostName = addr.getHostName();
            } catch (UnknownHostException e) {
                ipAddr = "localhost";
                hostName = "127.0.0.1";
            }
            String uuId = UUID.randomUUID().toString();
            String timeStamp = System.currentTimeMillis() + "";
            String carbon_instance_name = timeStamp + "_" + hostName + "_" + ipAddr + "_" + uuId;
            System.setProperty("carbon.instance.name", carbon_instance_name);
        }
        writePID(System.getProperty(LauncherConstants.CARBON_HOME));
        processCmdLineArgs(args);

        // set WSO2CarbonProfile as worker if workerNode=true present
        if ((System.getProperty(LauncherConstants.WORKER_NODE) != null) &&
                ("true".equals(System.getProperty(LauncherConstants.WORKER_NODE))) &&
                System.getProperty(LauncherConstants.PROFILE) == null) {
            File profileDir = new File(Utils.getCarbonComponentRepo() + File.separator + LauncherConstants.WORKER_PROFILE);
               /*
                *   Better check profile directory is present or not otherwise osgi will hang
                * */
            if (!profileDir.exists()) {
                log.fatal("OSGi runtime " + LauncherConstants.WORKER_PROFILE + " profile not found");
                throw new RuntimeException(LauncherConstants.WORKER_PROFILE + " profile not found");
            }
            System.setProperty(LauncherConstants.PROFILE, LauncherConstants.WORKER_PROFILE);
        }
        //setting default WSO2CarbonProfile as the running Profile if no other Profile is given as an argument
        if (System.getProperty(LauncherConstants.PROFILE) == null) {
            System.setProperty(LauncherConstants.PROFILE, LauncherConstants.DEFAULT_CARBON_PROFILE);
        }

        invokeExtensions();
        removeAllAppendersFromCarbon();
        startEquinox();

    }


    /**
     * Removing all the appenders which were added in the non osigi environment, after the carbon starts up.
     * Since another appender thread is there from osgi environment, it will be a conflict to access the log file by
     * non osgi and osgi appenders which resulted log rotation fails in windows.
     * This fix was introduced  for this jira: https://wso2.org/jira/browse/ESBJAVA-1614 .
     */

    private static void removeAllAppendersFromCarbon() {
        try {
            Logger.getRootLogger().removeAllAppenders();
        } catch (Throwable e) {
            System.err.println("couldn't remove appnders from Carbon non osgi environment");
        }
    }

    /**
     * Invoke the extensions specified in the carbon.xml
     */
    public static void invokeExtensions() {
        //converting jars found under components/lib and putting them in components/dropins dir
        new DefaultBundleCreator().perform();
        new SystemBundleExtensionCreator().perform();
        new Log4jPropFileFragmentBundleCreator().perform();
        new LibraryFragmentBundleCreator().perform();

        //Add bundles in the dropins directory to the bundles.info file.
        new DropinsBundleDeployer().perform();

        //copying patched jars to components/plugins dir
        new PatchInstaller().perform();

        //rewriting the eclipse.ini file
        new EclipseIniRewriter().perform();
    }

    /**
     * Write the process ID of this process to the file
     *
     * @param carbonHome carbon.home sys property value.
     */
    private static void writePID(String carbonHome) {
        byte[] bo = new byte[100];
        String[] cmd = {"sh", "-c", "echo $PPID"};
        Process p;
        try {
            p = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            //ignored. We might be invoking this on a Window platform. Therefore if an error occurs
            //we simply ignore the error.
            return;
        }

        try {
            int bytes = p.getInputStream().read(bo);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        String pid = new String(bo);
        if (pid.length() != 0) {
            BufferedWriter out = null;
            try {
                FileWriter writer = new FileWriter(carbonHome + File.separator + "wso2carbon.pid");
                out = new BufferedWriter(writer);
                out.write(pid);
            } catch (IOException e) {
                log.warn("Cannot write wso2carbon.pid file");
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    /**
     * Process command line arguments and set corresponding system properties.
     *
     * @param args cmd line args
     */
    private static void processCmdLineArgs(String[] args) {
        String cmd = null;
        int index = 0;

        // Set the System properties
        for (String arg : args) {
            index++;
            if (arg.startsWith("-D")) {
                int indexOfEq = arg.indexOf('=');
                String property;
                String value;
                if (indexOfEq != -1) {
                    property = arg.substring(2, indexOfEq);
                    value = arg.substring(indexOfEq + 1);
                } else {
                    property = arg.substring(2);
                    value = "true";
                }
                System.setProperty(property, value);
            } else if (arg.toUpperCase().endsWith(LauncherConstants.COMMAND_HELP)) {
                Utils.printUsages();
                System.exit(0);
            } else if (arg.toUpperCase().endsWith(LauncherConstants.COMMAND_CLEAN_REGISTRY)) {
                // sets the system property marking a registry cleanup
                System.setProperty("carbon.registry.clean", "true");
            } else {
                if (cmd == null) {
                    cmd = arg;
                }
            }
        }
    }


    private static void startEquinox() {
        /**
         * Launches Equinox OSGi framework by  invoking EclipseStarter.startup() method using reflection.
         * Creates a ChildFirstClassLoader out of the OSGi framework jar and set the classloader as the framework
         * classloader.
         */
        URLClassLoader frameworkClassLoader = null;
        platformDirectory = Utils.getCarbonComponentRepo();
        if (platformDirectory == null) {
            throw new IllegalStateException(
                    "Could not start the Framework - (not deployed)");
        }

        if (frameworkClassLoader != null) {
            return;
        }

        final Map<String, String> initialPropsMap = buildInitialPropertyMap();
        String[] args2 = Utils.getArgs();

        ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            System.setProperty("osgi.framework.useSystemProperties", "false");

            frameworkClassLoader = java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<URLClassLoader>() {
                        public URLClassLoader run() {
                            URLClassLoader cl = null;
                            try {
                                cl = new ChildFirstURLClassLoader(
                                        new URL[]{new URL(initialPropsMap.get(OSGI_FRAMEWORK))}, null);
                            } catch (MalformedURLException e) {
                                log.error(e.getMessage(), e);
                            }
                            return cl;
                        }
                    }
            );

//            frameworkClassLoader =

            //Loads EclipseStarter class.
            Class clazz = frameworkClassLoader.loadClass(STARTER);

            //Set the propertyMap by invoking setInitialProperties method.
            Method setInitialProperties =
                    clazz.getMethod("setInitialProperties", Map.class);
            setInitialProperties.invoke(null, initialPropsMap);

            //Invokes the startup method with some arguments.
            Method runMethod = clazz.getMethod("startup", String[].class, Runnable.class);
            runMethod.invoke(null, args2, null);

        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t == null) {
                t = ite;
            }
            throw new RuntimeException(t.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }


    /**
     * buildInitialPropertyMap create the initial set of properties from the contents of launch.ini
     * and for a few other properties necessary to launch defaults are supplied if not provided.
     * The value '@null' will set the map value to null.
     *
     * @return a map containing the initial properties
     */
    private static Map<String, String> buildInitialPropertyMap() {
        Map<String, String> initialPropertyMap = new HashMap<String, String>();
        String carbonConfigHome = System.getProperty(LauncherConstants.CARBON_CONFIG_DIR_PATH);
        Properties launchProperties;
        if (carbonConfigHome == null) {
            String carbonHome = System.getProperty(LauncherConstants.CARBON_HOME);
            launchProperties = Utils.loadProperties(Paths.get(carbonHome, "repository", "conf", "etc", LauncherConstants.LAUNCH_INI).toString());
        } else {
            launchProperties = Utils.loadProperties(Paths.get(carbonConfigHome, "etc", LauncherConstants.LAUNCH_INI).toString());
        }
        for (Object o : launchProperties.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (key.endsWith("*")) { //$NON-NLS-1$
                if (value.equals(NULL_IDENTIFIER)) {
                    Utils.clearPrefixedSystemProperties(key.substring(0, key.length() - 1),
                            initialPropertyMap);
                }
            } else if (value.equals(NULL_IDENTIFIER)) {
                initialPropertyMap.put(key, null);
            } else {
                initialPropertyMap.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
        try {

            /*
             *  in order to support multiple profiling, the new install, configuration and workspace area got to move
             *  from ../components/ to ../components/ PROFILE_ID/
             */
            // install.area if not specified
            if (initialPropertyMap.get(OSGI_INSTALL_AREA) == null) {
                //specifying the install.area according to the running Profile
                File installDir = new File(platformDirectory, System.getProperty(LauncherConstants.PROFILE_ID));

                initialPropertyMap
                        .put(OSGI_INSTALL_AREA, installDir.toURL().toExternalForm());
            }

            // configuration.area if not specified
            if (initialPropertyMap.get(OSGI_CONFIGURATION_AREA) == null) {
                File configurationDirectory = new File(platformDirectory,
                        System.getProperty(LauncherConstants.PROFILE_ID) +
                                File.separator + "configuration");
                initialPropertyMap.put(OSGI_CONFIGURATION_AREA,
                        configurationDirectory.toURL().toExternalForm());
            }

            // instance.area if not specified
            if (initialPropertyMap.get(OSGI_INSTANCE_AREA) == null) {
                File workspaceDirectory = new File(platformDirectory, System.getProperty(LauncherConstants.PROFILE_ID) +
                        File.separator + "workspace");
                initialPropertyMap
                        .put(OSGI_INSTANCE_AREA, workspaceDirectory.toURL().toExternalForm());
            }

            // osgi.framework if not specified
            if (initialPropertyMap.get(OSGI_FRAMEWORK) == null) {
                // search for osgi.framework in osgi.install.area
                /*String installArea = initialPropertyMap.get(OSGI_INSTALL_AREA);

                // only support file type URLs for install area
                if (installArea.startsWith(FILE_SCHEME)) {
                    installArea = installArea.substring(FILE_SCHEME.length());
                }

                String path = new File(installArea, "plugins").toString();*/
                String path = new File(platformDirectory, "plugins").toString();
                path = Utils.searchFor(FRAMEWORK_BUNDLE_NAME, path);
                if (path == null) {
                    throw new RuntimeException("Could not find framework");
                }

                initialPropertyMap.put(OSGI_FRAMEWORK,
                        new File(path).toURL().toExternalForm());
            }
            if (initialPropertyMap.get(P2_DATA_AREA) == null) {
                /*initialPropertyMap.put(P2_DATA_AREA, new File(platformDirectory, System.getProperty(LauncherConstants.PROFILE_ID) +
                                                                   File.separator + "p2").toString());*/

                initialPropertyMap.put(P2_DATA_AREA, new File(platformDirectory, "p2").toString());
                //System.out.println("the data area: " + initialPropertyMap.get(P2_DATA_AREA));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error establishing location");
        }
        return initialPropertyMap;
    }
}
