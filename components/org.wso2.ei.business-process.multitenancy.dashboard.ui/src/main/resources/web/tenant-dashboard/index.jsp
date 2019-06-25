<!--
 ~ Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<jsp:include page="../dialog/display_messages.jsp"/>


<link href="../tenant-dashboard/css/dashboard-common.css" rel="stylesheet" type="text/css" media="all"/>
<%
        Object param = session.getAttribute("authenticated");
        String passwordExpires = (String) session.getAttribute(ServerConstants.PASSWORD_EXPIRATION);
        boolean loggedIn = false;
        if (param != null) {
            loggedIn = (Boolean) param;             
        } 
%>
  
<div id="passwordExpire">
         <%
         if (loggedIn && passwordExpires != null) {
         %>
              <div class="info-box"><p>Your password expires at <%=passwordExpires%>. Please change by visiting <a href="../user/change-passwd.jsp?isUserChange=true&returnPath=../admin/index.jsp">here</a></p></div>
         <%
             }
         %>
</div>
<div id="middle">
<div id="workArea">


    <style type="text/css">
    .tip-table td.ws-bpel {
        background-image: url(../../carbon/tenant-dashboard/images/ws-bpel.png);
    }

    .tip-table td.secure-business {
        background-image: url(../../carbon/tenant-dashboard/images/secure-business.png);
    }
    .tip-table td.process-monitoring {
        background-image: url(../../carbon/tenant-dashboard/images/process-monitoring.png);
    }
    .tip-table td.cachingandthrottling {
        background-image: url(../../carbon/tenant-dashboard/images/cachingandthrottling.png);
    }


    .tip-table td.instance-data-cleanup {
        background-image: url(../../carbon/tenant-dashboard/images/instance-data-cleanup.png);
    }

    .tip-table td.bpel-extensions {
        background-image: url(../../carbon/tenant-dashboard/images/bpel-extensions.png);
    }
    .tip-table td.process-versioning {
        background-image: url(../../carbon/tenant-dashboard/images/process-versioning.png);
    }
    .tip-table td.atomic-scopes {
        background-image: url(../../carbon/tenant-dashboard/images/atomic-scopes.png);
    }
</style>
 <h2 class="dashboard-title">WSO2 BPS quick start dashboard</h2>
        <table class="tip-table">
            <tr>
                <td class="tip-top ws-bpel"></td>
                <td class="tip-empty"></td>
                <td class="tip-top secure-business"></td>
                <td class="tip-empty "></td>
                <td class="tip-top process-monitoring"></td>
            </tr>
            <tr>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                        <h3 class="tip-title">WS-BPEL 2.0 & BPELWS 1.1</h3> <br/>


                        <p>WSO2 BPS supports both WS-BPEL 2.0 and BPEL4WS 1.1 standards. It also includes WSO2 BPS specific extensions to enhance the features provide by WS-BPEL.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                        <h3 class="tip-title">Secure Business Processes</h3><br/>

                        <p>WSO2 BPS provides support for securing business processes.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                        <h3 class="tip-title">Process Monitoring</h3> <br/>

                        <p>WSO2 Business Process Server Management Console allows real time monitoring of business processes</p>

                    </div>
                </td>
            </tr>
            <tr>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
            </tr>
        </table>
        <div class="tip-table-div"></div>
        <table class="tip-table">
            <tr>
                <td class="tip-top instance-data-cleanup"></td>
                <td class="tip-empty"></td>
                <td class="tip-top bpel-extensions"></td>
                <td class="tip-empty "></td>
                <td class="tip-top process-versioning"></td>
            </tr>
            <tr>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                        <h3 class="tip-title">Instance Data Cleanup </h3> <br/>


                        <p>Allows multiple levels of instance data cleanup which are accumulated during process instance lifetime.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                        <h3 class="tip-title">BPEL Extensions</h3><br/>

                        <p>XPath extensions, Iteratable ForEach, Flexible Assigns, XQuery 1.0 support and various other extensions to BPEL language.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                        <h3 class="tip-title">Process Versioning</h3> <br/>

                        <p>Hot update BPEL processes and allows you to maintain multiple versions of processes</p>

                    </div>
                </td>
            </tr>
            <tr>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
            </tr>
        </table>


<p>
    <br/>
</p> </div>
</div>
