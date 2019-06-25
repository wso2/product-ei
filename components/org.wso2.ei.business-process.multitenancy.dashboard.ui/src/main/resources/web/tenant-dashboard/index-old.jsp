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
<jsp:include page="../dialog/display_messages.jsp"/>

<script type="text/javascript" src="../statistics/js/statistics.js"></script>
<script type="text/javascript" src="../statistics/js/graphs.js"></script>

<script type="text/javascript" src="../admin/js/jquery.flot.js"></script>
<script type="text/javascript" src="../admin/js/excanvas.js"></script>

<%@include file="../admin/index.jsp" %>

<script id="source" type="text/javascript">
        jQuery.noConflict();
        var responseTimeGraphWidth = 500;
        var responseTimeXScale = 25;

        initStats(responseTimeXScale, 25);

        function drawResponseTimeGraph() {
            jQuery.plot(jQuery("#responseTimeGraph"), [
                {
                    data: graphAvgResponse.get(),
                    lines: { show: true, fill: true }
                }
            ],  {
                    xaxis: {
                        ticks: graphAvgResponse.tick(),
                        min: 0
                    },
                    yaxis: {
                        ticks: 10,
                        min: 0
                    }
            });
        }
</script>

<fmt:bundle basename="org.wso2.carbon.statistics.ui.i18n.Resources">
    <div id="middle">
        <div id="workArea">
            <div id="systemStats"></div>
            <script type="text/javascript">
                jQuery.noConflict();
                var varRefreshSystemStats;
                function refreshSystemStats() {
                    var url = "../tenant-dashboard/system_stats_ajaxprocessor.jsp";
                    jQuery("#systemStats").load(url, null, function (responseText, status, XMLHttpRequest) {
                                if ( status != "success"){
                                    stopRefreshSystemStats();
                                    document.getElementById('systemStats').innerHTML = responseText;
                                }
                    });
                }
                function stopRefreshSystemStats() {
                    if (varRefreshSystemStats) {
                        clearInterval(varRefreshSystemStats);
                    }
                }
                jQuery(document).ready(function() {
                    refreshSystemStats();
                    varRefreshSystemStats = setInterval("refreshSystemStats()", 10000);
                });
            </script>
        </div>
    </div>
</fmt:bundle>

