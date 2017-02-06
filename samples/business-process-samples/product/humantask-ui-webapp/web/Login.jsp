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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link href="css/global.css" rel="stylesheet" type="text/css" media="all">
    <link href="css/main.css" rel="stylesheet" type="text/css" media="all">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>HumanTask Web App</title>
    <script type="text/javascript">
        function submitLoginForm() {
            var loginForm = document.getElementById('LoginForm');
            if (validateLoginForm()) {
                loginForm.submit();
            }
        }
        function validateLoginForm() {
            var userName = document.getElementById('userName');
            var errorStrip = document.getElementById('errorStrip');
            if (userName.value == "") {
                errorStrip.style.display = "";
                errorStrip.innerHTML = "Please enter a user name to login";
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
<table class="main-table" border="0" cellspacing="0">
    <tbody>
    <tr>
        <td id="header" colspan="3">
            <div id="header-div">
                <div class="right-logo">Human Task App</div>
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
                                    <td width="40%">
                                        <div id="features">
                                            <h3>Welcome to Human Task Web App sample!</h3>
                                        </div>
                                    </td>
                                    <td width="20%">
                                        <div id="loginbox">
                                            <h2>Sign-in</h2>

                                            <form id="LoginForm" action="login" method="POST">
                                                <table>
                                                    <tbody>
                                                    <tr>
                                                        <td><label for="txtUserName">Username</label></td>
                                                        <td><input type="text" id="txtUserName"
                                                                   name="userName" class="user" tabindex="1"></td>
                                                    </tr>
                                                    <tr>
                                                        <td><label for="txtPassword">Password</label></td>
                                                        <td><input type="password" id="txtPassword"
                                                                   name="userPassword" class="password" tabindex="2">
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td></td>
                                                        <td><%--@declare id="txtrememberme"--%><label>
                                                            <input type="checkbox" name="rememberMe"
                                                                   value="rememberMe" tabindex="3">
                                                        </label> <label
                                                                for="txtRememberMe">Remember Me</label></td>
                                                    </tr>
                                                    <tr>
                                                        <td>&nbsp;</td>
                                                        <td><input type="submit" value="Sign-in"
                                                                   class="button" tabindex="3"
                                                                   onclick="submitLoginForm()"></td>
                                                    </tr>
                                                    <tr>
                                                        <td></td>
                                                        <td>
                                                            <%
                                                                if (request.getAttribute("message") != null) {
                                                            %>
                                                            <label id="errorStrip"><%=request.getAttribute("message").toString()%>
                                                            </label>
                                                            <%
                                                                }
                                                            %>
                                                        </td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </form>
                                            <br>
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
        <td width="40%"></td>
    </tr>
    <tr>
        <td id="footer" colspan="3">
            <div id="footer-div">
                <div class="footer-content">
                    <div class="copyright" style="text-align:center">© 2005 - 2015 WSO2 Inc. All Rights
                                                                     Reserved.
                    </div>
                </div>
            </div>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>