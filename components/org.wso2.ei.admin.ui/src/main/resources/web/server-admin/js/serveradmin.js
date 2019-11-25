/*
*  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
function shutdownServerGracefully() {
    jQuery.noConflict();
    CARBON.showConfirmationDialog(jsi18n["graceful.shutdown.verify"],shutdownServerGracefullyCallback,null);

}

function shutdownServerGracefullyCallback() {
    jQuery.noConflict();
    jQuery.post("proxy_ajaxprocessor.jsp",
        {
            "action":"shutdownGracefully"
        },function (responseText, status, XMLHttpRequest) {
            if (status != "success") {
                CARBON.showErrorDialog(jsi18n["graceful.shutdown.error"]);
            } else {
                CARBON.showInfoDialog(jsi18n["graceful.shutdown.in.progress.message"]);
            }
        }
    );
}

function shutdownServer() {
    jQuery.noConflict();
    CARBON.showConfirmationDialog(jsi18n["shutdown.verification"],shutdownServerCallback,null);
}

function shutdownServerCallback() {
    jQuery.noConflict();
    jQuery.post("proxy_ajaxprocessor.jsp",
        {
            "action":"shutdown"
        },function (responseText, status, XMLHttpRequest) {
            if (status != "success") {
                CARBON.showErrorDialog(jsi18n["shutdown.error"]);
            } else {
                CARBON.showInfoDialog(jsi18n["shutdown.in.progress.message"]);
            }
        }
    );
}

function restartServerGracefully() {
    jQuery.noConflict();
    CARBON.showConfirmationDialog(jsi18n["graceful.restart.verification"],restartServerGracefullyCallback,null);
}

function restartServerGracefullyCallback() {
    jQuery.noConflict();
    jQuery.post("proxy_ajaxprocessor.jsp",
        {
            "action":"restartGracefully"
        },function (responseText, status, XMLHttpRequest) {
            if (status != "success") {
                CARBON.showErrorDialog(jsi18n["graceful.restart.error"]);
            } else {
                CARBON.showInfoDialog(jsi18n["graceful.restart.in.progress.message"]);
            }
        }
    );
}

function restartServer() {
    jQuery.noConflict();
    CARBON.showConfirmationDialog(jsi18n["restart.verification"],restartServerCallback,null);
}

function restartServerCallback() {
    jQuery.noConflict();
    jQuery.post("proxy_ajaxprocessor.jsp",
        {
            "action":"restart"
        },function (responseText, status, XMLHttpRequest) {
            if (status != "success") {
                CARBON.showErrorDialog(jsi18n["restart.error"]);
            } else {
                CARBON.showInfoDialog(jsi18n["restart.in.progress.message"]);
            }
        }
    );
}
