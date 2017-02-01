var TYPE = 1;
var TOPIC = "subscriber";
var PUBLISHER_TOPIC = "node-clicked";
var page = gadgetUtil.getCurrentPage();
var qs = gadgetUtil.getQueryString();
var timeFrom, timeTo, timeUnit = null;

var TOPDOWN = "TD";
var LEFT_TO_RIGHT = "TD";
var orientation = TOPDOWN;
var gadgetMaximized = gadgetUtil.getView() == 'maximized';

var SHARED_PARAM = "&shared=true";

/**
 * detect IE
 * returns version of IE or false, if browser is not Internet Explorer
 */
function detectIE() {
    var ua = window.navigator.userAgent;

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        var rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
       // Edge (IE 12+) => return version number
       return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
    }

    // other browser
    return false;
}

/**
 * Fix for browser version compatible issue
 * returns compatible styling for IE or Non IE by detecting IE
 */
var hiddenLineStyle;
if(detectIE() !== false){
    hiddenLineStyle = 'display: none;';
}
else {
    hiddenLineStyle = 'stroke-width: 0px;';
}

$(function() {
    
    if (qs[PARAM_ID] == null) {
        
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
    timeFrom = gadgetUtil.timeFrom();
    timeTo = gadgetUtil.timeTo();

    gadgetUtil.fetchData(CONTEXT, {
        type: page.type,
        id: qs.id,
        timeFrom: timeFrom,
        timeTo: timeTo
    }, onData, onError);

    $("body").on("click", ".nodeLabel", function(e) {
        e.preventDefault();
        if ($(this).data("node-type") === "UNKNOWN") {
            return;
        }
        if (page.name != TYPE_MESSAGE) {
            var targetUrl = $(this).data("target-url");
            if (gadgetUtil.isSharedDashboard()) {
                targetUrl += SHARED_PARAM;
            }
            parent.window.location = targetUrl;
        } else {
            var componentId = $(this).data("component-id");
            var hashCode = $(this).data("hash-code");
            message = {
                componentId: componentId,
                hashCode: hashCode
            };
            gadgets.Hub.publish(PUBLISHER_TOPIC, message);
        }
    });

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
        type: page.type,
        id: qs.id,
        timeFrom: timeFrom,
        timeTo: timeTo
    }, onData, onError);
};

