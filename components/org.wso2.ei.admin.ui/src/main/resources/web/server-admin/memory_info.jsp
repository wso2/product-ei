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
<%@ page import="java.lang.management.*" %>
<%@ page import="java.util.*" %>

<table border="0" width="100%" width="100%" class="styledLeft">
    <thead>
    <tr>
        <td colspan="2" align="center">Memory Statistics</td>
    </tr>
    </thead>
    <tr>
        <td width="25%">Heap Memory Usage</td>
        <td><%=
        ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()
        %>
        </td>
    </tr>
    <tr>
        <td>Non-Heap Memory Usage</td>
        <td><%=
        ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()
        %>
        </td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
        <td colspan="2" align="center"><h3>Memory Pool MXBeans</h3></td>
    </tr>
    <%
        for (Iterator iter = ManagementFactory.getMemoryPoolMXBeans().iterator(); iter.hasNext();) {
            MemoryPoolMXBean item = (MemoryPoolMXBean) iter.next();
    %>
    <tr>
        <td colspan="2">
            <table border="0" width="100%" class="styledLeft">
                <tr>
                    <td colspan="2" align="center"><b><%= item.getName() %>
                    </b></td>
                </tr>
                <tr>
                    <td width="25%">Type</td>
                    <td><%= item.getType() %>
                    </td>
                </tr>
                <tr>
                    <td>Usage</td>
                    <td><%= item.getUsage() %>
                    </td>
                </tr>
                <tr>
                    <td>Peak Usage</td>
                    <td><%= item.getPeakUsage() %>
                    </td>
                </tr>
                <tr>
                    <td>Collection Usage</td>
                    <td><%= item.getCollectionUsage() %>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;</td>
    </tr>
    <%
        }
    %>

</table>
