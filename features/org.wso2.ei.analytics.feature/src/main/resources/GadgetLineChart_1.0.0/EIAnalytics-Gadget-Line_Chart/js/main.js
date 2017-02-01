var TOPIC = "subscriber";
var PUBLISHER_TOPIC = "chart-zoomed";
var page = gadgetUtil.getCurrentPageName();
var qs = gadgetUtil.getQueryString();
var prefs = new gadgets.Prefs();
var type;
var chart = gadgetUtil.getChart(prefs.getString(PARAM_GADGET_ROLE));
var rangeStart;
var rangeEnd;

if (chart) {
    type = gadgetUtil.getRequestType(page, chart);
}

$(function() {
    if (!chart) {
        $("#canvas").html(gadgetUtil.getErrorText("Gadget initialization failed. Gadget role must be provided."));
        return;
    }
    if (page != TYPE_LANDING && qs[PARAM_ID] == null) {

        switch(page) {
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
    var timeFrom = gadgetUtil.timeFrom();
    var timeTo = gadgetUtil.timeTo();
    gadgetUtil.fetchData(CONTEXT, {
        type: type,
        id: qs.id,
        timeFrom: timeFrom,
        timeTo: timeTo,
        entryPoint: qs.entryPoint
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
        if (data.length == 0) {
            $('#canvas').html(gadgetUtil.getEmptyRecordsText());
            return;
        }
        //sort the timestamps
        data.sort(function(a, b) {
            return a.timestamp - b.timestamp;
        });
        //perform necessary transformation on input data
        chart.schema[0].data = chart.processData(data);
        //finally draw the chart on the given canvas
        chart.chartConfig.width = $('body').width();
        chart.chartConfig.height = $('body').height();

        var vg = new vizg(chart.schema, chart.chartConfig);
        $("#canvas").empty();
        vg.draw("#canvas",[{type:"range", callback:onRangeSelected}]);
    } catch (e) {
        $('#canvas').html(gadgetUtil.getErrorText(e));
    }
};

function onError(msg) {
    $("#canvas").html(gadgetUtil.getErrorText(msg));
};

// $(window).resize(function() {
//     // if (page != TYPE_LANDING && qs[PARAM_ID]) {
//         drawChart();
//     // }
// });

document.body.onmouseup = function() {
    // var div = document.getElementById("dChart");
    // div.innerHTML = "<p> Start : " + rangeStart + "</p>" + "<p> End : " + rangeEnd + "</p>";

    if((rangeStart) && (rangeEnd) && (rangeStart.toString() !== rangeEnd.toString())){
        var message = {
            timeFrom: new Date(rangeStart).getTime(),
            timeTo: new Date(rangeEnd).getTime(),
            timeUnit: "Custom"
        };
        gadgets.Hub.publish(PUBLISHER_TOPIC, message);
    }
}

var onRangeSelected = function(start, end) {
    rangeStart = start;
    rangeEnd = end;
};