/*                                                                             
 * Copyright 2004,2005 The Apache Software Foundation.                         
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
package org.wso2.carbon.service.mgt.util;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.transports.http.HttpTransportListener;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.NetworkUtils;

import java.io.File;
import java.net.SocketException;

//import org.wso2.carbon.core.multitenancy.transports.DummyTransportListener;

/**
 *
 */
public final class Utils {

    private static final Log log = LogFactory.getLog(Utils.class);

    private static boolean isServletTransport = false;
    private static boolean isServletTransportSet = false;
    private static final int SECONDS_PER_DAY = 3600 * 24;

    private Utils() {
    }

    public static String[] getWsdlInformation(String serviceName,
                                              AxisConfiguration axisConfig) throws AxisFault {
        String ip;
        try {
            ip = NetworkUtils.getLocalHostname();
        } catch (SocketException e) {
            throw new AxisFault("Cannot get local host name", e);
        }

        //TODO Ideally, The transport on which wsdls are displayed, should be configurable.
        TransportInDescription transportInDescription = axisConfig.getTransportIn("http");

        if (transportInDescription == null) {
            transportInDescription = axisConfig.getTransportIn("https");
        }

        if (transportInDescription != null) {
            EndpointReference[] epr =
                    transportInDescription.getReceiver().getEPRsForService(serviceName, ip);
            String wsdlUrlPrefix = epr[0].getAddress();
            if (wsdlUrlPrefix.endsWith("/")) {
                wsdlUrlPrefix = wsdlUrlPrefix.substring(0, wsdlUrlPrefix.length() - 1);
            }
            return new String[]{wsdlUrlPrefix + "?wsdl", wsdlUrlPrefix + "?wsdl2"};
        }
        return new String[]{};
    }

    /**
     * A utility method to check whether Axis2 uses the Servlet transport or the NIO transport
     *
     * @param axisConfig
     * @return
     */
    private static boolean isServletTransport(AxisConfiguration axisConfig) {
        if (!isServletTransportSet) {
            TransportInDescription transportInDescription = axisConfig.getTransportIn("http");
            if (transportInDescription == null) {
                transportInDescription = axisConfig.getTransportIn("https");
            }

            if (transportInDescription != null) {
                if (transportInDescription.getReceiver() instanceof HttpTransportListener) {
                    isServletTransport = true;
                }
            }
            isServletTransportSet = true;
        }
        return isServletTransport;
    }

    public static String getTryitURL(String serviceName,
                                     ConfigurationContext configurationContext)
            throws AxisFault {
        AxisConfiguration axisConfig = configurationContext.getAxisConfiguration();
        // If axis2 uses the servlet transport then we could use the prefix of its endpoint URL to
        // determine the tryit url
        String wsdlURL = getWsdlInformation(serviceName, axisConfig)[0];
        String tryitPrefix = wsdlURL.substring(0, wsdlURL.length() - serviceName.length() - 5);
        if (!isServletTransport(axisConfig)) {
            int tenantIndex = tryitPrefix.indexOf("/t/");
            if (tenantIndex != -1) {
                String tmpTryitPrefix = tryitPrefix.substring(
                        tryitPrefix.substring(0, tryitPrefix.indexOf("/t/")).lastIndexOf("/"));  
                //Check if the  Webapp context root of WSO2 Carbon is set.
                tryitPrefix = tryitPrefix.replaceFirst("//", "");
                if(tryitPrefix.substring(0, tryitPrefix.indexOf("/services/")).lastIndexOf("/") > -1){
                    tryitPrefix = tryitPrefix.substring(
                            tryitPrefix.substring(0, tryitPrefix.indexOf("/services/")).lastIndexOf("/"));                
                }else{
                	tryitPrefix = tmpTryitPrefix;	
                }
                
            } else {
                tryitPrefix = configurationContext.getServiceContextPath() + "/";
            }
        }
        return CarbonUtils.getProxyContextPath(false) + tryitPrefix + serviceName + "?tryit";
    }

    /**
     * Deletes all empty directories on the given path, starting from the end. If
     * an unempty folder is found, stop there and return. This is only done, upto
     * the given directory (stopPoint).
     * Path can be a file path or a directory path.
     * <p/>
     * Ex: path = /home/axis2/repository/isuru/foo/bar
     * upto = isuru
     * - delete bar if it is empty
     * - delete foo if it is empty
     * - delete isuru if it is empty and stop as stopPoint = isuru
     *
     * @param path      - path to delete directories
     * @param stopPoint - dirs are deleted only upto this point
     */
    public static void deleteEmptyDirsOnPath(String path, String stopPoint) {
        // if the stop point is not in the path, we don't proceed
        if (!path.contains(File.separator + stopPoint)) {
            return;
        }
        // remove ending "/" if any
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length());
        }
        // if there's a file in the current path and if it is an empty dir, delete it
        File currentFile = new File(path);
        if (currentFile.exists() && currentFile.isDirectory()) {
            String[] contents = currentFile.list();
            if ((contents == null || contents.length == 0)) {
                // folder is empty, delete it
                if (!currentFile.delete()) {
                    log.error("Unable to delete File : " + currentFile.getAbsoluteFile());
                }
            } else {
                // if this folder is not empty we stop here and return
                return;
            }
        }
        // if the path ends with stopPoint, we are done
        if (!path.endsWith(File.separator + stopPoint)) {
            // if not done, call recursively for the next dir
            deleteEmptyDirsOnPath(path.substring(0, path.lastIndexOf(File.separator)), stopPoint);
        }
    }

    /**
     * Calculates the difference between current time and the given time and formats it according
     * to the days, hours and minutes format.
     *
     * @param timeInMilliSeconds - start time
     * @return - formatted duration
     */
    public static String getFormattedDuration(long timeInMilliSeconds) {
        long timeDifference = (System.currentTimeMillis() - timeInMilliSeconds) / 1000;
        long days;
        int hours;
        int minutes;
        String value = "";
        days = timeDifference / SECONDS_PER_DAY;
        if (days > 0) {
            value += days + "day(s) ";
        }
        timeDifference = timeDifference - (days * SECONDS_PER_DAY);
        hours = (int) (timeDifference / 3600);
        if (hours > 0) {
            value += hours + "hr(s) ";
        }
        timeDifference = timeDifference - (hours * 3600);
        minutes = (int) (timeDifference / 60);
        value += minutes + "min(s)";
        return value;
    }
}
