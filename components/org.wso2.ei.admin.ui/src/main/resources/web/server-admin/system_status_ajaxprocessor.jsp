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
<%@ page import="org.wso2.carbon.CarbonError" %>
<%@ page import="org.wso2.carbon.server.admin.common.IServerAdmin" %>
<%@ page import="org.wso2.carbon.server.admin.common.ServerData" %>
<%@ page import="org.wso2.ei.admin.ui.ServerAdminClient" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.wso2.carbon.utils.multitenancy.MultitenantConstants" %>

<fmt:bundle basename="org.wso2.ei.admin.ui.i18n.Resources">
<%
    //Server URL which is defined in the server.xml
    String serverURL = CarbonUIUtil.getServerURL(config.getServletContext(),
                                                     session);
    boolean isTenantRequest =
            !MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals((String)session.getAttribute(MultitenantConstants.TENANT_DOMAIN));
            //request.getAttribute(MultitenantConstants.TENANT_DOMAIN) != null ||
    if (isTenantRequest) {
        return;
    }
    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);

    //Obtaining the client-side ConfigurationContext instance.
    ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
            .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
    ServerData data;
    try {
        IServerAdmin proxy = new ServerAdminClient(configContext, serverURL, cookie, session);
        data = proxy.getServerData();
    } catch (Exception e) {
        CarbonError error = new CarbonError();
        error.addError(e.getMessage());
        request.setAttribute(CarbonError.ID, error);
%>
<jsp:forward page="../admin/error.jsp"/>
<%
        return;
    }
%>

<%--System Info--%>
<table id="systemInfoTable" width="100%" class="styledLeft">
    <thead>
    <tr>
        <th colspan="2"><fmt:message key="server"/></th>
    </tr>
    </thead>
    <% if (data.getServerIp() != null) { %>
    <tr>
        <td width="25%"><fmt:message key="host"/></td>
        <td><%= data.getServerIp()%>
        </td>
    </tr>
    <% } %>
    <tr>
        <td width="25%">
            <fmt:message key="server.url"/>
        </td>
        <td>
            <%= serverURL%>
        </td>
    </tr>
    <% if (data.getServerStartTime() != null) { %>
    <tr>
        <td width="25%"><fmt:message key="server.start.time"/></td>
        <td><%= data.getServerStartTime()%>
        </td>
    </tr>
    <% } %>
    <% if (data.getServerUpTime() != null) { %>
    <tr>
        <td width="25%"><fmt:message key="system.up.time"/></td>
        <td>
            <%= data.getServerUpTime().getDays()%>&nbsp;<fmt:message key="days"/>&nbsp;
            <%= data.getServerUpTime().getHours()%>&nbsp;<fmt:message key="hours"/>&nbsp;
            <%= data.getServerUpTime().getMinutes()%>&nbsp;<fmt:message key="minutes"/>&nbsp;
            <%= data.getServerUpTime().getSeconds()%>&nbsp;<fmt:message key="seconds"/>&nbsp;
        </td>
    </tr>
    <% } %>
    <% if (data.getCarbonVersion() != null) { %>
    <tr>
        <td width="25%"><fmt:message key="version"/></td>
        <td>
            <%= data.getCarbonVersion()%>
        </td>
    </tr>
    <% } %>
    <% if (data.getRepoLocation() != null) { %>
    <tr>
        <td width="25%">
            <fmt:message key="repository.location"/>
        </td>
        <td>
            <%= data.getRepoLocation()%>
        </td>
    </tr>
    <% } %>
</table>

<%--Server Info--%>
<% if ( data.getOsName() != null) {%>
<p>&nbsp;</p>
<table id="serverTable" width="100%" class="styledLeft">
    <thead>
    <tr>
        <th colspan="2"><fmt:message key="operating.system"/></th>
    </tr>
    </thead>
    <tr>
        <td  width="25%">
            <fmt:message key="os.name"/>
        </td>
        <td>
            <%= data.getOsName()%>
        </td>
    </tr>
    <tr>
        <td width="25%">
            <fmt:message key="os.version"/>
        </td>
        <td>
            <%= data.getOsVersion()%>
        </td>
    </tr>
</table>
<% } %>

