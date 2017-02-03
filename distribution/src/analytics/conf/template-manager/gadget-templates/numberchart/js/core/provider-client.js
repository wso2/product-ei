/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
// var registerCallBackforPush;

(function() {

    var callback;

    /**
     * TODO Need to read hostname,port, and tenantId from providerConfig
     * @param providerConfig
     * @param schema
     */
    registerCallBackforPush = function(providerConfig, schema, _callback) {
        var streamId = providerConfig['streamName'];
        var hostname = window.parent.location.hostname;
        var port = window.parent.location.port;
        var tenantDomain = "carbon.super";

        if (window.parent.location.pathname.split( '/' )[2] == "t") {
            tenantDomain = window.parent.location.pathname.split( '/' )[3];
        }

        subscribe(streamId.split(":")[0], streamId.split(":")[1],
            '10', tenantDomain,
            onData, onError,
            hostname,
            port,
            'WEBSOCKET',
            "SECURED"
        );
        callback = _callback;
    };

    function onData(streamId, data) {
        callback(data);
    };

    function onError(error) {
        console.error(error);
    };

}());