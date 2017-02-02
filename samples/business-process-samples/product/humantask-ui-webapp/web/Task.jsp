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
<%@page import="org.apache.axiom.om.OMElement" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.apache.axis2.databinding.types.URI" %>
<%@ page import="org.wso2.bps.humantask.sample.clients.HumanTaskClientAPIServiceClient" %>
<%@ page import="org.wso2.bps.humantask.sample.util.HumanTaskSampleConstants" %>
<%@ page import="org.wso2.carbon.humantask.stub.ui.task.client.api.types.TTaskAbstract" %>
<%@ page import="javax.xml.namespace.QName" %>
<%
    String backendServerURL = config.getServletContext().getInitParameter(HumanTaskSampleConstants.BACKEND_SERVER_URL);
    String cookie = (String) session.getAttribute(HumanTaskSampleConstants.SESSION_COOKIE);
    String userName = (String) session.getAttribute(HumanTaskSampleConstants.USERNAME);
    HumanTaskClientAPIServiceClient taskAPIClient =
            new HumanTaskClientAPIServiceClient(cookie, backendServerURL + HumanTaskSampleConstants.SERVICE_URL, null);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link href="css/global.css" rel="stylesheet" type="text/css" media="all">
    <link href="css/main.css" rel="stylesheet" type="text/css" media="all">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Task</title>
    <script type="text/javascript">
        function submitTaskForm(op) {
            document.getElementById('operation').value = op;
            if (op == 'complete') {
                document.getElementById('payload').value = createTaskOutput();
            }
            var taskForm = document.getElementById('TaskOperationForm');
            taskForm.submit();
        }
        createTaskOutput = function () {
            var outputVal = getCheckedRadio();
            if (outputVal == 'approve') {
                return '<sch:ClaimApprovalResponse xmlns:sch="http://www.example.com/claims/schema"><sch:approved>true</sch:approved></sch:ClaimApprovalResponse>';
            } else if (outputVal == 'disapprove') {
                return '<sch:ClaimApprovalResponse xmlns:sch="http://www.example.com/claims/schema"><sch:approved>false</sch:approved></sch:ClaimApprovalResponse>';
            }
        };
        getCheckedRadio = function () {
            var radioButtons = document.getElementsByName("responseRadio");
            for (var x = 0; x < radioButtons.length; x++) {
                if (radioButtons[x].checked) {
                    return radioButtons[x].value;
                }
            }
        };
        function submitSignOutForm() {
            var taskForm = document.getElementById('signOutForm');
            taskForm.submit();
        }
    </script>
