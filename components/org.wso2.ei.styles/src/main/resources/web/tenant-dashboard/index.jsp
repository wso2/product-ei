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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil"%>
<link href="../tenant-dashboard/css/dashboard-common.css" rel="stylesheet" type="text/css" media="all"/>
<%
        Object param = session.getAttribute("authenticated");
        String passwordExpires = (String) session.getAttribute(ServerConstants.PASSWORD_EXPIRATION);
        boolean hasPermission = CarbonUIUtil.isUserAuthorized(request,
		"/permission/admin/manage/mediation");
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
    .tip-table td.proxy {
        background-image: url(../../carbon/tenant-dashboard/images/proxy.png);
    }

    .tip-table td.sequence {
        background-image: url(../../carbon/tenant-dashboard/images/sequence.png);
    }
    .tip-table td.endpoint {
        background-image: url(../../carbon/tenant-dashboard/images/endpoint.png);
    }
    .tip-table td.schedule-tasks {
        background-image: url(../../carbon/tenant-dashboard/images/schedule-tasks.png);
    }

    
    .tip-table td.message-processors {
        background-image: url(../../carbon/tenant-dashboard/images/message-processors.png);
    }
    .tip-table td.priority-executors {
        background-image: url(../../carbon/tenant-dashboard/images/priority-executors.png);
    }
    .tip-table td.service-testing {
        background-image: url(../../carbon/tenant-dashboard/images/service-testing.png);
    }
    .tip-table td.message-tracing {
        background-image: url(../../carbon/tenant-dashboard/images/message-tracing.png);
    }
</style>
 <h2 class="dashboard-title">WSO2 ESB Quick Start Dashboard</h2>
        <table class="tip-table">
            <tr>
                <td class="tip-top proxy"></td>
                <td class="tip-empty"></td>
                <td class="tip-top sequence"></td>
                <td class="tip-empty "></td>
                <td class="tip-top endpoint"></td>
                <td class="tip-empty "></td>
                <td class="tip-top schedule-tasks"></td>
            </tr>
            <tr>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                        <%
							if (hasPermission) {
						%>
                        <a class="tip-title" href="../proxyservices/templates.jsp?region=region1&item=proxy_services_menu">Proxy Service</a> <br/>
						<%
							} else {
						%>
						<h3>Proxy Service</h3> <br/>
						<%
							}
						%>
                        <p>Proxy services facilitate location transparency and provides the means for the integration between
                        various other services, on different formats</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                   	   <%
							if (hasPermission) {
						%>
                        <a class="tip-title" href="../sequences/list_sequences.jsp?region=region1&item=sequences_menu">Sequence</a><br/>
                        <%
							} else {
						%>
						<h3>Sequence</h3><br/>
						<%
							}
						%>
                        <p>Sequence is a collection of mediators attached to perform a given mediation flow in an
                        integration.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                         <%
							if (hasPermission) {
						%>
                        <a class="tip-title" href="../endpoints/index.jsp?region=region1&item=endpoints_menu">Endpoint</a> <br/>
                         <%
							} else {
						%>
						<h3>Endpoint </h3><br/>
						<%
							}
						%>
                        <p>Endpoints represent the third party services or other endpoints the ESB is talking to, this
                        configuration allows you to record a set of meta data about the external endpoint apart from it's
                        EPR</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                     <%
							if (hasPermission) {
						%>
                        <a class="tip-title" href="../task/index.jsp?region=region1&item=tasks_menu">Scheduled Tasks</a> <br/>
						  <%
							} else {
						%>
						<h3>Scheduled Tasks</h3> <br/>
							<%
							}
						%>
                        <p>Job scheduling facility in the ESB with quartz scheduler, helping several tasks to be initiated and scheduled by the ESB.</p>

                    </div>
                </td>
            </tr>
            <tr>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
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
                <td class="tip-top message-processors"></td>
                <td class="tip-empty"></td>
                <td class="tip-top priority-executors"></td>
                <td class="tip-empty "></td>
                <td class="tip-top service-testing"></td>
                <td class="tip-empty "></td>
                <td class="tip-top message-tracing"></td>
            </tr>
            <tr>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                    <%
							if (hasPermission) {
						%>
                        <a class="tip-title" href="../message_processor/index.jsp?region=region1&item=messageProcessor_menu">Store & Forward</a> <br/>
 						<%
							} else {
						%>
						<h3>Store & Forward</h3> <br/>
							<%
							}
						%>
                        <p>Message Stores and Processors can be used to persist messages to queues and then process preserving SLAs</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                    <%
							if (hasPermission) {
						%>
                        <a class="tip-title" href="../executors/list_executors.jsp?region=region1&item=priority_executor_menu">Priority Execution</a><br/>
						 <%
							} else {
						%>
						 <h3>Priority Execution</h3><br/>
							<%
							}
						%>
                        <p>Ability to categorize mediation flows with a priority allowing certain messages or mediation paths
                            to get a high priority with compared to other messages or paths.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                    <%
							if (hasPermission) {
						%>
                        <a class="tip-title" href="../tryit/index.jsp?region=region5&item=tryit">Service Testing</a> <br/>
						 <%
							} else {
						%>
						<h3>Service Testing</h3> <br/>
							<%
							}
						%>

                        <p>Tryit tool can be used as a simple Web Service client which can be used to try your services
                            within Enterprise Service Bus itself.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                    <%
							if (hasPermission) {
						%>
                        <a class="tip-title" href="../tracer/index.jsp?region=region4&item=tracer_menu">Message Tracing</a> <br/>
						 <%
							} else {
						%>
						<h3>Message Tracing</h3> <br/>
							<%
							}
						%>
                        <p>Trace the request and responses to your service. Message Tracing is a vital debugging tool when you have clients from heterogeneous platforms.</p>

                    </div>
                </td>
            </tr>
            <tr>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
            </tr>
        </table>
        
     </div>
</div>
