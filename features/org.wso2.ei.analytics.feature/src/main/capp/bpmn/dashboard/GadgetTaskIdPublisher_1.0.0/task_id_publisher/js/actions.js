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
var from,to;

window.onload = function() {
    var pname = getUrlVars()["pname"];

    if(pname) {pname;
        $('#process_name').val(pname);
    }
    var today = new Date();
    var todayString = (today.getMonth()+1)+"/"+today.getDate()+"/"+today.getFullYear();
    $('#taskEndDate').attr('placeholder',todayString);
    var startDate = new Date(today);
    startDate.setDate(startDate.getDate() - 90);
    var startDateString = (startDate.getMonth()+1)+"/"+startDate.getDate()+"/"+startDate.getFullYear();
    $('#taskStartDate').attr('placeholder',startDateString);
    loadTaskList('taskIdList');
};

$(document).ready(function () {
    $(function () {
        $("#taskStartDate").datepicker({}).on('focusin', function () {
            wso2.gadgets.controls.resizeGadget({
                height: '400px'
            });
        });
        $("#taskEndDate").datepicker({}).on('focusin', function () {
            wso2.gadgets.controls.resizeGadget({
                height: '400px'
            });
        });

        wso2.gadgets.controls.addLostFocusListener(function () {
            $("#startDate").datepicker('hide');
            $("#endDate").datepicker('hide');
            wso2.gadgets.controls.restoreGadget();
        });
    });

    $("#taskStartDate").datepicker({}).on('changeDate', function (e) {
        var fromdate = e.date;
        from = fromdate.getTime();
        $("#taskStartDate").datepicker('hide');
        wso2.gadgets.controls.restoreGadget();
    });

    $("#taskEndDate").datepicker({}).on('changeDate', function (e) {
        var todate = e.date;
        to = todate.getTime();
        $("#taskEndDate").datepicker('hide');
        wso2.gadgets.controls.restoreGadget();
    });
});

function loadTaskList(dropdownId) {
    var dropdownElementID = '#' + dropdownId;
    
    var body = {
        'processId': $('#process_name').val()
    };

    $.ajax({
        type: 'POST',
        url: '/portal/controllers/apis/bpmn-api/task_definition_key_list_vs_process_id.jag',
        data: {'filters':JSON.stringify(body)},
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


function publish() {
    var task_id = $('#taskIdList').val();
    if(!from) {
        from = 0;
    }
    if(!to) {
        to = 0;
    }
    console.log(from+" "+to);
    gadgets.Hub.publish('task_id', {
        task_id: task_id,
        from: from,
        to: to
    });
}

function reset() {
    $("#taskStartDate").val("");
    $("#taskEndDate").val("");
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