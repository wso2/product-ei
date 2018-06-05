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

import org.apache.axis2.context.ConfigurationContext;
import org.apache.http.protocol.HTTP;
import org.wso2.carbon.core.transports.CarbonHttpRequest;
import org.wso2.carbon.core.transports.CarbonHttpResponse;
import org.wso2.carbon.core.transports.HttpGetRequestProcessor;

import java.io.PrintWriter;

/**
 * The purpose of this mock class is to provide information to user of WSAS, when ?tryit has
 * used. WSAS does not support ?tryit ?stub.
 */
public class MockTryItProcessor implements HttpGetRequestProcessor {

    public void process(CarbonHttpRequest request,
                        CarbonHttpResponse response,
                        ConfigurationContext configurationContext) throws Exception {
        response.addHeader(HTTP.CONTENT_TYPE, "text/html");
        PrintWriter out = response.getWriter();

        out.write("<div id=\"alertMessageBox\" style=\"display:none;position:absolute;z-index: 600;\">\n" +
                  "    <!--the message area-->\n" +
                  "    <p id=\"alertMessageBoxMessageArea\"></p>\n" +
                  "    <!-- the button area-->\n" +
                  "    <p id=\"alertButton\" align=\"right\">\n" +
                  "        <input id=\"alertBoxButton\" type=\"button\"\n" +
                  "                value=\"  OK  \"\n" +
                  "                onclick=\"javascript:document.getElementById('alertMessageBox').style.display='none';return false;\"/></p>\n" +
                  "</div>" +
                  "<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/main.css\" media=\"screen, projection\" />" +
                  "<script type=\"text/javascript\" src=\"../js/main.js\"></script>" +
                  "<script type=\"text/javascript\">" +
                  "wso2.wsf.Util.alertMessage('This function is not currently supported by WSO2 WSAS');" +
                  "setTimeout(\"history.back()\",3000)" +
                  "</script>");
        out.flush();
        out.close();
    }
}
