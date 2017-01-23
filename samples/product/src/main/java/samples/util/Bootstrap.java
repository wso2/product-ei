/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package samples.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * This bootstrap class used for bootstrapping a Axis2 server in standalone
 * mode for windows environment.
 * This will avoid long classpath issue occurs @ windows environment)
 * Rather defining all library classpaths @ wrapper.conf user can give,
 * 'wrapper.java.additional' parameter which should have all library paths seperated by commas(,).
 * eg:
 * wrapper.java.additional.8=-Djar.class.paths=../../lib,../../repository/components/plugins,
 * ../../repository/components/extensions,
 * <p/>
 * In addition to above parameter 'system home' also defined for the current working
 * directory.
 * eg:
 * wrapper.java.additional.9=-Dsystem.home=.
 * <p/>
 * Please note that this class will add only the *.jar files to the classpath. Other required files
 * user should provide in the wrapper.conf
 */

public class Bootstrap {

    private static final String JAR_CLASS_PATHS = "jar.class.paths";
    private static final String SYSTEM_HOME = "system.home";

    public static void main(String args[]) {

        if (System.getProperty(JAR_CLASS_PATHS) != null) {
            String root;

            root = System.getProperty(SYSTEM_HOME, ".");
            String classpaths = System.getProperty(JAR_CLASS_PATHS);

            String[] paths = classpaths.split(","); // read class paths from
            // wrapper.conf file
            List<URL> classpath = new ArrayList<URL>();

            if (paths == null || root == null) {
                System.out.println("system.home and jar.class.paths system properties should be set");
                System.exit(1);
            }
            try {
                // add all *.jars available under all class paths
                for (String path : paths) {

                    String path_new, prefix, suffix_without_fileseperator;
                    // find the prefix of the classpaths(eg: ../../lib/conf or lib/conf,
                    // assuming paths will be provided only in these 2 ways
                    // against the 'system.home' path)

                    int index = path.lastIndexOf("../");
                    if (index > 0) {
                        prefix = path.substring(0, index + 3);
                        path_new = path.substring(index + 3, path.length());
                    } else {
                        prefix = "";
                        path_new = path;
                    }
                    //split the path to get folders
                    String folders[] = path_new.split("/");

                    String suffix = "";
                    for (String folder : folders) {
                        String suffix_new = folder + File.separator;
                        suffix = suffix + suffix_new;
                    }

                    int fileSepLastIndex = suffix.lastIndexOf(File.separator);
                    suffix_without_fileseperator = suffix.substring(0, fileSepLastIndex);

                    File file;
                    String jarRoot;
                    if ("".equals(prefix)) {
                        file = new File(root + File.separator + suffix);
                        jarRoot = root + File.separator + suffix_without_fileseperator;
                    } else {
                        file =
                                new File(root + File.separator + prefix + File.separator +
                                        suffix);
                        jarRoot =
                                root + File.separator + prefix + File.separator +
                                        suffix_without_fileseperator;
                    }
                    classpath.add(file.toURI().toURL());
                    addJarFileUrls(classpath, new File(jarRoot));
                }
                //We do not want system class loader or even extension class loaders our parent.
                //We want only boot class loader as our parent. Boot class loader is represented as null.
                ClassLoader classLoader =
                        new URLClassLoader(classpath.toArray(new URL[classpath.size()]), null);

                // Set the proper classloader for this thread.
                Thread.currentThread().setContextClassLoader(classLoader);

                // Use reflection to load a class to normally load the
                // rest of the app. Reflection will use the Thread's context class loader
                // and therefore pick up the rest of our libraries.

                Class appClass = classLoader.loadClass("samples.util.SampleAxis2Server");
                Object app = appClass.newInstance();

                Method m = app.getClass().getMethod("startServer",
                        new Class[]{String[].class});
                m.invoke(app, new Object[]{args});

            } catch (Exception e) {
                System.out.println("Server could not start due to class loading issue " + e);
                System.exit(1);
            }

        }

    }

    /**
     * Add JAR files found in the given directory to the list of URLs.
     *
     * @param jarUrls the list to add URLs to
     * @param root    the directory to recursively search for JAR files.
     * @throws MalformedURLException If a provided JAR file URL is malformed
     */
    private static void addJarFileUrls(List<URL> jarUrls, File root) throws MalformedURLException {
        File[] children = root.listFiles();

        if (children == null) {
            return;
        }
        for (File child : children) {
            if (child.isDirectory() && child.canRead()) {
                addJarFileUrls(jarUrls, child);
            } else if (child.isFile() && child.canRead() &&
                    child.getName().toLowerCase().endsWith(".jar")) {
                jarUrls.add(child.toURI().toURL());
            }
        }
    }
}
