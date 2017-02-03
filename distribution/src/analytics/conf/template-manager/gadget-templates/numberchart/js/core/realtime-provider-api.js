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
var getConfig, validate, getMode, getSchema, getData, registerCallBackforPush;

(function() {

    var PROVIDERS_LOCATION = '/extensions/providers/';
    var PROVIDER_NAME = 'realtime';

    var log = new Log();
    var utils = require('/modules/utils.js');
    var carbon = require("carbon");
    var EventPublisherConstants = Packages.org.wso2.carbon.event.publisher.core.config.EventPublisherConstants;
    var eventPublisherService = carbon.server.osgiService('org.wso2.carbon.event.publisher.core.EventPublisherService');
    var eventStreamService = carbon.server.osgiService('org.wso2.carbon.event.stream.core.EventStreamService');

    var typeMap = {
        "bool": "string",
        "boolean": "string",
        "string": "string",
        "int": "number",
        "integer": "number",
        "long": "number",
        "double": "number",
        "float": "number",
        "time": "time"
    };

    getConfig = function() {
        var formConfig = require(PROVIDERS_LOCATION + '/' + PROVIDER_NAME + '/config.json');
        var datasources = [];
        try {
            var eventPublisherConfigurationList = eventPublisherService.getAllActiveEventPublisherConfigurations();
            for (var i = 0; i < eventPublisherConfigurationList.size(); i++) {
                var eventPublisherConfiguration = eventPublisherService.getActiveEventPublisherConfiguration(
                    eventPublisherConfigurationList.get(i).getEventPublisherName());;

                var mappingTypeIsWso2 = eventPublisherConfiguration.getOutputMapping()
                    .getMappingType().equals(EventPublisherConstants.EF_WSO2EVENT_MAPPING_TYPE);

                var adapterType = null;
                if (eventPublisherConfiguration.getToAdapterConfiguration() != null) {
                    adapterType = eventPublisherConfiguration.getToAdapterConfiguration().getType();
                }
                if (mappingTypeIsWso2 && adapterType.trim() == "ui") {
                    var streamName = eventPublisherConfiguration.getFromStreamName();
                    var streamVersion = eventPublisherConfiguration.getFromStreamVersion();
                    var streamId = streamName + ":" + streamVersion;
                    datasources.push(streamId);
                }
            }
            var datasourceCfg = {
                "fieldLabel": "Event Stream",
                "fieldName": "streamName",
                "fieldType": "dropDown"
            };
            datasourceCfg['valueSet'] = datasources;
        } catch (e) {
            log.error(e);
        }
        formConfig.config.push(datasourceCfg);
        return formConfig;
    };

    /**
     * validate the user input of provider configuration
     * @param providerConfig
     */
    validate = function(providerConfig) {
        /*
         validate the form and return

         */
        return true;
    };

    /**
     * returns the data mode either push or pull
     */
    getMode = function() {
        return 'push';
    };

    /**
     * returns an array of column names & types
     * @param providerConfig
     */
    getSchema = function(providerConfig) {
        var streamId = providerConfig["streamName"];
        var output = [];

        output.push({
            fieldName: "TIMESTAMP",
            fieldType: "time"
        });

        if (eventStreamService != null) {
            var eventStreamConfiguration = eventStreamService.getEventStreamConfiguration(streamId);
            if (eventStreamConfiguration != null) {
                var metaData = eventStreamConfiguration.getStreamDefinition().getMetaData();
                var correlationData = eventStreamConfiguration.getStreamDefinition().getCorrelationData();
                var payloadData = eventStreamConfiguration.getStreamDefinition().getPayloadData();
                if (metaData != null) {
                    for (var i = 0; i < metaData.size(); i++) {
                        var type = metaData.get(i).getType().toString().toLowerCase();
                        output.push({
                            fieldName: metaData.get(i).getName(),
                            fieldType: typeMap[type.toLowerCase()]
                        });
                    }
                }
                if (correlationData != null) {
                    for (var i = 0; i < correlationData.size(); i++) {
                        var type = correlationData.get(i).getType().toString().toLowerCase();
                        output.push({
                            fieldName: correlationData.get(i).getName(),
                            fieldType: typeMap[type.toLowerCase()]
                        });
                    }
                }
                if (payloadData != null) {
                    for (var i = 0; i < payloadData.size(); i++) {
                        var type = payloadData.get(i).getType().toString().toLowerCase();
                        output.push({
                            fieldName: payloadData.get(i).getName(),
                            fieldType: typeMap[type.toLowerCase()]
                        });
                    }
                }
            }
        }
        return output;
    };

    getData = function(providerConfig,limit) {
      var data = [];
      return data;
    };


}());
