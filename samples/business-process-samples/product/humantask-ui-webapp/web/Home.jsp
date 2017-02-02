<!--
~ Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~      http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
-->
<%@page import="org.wso2.bps.humantask.sample.manager.LoginManager" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.wso2.bps.humantask.sample.util.HumanTaskSampleUtil" %>
<%@ page import="org.wso2.carbon.humantask.stub.ui.task.client.api.types.TSimpleQueryCategory" %>
<%@ page import="org.wso2.carbon.humantask.stub.ui.task.client.api.types.TSimpleQueryInput" %>
<%@ page import="org.wso2.carbon.humantask.stub.ui.task.client.api.types.TTaskSimpleQueryResultRow" %>
<%@ page import="org.wso2.carbon.humantask.stub.ui.task.client.api.types.TTaskSimpleQueryResultSet" %>
<%@ page import="org.wso2.bps.humantask.sample.clients.HumanTaskClientAPIServiceClient" %>
<%@ page import="org.wso2.bps.humantask.sample.util.HumanTaskSampleConstants" %>
<%
    String backendServerURL = config.getServletContext().getInitParameter(HumanTaskSampleConstants.BACKEND_SERVER_URL);
    String cookie = (String) session.getAttribute(HumanTaskSampleConstants.SESSION_COOKIE);
    String userName = (String) session.getAttribute(HumanTaskSampleConstants.USERNAME);
    HumanTaskClientAPIServiceClient taskAPIClient =
            new HumanTaskClientAPIServiceClient(cookie, backendServerURL + HumanTaskSampleConstants.SERVICE_URL, null);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN""http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link href="css/global.css" rel="stylesheet" type="text/css" media="all">
    <link href="css/main.css" rel="stylesheet" type="text/css" media="all">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Home</title>
</head>
<script type="text/javascript">
    function submitSignOutForm() {
        var taskForm = document.getElementById('signOutForm');
        taskForm.submit();
    }
</script>
<body>
<table class="main-table" border="0" cellspacing="0">
    <tbody>
    <tr>
        <td id="header" colspan="3">
            <div id="header-div">
                <div class="right-logo">
                    Signed-in as:
                    <%=userName%>
                    <form id="signOutForm" action="login" method="POST">
                        <input type="hidden" id="logout" name="logout" value=""/>
                        <h6>
                            <a href="#" onclick="submitSignOutForm()">Sign out</a>
                        </h6>
                    </form>
                </div>
                <div class="left-logo"></div>
                <div class="middle-ad"></div>
            </div>
        </td>
    </tr>
    <tr>
        <td id="middle-content">
            <table id="content-table" border="0" cellspacing="0">
                <tbody>
                <tr>
                    <td id="body">
                        <div id="middle">
                            <table cellspacing="0" width="100%">
                                <tbody>
                                <tr>
                                    <td width="20%">
                                        <div id="features">
                                            <h3>Welcome to Human Task Web App sample!</h3>
                                        </div>
                                    </td>
                                    <td width="60%">
                                        <div id="workArea">
                                            <br/>
                                            <table align="center" class="main-table">
                                                <tr>
                                                    <td><a href="Home.jsp?queryType=assignedToMe"
                                                           class="opbutton">My Tasks</a>
                                                    </td>
                                                    <td><a href="Home.jsp?queryType=claimableTasks"
                                                           class="opbutton">Claimable</a></td>
                                                    <td><a href="Home.jsp?queryType=adminTasks"
                                                           class="opbutton">Admin Tasks</a>
                                                    </td>
                                                    <td><a href="Home.jsp?queryType=notifications"
                                                           class="opbutton">Notifications</a></td>
                                                    <td><a href="Home.jsp?queryType=allTasks"
                                                           class="opbutton">All Tasks</a>
                                                    </td>
                                                </tr>
                                            </table>
                                            <br>
                                                <%
																String pageNumber = request.getParameter("pageNumber");
																int pageNumberInt = 0;
																if (pageNumber == null) {
																	pageNumber = "0";
																}
																try {
																	pageNumberInt = Integer.parseInt(pageNumber);
																} catch (NumberFormatException ignored) {

																}
																String queryType = request.getParameter("queryType");
																TTaskSimpleQueryResultSet taskResults = null;
																try {
																	TSimpleQueryInput queryInput = new TSimpleQueryInput();
																	queryInput.setPageNumber(pageNumberInt);
																	queryInput
																			.setSimpleQueryCategory(TSimpleQueryCategory.ASSIGNED_TO_ME);
																	if (queryType != null && !"".equals(queryType)) {
																		if ("allTasks".equals(queryType)) {
																			queryInput
																					.setSimpleQueryCategory(TSimpleQueryCategory.ALL_TASKS);
																		} else if ("assignedToMe".equals(queryType)) {
																			queryInput
																					.setSimpleQueryCategory(TSimpleQueryCategory.ASSIGNED_TO_ME);
																		} else if ("adminTasks".equals(queryType)) {
																			queryInput
																					.setSimpleQueryCategory(TSimpleQueryCategory.ASSIGNABLE);
																		} else if ("claimableTasks".equals(queryType)) {
																			queryInput
																					.setSimpleQueryCategory(TSimpleQueryCategory.CLAIMABLE);
																		} else if ("notifications".equals(queryType)) {
																			queryInput
																					.setSimpleQueryCategory(TSimpleQueryCategory.NOTIFICATIONS);
																		}
																	}
																	taskResults = taskAPIClient
																			.taskListQuery(queryInput);
																} catch (Exception e) {

																	request.getRequestDispatcher("./Login.jsp?logout=true")
																			.forward(request, response);
																}
															%>
                                            <table class="tableEvenRow" width="100%" align="center"
                                                   class="main-table">
                                                <tr>
                                                    <th>Task ID</th>
                                                    <th>Subject</th>
                                                    <th>Status</th>
                                                    <th>Priority</th>
                                                    <th>Created On</th>
                                                </tr>
                                                <%
                                                    if (taskResults != null && taskResults.getRow() != null
                                                        && taskResults.getRow().length > 0) {
                                                        TTaskSimpleQueryResultRow[] rows = taskResults.getRow();
                                                        for (TTaskSimpleQueryResultRow row : rows) {
                                                            String qname = row.getName().getLocalPart();
                                                %>
                                                <tr>
                                                    <td><a
                                                            href="Task.jsp?queryType=<%=queryType%>&taskId=<%=row.getId().toString()%>"><%=row.getId().toString()%>
                                                        -<%=qname%>
                                                    </a>
                                                    </td>
                                                    <td>
                                                    <td>
                                                        <%
                                                            String presentationName = HumanTaskSampleUtil
                                                                    .getTaskPresentationHeader(
                                                                            row.getPresentationSubject(),
                                                                            row.getPresentationName());
                                                        %> <%=presentationName%>
                                                    </td>
                                                    <td><%=row.getStatus().toString()%>
                                                    </td>
                                                    <td><%=row.getPriority()%>
                                                    </td>
                                                    <td><%=row.getCreatedTime().getTime().toString()%>
                                                    </td>
                                                </tr>
                                                <%
                                                        }
                                                    }
                                                %>
                                            </table>
                                            <br/>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </td>
        <td width="20%"></td>
    </tr>
    <tr>
        <td id="footer" colspan="3">
            <div id="footer-div">
                <div class="footer-content">
                    <div class="copyright" style="text-align: center">© 2005 -
                                                                      2015 WSO2 Inc. All Rights Reserved.
                    </div>
                </div>
            </div>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>