function onData(response) {
    var data = response.message;
    if (data.length == 0) {
        $("#canvas").html(gadgetUtil.getEmptyRecordsText());
        return;
    }
    var groups = [];
    $("#canvas").empty();
    var nodes = data;
    // Create the input graph

    orientation = (page.name === TYPE_MESSAGE) ? LEFT_TO_RIGHT : TOPDOWN;

    var g = new dagreD3.graphlib.Graph({ compound: true })
        .setGraph({ rankdir: orientation })
        .setDefaultEdgeLabel(function() {
            return {};
        });

    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i].id != null) {
            //Set Nodes
            if (nodes[i].type == "group") {
                g.setNode(nodes[i].id, { label: "", clusterLabelPos: 'top' });

                //Add arbitary nodes for group
                g.setNode(nodes[i].id + "-s", { label: nodes[i].label, style: hiddenLineStyle });
                // g.setEdge(nodes[i].id + "-s", nodes[i].id + "-e",  { style: 'display: none;; fill: #ffd47f'});
                g.setNode(nodes[i].id + "-e", { label: "", style: hiddenLineStyle });
                g.setParent(nodes[i].id + "-s", nodes[i].id);
                g.setParent(nodes[i].id + "-e", nodes[i].id);

                groups.push(nodes[i]);
            } else {
                var label = buildLabel(nodes[i]);
                g.setNode(nodes[i].id, { labelType: "html", label: label });
                // g.setNode(nodes[i].id, {label: nodes[i].label});
            }

            //Set Edges
            if (nodes[i].parents != null) {
                for (var x = 0; x < nodes[i].parents.length; x++) {
                    var isParentGroup = false;
                    for (var y = 0; y < groups.length; y++) {
                        if (groups[y].id == nodes[i].parents[x] && groups[y].type == "group") {
                            isParentGroup = true;
                        }
                    }

                    if (nodes[i].type == "group") {
                        if (isParentGroup) {
                            g.setEdge(nodes[i].parents[x] + "-e", nodes[i].id + "-s", { lineInterpolate: 'basis', arrowheadClass: 'arrowhead' });
                        } else {
                            g.setEdge(nodes[i].parents[x], nodes[i].id + "-s", { lineInterpolate: 'basis', arrowheadClass: 'arrowhead' });
                        }
                    } else {
                        if (isParentGroup) {
                            g.setEdge(nodes[i].parents[x] + "-e", nodes[i].id, { lineInterpolate: 'basis', arrowheadClass: 'arrowhead' });
                        } else {
                            g.setEdge(nodes[i].parents[x], nodes[i].id, { lineInterpolate: 'basis', arrowheadClass: 'arrowhead' });
                        }
                    }
                }
            }

            if (nodes[i].group != null) {
                g.setParent(nodes[i].id, nodes[i].group);
                if (nodes[i].type != "group" && !isParent(nodes, nodes[i])) {
                    g.setEdge(nodes[i].group + "-s", nodes[i].id, { style: hiddenLineStyle });
                    g.setEdge(nodes[i].id, nodes[i].group + "-e", { style: hiddenLineStyle });
                }


            }

        }

    }

    g.nodes().forEach(function(v) {
        var node = g.node(v);

        node.rx = node.ry = 7;
    });

    // Create the renderer
    var render = new dagreD3.render();

    var svg = d3.select("svg"),
        svgGroup = svg.append("g");
    inner = svg.select("g"),
        zoom = d3.behavior.zoom().on("zoom", function() {
            inner.attr("transform", "translate(" + d3.event.translate + ")" +
                "scale(" + d3.event.scale + ")");
        });

    //TODO hide zoom when the gadget is gadgetMaximized
    if (gadgetMaximized) {
        svg.call(zoom);
        var nanoScrollerSelector = $(".nano");
        nanoScrollerSelector.nanoScroller();
    } else {
        
    }
    inner.call(render, g);

    // Zoom and scale to fit
    var graphWidth = g.graph().width + 80;
    var graphHeight = g.graph().height + 40;
    var width = parseInt(svg.style("width").replace(/px/, ""));
    var height = parseInt(svg.style("height").replace(/px/, ""));
    var zoomScale = Math.min(width / graphWidth, height / graphHeight);
    var translate = [(width / 2) - ((graphWidth * zoomScale) / 2), (height / 2) - ((graphHeight * zoomScale) / 2)];

    zoom.translate(translate);
    zoom.scale(zoomScale);
    // zoom.event(isUpdate ? svg.transition().duration(500) : d3.select("svg"));
    zoom.event(svg);

};

function isParent(searchNodes, id) {
   for (var x = 0; x < searchNodes.length; x++) { 
        if (searchNodes[x].parent == id) {
            return true;
        }
   }
   return false;
}

function buildLabel(node) {
    var pageUrl = MEDIATOR_PAGE_URL;
    if (node.type === "Sequence") {
        pageUrl = SEQUENCE_PAGE_URL;
    } else if (node.type === "Endpoint") {
        pageUrl = ENDPOINT_PAGE_URL;
    }
    var hashCode;
    var hiddenParams = '';
    if (node.hiddenAttributes) {
        node.hiddenAttributes.forEach(function(item, i) {
            hiddenParams += '&' + item.name + '=' + item.value;
            if (item.name === "hashCode") {
                hashCode = item.value;
            }
        });
    }
    var targetUrl = pageUrl + '?' + hiddenParams;
    var labelText = '<div class="nodeLabel" data-node-type="' + node.type + '" data-component-id="' + node.modifiedId
    + '" data-hash-code="' + hashCode + '" data-target-url="' + targetUrl + '"><h4><a href="#">' + node.label + "</a></h4>";

    if (node.dataAttributes && gadgetMaximized) {
        node.dataAttributes.forEach(function(item, i) {
            labelText += "<h5><label>" + item.name + " : </label><span>" + item.value + "</span></h5>";
        });
    }
    labelText += "</div>";
    return labelText;
};

function onError(msg) {
    $("#canvas").html(gadgetUtil.getErrorText(msg));
};

$('body').on('click', '#btnViewToggle', function(){
    $('#' + gadgets.rpc.RPC_ID, window.parent.document)
        .closest('.grid-stack-item')
        .find('.ues-component-full-handle')
        .click();
});