var TOPIC = "subscriber";
var type = 48;
var qs = gadgetUtil.getQueryString();
var BEFORE = "before";
var AFTER = "after";

$(function() {
    $("#gadget-message").html(gadgetUtil.getInfoText('Please select medaitor from the above message flow chart to view properties of it.', 'Select a mediator'));
    $("#gadget-message").show();
    $("#props").hide();
});

gadgets.HubSettings.onConnect = function() {
    gadgets.Hub.subscribe(TOPIC, function(topic, data, subscriberData) {
        $("#gadget-message").hide();
        $("#props").show();
        mediatorClicked(data);
    });
};

function mediatorClicked(data) {
    var componentId = data.componentId;
    var hashCode = data.hashCode;
    if(componentId && hashCode) {
        gadgetUtil.fetchData(CONTEXT, {
            type: type,
            id: qs.id,
            componentId: componentId,
            hashCode: hashCode
        }, onData, onError);
    }
    $('.nano').nanoScroller();
};

function onData(response) {
    try {
        var data = response.message;
        if (!data) {
            $("#canvas").html(gadgetUtil.getEmptyRecordsText());
            return;
        }

        drawMergeView("payloadView", data.payload.before.trim(), data.payload.after.trim());

        if(data.transportProperties) {
           var transportPropertiesBefore = "";
           var transportPropertiesAfter = "";
            data.transportProperties.forEach(function (property) {
                if(typeof(property.before) === "string") {
                    property.before = "'" + property.before + "'";
                }
                if(typeof(property.after) === "string") {
                    property.after = "'" + property.after + "'";
                }

                transportPropertiesBefore += property.name + " : "+ property.before + "\n";
                transportPropertiesAfter += property.name + " : "+ property.after + "\n";
            });
            drawMergeView("transportPropView", transportPropertiesBefore.trim(), transportPropertiesAfter.trim());
        }

        if(data.contextProperties) {
            var contextPropertiesBefore = "";
            var contextPropertiesAfter = "";
            data.contextProperties.forEach(function (property) {
                if(typeof(property.before) === "string") {
                    property.before = "'" + property.before + "'";
                }
                if(typeof(property.after) === "string") {
                    property.after = "'" + property.after + "'";
                }
                contextPropertiesBefore += property.name + " : "+ property.before + "\n";
                contextPropertiesAfter += property.name + " : "+ property.after + "\n";
            });
            drawMergeView("contextPropView", contextPropertiesBefore.trim(), contextPropertiesAfter.trim());
        }
    } catch (e) {
        $("#gadget-message").html(gadgetUtil.getErrorText(e));
    }
};

function onError(msg) {
    $("#gadget-message").html(gadgetUtil.getErrorText(msg));
};

function drawMergeView(placeholder, before, after) {
    var view = document.getElementById(placeholder);
    if(isJSON(before)) {
        before = JSON.stringify(JSON.parse(before), null, "\t");   
    } else {
        before = formatXML(before);
    }
    if(isJSON(after)) {
        after = JSON.stringify(JSON.parse(after), null, "\t");  
    } else {
        after = formatXML(after);
    }
    view.innerHTML = "";
      var dv = CodeMirror.MergeView(view, {
        value: before,
        origLeft: after,
        lineNumbers: true,
        theme:"ambiance",
        highlightDifferences: true,
        connect: "connect"
    });
}

function isJSON(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}


function formatXML(xml) {
    var reg = /(>)(<)(\/*)/g;
    var wsexp = / *(.*) +\n/g;
    var contexp = /(<.+>)(.+\n)/g;
    xml = xml.replace(reg, '$1\n$2$3').replace(wsexp, '$1\n').replace(contexp, '$1\n$2');
    var pad = 0;
    var formatted = '';
    var lines = xml.split('\n');
    var indent = 0;
    var lastType = 'other';
    var transitions = {
        'single->single'    : 0,
        'single->closing'   : -1,
        'single->opening'   : 0,
        'single->other'     : 0,
        'closing->single'   : 0,
        'closing->closing'  : -1,
        'closing->opening'  : 0,
        'closing->other'    : 0,
        'opening->single'   : 1,
        'opening->closing'  : 0, 
        'opening->opening'  : 1,
        'opening->other'    : 1,
        'other->single'     : 0,
        'other->closing'    : -1,
        'other->opening'    : 0,
        'other->other'      : 0
    };

    for (var i=0; i < lines.length; i++) {
        var ln = lines[i];
        var single = Boolean(ln.match(/<.+\/>/));
        var closing = Boolean(ln.match(/<\/.+>/));
        var opening = Boolean(ln.match(/<[^!].*>/));
        var type = single ? 'single' : closing ? 'closing' : opening ? 'opening' : 'other';
        var fromTo = lastType + '->' + type;
        lastType = type;
        var padding = '';

        indent += transitions[fromTo];
        for (var j = 0; j < indent; j++) {
            padding += '    ';
        }

        formatted += padding + ln + '\n';
    }

    return formatted;
};