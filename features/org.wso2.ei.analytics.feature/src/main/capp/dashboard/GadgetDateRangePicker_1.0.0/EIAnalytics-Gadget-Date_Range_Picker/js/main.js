var href = parent.window.location.href,
    hrefLastSegment = href.substr(href.lastIndexOf('/') + 1),
    resolveURI = parent.ues.global.dashboard.id == hrefLastSegment ? '../' : '../../',
    parentWindow = window.parent.document,
    gadgetWrapper = $('#' + gadgets.rpc.RPC_ID, parentWindow).closest('.grid-stack-item');

var TOPIC = "range-selected";
$(function() {
    var dateLabel = $('#reportrange'),
        datePickerBtn = $('#btnCustomRange');
    //if there are url elemements present, use them. Otherwis use last hour

    var timeFrom = moment().startOf('minute').subtract(29, 'days');
    var timeTo = moment().startOf('minute');
    var message = {};

    var count = 0;

    //make the selected time range highlighted
    $("#date-select [role=date-update][data-value=LastMonth]").addClass("active");
    lastMonthUpdate();
    cb(timeFrom, timeTo);

    function cb(start, end) {
        dateLabel.html(start.format('MMMM D, YYYY hh:mm A') + ' - ' + end.format('MMMM D, YYYY hh:mm A'));
        if (count != 0) {
            message = {
                timeFrom: new Date(start).getTime(),
                timeTo: new Date(end).getTime(),
                timeUnit: "Custom"
            };
            gadgets.Hub.publish(TOPIC, message);
        }
        count++;
        if (message.timeUnit && (message.timeUnit == 'Custom')) {
            $("#date-select button").removeClass("active");
            $(datePickerBtn).addClass("active");
        }
    }
    
    $(datePickerBtn).on('apply.daterangepicker', function(ev, picker) {
        cb(moment(picker.startDate).startOf('minute'), moment(picker.endDate).startOf('minute'));
    });
    
    $(datePickerBtn).on('show.daterangepicker', function(ev, picker) {
        $(this).attr('aria-expanded', 'true');
        wso2.gadgets.controls.resizeGadget({
            height: "710px"
        });
    });
    
    $(datePickerBtn).on('hide.daterangepicker', function(ev, picker) {
        $(this).attr('aria-expanded', 'false');
        
        wso2.gadgets.controls.restoreGadget();
    });

    $(datePickerBtn).daterangepicker({
        "timePicker": true,
        "autoApply": true,
        "alwaysShowCalendars": true,
        "opens": "left"
    });
    
    $("#date-select [role=date-update]").click(function(){
        
        $("#date-select button").removeClass("active");
        $("#date-select [data-value=" + $(this).data('value') + "]").addClass("active");
        $('#btnDropdown > span:first-child').html($(this).html());
        $('#btnDropdown').addClass('active');
        switch($(this).data('value')){
            case 'Last5Minutes':
                dateLabel.html(moment().subtract(5, 'minutes').format('MMMM D, YYYY hh:mm:ss A') + ' - ' + moment().format('MMMM D, YYYY hh:mm:ss A'));
                message = {
                    timeFrom: new Date(moment().subtract(5, 'minutes')).getTime(),
                    timeTo: new Date(moment()).getTime(),
                    timeUnit: "Minute"
                };
                gadgetUtil.updateURLParam("timeFrom", message.timeFrom.toString());
                gadgetUtil.updateURLParam("timeTo", message.timeTo.toString());
                break;  
            case 'LastHour':
                dateLabel.html(moment().subtract(1, 'hours').format('MMMM D, YYYY hh:mm A') + ' - ' + moment().format('MMMM D, YYYY hh:mm A'));
                message = {
                    timeFrom: new Date(moment().startOf('minute').subtract(1, 'hours')).getTime(),
                    timeTo: new Date(moment().startOf('minute')).getTime(),
                    timeUnit: "Hour"
                };
                gadgetUtil.updateURLParam("timeFrom", message.timeFrom.toString());
                gadgetUtil.updateURLParam("timeTo", message.timeTo.toString());
                break;
            case 'LastDay':
                dateLabel.html(moment().subtract(1, 'day').format('MMMM D, YYYY hh:mm A') + ' - ' + moment().format('MMMM D, YYYY hh:mm A'));
                message = {
                    timeFrom: new Date(moment().startOf('minute').subtract(1, 'day')).getTime(),
                    timeTo: new Date(moment().startOf('minute')).getTime(),
                    timeUnit: "Day"
                };
                gadgetUtil.updateURLParam("timeFrom", message.timeFrom.toString());
                gadgetUtil.updateURLParam("timeTo", message.timeTo.toString());
                break;
            case 'LastMonth':
                lastMonthUpdate();
                break;
            case 'LastYear':
                dateLabel.html(moment().subtract(1, 'year').format('MMMM D, YYYY hh:mm A') + ' - ' + moment().format('MMMM D, YYYY hh:mm A'));
                message = {
                    timeFrom: new Date(moment().startOf('minute').subtract(1, 'year')).getTime(),
                    timeTo: new Date(moment().startOf('minute')).getTime(),
                    timeUnit: "Year"
                };
                gadgetUtil.updateURLParam("timeFrom", message.timeFrom.toString());
                gadgetUtil.updateURLParam("timeTo", message.timeTo.toString());
                break;
            default:
                return;
        }
        gadgets.Hub.publish(TOPIC, message);
        
        $(gadgetWrapper).removeClass('btn-dropdown-menu-open');
        $('#btnDropdown').attr('aria-expanded', 'false');
    });

    function lastMonthUpdate() {
        dateLabel.html(moment().subtract(29, 'days').format('MMMM D, YYYY hh:mm A') + ' - ' + moment().format('MMMM D, YYYY hh:mm A'));
        message = {
            timeFrom: new Date(moment().startOf('minute').subtract(29, 'days')).getTime(),
            timeTo: new Date(moment().startOf('minute')).getTime(),
            timeUnit: "Month"
        };
        gadgetUtil.updateURLParam("timeFrom", message.timeFrom.toString());
        gadgetUtil.updateURLParam("timeTo", message.timeTo.toString());
    }
    
    $('.date-shortcuts').on('show.bs.dropdown', function(e){
        wso2.gadgets.controls.resizeGadget({
            height: "180px"
        });
    });
    
    $('.date-shortcuts').on('hide.bs.dropdown', function(e){
        wso2.gadgets.controls.restoreGadget();
    });

});

gadgets.HubSettings.onConnect = function() {
    gadgets.Hub.subscribe("chart-zoomed", function(topic, data, subscriberData) {
        onChartZoomed(data);
    });
};

function onChartZoomed(data) {
    message = {
        timeFrom: data.timeFrom,
        timeTo: data.timeTo,
        timeUnit: "Custom"
    };
    gadgets.Hub.publish(TOPIC, message);
    var start = data.timeFrom;
    var end = data.timeTo;
    // dateLabel.html(start.format('MMMM D, YYYY hh:mm A') + ' - ' + end.format('MMMM D, YYYY hh:mm A'));
    if (data.timeUnit && (data.timeUnit == 'Custom')) {
        $("#date-select button").removeClass("active");
        $(datePickerBtn).addClass("active");
    }
};

$(window).resize(function() {
    if(($('body').attr('media-screen') == 'md') || ($('body').attr('media-screen') == 'lg') || ($('body').attr('media-screen') == 'sm')){
        $(gadgetWrapper).removeClass('btn-dropdown-menu-open');
        $('#btnDropdown').attr('aria-expanded', 'false');
    }
});