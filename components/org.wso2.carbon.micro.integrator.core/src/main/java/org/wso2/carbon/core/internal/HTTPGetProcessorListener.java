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
package org.wso2.carbon.core.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.wso2.carbon.core.transports.CarbonServlet;
import org.wso2.carbon.core.transports.HttpGetRequestProcessor;
import org.wso2.carbon.utils.component.xml.ComponentConstants;

/**
 * this class will Listen for bundles exposing  HTTPGetRequestProcessors as OSGI services and add
 * then to the CarbonServlet.
 */
public class HTTPGetProcessorListener implements ServiceListener {

    CarbonServlet carbonServlet;
    BundleContext bundleContext;

    public HTTPGetProcessorListener(CarbonServlet carbonServlet, BundleContext bundleContext) {
        this.carbonServlet = carbonServlet;
        this.bundleContext = bundleContext;
    }

    /**
     * When any of the OSGI services exposing a HTTPGetRequestProcesso changes we have to act
     * accordingly
     * @param serviceEvent
     */
    public void serviceChanged(ServiceEvent serviceEvent) {
        ServiceReference reference = serviceEvent.getServiceReference();
        addHTTPGetRequestProcessor(reference, serviceEvent.getType());
    }

    public void addHTTPGetRequestProcessor(ServiceReference reference, int action) {
        HttpGetRequestProcessor getRequestProcessor = (HttpGetRequestProcessor)
                bundleContext.getService(reference);
        String item = (String) reference.getProperty(ComponentConstants.ELE_ITEM);
        // If the event if a service registration or modification we need to add it.
        // If the event is an unregistration we need to remove it.
        if (action == ServiceEvent.REGISTERED || action == ServiceEvent.MODIFIED) {
            carbonServlet.addGetRequestProcessor(item, getRequestProcessor);
        } else if (action == ServiceEvent.UNREGISTERING) {
            carbonServlet.removeGetRequestProcessor(item);
        }
    }
}
