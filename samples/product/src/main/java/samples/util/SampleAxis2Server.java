/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package samples.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class SampleAxis2Server {

    private static final Log log = LogFactory.getLog(SampleAxis2Server.class);

    /**
     * Expected system properties
     * http_port: Port to bind HTTP transport (default is 9000)
     * https_port: Port to bind HTTPS transport (default is 9002)
     * server_name: Name of this instance of the server (optional)
     *
     * @param args 1: Axis2 repository
     *             2: Axis2 configuration file (axis2.xml)
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (System.getProperty("carbon.components.dir.path") != null) {
            addJarFileUrls(new File(System.getProperty("carbon.components.dir.path")));
        }
        startServer(args);
        addShutdownHook();
    }

    private static void addShutdownHook() {
        Thread shutdownHook = new Thread() {
            public void run() {
                log.info("Shutting down SimpleAxisServer ...");
                try {
                    stopServer();
                    log.info("Shutdown complete");
                    log.info("Halting JVM");
                } catch (Exception e) {
                    log.warn("Error occurred while shutting down SimpleAxisServer : " + e);
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public static void startServer(String[] args) throws Exception {
        SampleAxis2ServerManager.getInstance().start(args);
    }

    public static void stopServer() throws Exception {
        SampleAxis2ServerManager.getInstance().stop();
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
                    child.getName().toLowerCase().endsWith(".jar") && !child.getName().toLowerCase().startsWith("org.apache.synapse.module") && !child.getName().toLowerCase().startsWith("wss4j") && !child.getName().contains("slf4j")) {
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
