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
        .tip-table td.mb1 {
            background-image: url(../../carbon/tenant-dashboard/images/mb1.png);
        }
        .tip-table td.mb2 {
            background-image: url(../../carbon/tenant-dashboard/images/mb2.png);
        }
        .tip-table td.mb3 {
            background-image: url(../../carbon/tenant-dashboard/images/mb3.png);
        }
        .tip-table td.mb4 {
            background-image: url(../../carbon/tenant-dashboard/images/mb4.png);
        }



        .tip-table td.mb5 {
            background-image: url(../../carbon/tenant-dashboard/images/mb5.png);
        }
        .tip-table td.mb6 {
            background-image: url(../../carbon/tenant-dashboard/images/mb6.png);
        }
        .tip-table td.mb7 {
            background-image: url(../../carbon/tenant-dashboard/images/mb7.png);
        }
        .tip-table td.mb8 {
            background-image: url(../../carbon/tenant-dashboard/images/mb8.png);
        }
    </style>
    <h2 class="dashboard-title">WSO2 MB quick start dashboard</h2>
    <table class="tip-table">
        <tr>
            <td class="tip-top mb1"></td>
            <td class="tip-empty"></td>
            <td class="tip-top mb2"></td>
            <td class="tip-empty "></td>
            <td class="tip-top mb3"></td>
            <td class="tip-empty "></td>
            <td class="tip-top mb4"></td>
        </tr>
        <tr>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    
                    <h3 class="tip-title">Publish/Subscribe to Topics</h3> <br/>


                                      <p>WS-Eventing to publish/subscribe to topics using web service standards.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <h3 class="tip-title">AMQP</h3> <br/>

                                       <p>JMS support thorough AMQP to publish/subscribe to topics and Queues.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                   <h3 class="tip-title">Topic Authorization</h3> <br/>

                    <p>Role based authorization to topics.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <h3 class="tip-title">Manage topics and queues permissions</h3> <br/>

                                      <p>Administrative console support to manage topics and queues permissions.</p>

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
            <td class="tip-top mb5"></td>
            <td class="tip-empty"></td>
            <td class="tip-top mb6"></td>
            <td class="tip-empty"></td>
            <td class="tip-top mb7"></td>
            <td class="tip-empty "></td>
            <td class="tip-top mb8"></td>
        </tr>
        <tr>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <h3 class="tip-title">Clustering support</h3> <br/>

                                   <p>Clustering support to facilitate high-availability and fail-over support.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <h3 class="tip-title">User based authorization for queues</h3> <br/>


                                       <p>User based authorization for queues.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <h3 class="tip-title">Message browsing</h3> <br/>


                                        <p>Message browsing support to view message content in the admin console.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <h3 class="tip-title">WS-eventing support</h3> <br/>


                                      <p>WS-eventing support to expose and consume events using two different standard APIs.</p>
               
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
<p>
    <br/>
</p> </div>
</div>
