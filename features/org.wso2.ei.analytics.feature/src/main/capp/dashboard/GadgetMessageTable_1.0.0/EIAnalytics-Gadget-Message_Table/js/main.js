var TOPIC = "subscriber";
var timeFrom;
var timeTo;
var timeUnit = null;
var page = gadgetUtil.getCurrentPage();
var qs = gadgetUtil.getQueryString();
var oTable;
var SHARED_PARAM = "?shared=true&";

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

    $.fn.dataTable.ext.errMode = 'none';
    
    oTable = $('#tblMessages').DataTable({
        dom: '<"dataTablesTop"' +
             'f' +
             '<"dataTables_toolbar">' +
             '>' +
             'rt' +
             '<"dataTablesBottom"' +
             'lip' +
             '>',
        language: {
            searchPlaceholder: "beforePayload: \"value\" OR afterPayload: \"value\""
        },
        "processing": true,
        "serverSide": true,
        "columns" : [
                    { title: "Message ID" },
                    { title: "Host" },
                    { title: "Start Time" },
                    { title: "Status" }
        ],
        "sErrMode": 'throw',
        "ajax": {
            "url" : CONTEXT,
            "data" : function (d) {
                d.id = qs.id;
                d.type = page.type;
                d.timeFrom = timeFrom;
                d.timeTo = timeTo;
                d.entryPoint = qs.entryPoint;
            }
        }
    });
    
    $('#tblMessages').on('error.dt', function ( e, settings, techNote, message ) {
        console.error( message );
    }).DataTable();

    //Binding custom searching on Enter key press
    $('#tblMessages_filter input').unbind();
    $('#tblMessages_filter input').bind('keyup', function(e) {
    if(e.keyCode == 13) {
        oTable.search( this.value).draw();
    }
    });

    $('#tblMessages').on('click', 'tbody tr', function() {
        var id = $(this).find("td:first").html(); 
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            oTable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
        if( timeUnit == null) {
            timeUnit = qs.timeUnit;
        }
        // var targetUrl = MESSAGE_PAGE_URL + "?" + PARAM_ID + "=" + id + "&timeFrom=" + timeFrom + "&timeTo=" + timeTo + "&timeUnit=" + timeUnit;

        var baseUrl = MESSAGE_PAGE_URL;
        if (gadgetUtil.isSharedDashboard()) {
            baseUrl += SHARED_PARAM;
        } else {
            baseUrl += "?";
        }
        parent.window.location =  baseUrl + PARAM_ID + "=" + id;
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

    oTable.clear().draw();
    oTable.ajax.reload().draw();
};

function onData(response) {
    try {
        var data = response.message;
        if (data.length <= 0) {
            $("#canvas").html(gadgetUtil.getEmptyRecordsText());
            return;
        }
        $("#tblMessages thead tr").empty();
        $("#tblMessages tbody").empty();
        var columns = page.columns;
        var thead = $("#tblMessages thead tr");
        columns.forEach(function(column) {
            var th = jQuery('<th/>');
            th.append(column.label);
            th.appendTo(thead);
        });

        var tbody = $("#tblMessages tbody");
        data.forEach(function(row, i) {
            var tr = jQuery('<tr/>');
            columns.forEach(function(column) {
                var td = jQuery('<td/>');
                var value = row[column.name];
                td.text(value);
                td.appendTo(tr);

            });
            tr.appendTo(tbody);

        });
        
        dataTable = $('#tblMessages').DataTable({
            dom: '<"dataTablesTop"' +
                'f' +
                '<"dataTables_toolbar">' +
                '>' +
                'rt' +
                '<"dataTablesBottom"' +
                'lip' +
                '>'
        });
    } catch (e) {
        $("#canvas").html(gadgetUtil.getErrorText(e));
    }
};

function onError(msg) {
    $("#canvas").html(gadgetUtil.getErrorText(msg));
};