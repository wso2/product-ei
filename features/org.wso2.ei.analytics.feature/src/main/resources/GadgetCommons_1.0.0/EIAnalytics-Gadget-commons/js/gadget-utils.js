/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This Javascript module groups utility methods that are being used by all the gadgets in the ESB analytics dashboard
 */

var CONTEXT = "/portal/apis/eianalytics";
var DASHBOARD_NAME = parent.ues.global.dashboard.id; //"esb-analytics"
var BASE_URL = getDashboardBaseUrl();

var TYPE_LANDING = "landing";
var TYPE_PROXY = "proxy";
var TYPE_API = "api";
var TYPE_SEQUENCE = "sequences";
var TYPE_ENDPOINT = "endpoint";
var TYPE_INBOUND_ENDPOINT = "inbound";
var TYPE_MEDIATOR = "mediator";
var TYPE_MESSAGE = "message";

var ROLE_TPS = "tps";
var ROLE_LATENCY = "latency";
var ROLE_RATE = "rate";

var PARAM_ID = "id";
var PARAM_TYPE = "type";
var PARAM_GADGET_ROLE = "role";

var PROXY_PAGE_URL = BASE_URL + TYPE_PROXY;
var API_PAGE_URL = BASE_URL + TYPE_API;
var SEQUENCE_PAGE_URL = BASE_URL + TYPE_SEQUENCE;
var ENDPOINT_PAGE_URL = BASE_URL + TYPE_ENDPOINT;
var INBOUND_ENDPOINT_PAGE_URL = BASE_URL + TYPE_INBOUND_ENDPOINT;
var MEDIATOR_PAGE_URL = BASE_URL + TYPE_MEDIATOR;
var MESSAGE_PAGE_URL = BASE_URL + TYPE_MESSAGE;

var COLOR_BLUE = "#438CAD";
var COLOR_RED = "#D9534F";
var COLOR_GREEN = "#5CB85C";

var PARENT_WINDOW = window.parent.document;

function getDashboardBaseUrl() {
    var currentUrl = window.parent.location.href;
    var BaseUrlRegex = new RegExp(".*?(portal.*dashboards)");
    var tenantBaseUrl = BaseUrlRegex.exec(currentUrl)[1];
    return "/" + tenantBaseUrl + "/"  + DASHBOARD_NAME + "/";
}

