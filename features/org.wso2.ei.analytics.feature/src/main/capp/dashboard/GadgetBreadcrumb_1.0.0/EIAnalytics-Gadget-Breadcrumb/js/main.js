var href = parent.window.location.href;
var lastSegment = href.substr(0,href.lastIndexOf('/') + 1);
var baseUrl = parent.window.location.protocol + "//" + parent.window.location.host + BASE_URL;
var qs = gadgetUtil.getQueryString();
var pageName = gadgetUtil.getCurrentPageName();
var currentLocation;

$(function() {
    
    $("#homeLink").attr("href", baseUrl);
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
        
        if(['message','mediator'].indexOf(text) > -1){
            li.addClass("text-muted truncate");
            li.append(text);
        }
        else{
            li.addClass("truncate");
            a.attr("href",url);
            a.text(text);
            li.append(a);
        }
        ol.append(li);
    }
    
    $(".breadcrumb").on('click', 'a', function(e) {
        e.preventDefault();
        parent.window.location = $(this).attr('href');
    });

});