</head>
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
                                            <% String taskId = request.getParameter("taskId");
                                                String status = "";
                                                String queryType = request.getParameter("queryType");
                                                if (queryType == null) {
                                                    queryType = "assignedToMe";
                                                }
                                                TTaskAbstract task;
                                                OMElement requestElement = null;
                                                try {
                                                    task = taskAPIClient.loadTask(new URI(taskId));
                                                    requestElement = taskAPIClient
                                                            .loadTaskInput(new URI(taskId));
                                                    status = task.getStatus().toString();
                                                } catch (Exception e) {
                                                    request.getRequestDispatcher("./Home.jsp?logout=true")
                                                            .forward(request, response);
                                                }
                                                String customerId = "";
                                                String customerFirstName = "";
                                                String customerLastName = "";
                                                String amount = "";
                                                String region = "";
                                                String ns = "http://www.example.com/claims/schema";

                                                if (requestElement != null) {
                                                    OMElement customerElement = requestElement
                                                            .getFirstChildWithName(new QName(ns, "cust"));
                                                    if (customerElement != null) {
                                                        OMElement id = customerElement
                                                                .getFirstChildWithName(new QName(ns, "id"));
                                                        if (id != null) {
                                                            customerId = id.getText();
                                                        }
                                                        OMElement fName = customerElement
                                                                .getFirstChildWithName(new QName(ns, "firstname"));
                                                        if (fName != null) {
                                                            customerFirstName = fName.getText();
                                                        }
                                                        OMElement lName = customerElement
                                                                .getFirstChildWithName(new QName(ns, "lastname"));
                                                        if (lName != null) {
                                                            customerLastName = lName.getText();
                                                        }
                                                    }
                                                    OMElement regionElement = requestElement
                                                            .getFirstChildWithName(new QName(ns, "region"));

                                                    if (regionElement != null) {
                                                        region = regionElement.getText();
                                                    }
                                                    OMElement amountElement = requestElement
                                                            .getFirstChildWithName(new QName(ns, "amount"));
                                                    if (amountElement != null) {
                                                        amount = amountElement.getText();
                                                    }
                                                }
                                            %>
                                            <form id="TaskOperationForm" action="task" method="POST">
                                                <input type="hidden" id="operation" name="operation"/>
                                                <input type="hidden" name="taskID" value="<%=taskId%>"/>
                                                <input type="hidden" id="payload" name="payload"/>
                                                <table align="center" id="task-table">
                                                    <tr>
                                                        <%
                                                            if (status.toUpperCase().equals("IN_PROGRESS")) {
                                                        %>
                                                        <td><a href="#" class="opbutton"
                                                               onclick="submitTaskForm('stop')">Stop</a>
                                                                <%} else if (status.toUpperCase().equals("RESERVED")) {%>
                                                        <td><a href="#" class="opbutton"
                                                               onclick="submitTaskForm('start')">Start</a></td>
                                                        <%
                                                            }
                                                        %>
                                                    </tr>
                                                </table>
                                            </form>
                                            <table class="tableEvenRow" width="100%" align="center"
                                                    >
                                                <tr>
                                                    <td><h3>Status</h3></td>
                                                    <td><%=status%>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>Customer Id</td>
                                                    <td><%=customerId%>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>First Name</td>
                                                    <td><%=customerFirstName%>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>Last Name</td>
                                                    <td><%=customerLastName%>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>Amount</td>
                                                    <td><%=amount%>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>Region</td>
                                                    <td><%=region%>
                                                    </td>
                                                </tr>
                                            </table>
                                            <%
                                                if (status.toUpperCase().equals("IN_PROGRESS")) {
                                            %>
                                            <h3>Approve</h3>
                                            <table align="center" class="main-table">
                                                <tr>
                                                    <td><label for="responseRadio1">Approve </label><input type="radio"
                                                                                                           name="responseRadio"
                                                                                                           id="responseRadio1"
                                                                                                           value="approve"/>
                                                        <label
                                                                for="responseRadio2">Disapprove</label><input
                                                                type="radio" name="responseRadio" id="responseRadio2"
                                                                value="disapprove"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td></td>
                                                    <td><a href="#" class="opbutton"
                                                           onclick="submitTaskForm('complete')">Complete</a>
                                                    </td>
                                                </tr>
                                            </table>
                                            <%
                                            } else if (status.toUpperCase().equals("COMPLETED")) {
                                                String approved = "No Value Assigned";

                                                OMElement responseElement = null;
                                                try {
                                                    responseElement = taskAPIClient
                                                            .loadTaskOutput(new URI(taskId));
                                                } catch (Exception ex) {
                                                    request.getRequestDispatcher("./Home.jsp?logout=true")
                                                            .forward(request, response);
                                                }

                                                if (responseElement != null) {
                                                    approved = responseElement.getFirstElement().getText();
                                                }
                                            %>
                                            <table border="0" class="main-table">
                                                <tr>
                                                    <td><h3>Approved :</h3></td>
                                                    <td><h3><%=approved%>
                                                    </h3></td>
                                                </tr>
                                            </table>
                                            <%
                                                }
                                            %>
                                            <a href="./Home.jsp?queryType=<%=queryType%>">Back To Home</a> <br>
                                        </div>
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
                    <div class="copyright">
                        © 2005 -
                        <p>2015 WSO2 Inc. All Rights Reserved.</p>
                    </div>
                </div>
            </div>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>