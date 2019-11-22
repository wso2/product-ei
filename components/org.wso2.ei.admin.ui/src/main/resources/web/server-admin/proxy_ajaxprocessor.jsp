<!--
 ~ Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.ei.admin.ui.ServerAdminClient" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.ui.util.CharacterEncoder" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>

<%
    String httpMethod = request.getMethod().toLowerCase();

    if (!"post".equals(httpMethod)) {
        response.sendError(405);
        return;
    }

    ConfigurationContext ctx = (ConfigurationContext) config.getServletContext()
            .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
    String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);

    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
    String action = CharacterEncoder.getSafeText(request.getParameter("action"));

    ServerAdminClient client = new ServerAdminClient(ctx, backendServerURL, cookie, session);

    try {
        if ("restart".equals(action)) {
            client.restart();
        } else if ("restartGracefully".equals(action)) {
            client.restartGracefully();
        } else if ("shutdown".equals(action)) {
            client.shutdown();
        } else if ("shutdownGracefully".equals(action)) {
            client.shutdownGracefully();
        }

    } catch (Exception e) {
        response.sendError(500, e.getMessage());
        return;
    }
%>
