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
var user;

function drawGraph() {

    var userId = $('#UserList').val();
    var startDate = document.getElementById("from");
    var startDateTemp = startDate.value;

    var endDate = document.getElementById("to");
    var endDateTemp = endDate.value;

    if (userId != '') {
        var body = {
            'startTime': startDateTemp,
            'endTime': endDateTemp,
            'userId': userId,
            'order': $('#TasksOrder').val(),
            'count': parseInt($('#TasksCount').val())
        };

        $.ajax({
            type: 'POST',
            url: '/portal/controllers/apis/bpmn-api/total_involved_time_vs_process_id.jag',
            data: {'filters': JSON.stringify(body)},
            success: function (data) {

                var responseJsonArr = [];
                if (!$.isEmptyObject(data)) {
                    responseJsonArr = JSON.parse(data);

                    var responseStr = '';
                    var scale = getTimeScale(responseJsonArr[0].totalInvolvedTime);
                    for (var i = 0; i < responseJsonArr.length; i++) {
                        responseJsonArr[i].totalInvolvedTime = convertTime(scale, responseJsonArr[i].totalInvolvedTime);
                        var temp = '["' + responseJsonArr[i].processDefKey + '",' + responseJsonArr[i].totalInvolvedTime + '],';
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
    } else {
        console.log('Empty user id list.');
        alert("User id list is empty.");
    }
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

function loadUserList(dropdownId) {
    var dropdownElementID = '#' + dropdownId;
    user = getUrlVars()["user"];

    $.getJSON("/portal/store/carbon.super/fs/gadget/total_involved_time_vs_process_id/js/meta-data-totalTimeVsProcessID.json.js", function (result) {
        $.each(result, function (i, field) {
            jsonObj.push(field);
            if (user) {
                var el = document.createElement("option");
                el.textContent = user;
                el.value = user;
                $(dropdownElementID).append(el);
                $(dropdownElementID).attr("disabled", true);
                $(dropdownElementID).selectpicker("refresh");
                drawGraph();
            }
            else {
                loadList(dropdownElementID);
            }
        });
    });
}

function loadList(dropdownElementID) {
    $.ajax({
        type: 'POST',
        url: '/portal/controllers/apis/bpmn-api/user_id_list.jag',
        success: function (data) {
            if (!$.isEmptyObject(data)) {
                var dataStr = JSON.parse(data);
                for (var i = 0; i < dataStr.length; i++) {
                    var opt = dataStr[i].assignUser;
                    var el = document.createElement("option");
                    el.textContent = opt;
                    el.value = opt;
                    $(dropdownElementID).append(el);
                }
                $(dropdownElementID).selectpicker("refresh");
                drawGraph();
            }
            else {
                console.log('Empty User ID list.');
            }
        },
        error: function (xhr, status, error) {
            var errorJson = eval("(" + xhr.responseText + ")");
            alert(errorJson.message);
        }
    });
}

function getUrlVars() {
    var vars = [], hash;
    var hashes = top.location.href.slice(top.location.href.indexOf('?') + 1).split('&');

    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }

    return vars;
}