<%--User Info--%>
<% if(data.getUserHome() != null) {%>
<p>&nbsp;</p>
<table id="userTable" width="100%" class="styledLeft">
    <thead>
    <tr>
        <th colspan="2"><fmt:message key="user"/></th>
    </tr>
    </thead>
    <tr>
        <td  width="25%">
            <fmt:message key="user.country"/>
        </td>
        <td>
            <%= data.getUserCountry()%>
        </td>
    </tr>
    <tr>
        <td width="25%">
            <fmt:message key="user.home"/>
        </td>
        <td>
            <%= data.getUserHome()%>
        </td>
    </tr>
    <tr>
        <td width="25%"><fmt:message key="user.name"/></td>
        <td>
            <%= data.getUserName()%>
        </td>
    </tr>
    <tr>
        <td width="25%"><fmt:message key="user.timezone"/></td>
        <td>
            <%= data.getUserTimezone()%>
        </td>
    </tr>
</table>
<% } %>

<%--JVM Info--%>
<% if ( data.getJavaHome()!= null || data.getJavaRuntimeName() != null || data.getJavaVersion() !=null || data.getJavaVMVendor() !=null || data.getJavaVMVendor() != null ) {%>
<p>&nbsp;</p>
<table id="vmTable" width="100%" class="styledLeft">
    <thead>
    <tr>
        <th colspan="2"><fmt:message key="java.vm"/></th>
    </tr>
    </thead>
    <% if (data.getJavaHome() != null) { %>
    <tr>
        <td width="25%">
            <fmt:message key="java.home"/>
        </td>
        <td>
            <%= data.getJavaHome()%>
        </td>
    </tr>
    <% } %>
    <% if (data.getJavaRuntimeName() != null) { %>
    <tr>
        <td width="25%">
            <fmt:message key="java.runtime.name"/>
        </td>
        <td>
            <%= data.getJavaRuntimeName()%>
        </td>
    </tr>
    <% } %>
    <% if (data.getJavaVersion() != null) { %>
    <tr>
        <td width="25%">
            <fmt:message key="java.version"/>
        </td>
        <td>
            <%= data.getJavaVersion()%>
        </td>
    </tr>
    <% } %>
    <% if (data.getJavaVMVendor() != null) { %>
    <tr>
        <td width="25%">
            <fmt:message key="java.vendor"/>
        </td>
        <td>
            <%= data.getJavaVMVendor()%>
        </td>
    </tr>
    <% } %>
    <% if (data.getJavaVMVersion() != null) { %>
    <tr>
        <td width="25%">
            <fmt:message key="java.vm.version"/>
        </td>
        <td>
            <%= data.getJavaVMVersion()%>
        </td>
    </tr>
    <% } %>
</table>
<% } %>
<%--Registry Info--%>
<p>&nbsp;</p>
<% if(data.getRegistryType() != null) { %>
<table id="registryTable" width="100%" class="styledLeft">
    <thead>
    <tr>
        <th colspan="2"><fmt:message key="registry"/></th>
    </tr>
    </thead>
<% if(data.getRegistryType().equals("embedded")) { %>
    <tr>
        <td width="25%">
            <fmt:message key="registry.db.name"/>
        </td>
        <td>
            <%= data.getDbName()%>
        </td>
    </tr>
    <tr>
        <td  width="25%">
            <fmt:message key="registry.db.version"/>
        </td>
        <td>
            <%= data.getDbVersion()%>
        </td>
    </tr>
    <tr>
        <td  width="25%">
            <fmt:message key="registry.db.driver.name"/>
        </td>
        <td>
            <%= data.getDbDriverName()%>
        </td>
    </tr>
    <tr>
        <td  width="25%">
            <fmt:message key="registry.db.driver.version"/>
        </td>
        <td>
            <%= data.getDbDriverVersion()%>
        </td>
    </tr>
    <tr>
        <td  width="25%">
            <fmt:message key="registry.db.url"/>
        </td>
        <td>
            <%= data.getDbURL()%>
        </td>
    </tr>
<% } else if (data.getRegistryType().equals("remote")) { %>
    <tr>
        <td  width="25%">
            <fmt:message key="registry.url"/>
        </td>
        <td>
            <%= data.getRemoteRegistryURL()%>
        </td>
    </tr>
    <tr>
        <td  width="25%">
            <fmt:message key="registry.chroot"/>
        </td>
        <td>
            <%= data.getRemoteRegistryChroot()%>
        </td>
    </tr>
<% }}%>
</table>

<script type="text/javascript">
    alternateTableRows('systemInfoTable', 'tableEvenRow', 'tableOddRow');
    alternateTableRows('serverTable', 'tableEvenRow', 'tableOddRow');
    alternateTableRows('userTable', 'tableEvenRow', 'tableOddRow');
    alternateTableRows('vmTable', 'tableEvenRow', 'tableOddRow');
    alternateTableRows('registryTable', 'tableEvenRow', 'tableOddRow');
</script>

</fmt:bundle>