function GadgetUtil() {
    var DEFAULT_START_TIME = new Date(moment().subtract(29, 'days')).getTime();
    var DEFAULT_END_TIME = new Date(moment()).getTime();

    this.getQueryString = function() {
        var queryStringKeyValue = window.parent.location.search.replace('?', '').split('&');
        var qsJsonObject = {};
        if (queryStringKeyValue != '') {
            for (i = 0; i < queryStringKeyValue.length; i++) {
                qsJsonObject[queryStringKeyValue[i].split('=')[0]] = queryStringKeyValue[i].split('=')[1];
            }
        }
        return qsJsonObject;

    };

    this.getChart = function(chartType) {
        var chart = null;
        charts.forEach(function(item, i) {
            if (item.name === chartType) {
                chart = item;
            }
        });
        return chart;
    };

    this.getCurrentPageName = function() {
        var pageName,type;
        var href = parent.window.location.href;
        href = href.replace(/\/\?/,"?");
        var lastSegment = href.substr(href.lastIndexOf('/') + 1);
        if (lastSegment.indexOf('?') == -1) {
            pageName = lastSegment;
        } else {
            pageName = lastSegment.substr(0, lastSegment.indexOf('?'));
        }
        if(!pageName || pageName === DASHBOARD_NAME) {
            pageName = TYPE_LANDING;
        }
        return pageName;
    };

    this.getRequestType = function(pageName,chart) {
        chart.types.forEach(function(item, i) {
            if (item.name === pageName) {
                type = item.type;
            }
        });
        return type;
    };

    this.getGadgetConfig = function(typeName) {
        var config = null;
        configs.forEach(function(item, i) {
            if (item.name === typeName) {
                config = item;
            }
        });
        return config;
    };

    this.getCurrentPage = function() {
        var page, pageName;
        var href = parent.window.location.href;
        var lastSegment = href.substr(href.lastIndexOf('/') + 1);
        if (lastSegment.indexOf('?') == -1) {
            pageName = lastSegment;
        } else {
            pageName = lastSegment.substr(0, lastSegment.indexOf('?'));
        }
        return this.getGadgetConfig(pageName);
    };

    this.timeFrom = function() {
        var timeFrom = DEFAULT_START_TIME;
        var qs = this.getQueryString();
        if (qs.timeFrom != null) {
            timeFrom = qs.timeFrom;
        }
        return timeFrom;
    };

    this.timeTo = function() {
        var timeTo = DEFAULT_END_TIME;
        var qs = this.getQueryString();
        if (qs.timeTo != null) {
            timeTo = qs.timeTo;
        }
        return timeTo;
    };

    this.fetchData = function(context, params, callback, error) {
        var url = "?";
        for (var param in params) {
            url = url + param + "=" + params[param] + "&";
        }
        $.ajax({
            url: context + url,
            type: "GET",
            success: function(data) {
                callback(data);
            },
            error: function(msg) {
                error(msg);
            }
        });
    };

    this.getDefaultText = function() {
        return '<div class="status-message">'+
                '<div class="message message-info">'+
                    '<h4><i class="icon fw fw-info"></i>No content to display</h4>'+
                    '<p>Please select a date range to view stats.</p>'+
                '</div>'+
            '</div>';
    };

    this.getEmptyRecordsText = function() {
        return '<div class="status-message">'+
                '<div class="message message-info">'+
                    '<h4><i class="icon fw fw-info"></i>No records found</h4>'+
                    '<p>Please select a valid date range to view stats.</p>'+
                '</div>'+
            '</div>';
    };

    this.getInfoText = function(msg, title) {
        var title = (title) ? title : 'No records found',
            msg = (msg) ? msg : 'Nothing selected or no data to show.';

        return '<div class="status-message">'+
                '<div class="message message-info">'+
                    '<h4><i class="icon fw fw-info"></i>' + title + '</h4>'+
                    '<p>' + msg + '</p>'+
                '</div>'+
            '</div>';
    };

    this.getErrorText = function(msg) {
        return '<div class="status-message">'+
                '<div class="message message-danger">'+
                    '<h4><i class="icon fw fw-info"></i>Error</h4>'+
                    '<p>An error occured while attempting to display this gadget. Error message is: ' + msg.status + ' - ' + msg.statusText + '</p>'+
                '</div>'+
            '</div>';
    };

    this.getCookie = function(cname) {
        var name = cname + "=";
        var ca = parent.document.cookie.split(';');
        for(var i=0; i<ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1);
            if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
        }
        return "";
    };

    this.getGadgetWrapper = function(){
        return $('#' + gadgets.rpc.RPC_ID, PARENT_WINDOW).closest('.gadget-body');
    };

    this.getGadgetParentWrapper = function(){
        return $('#' + gadgets.rpc.RPC_ID, PARENT_WINDOW).closest('.ues-component-box');
    };

    this.getView = function(){
        if( $('#' + gadgets.rpc.RPC_ID, PARENT_WINDOW).closest('.grid-stack-item').hasClass('ues-component-fullview') ){
            return 'maximized';
        }
        else{
            return 'minimized';
        }
    };

    this.getUrlParameters = function() {
        var currentUrl = parent.window.location.href;
        var urlParametersRegex = new RegExp(".*?(\\?.*)");
        var urlParameters = "";
        var parameters = urlParametersRegex.exec(currentUrl);
        if (parameters != null && parameters.length > 1) {
            urlParameters = parameters[1];
            return urlParameters;
        }
        return null;
    };

    this.isSharedDashboard = function () {
        if (this.getQueryString().shared &&
            (['true', 'false'].indexOf(this.getQueryString().shared) > -1)) {
            return $.parseJSON(this.getQueryString().shared);
        }
    }

    this.updateURLParam = function (key, value) {
        if (typeof (history.pushState) === "undefined") {
            console.warn("Browser doesn't support updating the url.");
            return;
        }

        var searchPath = window.parent.location.search,
            replace = new RegExp("(&|\\?)" + key + "=(.*?)(&|$)", "g"),
            urlParams = this.getURLParams(),
            values = [],
            unfiltered = "?filtered=false";

        if (Object.prototype.toString.call(value) === '[object Array]') {
            values = value;
        } else if (Object.prototype.toString.call(value) === "[object String]") {
            values.push(value);
        } else {
            console.error("value should be either an array of strings or a string");
            return;
        }
        if (searchPath.replace(unfiltered, "")) {
            if (key in urlParams) {
                if (values.length > 0) {
                    searchPath = searchPath.replace(replace, "$1" + key + "=" + values.toString() + "$3");
                } else {
                    if (searchPath.replace(replace, "")) {
                        searchPath = searchPath.replace(replace, "$1").replace(/&$/, '');
                    } else {
                        searchPath = unfiltered;
                    }
                }
            } else if (values.length > 0) {
                searchPath += "&" + key + "=" + values.toString();
            }
        } else if (values.length > 0) {
            searchPath = searchPath.replace(unfiltered, "");
            searchPath += "?" + key + "=" + values.toString();
        }
        window.parent.history.pushState({}, "", searchPath);
    }

    this.getURLParams = function () {
        var match,
            pl = /\+/g,
            search = /([^&=]+)=?([^&]*)/g,
            decode = function (s) {
                return decodeURIComponent(s.replace(pl, " "));
            },
            query = this.getUrlParameters(),
            urlParams = {};
        while (match = search.exec(query))
            urlParams[decode(match[1])] = decode(match[2]).split(',');
        return urlParams;
    }
}

var gadgetUtil = new GadgetUtil();

function mediaScreenSize(){
    var windowWidth = $(window).width();
    if(windowWidth < 767){
        $('body').attr('media-screen', 'xs');
    }
    if((windowWidth > 768) && (windowWidth < 991)){
        $('body').attr('media-screen', 'sm');
    }
    if((windowWidth > 992) && (windowWidth < 1199)){
        $('body').attr('media-screen', 'md');
    }
    if(windowWidth > 1200){
        $('body').attr('media-screen', 'lg');
    }
}

$(document).ready(function() {
    if(typeof $.fn.nanoScroller == 'function'){
        $(".nano").nanoScroller();
    }
    mediaScreenSize();
});

$(window).resize(function(){
    mediaScreenSize();
});
