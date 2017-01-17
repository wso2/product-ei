/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var CONSTANTS = {
    urlSeperator: '/',
    queryParamStreamName : '?streamname=',
    queryParamStreamVersion : '&version=',
    queryParamLastUpdatedTime : '&lastUpdatedTime=',
    urlSecureTransportWebsocket : 'wss://',
    urlSecureTransportHttp : 'https://',
    colon : ':',
    defaultIntervalTime : 10 * 1000,
    defaultHostName : 'localhost',
    defaultSecurePortNumber : '9443',
    defaultMode : 'AUTO',
    processModeHTTP : 'HTTP',
    processModeWebSocket : 'WEBSOCKET',
    processModeAuto : 'AUTO',
    numThousand : 1000,
    websocketTimeAppender : 400,
    websocketSubscriptionEndpoint : 'portal/uipublisher/websocketSubscriptionEndpoint.jag',
    httpEventRetrievalEndpoint : 'portal/uipublisher/httpEventRetrievalEndpoint.jag'
};


var websocket = null;
var webSocketUrl;
var httpUrl;
var cepHostName;
var cepPortNumber;
var isErrorOccured = false;
var lastUpdatedtime = -1;
var polingInterval;
var stream;
var streamVersion;
var firstPollingAttempt;
var processMode;
var onSuccessFunction;
var onErrorFunction;
var terminateWebsocketInstance = false;
var pollingContinue = true;

function subscribe(streamName,version,intervalTime,
                   listeningFuncSuccessData,listeningFuncErrorData,cepHost,cepPort,mode){

    stopPollingProcesses();
    stream = streamName;
    streamVersion = version;
    onSuccessFunction = listeningFuncSuccessData;
    onErrorFunction = listeningFuncErrorData;

    if(intervalTime == null || intervalTime == ""){
        polingInterval = CONSTANTS.defaultIntervalTime;
    } else{
        polingInterval = intervalTime * CONSTANTS.numThousand;
    }

    if(cepHost == null || cepHost == ""){
        cepHostName = CONSTANTS.defaultHostName;
    } else{
        cepHostName = cepHost;
    }

    if(cepPort == null || cepPort == ""){
        cepPortNumber = CONSTANTS.defaultSecurePortNumber;
    } else{
        cepPortNumber = cepPort;
    }

    if(mode == null || mode == ""){
        processMode = CONSTANTS.defaultMode;
    } else{
        processMode = mode;
    }

    webSocketUrl = CONSTANTS.urlSecureTransportWebsocket + cepHostName + CONSTANTS.colon + cepPortNumber +
        CONSTANTS.urlSeperator + CONSTANTS.websocketSubscriptionEndpoint;

    if(processMode == CONSTANTS.processModeHTTP){
        firstPollingAttempt = true;
        pollingContinue = true;
        startPoll();
    } else{
        initializeWebSocket(webSocketUrl);
    }
}


/**
 * Initializing Web Socket
 */
function initializeWebSocket(webSocketUrl){
    websocket = new WebSocket(webSocketUrl);
    websocket.onopen = webSocketOnOpen;
    websocket.onmessage = webSocketOnMessage;
    websocket.onclose = webSocketOnClose;
    websocket.onerror = webSocketOnError;
}

/**
 * Web socket On Open
 */

var webSocketOnOpen = function () {
    websocket.send(stream + ":" + streamVersion);
};


/**
 * On server sends a message
 */
var webSocketOnMessage = function (evt) {
    var event = evt.data;
    var array = JSON.parse(event);
    constructPayload(array);
};

/**
 * On server close
 */
var webSocketOnClose =function (e) {

    if(isErrorOccured){
        if(processMode != CONSTANTS.processModeWebSocket){
            firstPollingAttempt = true;
            pollingContinue = true;
            startPoll();
        }
    } else{
        if(!terminateWebsocketInstance){
            waitForSocketConnection(websocket);
        } else{
            terminateWebsocketInstance = false;
        }

    }
};

/**
 * On server Error
 */
var webSocketOnError = function (err) {
    var error = "Error: Cannot connect to Websocket URL:" + webSocketUrl + " .Hence closing the connection!";

    onErrorFunction(error);
    isErrorOccured = true;

};

/**
 * Gracefully increments the connection retry
 */
var waitTime = CONSTANTS.numThousand;
function waitForSocketConnection(socket, callback){
    setTimeout(
        function () {
            if (socket.readyState === 1) {
                initializeWebSocket(webSocketUrl);
                console.log("Connection is made");
                if(callback != null){
                    callback();
                }
                return;
            } else {
                websocket = new WebSocket(webSocketUrl);
                waitTime += CONSTANTS.websocketTimeAppender;
                waitForSocketConnection(websocket, callback);
            }
        }, waitTime);
}

/**
 * Polling to retrieve events from http request periodically
 */
function startPoll(){

    (function poll(){
        setTimeout(function(){
            httpUrl = CONSTANTS.urlSecureTransportHttp + cepHostName + CONSTANTS.colon + cepPortNumber +
                      CONSTANTS.urlSeperator + CONSTANTS.httpEventRetrievalEndpoint + CONSTANTS.queryParamStreamName + stream +
                      CONSTANTS.queryParamStreamVersion + streamVersion + CONSTANTS.queryParamLastUpdatedTime + lastUpdatedtime;;
            $.getJSON(httpUrl, function(responseText) {
                if(firstPollingAttempt){
                    /*var data = $("textarea#idConsole").val();
                     $("textarea#idConsole").val(data + "Successfully connected to HTTP.");*/
                    firstPollingAttempt = false;
                }
                var eventList = $.parseJSON(responseText.events);
                if(eventList.length != 0){
                    lastUpdatedtime = responseText.lastEventTime;
                    for(var i=0;i<eventList.length;i++){
                        var arr = eventList[i];
                        constructPayload(arr);
                    }
                }
                if(pollingContinue){
                    startPoll();
                }
            })
                .fail(function(errorData) {
                    var errorData = JSON.parse(errorData.responseText);
                    onErrorFunction(errorData.error);
                });
        }, polingInterval);
    })()
}

function stopPollingProcesses(){

    //stopping the Websocket
    if(websocket != null){
        terminateWebsocketInstance = true;
        websocket.close();
    }
    //stopping the HTTPS Request
    pollingContinue = false;

}

function constructPayload(eventsArray){

    var streamId = stream + CONSTANTS.colon + streamVersion;
    var twoDimentionalArray = [eventsArray];
    onSuccessFunction(streamId,twoDimentionalArray);

}