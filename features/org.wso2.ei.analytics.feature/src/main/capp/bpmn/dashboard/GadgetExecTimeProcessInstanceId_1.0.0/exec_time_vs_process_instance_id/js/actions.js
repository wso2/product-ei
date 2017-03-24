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
    x : "Process Instance Id",
    highlight : "multi",
    charts : [{type: "bar",  y : "Time"}],
    maxLength: 200,
    xAxisAngle:true,
    padding: {"top": 20, "left": 80, "bottom": 100, "right": 0},
    transform:[60,70]
};

var jsonObj = [];

var callbackmethod = function(event, item) {
};

window.onload = function() {

    $.getJSON("/portal/store/carbon.super/fs/gadget/exec_time_vs_process_instance_id/js/meta-data.json.js", function(result){
        $.each(result, function(i, field) {
            var pname = getUrlVars()["pname"];
            jsonObj.push(field);
            if(pname){
                var el = document.createElement("option");
                el.textContent = pname;
                el.value = pname;
                $(processInstanceIdExecTimeProcessList).append(el);
                $(processInstanceIdExecTimeProcessList).prop( "disabled", true );
                drawExecutionTimeVsProcessInstanceIdResult();
            } else {
                loadProcessList("processInstanceIdExecTimeProcessList");
            }
        });
    });
	$('#collapse').collapse("hide");
};

function drawExecutionTimeVsProcessInstanceIdResult() {
    var processId = $('#processInstanceIdExecTimeProcessList').val();

    var body = {
        'startTime': $("#from").val()||0,
        'endTime': $("#to").val()||0,
        'processId': processId,
        'order': $('#processInstanceIdExecTimeOrder').val(),
        'limit': parseInt($('#processInstanceIdExecTimeLimit').val())
    };
    
    if (processId != '') {
        $.ajax({
            url: '/portal/controllers/apis/bpmn-api/exec_time_vs_process_instance_id.jag',
            type: 'POST',
            data: {'filters': JSON.stringify(body)},
            success: function (data) {
                var responseJsonArr = [];
                if (!$.isEmptyObject(data)) {
                    responseJsonArr = JSON.parse(data);

                    var responseStr = '';
                    var scale = getTimeScale(responseJsonArr[0].duration);
                    for (var i = 0; i < responseJsonArr.length; i++) {
                        responseJsonArr[i].duration = convertTime(scale, responseJsonArr[i].duration);
                        var temp = '["' + responseJsonArr[i].processInstanceId + ' ",' + responseJsonArr[i].duration + '],';
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
                else {
                    /*jsonObj[0].data = [];
                    config.width = $('#chartA').width();
                    config.height = $('#chartA').height();
                    var barChart = new vizg(jsonObj, config);
                    barChart.draw("#chartA", [{type: "click"}]);*/
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


function loadProcessList(dropdownId) {
    var dropdownElementID = '#' + dropdownId;
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
                drawExecutionTimeVsProcessInstanceIdResult();
            }
        },
        error: function (xhr, status, error) {
            var errorJson = eval("(" + xhr.responseText + ")");
            alert(errorJson.message);
        }
    });
}

