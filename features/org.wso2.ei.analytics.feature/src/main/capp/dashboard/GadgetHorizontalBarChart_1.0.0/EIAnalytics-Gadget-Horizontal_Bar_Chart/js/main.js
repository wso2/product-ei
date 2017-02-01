var TOPIC = "subscriber";
var timeFrom;
var timeTo;
var timeUnit = null;
var prefs = new gadgets.Prefs();
var config = gadgetUtil.getGadgetConfig(prefs.getString(PARAM_TYPE));

$(function() {
    if (config == null) {
        $("#canvas").html(gadgetUtil.getErrorText("Initialise gadget type first."));
        return;
    }
    timeFrom = gadgetUtil.timeFrom();
    timeTo = gadgetUtil.timeTo();
    gadgetUtil.fetchData(CONTEXT, {
        type: config.type,
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
    timeFrom = data.timeFrom;
    timeTo = data.timeTo;
    timeUnit = data.timeUnit;
    gadgetUtil.fetchData(CONTEXT, {
        type: config.type,
        timeFrom: timeFrom,
        timeTo: timeTo
    }, onData, onError);
}

function onData(data) {
    try {
        if (data.message.length == 0) {
            $("#canvas").html(gadgetUtil.getEmptyRecordsText());
            return;
        }
        var schema = [{
            "metadata": {
                "names": ["name", "requests"],
                "types": ["ordinal", "linear"]
            },
            "data": []
        }];
        var chartConfig = {
            type: "bar",
            x: "name",
            charts: [{ type: "bar", y: "requests", orientation: "left" }],
            grid: false,
            width: $('body').width(),
            height: $('body').height(),
            colorScale: [COLOR_BLUE],
            padding: { "top": 30, "left": 60, "bottom": 60, "right": 30 },
            textColor:"#000000",
            text:"name",
            textAlign:"left",
            xAxisStrokeSize:0,
            xAxisFontSize:0
        };

        data.message.forEach(function(row, i) {
            schema[0].data.push([row.name, row.requests]);
        });

        var onChartClick = function(event, item) {
            var id = -1;
            if (item != null) {
                id = item.datum.name;
            }
            var baseUrl = config.targetUrl;
            var urlParameters = gadgetUtil.getUrlParameters();
            if (urlParameters != null) {
                baseUrl += urlParameters + "&";
            } else {
                baseUrl += "?";
            }
            var targetUrl = baseUrl + PARAM_ID + "=" + id;
            gadgetUtil.updateURLParam("timeFrom", timeFrom.toString());
            gadgetUtil.updateURLParam("timeTo", timeFrom.toString());
            if (timeUnit != null) {
                targetUrl += "&timeUnit=" + timeUnit;
            }
            parent.window.location = targetUrl;
        };
        var chart = new vizg(schema, chartConfig);
        $("#canvas").empty();
        chart.draw("#canvas", [{ type: "click", callback: onChartClick }]);
    } catch (e) {
        $("#canvas").html(gadgetUtil.getErrorText(e));
    }
};

function onError(msg) {
    $("#canvas").html(gadgetUtil.getErrorText(msg));
};

// $(window).resize(function() {
//     if (page != TYPE_LANDING && qs[PARAM_ID]) {
//         drawChart();
//     }
// });