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
    x : "Task Instance Id",
    highlight : "multi",
    charts : [{type: "bar",  y : "Execution Time"}],
    maxLength: 200,
    xAxisAngle:true,
    padding: {"top": 10, "left": 80, "bottom": 100, "right": 0},
    transform:[60,70]
};

var jsonObj = [];

var callbackmethod = function(event, item) {
    
};

window.onload = function() {
    $.getJSON("/portal/store/carbon.super/fs/gadget/exec_time_vs_task_instance_id/js/meta-data.json.js", function(result){
        $.each(result, function(i, field){
            jsonObj.push(field);
            loadTaskList('taskInstanceIdExecTimeTaskList');
        });
    });
    $('.collapse').collapse("hide");
};

function drawExecutionTimeVsTaskInstanceIdResult() {

    var taskId = $('#taskInstanceIdExecTimeTaskList').val();
    var processId = $('#processIdList').val();
    
    if (taskId != '') {

        var body = {
            'startTime': $("#from").val()||0,
            'endTime': $("#to").val()||0,
            'taskId': taskId,
            'processId': processId,
            'order': $('#taskInstanceIdExecTimeOrder').val(),
            'limit': parseInt($('#taskInstanceIdExecTimeLimit').val())
        };
        
            $.ajax({
                url: '/portal/controllers/apis/bpmn-api/exec_time_vs_task_instance_id.jag',
                type: 'POST',
                data: {'filters': JSON.stringify(body)},
                success: function (data) {
                    // console.log(data);
                    var responseJsonArr = [];
                    if (!$.isEmptyObject(data))
                        responseJsonArr = JSON.parse(data);
    
                    var responseStr = '';
                    var scale = getTimeScale(responseJsonArr[0].duration);
                    for (var i = 0; i < responseJsonArr.length; i++) {
                        responseJsonArr[i].duration = convertTime(scale, responseJsonArr[i].duration);
                        var temp = '["' + responseJsonArr[i].taskInstanceId + ' ",' + responseJsonArr[i].duration + '],';
                        responseStr += temp;
                    }
                    jsonObj[0].metadata.names[1] = "Time(" + scale + ")";
                    config.charts[0].y = "Time(" + scale + ")";
                    responseStr = responseStr.slice(0, -1);
                    var jsonArrObj = JSON.parse('[' + responseStr + ']');
                    jsonObj[0].data = jsonArrObj;
                    // console.log(jsonObj);

                    config.width = $('#chartA').width();
                    config.height = $('#chartA').height();
                    var barChart = new vizg(jsonObj, config);
                    barChart.draw("#chartA", [{type: "click", callback: callbackmethod}]);
                },
                error: function (xhr, status, error) {
                    var errorJson = eval("(" + xhr.responseText + ")");
                    alert(errorJson.message);
                }
            });
        $('.collapse').collapse("hide");
    }
}


function loadTaskList(dropdownId) {
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
        drawExecutionTimeVsTaskInstanceIdResult();
    } else {
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
                    drawExecutionTimeVsTaskInstanceIdResult();
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