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
var jsonObj = [];

window.onload = function() {

    $('#collapse').collapse("hide");
    $('.selectpicker').selectpicker({
        style: 'btn',
        size: 4
    });

    var today = new Date();
    $("#to").val(today.getTime());
    var startDate = new Date(today);
    startDate.setDate(startDate.getDate() - 90);
    $("#from").val(startDate.getTime());

    $.getJSON("/portal/store/carbon.super/fs/gadget/total_involved_time_vs_user_id_for_process/js/meta-data-totalTimeVsUserIDFP.json.js", function(result){
        $.each(result, function(i, field){
            var pname = getUrlVars()["pname"];
            jsonObj.push(field);
            if(pname){
                var el = document.createElement("option");
                el.textContent = pname;
                el.value = pname;
                $('#totalTimeVsUserIdForProcessProcessList').append(el);
                $('#totalTimeVsUserIdForProcessProcessList').selectpicker("refresh");
                $('#totalTimeVsUserIdForProcessProcessList').prop( "disabled", true );
                drawInvolvedTimeVsUserIdForProcessGraph();
            } else {
                loadProcessList("totalTimeVsUserIdForProcessProcessList");
            }
        });
    });
};

function drawInvolvedTimeVsUserIdForProcessGraph() {

    var config = {
        type: "bar",
        x : "User",
        highlight : "multi",
        charts : [{type: "bar", y : "Total Time Worked"}],
        maxLength: 200,
        xAxisAngle:true,
        padding: {"top": 10, "left": 80, "bottom": 100, "right": 0},
        transform:[60,70]
    };

    var processId = $('#totalTimeVsUserIdForProcessProcessList').val();

    var startDate = document.getElementById("from");
    var startDateTemp = startDate.value;

    var endDate = document.getElementById("to");
    var endDateTemp = endDate.value;

    var body = {
        'startTime': startDateTemp,
        'endTime': endDateTemp,
        'processId': processId,
        'order': $('#userIdTotalTimeForProcessOrder').val(),
        'count': parseInt($('#userIdTimeForProcessCount').val())
    };

    $.ajax({
        type: 'POST',
        url: '/portal/controllers/apis/bpmn-api/total_involved_time_vs_user_id_for_process.jag',
        data: {'filters': JSON.stringify(body)},
        success: function (data) {

            var responseJsonArr = [];
            if (!$.isEmptyObject(data)) {
                responseJsonArr = JSON.parse(data);

                var responseStr = '';
                var scale = getTimeScale(responseJsonArr[0].totalInvolvedTime);
                for (var i = 0; i < responseJsonArr.length; i++) {
                    responseJsonArr[i].totalInvolvedTime = convertTime(scale, responseJsonArr[i].totalInvolvedTime);
                    var temp = '["' + responseJsonArr[i].assignUser + '",' + responseJsonArr[i].totalInvolvedTime + '],';
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
                barChart.draw("#chartA", [{type: "click"}]);

            } else {

                jsonObj[0].data = [];
                config.width = $('#chartA').width();
                config.height = $('#chartA').height();
                var barChart = new vizg(jsonObj, config);
                barChart.draw("#chartA", [{type: "click"}]);
            }
        },
        error: function (xhr, status, error) {
            alert(xhr.responseText);
            var errorJson = eval("(" + xhr.responseText + ")");
            alert(errorJson.message);
        }
    });
}

function selectPickerValChange(selectPickerElement) {
    var idx = selectPickerElement.options.selectedIndex;
    if (selectPickerElement.options[idx].value == 'other') {
        var other = prompt("Please indicate 'other' value:");
        if (other != '' && isInteger(other)) {
            var opt = document.createElement('option');
            opt.value = other;
            opt.innerHTML = other;
            selectPickerElement.appendChild(opt);
            $(selectPickerElement).selectpicker('val', other);
        } else {
            selectPickerElement.selectedIndex = 1;
            $(selectPickerElement).selectpicker("refresh");
        }
    }
}

function isInteger(param) {
    return (Math.floor(param) == param && $.isNumeric(param));
}

function loadProcessList(dropdownId) {

    var dropdownElementID = '#' + dropdownId;
    var url = "/portal/controllers/apis/bpmn-api/process_definition_key_list.jag";

    $.ajax({
        type: 'POST',
        url: url,
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
                drawInvolvedTimeVsUserIdForProcessGraph();
            }
        },
        error: function (xhr, status, error) {
            alert("Error loading processes: " + xhr.responseText);
            var errorJson = eval("(" + xhr.responseText + ")");
            alert(errorJson.message);
        }
    });
}

function getUrlVars() {
    var vars = [], hash;
    var hashes = top.location.href.slice(top.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}
