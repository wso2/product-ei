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

package org.wso2.carbon.core.transports;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axiom.util.blob.OverflowBlob;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.transport.http.AxisServlet;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.protocol.HTTP;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.osgi.util.tracker.ServiceTracker;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.core.CarbonThreadFactory;
import org.wso2.carbon.core.transports.metering.MeteredServletRequest;
import org.wso2.carbon.core.transports.metering.MeteredServletResponse;
import org.wso2.carbon.core.transports.metering.RequestDataPersister;
import org.wso2.carbon.micro.integrator.core.internal.CarbonCoreDataHolder;
import org.wso2.carbon.utils.ServerConstants;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This is the main HTTP and HTTPS transport
 */
public class CarbonServlet extends AxisServlet {

    private static final long serialVersionUID = 3460108128756524161L;

    private Map<String, HttpGetRequestProcessor> getRequestProcessors =
            new LinkedHashMap<String, HttpGetRequestProcessor>();
    private static final QName ITEM_QN = new QName(ServerConstants.CARBON_SERVER_XML_NAMESPACE, "Item");
    private static final QName CLASS_QN = new QName(ServerConstants.CARBON_SERVER_XML_NAMESPACE, "Class");

    private static final Log log = LogFactory.getLog(CarbonServlet.class);

    private ScheduledExecutorService requestDataPersisterScheduler = Executors
            .newScheduledThreadPool(25, new CarbonThreadFactory(new ThreadGroup("RequestDataPersisterThread")));

    private RequestDataPersisterTask requestDataPersister;

    private boolean isMeteringEnabled = false;

    public CarbonServlet(ConfigurationContext configurationContext){
        this.configContext = configurationContext;
    }

    public void init(ServletConfig config) throws ServletException {
        this.axisConfiguration = this.configContext.getAxisConfiguration();
        this.servletConfig = config;
        populateGetRequestProcessors();
        configContext.setProperty("GETRequestProcessorMap", getRequestProcessors);
        initParams();
        String isMeteringEnabledStr = ServerConfiguration.getInstance().getFirstProperty("EnableMetering");
        if(isMeteringEnabledStr!=null){
            isMeteringEnabled = Boolean.parseBoolean(isMeteringEnabledStr);
        }
        if(isMeteringEnabled){
            requestDataPersister = new RequestDataPersisterTask();
            new Thread(requestDataPersister).start();
            requestDataPersisterScheduler.scheduleWithFixedDelay(requestDataPersister, 5,
                    5, TimeUnit.SECONDS);
        }
    }

    private void populateGetRequestProcessors() throws ServletException {
        try {
            OMElement docEle = XMLUtils.toOM(CarbonCoreDataHolder.getInstance().getServerConfigurationService().getDocumentElement());
            if (docEle != null) {
                SimpleNamespaceContext nsCtx = new SimpleNamespaceContext();
                nsCtx.addNamespace("wsas", ServerConstants.CARBON_SERVER_XML_NAMESPACE);
                XPath xp = new AXIOMXPath("//wsas:HttpGetRequestProcessors/wsas:Processor");
                xp.setNamespaceContext(nsCtx);
                List nodeList = xp.selectNodes(docEle);
                for (Object aNodeList : nodeList) {
                    OMElement processorEle = (OMElement) aNodeList;
                    OMElement itemEle = processorEle.getFirstChildWithName(ITEM_QN);
                    if (itemEle == null) {
                        throw new ServletException("Required element, 'Item' not found!");
                    }
                    OMElement classEle = processorEle.getFirstChildWithName(CLASS_QN);
                    HttpGetRequestProcessor processor;
                    if (classEle == null) {
                        throw new ServletException("Required element, 'Class' not found!");
                    } else {
                        processor =
                                (HttpGetRequestProcessor)
                                        Class.forName(classEle.getText().trim()).newInstance();
                    }
                    getRequestProcessors.put(itemEle.getText().trim(), processor);
                }
            }
        } catch (Exception e) {
            log.error("Cannot populate HTTPGetRequestProcessors", e);
            throw new ServletException(e);
        }
    }

