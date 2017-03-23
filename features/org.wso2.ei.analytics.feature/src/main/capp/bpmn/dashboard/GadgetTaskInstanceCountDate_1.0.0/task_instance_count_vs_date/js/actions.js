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

function drawGraph() {

    var startDate = document.getElementById("from");
    var startDateTemp = startDate.value;

    var endDate = document.getElementById("to");
    var endDateTemp = endDate.value;

    var taskIds = $('#taskInstanceCountDateTaskList').val();
    var taskIdArray = [];
    if (taskIds != null) {
        $('#taskInstanceCountDateTaskList :selected').each(function (i, selected) {
            taskIdArray[i] = $(selected).val();
        });
    }
    var processId = $('#processIdList').val();

    if(startDateTemp == 0|| endDateTemp == 0 ) {
        endDateTemp = new Date();
        startDateTemp = new Date(today.getFullYear(),(today.getMonth()+1-3),today.getDate());
    }

    var body = {
        'startTime': startDateTemp,
        'endTime': endDateTemp,
        'taskIdList': taskIdArray,
        'processId': processId
    };

    $.ajax({
        type: 'POST',
        url: '/portal/controllers/apis/bpmn-api/task_instance_count_vs_date.jag',
        data: {'filters': JSON.stringify(body)},
        success: function (data) {

            var responseJsonArr = [];
            if (!$.isEmptyObject(data)) {
                responseJsonArr = JSON.parse(data);

                if ($('#taskInstanceCountDateRangeCheckBox').is(":checked") && responseJsonArr.length > 1) {
                    responseJsonArr = fillEmptyDates(responseJsonArr);
                    config.charts[0].type = "line";
                    // responseJsonArr = "[{'finishTime':'2016-8-3','processInstanceCount':2}, {'finishTime':'2016-8-4','processInstanceCount':1}]"

                } else {
                    responseJsonArr = formatDates(responseJsonArr);
                    config.charts[0].type = "bar";
                }

                var responseStr = '';
                for (var i = 0; i < responseJsonArr.length; i++) {
                    var temp = '["' + responseJsonArr[i].finishTime + '",' + responseJsonArr[i].taskInstanceCount + '],';
                    responseStr += temp;
                }

                responseStr = responseStr.slice(0, -1);
                var jsonArrObj = JSON.parse('[' + responseStr + ']');
                jsonObj[0].data = jsonArrObj;

                config.width = $('#chartA').width();
                config.height = $('#chartA').height();
                var barChart = new vizg(jsonObj, config);
                barChart.draw("#chartA", [{type: "click"}]);
            }
            else {
                jsonObj[0].data = [];
                config.width = $('#chartA').width();
                config.height = $('#chartA').height();
                var barChart = new vizg(jsonObj, config);
                barChart.draw("#chartA", [{type: "click"}]);
            }
        },
        error: function (xhr, status, error) {
            var errorJson = eval("(" + xhr.responseText + ")");
            alert(errorJson.message);
        }
    });
}

function formatDates(valueDates) {
    var filledDates = [];

    for (var i = 0; i < valueDates.length; i++) {
        var fTime = valueDates[i].finishTime;
        var processInstanceCount = valueDates[i].taskInstanceCount;
        var fDate = new Date(fTime);
        var formatedDate = getFormatedDate(fDate);
        filledDates.push({finishTime:formatedDate, taskInstanceCount:processInstanceCount});
    }
    return filledDates;
}

/**
 Given a map M1 : (date -> value), creates a new map M2 : (date -> value), where each missing date in M1
 between its lowest and highest dates are filled with dates with 0 values.
 */
function fillEmptyDates(valueDates) {
    var valuesMap = {};
    var minDate = Number.MAX_VALUE;
    var maxDate = 0;

    for (var i = 0; i < valueDates.length; i++) {
        var fTime = valueDates[i].finishTime;
        if (fTime < minDate) {
            minDate = fTime;
        }
        if (fTime > maxDate) {
            maxDate = fTime;
        }

        var taskInstanceCount = valueDates[i].taskInstanceCount;
        var fDate = new Date(fTime);
        var formatedDate = getFormatedDate(fDate);
        valuesMap[formatedDate] = taskInstanceCount;
    }
    var minD = new Date(minDate);
    var maxD = new Date(maxDate);
    var dayInMillis = 1000 * 60 * 60 * 24;

    var numDays = Math.ceil((maxDate - minDate) / dayInMillis);

    var filledDates = [];
    for (var k = 0; k < numDays + 1; k++) {
        var fillDate = new Date(minDate);
        fillDate.setDate(fillDate.getDate() + k);
        var formatedDate = getFormatedDate(fillDate);
        var tiCount = valuesMap[formatedDate];
        if (tiCount == null) tiCount = 0;

        filledDates.push({finishTime:formatedDate, taskInstanceCount:tiCount});
    }
    return filledDates;
}

function getFormatedDate(fDate) {
    var formatedDate = fDate.getFullYear() + "-" + (fDate.getMonth() + 1) + "-" + fDate.getDate();
    return formatedDate;
}

function getDummyData() {
    var data1 = "[{\"taskInstanceCount\":1,\"finishTime\":1470249000000},{\"taskInstanceCount\":2,\"finishTime\":1470194400000}]";
    //var data1 = "[{\"processInstanceCount\":2,\"finishTime\":1470108000000}]";
    //var data1 = "test1";
    return data1;
}

function loadTaskList(dropdownId) {

    var dropdownElementID = document.getElementById(dropdownId);

    $.ajax({
        type: 'POST',
        url: '/portal/controllers/apis/bpmn-api/task_definition_key_list.jag',
        success: function (data) {
            if (!$.isEmptyObject(data)) {
                var dataStr = JSON.parse(data);
                for (var i = 0; i < dataStr.length; i++) {
                    var opt = dataStr[i].taskDefId;
                    var el = document.createElement("option");
                    el.textContent = opt;
                    el.value = opt;
                    dropdownElementID.appendChild(el);
                }
                $('.selectpicker').selectpicker('refresh');

                $.getJSON("/portal/store/carbon.super/fs/gadget/task_instance_count_vs_date/js/meta-data-taskInstanceCountVsDate.json.js", function (result) {
                    $.each(result, function (i, field) {
                        jsonObj.push(field);
                        loadProcessList('processIdList');
                        // drawGraph();
                    });

                });
            }
            else{
                console.log('Empty Task ID list.');
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
        $(dropdownElementID).selectpicker("refresh");
        drawGraph();
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
                    drawGraph();
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
