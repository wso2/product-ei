var TOPIC = "subscriber";
var page = gadgetUtil.getCurrentPage();
var qs = gadgetUtil.getQueryString();
var type = 38;

$(function() {

    if (page && qs[PARAM_ID] == null) {
        
        switch(page.name) {
            case 'api':
                $("#canvas").html(gadgetUtil.getInfoText('Please select an API and a valid date range to view stats.'));
                break;
            case 'proxy':
                $("#canvas").html(gadgetUtil.getInfoText('Please select a Proxy Service and a valid date range to view stats.'));
                break;
            case 'sequences':
                $("#canvas").html(gadgetUtil.getInfoText('Please select a Sequence and a valid date range to view stats.'));
                break;
            case 'endpoint':
                $("#canvas").html(gadgetUtil.getInfoText('Please select an Endpoint and a valid date range to view stats.'));
                break;
            case 'inbound':
                $("#canvas").html(gadgetUtil.getInfoText('Please select an Inbound Endpoint and a valid date range to view stats.'));
                break;
            default:
                $("#canvas").html(gadgetUtil.getInfoText());
        };
        
        return;
    }
    else {
        $('#stats').show();
        if(qs[PARAM_ID]) {
            $("#title")
                .html('for ' + qs[PARAM_ID])
                .attr('title', qs[PARAM_ID]);
        }
    }
    var timeFrom = gadgetUtil.timeFrom();
    var timeTo = gadgetUtil.timeTo();
    if (page) {
        type = page.type;
    }
    gadgetUtil.fetchData(CONTEXT, {
        type: type,
        id: qs.id,
        entryPoint: qs.entryPoint,
        timeFrom: timeFrom,
        timeTo: timeTo
    }, onData, onError);
});

gadgets.HubSettings.onConnect = function() {
    gadgets.Hub.subscribe(TOPIC, function(topic, data, subscriberData) {
        onTimeRangeChanged(data);
    });
};

function onTimeRangeChanged(data) {
    gadgetUtil.fetchData(CONTEXT, {
        type: type,
        id: qs.id,
        timeFrom: data.timeFrom,
        timeTo: data.timeTo,
        entryPoint: qs.entryPoint
    }, onData, onError);
};

function onData(response) {
    try {
        var data = response.message;
        var total = data.total;
        var failed = data.failed;
        if( (!total) || (total < 1) ) {
            $('#gadget-messge').html(gadgetUtil.getEmptyRecordsText());
            $('#gadget-messge').show();
            $('#stats').hide();
            return;
        } else{
            $('#gadget-messge').hide();
            $('#stats').show();
        }

        var success = total - failed;
        var failedPct = (failed / total) * 100;
        var successPct = 100 - failedPct;

        $("#totalCount").html(Number(total).toLocaleString('en'));
        $("#failedCount").html(Number(failed).toLocaleString('en'));
        $("#failedPercent").html(parseFloat(failedPct).toFixed(2));
        $("#successCount").html(Number(success).toLocaleString('en'));
        $("#successPercent").html(parseFloat(successPct).toFixed(2));

        var successColor = function(){
            return parseFloat(successPct) > 0 ? '#5CB85C' : '#353B48';
        };

        var failColor = function(){
            return parseFloat(failedPct) > 0 ? '#D9534F' : '#353B48';
        };

        //draw donuts
        var dataT = [{
            "metadata": {
                "names": ["rpm", "torque", "horsepower", "EngineType"],
                "types": ["linear", "linear", "ordinal", "ordinal"]
            },
            "data": [
                [0, parseFloat(successPct), 12, "YES"],
                [0, parseFloat(failedPct), 12, "NO"]
            ]
        }];

        var dataF = [{
            "metadata": {
                "names": ["rpm", "torque", "horsepower", "EngineType"],
                "types": ["linear", "linear", "ordinal", "ordinal"]
            },
            "data": [
                [0, parseFloat(failedPct), 12, "YES"],
                [0, parseFloat(successPct), 12, "NO"]
            ]
        }];

        var configT = {
            charts: [{ type: "arc", x: "torque", color: "EngineType" }],
            innerRadius: 0.3,
            tooltip: { "enabled": false },
            padding: { top:0, right:0, bottom:0, left:0 },
            legend: false,
            percentage: true,
            colorScale: [successColor(), "#353B48"],
            width: 220,
            height: 220
        }

        var configF = {
            charts: [{ type: "arc", x: "torque", color: "EngineType" }],
            innerRadius: 0.3,
            tooltip: { "enabled": false },
            padding: { top:0, right:0, bottom:0, left:0 },
            legend: false,
            percentage: true,
            colorScale: [failColor(), "#353B48"],
            width: 220,
            height: 220
        }
        var chartT = new vizg(dataT, configT);
        chartT.draw("#dChartTrue");

        var chartF = new vizg(dataF, configF);
        chartF.draw("#dChartFalse");

    } catch (e) {
        $("#canvas").html(gadgetUtil.getErrorText(e));
    }
};

function onError(msg) {
    $("#canvas").html(gadgetUtil.getErrorText(msg));
}
