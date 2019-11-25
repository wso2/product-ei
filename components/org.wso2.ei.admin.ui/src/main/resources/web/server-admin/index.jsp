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
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<jsp:include page="../dialog/display_messages.jsp"/>

<script type="text/javascript" src="js/serveradmin.js"></script>

<carbon:breadcrumb
        label="shutdown.restart.server"
        resourceBundle="org.wso2.ei.admin.ui.i18n.Resources"
        topPage="true"
        request="<%=request%>" />
<carbon:jsi18n
        resourceBundle="org.wso2.ei.admin.ui.i18n.JSResources"
        request="<%=request%>" />

<fmt:bundle basename="org.wso2.ei.admin.ui.i18n.Resources">
    <div id="middle">
        <h2><fmt:message key="shutdown.restart.server"/></h2>
        <div id="workArea">
            <div id="output" style="display:none;"></div>

            <table class="styledLeft" id="shutDown" width="100%">
                <thead>
                <tr><th colspan="2"><fmt:message key="shutdown"/></th></tr>
                </thead>
                <tbody>
                <tr>
                    <td width="50%"><strong><fmt:message key="forced.shutdown"/></strong></td>
                </tr>
                <tr>
                    <td width="50%">
                        <fmt:message key="forced.shutdown.explanation"/>
                    </td>
                </tr>
                <tr>
                    <td width="50%" class="buttonRow">
                        <a href="#" onclick="shutdownServer();return false;" style="cursor:pointer;">
                            <img src="images/shutdown.gif" alt="<fmt:message key="forced.shutdown"/>"/>
                            <fmt:message key="forced.shutdown"/>
                        </a>
                    </td>
                </tr>
                </tbody>
            </table>

            <p>&nbsp;</p>

            <table class="styledLeft" id="restart" width="100%">
                <thead>
                <tr><th colspan="2"><fmt:message key="restart"/></th></tr>
                </thead>
                <tbody>
                <tr>
                    <td width="50%"><strong><fmt:message key="forced.restart"/></strong></td>
                </tr>
                <tr>
                    <td width="50%">
                        <fmt:message key="forced.restart.explanation"/>
                    </td>
                </tr>
                <tr>
                    <td width="50%" class="buttonRow">
                        <a href="#" onclick="restartServer();return false;" style="cursor:pointer;">
                            <img src="images/restart.gif" alt="<fmt:message key="forced.restart"/>"/>
                            <fmt:message key="forced.restart"/>
                        </a>
                    </td>
                </tr>
                </tbody>
            </table>

            <p>&nbsp;</p>
        </div>
    </div>
</fmt:bundle>