    /**
     * WSAS specific GET implementation
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        initContextRoot(request);
        boolean isRequestHandled = false;

        try {
            String queryString = request.getQueryString();
            if (queryString != null) {
                for (String item : getRequestProcessors.keySet()) {
                    if (queryString.indexOf(item) == 0 &&
                            (queryString.equals(item) ||
                                    queryString.indexOf('&') == item.length() ||
                                    queryString.indexOf('=') == item.length())) {
                        processWithGetProcessor(request, response, item);
                        isRequestHandled = true;
                        break;
                    }
                }
            }
            if (!isRequestHandled) {
                handleRestRequest(request, response); // Assume that this is a REST request
            }
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    private void processWithGetProcessor(HttpServletRequest request,
                                            HttpServletResponse response,
                                            String item) throws Exception {
        OverflowBlob temporaryData = new OverflowBlob(256, 4048, "_servlet", ".dat");
        try {
            CarbonHttpRequest carbonHttpRequest = new CarbonHttpRequest(
                    "GET", request.getRequestURI(), request.getRequestURL().toString());

            Enumeration names = request.getParameterNames();
            while (names.hasMoreElements()) {
                Object name = names.nextElement();
                if (name != null && name instanceof String) {
                    carbonHttpRequest.setParameter((String) name,
                            request.getParameter((String) name));
                }
            }

            carbonHttpRequest.setContextPath(request.getContextPath());
            carbonHttpRequest.setQueryString(request.getQueryString());

            CarbonHttpResponse carbonHttpResponse = new CarbonHttpResponse(
                    temporaryData.getOutputStream());

            (getRequestProcessors.get(item)).process(carbonHttpRequest,
                                                     carbonHttpResponse, configContext);

            // adding headers
            Map responseHeaderMap = carbonHttpResponse.getHeaders();
            for (Object obj : responseHeaderMap.entrySet()) {
                Map.Entry entry = (Map.Entry) obj;
                response.setHeader(entry.getKey().toString(), entry.getValue().toString());
            }

            // setting status code
            response.setStatus(carbonHttpResponse.getStatusCode());

            // setting error codes
            if (carbonHttpResponse.isError()) {
                if (carbonHttpResponse.getStatusMessage() != null) {
                    response.sendError(carbonHttpResponse.getStatusCode(),
                            carbonHttpResponse.getStatusMessage());
                } else {
                    response.sendError(carbonHttpResponse.getStatusCode());
                }
            }

            if (carbonHttpResponse.isRedirect()) {
                response.sendRedirect(carbonHttpResponse.getRedirect());
            }

            if (carbonHttpResponse.getHeaders().get(HTTP.CONTENT_TYPE) != null) {
                response.setContentType(
                        carbonHttpResponse.getHeaders().get(HTTP.CONTENT_TYPE));
            }

            temporaryData.writeTo(response.getOutputStream());
        } finally {
            temporaryData.release();
        }
    }

    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
    	// Here we are using MeteredServletRequest and MeteredServletResponse to meter
    	// the request and response.
        if(isMeteringEnabled){
            final MeteredServletRequest wrappedRequest = new MeteredServletRequest(request);
            final MeteredServletResponse wrappedResponse = new MeteredServletResponse(response);
            super.doPost(wrappedRequest, wrappedResponse);
            // Call the callback to persist the wrapped request and wrapped response data
            requestDataPersister.addRequestResponse(wrappedRequest, wrappedResponse);
        }else{
            super.doPost(request, response);
        }
    }

    private class RequestDataPersisterTask implements Runnable {
        private volatile List<RequestResponse> list = new CopyOnWriteArrayList<RequestResponse>();

        public void addRequestResponse(MeteredServletRequest wrappedRequest,
                                       MeteredServletResponse wrappedResponse){
            list.add(new RequestResponse(wrappedRequest, wrappedResponse));
        }

        @Override
        public void run() {
            try {
                int itemsProcessed = 0;
                for (RequestResponse requestResponse : list) {
                    persistRequestData(requestResponse.getWrappedRequest(),
                                       requestResponse.getWrappedResponse());
                    list.remove(requestResponse);
                    itemsProcessed++;
                    if(itemsProcessed > 200){ // Don't continue inifinitely
                        return;
                    }
                }
            } catch (Throwable e) {
                log.error("Cannot persist request data", e);
            }
        }
    }

    private static class RequestResponse {
        private MeteredServletRequest wrappedRequest;
        private MeteredServletResponse wrappedResponse;

        private RequestResponse(MeteredServletRequest wrappedRequest,
                                MeteredServletResponse wrappedResponse) {
            this.wrappedRequest = wrappedRequest;
            this.wrappedResponse = wrappedResponse;
        }

        public MeteredServletRequest getWrappedRequest() {
            return wrappedRequest;
        }

        public MeteredServletResponse getWrappedResponse() {
            return wrappedResponse;
        }
    }

    protected void persistRequestData(MeteredServletRequest wrappedRequest,
                                      MeteredServletResponse wrappedResponse) {
        RequestDataPersister requestDataPersister = null;
        ServiceTracker meteringDataPersistTracker =
                new ServiceTracker(CarbonCoreDataHolder.getInstance().getBundleContext(),
                                   RequestDataPersister.class.getName(),
                                   null);
        meteringDataPersistTracker.open();
        try {
            requestDataPersister = (RequestDataPersister) meteringDataPersistTracker.getService();
        } finally {
            meteringDataPersistTracker.close();
        }
        if (requestDataPersister != null) {
            requestDataPersister.persist(wrappedRequest, wrappedResponse);
        }
    }

    protected void handleRestRequest(HttpServletRequest request,
                                     HttpServletResponse response) throws IOException,
                                                                          ServletException {
        if (!disableREST) {
            new RestRequestProcessor(HTTPConstants.HTTP_METHOD_GET,
                                     request,
                                     response).processURLRequest();
        } else {
            showRestDisabledErrorMessage(response);
        }
    }

    public void addGetRequestProcessor(String key, HttpGetRequestProcessor processor) {
        getRequestProcessors.put(key, processor);
    }

    public void removeGetRequestProcessor(String key) {
        getRequestProcessors.remove(key);
    }
}
