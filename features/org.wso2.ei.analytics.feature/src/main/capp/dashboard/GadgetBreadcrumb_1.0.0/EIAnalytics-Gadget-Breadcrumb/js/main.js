var href = parent.window.location.href;
var lastSegment = href.substr(0,href.lastIndexOf('/') + 1);
var baseUrl = parent.window.location.protocol + "//" + parent.window.location.host + BASE_URL;
var qs = gadgetUtil.getQueryString();
var pageName = gadgetUtil.getCurrentPageName();
var currentLocation;

$(function() {
    
    if ('message' === pageName){
        $('.breadcrumb li:first-child()').remove();
        $('.breadcrumb').append('<li><a class="back"href="">'+
            '<i class="icon fw fw-redo fw-flip-horizontal"></i> Back</a></li>')
    } else {
        $("#homeLink").attr("href", baseUrl);
    }

    currentLocation = pageName;
    if (currentLocation != TYPE_LANDING) {
        appendTrail(lastSegment + pageName,currentLocation);
        if (qs[PARAM_ID] != null) {
            appendTrail(lastSegment + pageName + "?" + PARAM_ID + "=" + qs[PARAM_ID],qs[PARAM_ID]);
        }
    }
    
    function appendTrail(url,text) {
        var ol = $(".breadcrumb");
        var li = $('<li/>');
        var a = $('<a/>');

        if (['message','mediator'].indexOf(text) > -1){
            li.addClass("text-muted truncate hidden");
            li.append(text);
        } else {
            li.addClass("truncate");
            a.attr("href",url);
            a.text(text);
            li.append(a);
        }

        ol.append(li);

        if ('message' === pageName){
            $('.breadcrumb li.hidden').next().addClass('hide-slash');
        }
    }

    $(".breadcrumb").on('click', 'a', function(e) {
        e.preventDefault();
        if ($(this).hasClass('back')){
            parent.window.history.back();
        } else {
            parent.window.location = $(this).attr('href');
        }
        
    });

});
