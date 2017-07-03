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