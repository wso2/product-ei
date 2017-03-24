/*
 ~ Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 */
var config = {
    type: "bar",
    x : "User",
    highlight : "multi",
    charts : [{type: "bar",  y : "Waiting Time"}],
    maxLength: 200,
    xAxisAngle:true,
    padding: {"top": 10, "left": 80, "bottom": 100, "right": 0},
    transform:[60,70]
};

var jsonObj = [];

gadgets.HubSettings.onConnect = function() {
    gadgets.Hub.subscribe('channel2', function(topic, data, subscriberData) {
        if(data.type === 'clear') {
            return;
        }
        wso2.gadgets.controls.showGadget();
        $('#userIdAvgExecTimeTaskList').val(data.task_id);
        $('#userIdAvgExecTimeTaskList').change();
        drawAvgExecuteTimeVsUserIdResult();
    });
};

var callbackmethod = function(event, item) {
};

window.onload = function() {
    $.getJSON("/portal/store/carbon.super/fs/gadget/avg_wait_time_vs_user_id/js/meta-data.json.js", function(result) {
        $.each(result, function(i, field) {
            jsonObj.push(field);
            loadTaskList('userIdAvgExecTimeTaskList');
        });
    });
    $('.collapse').collapse("hide");
};

function drawAvgExecuteTimeVsUserIdResult() {
    
    var taskId = $('#userIdAvgExecTimeTaskList').val();
    var processId = $('#processIdList').val();
    if (taskId != '') {
        var body = {
            'taskId': taskId,
            'processId': processId,
            'order': $('#userIdAvgExecTimeOrder').val(),
            'count': parseInt($('#userIdAvgExecTimeCount').val())
        };

        $.ajax({
            url: '/portal/controllers/apis/bpmn-api/avg_wait_time_vs_user_id.jag',
            type: 'POST',
            data: {'filters': JSON.stringify(body)},
            success: function (data) {
                var responseJsonArr = [];
                if (!$.isEmptyObject(data)) {
                    responseJsonArr = JSON.parse(data);

                    var responseStr = '';
                    var scale = getTimeScale(responseJsonArr[0].avgWaitingTime);
                    for (var i = 0; i < responseJsonArr.length; i++) {
                        responseJsonArr[i].avgWaitingTime = convertTime(scale, responseJsonArr[i].avgWaitingTime);
                        var temp = '["' + responseJsonArr[i].assignUser + '",' + responseJsonArr[i].avgWaitingTime + '],';
                        responseStr += temp;
                    }
                    jsonObj[0].metadata.names[1] = "Time(" + scale + ")";
                    config.charts[0].y = "Time(" + scale + ")";
                    responseStr = responseStr.slice(0, -1);
                    var jsonArrObj = JSON.parse('[' + responseStr + ']');
                    jsonObj[0].data = jsonArrObj;

                    config.width = $('#chartA').width();
                    config.height = $('#chartA').height();
                    var barChart = new vizg(jsonObj, config);
                    barChart.draw("#chartA", [{type: "click", callback: callbackmethod}]);
                }
            },
            error: function (xhr, status, error) {
                var errorJson = eval("(" + xhr.responseText + ")");
                alert(errorJson.message);
            }
        });
        $('.collapse').collapse("hide");
    }
}

function loadTaskList(dropdownId, barChartId, callback) {
    var dropdownElementID = '#' + dropdownId;
    var url = "/portal/controllers/apis/bpmn-api/task_definition_key_list.jag";
    $.ajax({
        type: 'POST',
        url: url,
        success: function (data) {
            if (!$.isEmptyObject(data)) {
                var dataStr = JSON.parse(data);
                for (var i = 0; i < dataStr.length; i++) {
                    var opt = dataStr[i].taskDefId;
                    var el = document.createElement("option");
                    el.textContent = opt;
                    el.value = opt;
                    $(dropdownElementID).append(el);
                }
                $(dropdownElementID).selectpicker("refresh");
                loadProcessList('processIdList');       
            }
        },
        error: function (xhr, status, error) {
            var errorJson = eval("(" + xhr.responseText + ")");
            alert(errorJson.message);
        }
    });
}

function loadProcessList(dropdownId) {
    var dropdownElementID = '#' + dropdownId;
    var pname = getUrlVars()["pname"];
    
    if(pname) {
        var el = document.createElement("option");
        el.textContent = pname;
        el.value = pname;
        $(dropdownElementID).append(el);
        $(dropdownElementID).prop( "disabled", true );
        drawAvgExecuteTimeVsUserIdResult();
    } else{
        $.ajax({
            type: 'POST',
            url: "/portal/controllers/apis/bpmn-api/process_definition_key_list.jag",
            success: function (data) {
                if (!$.isEmptyObject(data)) {
                    var dataStr = JSON.parse(data);
                    for (var i = 0; i < dataStr.length; i++) {
                        var opt = dataStr[i].processDefKey;
                        var el = document.createElement("option");
                        el.textContent = opt;
                        el.value = opt;
                        $(dropdownElementID).append(el);
                    }
                    $(dropdownElementID).selectpicker("refresh");
                    drawAvgExecuteTimeVsUserIdResult();
                }
            },
            error: function (xhr, status, error) {
                var errorJson = eval("(" + xhr.responseText + ")");
                alert(errorJson.message);
            }
        });
    }
}

function getUrlVars() {
    var vars = [], hash;
    var hashes = top.location.href.slice(top.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}