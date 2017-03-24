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
    x : "Process",
    highlight : "multi",
    charts : [{type: "bar",  y : "Time"}],
    maxLength: 200,
    xAxisAngle:true,
    padding: {"top": 10, "left": 80, "bottom": 150, "right": 0},
    transform:[60,70]
};


var jsonObj = [];

var callbackmethod = function(event, item) {

};

window.onload = function() {
    $.getJSON("/portal/store/carbon.super/fs/gadget/avg_time_vs_process_id/js/meta-data.json.js", function(result){
        $.each(result, function(i, field){
            jsonObj.push(field);
            drawAvgExecuteTimeVsProcessIdResult();
        });
    });
};

function drawAvgExecuteTimeVsProcessIdResult() {
    var body = {
        'startTime': $("#from").val()||0,
        'endTime': $("#to").val()||0,
        'order': $('#processIdAvgExecTimeOrder').val(),
        'count': parseInt($('#processIdAvgExecTimeCount').val())
    };

    $.ajax({
        url: '/portal/controllers/apis/bpmn-api/avg_time_vs_process_id.jag',
        type: 'POST',
        data: {'filters': JSON.stringify(body)},
        success: function (data) {
            // console.log(data);
            var responseJsonArr = [];
            if(!$.isEmptyObject(data))
                responseJsonArr = JSON.parse(data);

            var responseStr = '';
            var scale = getTimeScale(responseJsonArr[0].avgExecutionTime);
            for(var i = 0; i < responseJsonArr.length; i++) {
                responseJsonArr[i].avgExecutionTime = convertTime(scale, responseJsonArr[i].avgExecutionTime);
                var temp = '["' + responseJsonArr[i].processDefKey+'",'+responseJsonArr[i].avgExecutionTime+'],';
                responseStr+=temp;
            }
            jsonObj[0].metadata.names[1] = "Time(" + scale + ")";
            config.charts[0].y = "Time(" + scale + ")";
            responseStr = responseStr.slice(0,-1);
            var jsonArrObj = JSON.parse('[' + responseStr + ']');
            jsonObj[0].data = jsonArrObj;

            config.width = $('#chartA').width();
            config.height = $('#chartA').height();
            var barChart = new vizg(jsonObj, config);
            barChart.draw("#chartA", [{type:"click", callback:callbackmethod}]);
        },
        error: function () {
            
        }
    });
    $('#collapse').collapse("hide");
}
