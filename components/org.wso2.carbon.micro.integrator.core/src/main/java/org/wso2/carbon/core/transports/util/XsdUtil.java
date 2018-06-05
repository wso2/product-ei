/*
 * Copyright 2006,2007 WSO2, Inc. http://www.wso2.org
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

import org.apache.axiom.attachments.utils.IOUtils;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.description.AxisService;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.apache.ws.commons.schema.XmlSchema;
import org.wso2.carbon.core.transports.CarbonHttpRequest;
import org.wso2.carbon.core.transports.CarbonHttpResponse;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.carbon.utils.deployment.GhostDeployerUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;

public final class XsdUtil {

    private XsdUtil() {
    }

    public static void printXsd(CarbonHttpRequest request,
                                CarbonHttpResponse response,
                                ConfigurationContext configCtx,
                                String serviceName,
                                AxisService axisService) throws IOException {
        if (GhostDeployerUtils.isGhostService(axisService)) {
            // if the existing service is a ghost service, deploy the actual one
            axisService = GhostDeployerUtils.deployActualService(configCtx
                    .getAxisConfiguration(), axisService);
        }
        if (!RequestProcessorUtil.canExposeServiceMetadata(axisService)) {
            response.setError(HttpStatus.SC_FORBIDDEN,
                              "Access to service metadata for service: " + serviceName +
                              " has been forbidden");
            return;
        }
        OutputStream outputStream = response.getOutputStream();
        String contextRoot = request.getContextPath();
        if (axisService == null) {
            response.addHeader(HTTP.CONTENT_TYPE, "text/html");
            response.setError(HttpStatus.SC_NOT_FOUND);
            outputStream.write(("<h4>Service " +
                    serviceName +
                    " is not found. Cannot display Schema.</h4>").getBytes());
            outputStream.flush();
            return;
        }

        if (!axisService.isActive()) {
            response.addHeader(HTTP.CONTENT_TYPE, "text/html");
            outputStream.write(("<h4>Service " +
                    serviceName +
                    " is inactive. Cannot display Schema.</h4>").getBytes());
            outputStream.flush();
            return;
        }

        //cater for named xsds - check for the xsd name
        String uri = request.getQueryString();
        if (request.getQueryString().endsWith(".xsd")) {
            String schemaName = uri.substring(uri.lastIndexOf('=') + 1);

            Map services = configCtx.getAxisConfiguration().getServices();
            AxisService service = (AxisService) services.get(serviceName);
            if (service != null) {
                //run the population logic just to be sure
                service.populateSchemaMappings();
                //write out the correct schema
                Map schemaTable = service.getSchemaMappingTable();
                XmlSchema schema = (XmlSchema) schemaTable.get(schemaName);

                if (schema == null) {
                    int slashIndex = schemaName.lastIndexOf('/');
                    int dotIndex = schemaName.lastIndexOf('.');
                    if (slashIndex > 0) {
                        String schemaKey = schemaName.substring(slashIndex + 1, dotIndex);
                        schema = (XmlSchema) schemaTable.get(schemaKey);
                    }
                }

                if (schema == null) {
                    int dotIndex = schemaName.indexOf('.');
                    if (dotIndex > 0) {
                        String schemaKey = schemaName.substring(0, dotIndex);
                        schema = (XmlSchema) schemaTable.get(schemaKey);
                    }
                }
                //schema found - write it to the stream
                if (schema != null) {
                    response.setStatus(HttpStatus.SC_OK);
                    response.addHeader(HTTP.CONTENT_TYPE, "text/xml");
                    schema.write(response.getOutputStream());
                    return;
                } else {
                    InputStream instream = service.getClassLoader()
                            .getResourceAsStream(DeploymentConstants.META_INF + "/" + schemaName);

                    if (instream != null) {
                        response.setStatus(HttpStatus.SC_OK);
                        response.addHeader(HTTP.CONTENT_TYPE, "text/xml");
                        OutputStream outstream = response.getOutputStream();
                        boolean checkLength = true;
                        int length = Integer.MAX_VALUE;
                        int nextValue = instream.read();
                        if (checkLength) {
                            length--;
                        }
                        while (-1 != nextValue && length >= 0) {
                            outstream.write(nextValue);
                            nextValue = instream.read();
                            if (checkLength) {
                                length--;
                            }
                        }
                        outstream.flush();
                        return;
                    } else {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int ret = service.printXSD(baos, schemaName);
                        if (ret > 0) {
                            baos.flush();
                            instream = new ByteArrayInputStream(baos.toByteArray());
                            response.setStatus(HttpStatus.SC_OK);
                            response.addHeader(HTTP.CONTENT_TYPE, "text/xml");
                            OutputStream outstream = response.getOutputStream();
                            boolean checkLength = true;
                            int length = Integer.MAX_VALUE;
                            int nextValue = instream.read();
                            if (checkLength) {
                                length--;
                            }
                            while (-1 != nextValue && length >= 0) {
                                outstream.write(nextValue);
                                nextValue = instream.read();
                                if (checkLength) {
                                    length--;
                                }
                            }
                            outstream.flush();
                            return;
                        }
                    }
                }
            }
        }

        axisService.populateSchemaMappings();
        Map schemaMappingtable =
                axisService.getSchemaMappingTable();
        String xsds = request.getParameter("xsd");
        if (xsds != null && xsds.trim().length() != 0) {
            response.addHeader(HTTP.CONTENT_TYPE, "text/xml");
            XmlSchema schema =
                    (XmlSchema) schemaMappingtable.get(xsds);
            if (schema == null) {
                int dotIndex = xsds.indexOf('.');
                if (dotIndex > 0) {
                    String schemaKey = xsds.substring(0, dotIndex);
                    schema = (XmlSchema) schemaMappingtable.get(schemaKey);
                }
            }
            if (schema != null) {
                //schema is there - pump it outs
                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF8")) {
                    schema.write(outputStreamWriter);
                    outputStream.flush();
                }
            } else if  (xsds.endsWith(".xsd") && xsds.indexOf("..") == -1){
                InputStream in = axisService.getClassLoader()
                        .getResourceAsStream(DeploymentConstants.META_INF + "/" + xsds);
                if (in != null) {
                    outputStream.write(IOUtils.getStreamAsByteArray(in));
                    outputStream.flush();
                    outputStream.close();
                } else {
                    response.setError(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                String msg = "Invalid schema " + xsds + " requested";
                throw new IOException(msg);
            }
            return;
        }

        ArrayList schemas = axisService.getSchema();
        if (schemas.size() == 1) {
            response.addHeader(HTTP.CONTENT_TYPE, "text/xml");
            // Write to the output stream
            processSchema((XmlSchema) schemas.get(0), outputStream, contextRoot, request);

        } else {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                XmlSchema schema = axisService.getSchema(Integer.parseInt(idParam));
                if (schema != null) {
                    response.addHeader(HTTP.CONTENT_TYPE, "text/xml");
                    processSchema(schema, outputStream, contextRoot, request);
                } else {
                    response.addHeader(HTTP.CONTENT_TYPE, "text/html");
                    outputStream.write("<h4>Schema not found!</h4>".getBytes());
                }
            } else {
                /*String ipAddress = "http://" + NetworkUtils.getLocalHostname() + ":" +
                        ServerManager.getInstance().getHttpPort();
                String version =
                        ServerConfiguration.getInstance().getFirstProperty("Version");
                outputStream.write(("<html><head>" +
                        "<title>WSO2 Web Services Application Server v" +
                        version +
                        "Management Console" +
                        " - " +
                        axisService.getName() +
                        " Service Schema</title>" +
                        "</head>" +
                        "<body>" +
                        "<b>Schemas for " +
                        axisService.getName() +
                        " service</b><br/><br/>").getBytes());
                if (schemas.size() != 0) {
                    for (int i = 0; i < schemas.size(); i++) {
                        String st = "<a href=\"" + ipAddress +
                                RequestProcessorUtil.getServiceContextPath(configCtx) + "/" +
                                axisService.getName() + "?xsd&id=" + i +
                                "&" + ServerConstants.HTTPConstants.ANNOTATION + "=true" + "\">Schema " + i +
                                "</a><br/>";
                        outputStream.write(st.getBytes());
                    }
                } else {
                    outputStream.write("<p>No schemas found</p>".getBytes());
                }
                outputStream.write("</body></html>".getBytes());*/
            }
        }
    }

    private static void processSchema(XmlSchema schema, OutputStream outputStream,
                                      String contextRoot, CarbonHttpRequest request) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        schema.write(baos);
        RequestProcessorUtil.writeDocument(baos, outputStream, "annotated-xsd.xsl", contextRoot,
                isXSDAnnotated(request));
    }


    private static boolean isXSDAnnotated(CarbonHttpRequest request) {
        String param = request.getParameter(ServerConstants.HTTPConstants.ANNOTATION);
        if (param != null && param.length() != 0) {
            if (param.equals("true")) {
                return true;
            }
        }
        return false;
    }
}
