var charts = [{
    name: ROLE_TPS,
    columns: ["timestamp", "tps"],
    schema: [{
        "metadata": {
            "names": ["Time", "TPS"],
            "types": ["time", "linear"]
        },
        "data": []
    }],
    chartConfig: {
        x: "Time",
        charts: [{ type: "line", y: "TPS" }],
        padding: { "top": 30, "left": 60, "bottom": 60, "right": 30 },
        range: true,
        rangeColor: COLOR_BLUE,
        colorScale: [COLOR_BLUE]
    },
    types: [
        { name: TYPE_LANDING, type: 1 }
    ],
    processData: function(data) {
        var result = [];
        var schema = this.schema;
        var columns = this.columns;
        data.forEach(function(row, i) {
            var record = [];
            columns.forEach(function(column) {
                var value = row[column];
                record.push(value);
            });
            // schema[0].data.push(record);
            result.push(record);
        });
        return result;
    }
}, {
    name: ROLE_RATE,
    schema: [{
        "metadata": {
            "names": ["Time", "Status", "Count"],
            "types": ["time", "ordinal", "linear"]
        },
        "data": []
    }],
    chartConfig: {
        x: "Time",
        charts: [{ type: "line", y: "Count", color: "Status" }],
        padding: { "top": 30, "left": 60, "bottom": 60, "right": 110 },
        range: true,
        rangeColor: COLOR_BLUE,
        colorScale: [COLOR_GREEN,COLOR_RED],
        colorDomain: ["SUCCESS","FAULT"]
    },
    types: [
        { name: TYPE_LANDING, type: 2 },
        { name: TYPE_PROXY, type: 7 },
        { name: TYPE_API, type: 12 },
        { name: TYPE_MEDIATOR, type: 17 },
        { name: TYPE_ENDPOINT, type: 25 },
        { name: TYPE_SEQUENCE, type: 29 },
        { name: TYPE_INBOUND_ENDPOINT, type: 34 }
    ],
    processData: function(data) {
        var result = [];
        data.forEach(function(row, i) {
            var timestamp = row['timestamp'];
            var success = row["success"];
            var fault = row["faults"];

            result.push([timestamp, "SUCCESS", success]);
            result.push([timestamp, "FAULT", fault]);
            // result.push([timestamp, "TOTAL", success + fault]);
        });
        return result;
    }
}, {
    name: ROLE_LATENCY,
    schema: [{
        "metadata": {
            "names": ["Time", "Type", "Value"],
            "types": ["time", "ordinal", "linear"]
        },
        "data": []
    }],
    chartConfig: {
        x: "Time",
        charts: [{ type: "line", y: "Value", color: "Type" }],
        padding: { "top": 30, "left": 60, "bottom": 60, "right": 110 },
        range: true
    },
    types: [
        { name: TYPE_PROXY, type: 8 },
        { name: TYPE_API, type: 13 },
        { name: TYPE_MEDIATOR, type: 18 },
        { name: TYPE_ENDPOINT, type: 26 },
        { name: TYPE_SEQUENCE, type: 30 },
        { name: TYPE_INBOUND_ENDPOINT, type: 35 }
    ],
    processData: function(data) {
        var result = [];
        data.forEach(function(row, i) {
            var timestamp = row['timestamp'];
            var min = row["min"];
            var avg = row["avg"];
            var max = row["max"];

            result.push([timestamp, "Minimum", min]);
            result.push([timestamp, "Average", avg]);
            result.push([timestamp, "Maximum", max]);
        });
        return result;
    }
}];
