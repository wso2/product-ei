/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.core.transports.util;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.engine.AxisConfiguration;
import org.wso2.carbon.core.util.SystemFilter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class ServiceHTMLProcessor {

    /**
     * Returns the HTML text for the list of services deployed.
     * This can be delegated to another Class as well
     * where it will handle more options of GET messages.
     *
     * @param configContext The ConfigurationContext
     * @return The services list as an HTML string
     */
    public static String getServicesHTML(ConfigurationContext configContext) {
        StringBuffer temp = new StringBuffer();
        Iterator serviceGroupsItr = configContext.getAxisConfiguration().getServiceGroups();
        boolean status = false;
        if (serviceGroupsItr.hasNext()) {
            status = true;
            temp.append("<h2>" + "Deployed services" + "</h2>");
            while (serviceGroupsItr.hasNext()) {
                AxisServiceGroup axisServiceGroup = (AxisServiceGroup) serviceGroupsItr.next();
                if (!SystemFilter.isFilteredOutService(axisServiceGroup)) {
                    status = true;
                    for (Iterator serviceItr = axisServiceGroup.getServices();
                         serviceItr.hasNext();) {
                        AxisService axisService = (AxisService) serviceItr.next();
                        temp.append("<h3><a href=\"").append(axisService.getName()).append("?info\">").
                                append(axisService.getName()).append("</a></h3>");
                    }
                }
            }
        }
        Hashtable erroneousServices =
                configContext.getAxisConfiguration().getFaultyServices();

        if ((erroneousServices != null) && !erroneousServices.isEmpty()) {
            temp.append("<hr><h2><font color=\"blue\">Faulty Services</font></h2>");
            status = true;
            Enumeration faultyservices = erroneousServices.keys();
            while (faultyservices.hasMoreElements()) {
                String faultyserviceName = (String) faultyservices.nextElement();
                temp.append("<h3><font color=\"blue\">").append(faultyserviceName).
                        append("</font></h3>");
            }
        }

        if (!status) {
            temp.append("<h2>There are no services deployed</h2>");
        }

        return "<html><head><title>Axis2: Services</title></head>" + "<body>" + temp
               + "</body></html>";
    }

    public static String printServiceHTML(String serviceName,
                                          ConfigurationContext configurationContext) {
        StringBuffer temp = new StringBuffer();
        try {
            AxisConfiguration axisConfig = configurationContext.getAxisConfiguration();
            AxisService axisService = axisConfig.getService(serviceName);
            if (axisService != null) {
                if (!axisService.isActive()) {
                    temp.append("<b>Service ").append(serviceName).
                            append(" is inactive. Cannot display service information.</b>");
                } else {
                    temp.append("<h3>").append(axisService.getName()).append("</h3>");
                    temp.append("<a href=\"").append(axisService.getName()).append("?wsdl\">wsdl</a> : ");
                    temp.append("<a href=\"").append(axisService.getName()).append("?xsd\">schema</a> : ");
                    temp.append("<a href=\"").append(axisService.getName()).append("?policy\">policy</a><br/>");
                    temp.append("<i>Service Description :  ").
                            append(axisService.getDocumentation()).append("</i><br/><br/>");

                    for (Iterator pubOpIter = axisService.getPublishedOperations().iterator();
                         pubOpIter.hasNext();) {
                        temp.append("Published operations <ul>");
                        for (; pubOpIter.hasNext();) {
                            AxisOperation axisOperation = (AxisOperation) pubOpIter.next();
                            temp.append("<li>").
                                    append(axisOperation.getName().getLocalPart()).append("</li>");
                        }
                        temp.append("</ul>");
                    }
                }
            } else {
                temp.append("<b>Service ").append(serviceName).
                        append(" not found. Cannot display service information.</b>");
            }
            return "<html><head><title>Service Information</title></head>" + "<body>" + temp
                   + "</body></html>";
        }
        catch (AxisFault axisFault) {
            return "<html><head><title>Error Occurred</title></head>" + "<body>"
                   + "<hr><h2><font color=\"blue\">" + axisFault.getMessage() + "</font></h2></body></html>";
        }
    }
}
