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
 * This Javascript module exposes all the data analytics API as Javascript methods. This can be used
 * to develop custom webapps which use Analytics API.
 */

var DATA_TYPE_JSON = "json";
var CONTENT_TYPE_JSON = "application/json";
var AUTHORIZATION_HEADER = "Authorization";

function AnalyticsClient() {
    var TYPE_CLEAR_INDEX_DATA = 1;
    //var TYPE_CREATE_TABLE = 2;
    //var TYPE_DELETE_BY_ID = 3;
    //var TYPE_DELETE_BY_RANGE = 4;
    //var TYPE_DELETE_TABLE = 5;
    var TYPE_GET_RECORD_COUNT = 6;
    var TYPE_GET_BY_ID = 7;
    var TYPE_GET_BY_RANGE = 8;
    var TYPE_LIST_TABLES = 9;
    var TYPE_GET_SCHEMA = 10;
    //var TYPE_PUT_RECORDS = 11;
    //var TYPE_PUT_RECORDS_TO_TABLE = 12;
    var TYPE_SEARCH = 13;
    var TYPE_SEARCH_COUNT = 14;
    var TYPE_SET_SCHEMA = 15;
    var TYPE_TABLE_EXISTS = 16;
    var TYPE_WAIT_FOR_INDEXING = 17;
    var TYPE_PAGINATION_SUPPORTED = 18;
    var TYPE_DRILLDOWN_CATEGORIES = 19;
    var TYPE_DRILLDOWN_SEARCH = 20;
    var TYPE_DRILLDOWN_SEARCH_COUNT = 21;
    var TYPE_ADD_STREAM_DEFINITION = 22;
    var TYPE_GET_STREAM_DEFINITION = 23;
    var TYPE_PUBLISH_EVENTS = 24;
    var TYPE_GET_WITH_KEY_VALUES = 25;
    var TYPE_GET_RECORDSTORES = 26;
    var TYPE_GET_RECORDSTORE_BY_TABLE = 27;
    var TYPE_WAIT_FOR_INDEXING_FOR_TABLE = 28;
    var TYPE_SEARCH_WITH_AGGREGATES = 29;
    var HTTP_GET = "GET";
    var HTTP_POST = "POST";
    var RESPONSE_ELEMENT = "responseJSON";
    this.serverUrl = "";

    /**
     * Lists all the tables.
     * @param callback The callback functions which has one argument containing the response data.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.listTables = function (callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_LIST_TABLES,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Lists all the recordstores.
     * @param callback The callback functions which has one argument containing the response data.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.listRecordStores = function (callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_GET_RECORDSTORES,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * returns the recordstore to which the given table belongs.
     * @param callback The callback functions which has one argument containing the response data.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.getRecordStoreByTable = function (tableName, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_GET_RECORDSTORE_BY_TABLE + "&tableName=" + tableName,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Creates a table with a given name.
     * @param tableName The table name.
     * @param callback The callback function which has one argument containing the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    /*this.createTable = function (tableName, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_CREATE_TABLE + "&tableName=" + tableName,
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg);
                        }


                    });
    };*/

    /**
     * Check if the given table exists
     * @param tableName  table name
     * @param callback The callback function which has one argument containing the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.tableExists = function (tableName, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_TABLE_EXISTS + "&tableName=" + tableName,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Delete a table with a given name.
     * @param tableName The table name.
     * @param callback The callback function which has one argument containing the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    /*this.deleteTable = function (tableName, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_DELETE_TABLE + "&tableName=" + tableName,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg);
                        }
                    });
    };*/

    /**
     * Clears  all the indexed data for a specific table.
     * @param tableName The table name of which the index data to be removed.
     * @param callback The callback function which has one argument containing the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.clearIndexData = function (tableName, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_CLEAR_INDEX_DATA + "&tableName=" + tableName,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Gets the records given the table name and the timestamp range and pagination information.
     * @param rangeInfo Information containing the table name, range and pagination information.
     *  e.g. rangeInfo = {
     *          tableName : "TEST",
     *          timeFrom : 243243245354532,
     *          timeTo : 364654656435343,
     *          start : 0,
     *          count : 10,
     *          columns : [ "column1", "column2"]
     *      }
     * @param callback The callback function which has one argument containing the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.getRecordsByRange = function (rangeInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_GET_BY_RANGE + "&tableName=" + rangeInfo["tableName"] +
                             "&timeFrom=" + rangeInfo["timeFrom"] + "&timeTo=" + rangeInfo["timeTo"] +
                             "&start=" + rangeInfo["start"] + "&count=" + rangeInfo["count"],
                        data: JSON.stringify(rangeInfo["columns"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };


    /**
     * Gets the records given the table name and the matching primary key values batch.
     * @param recordInfo Information containing the table name, primary key values batch and columns interested.
     *  e.g. recordInfo = {
     *          tableName : "TEST",
     *          valueBatches : [
     *              {
     *               column1 : "value1",
     *               column2 : "value2"
     *              },
     *              {
     *              column1 : "anotherValue1",
     *              column2 : "anotherValue2"
     *              }
     *          ],
     *          columns : [ "column1"]
     *      }
     * @param callback The callback function which has one argument containing the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.getWithKeyValues = function (recordInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_GET_WITH_KEY_VALUES + "&tableName=" + recordInfo["tableName"],
                        data: JSON.stringify({
                                                valueBatches : recordInfo["valueBatches"],
                                                columns : recordInfo["columns"]
                                             }),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Gets the records given the record Ids.
     * @param recordsInfo The object which contains the record ids.
     *  e.g. recordsInfo = {
     *          tableName : "TEST",
     *          ids : [ "id1", "id2", "id3"]
     *      }
     * @param callback The callback function which has one argument containing the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.getRecordsByIds = function (recordsInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_GET_BY_ID + "&tableName=" + recordsInfo["tableName"],
                        data: JSON.stringify(recordsInfo["ids"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Returns the total record count.
     * @param tableName The table name
     * @param callback The callback function which has one argument containing the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.getRecordCount = function (tableName, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_GET_RECORD_COUNT + "&tableName=" + tableName,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Delete records by records ids.
     * @param recordsInfo The object which contains the record information.
     *  e.g. recordsInfo = {
     *          tableName : "TEST",
     *          ids : [ "id1", "id2", "id3"]
     *      }
     * @param callback The callback function which has one argument containing the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    /*this.deleteRecordsByIds = function (recordsInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_DELETE_BY_ID + "&tableName=" + recordsInfo["tableName"],
                        data: JSON.stringify(recordsInfo["ids"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg);
                        }
                    });
    };*/

    /**
     * Deletes the records given the time ranges.
     * @param rangeInfo The object information which contains the timestamp range.
     *  e.g rangeInfo = {
     *          tableName : "TEST",
     *          timeFrom : 12132143242422,
     *          timeTo : 3435353535335
     *      }
     * @param callback The callback function which has one argument containing the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    /*this.deleteRecordsByRange = function (rangeInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_DELETE_BY_RANGE + "&tableName=" + rangeInfo["tableName"]
                             + "&timeFrom=" + rangeInfo["timeFrom"] + "&timeTo=" + rangeInfo["timeTo"],
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg);
                        }
                    });
    };*/

    /**
     * Insert records ( tableName should be given for each record.
     * @param recordsInfo Records information containing the records array.
     *  e.g. recordsInfo = {
     *          records : [
     *              {
     *                  tableName : "TEST",
     *                  values : {
     *                      "field1" : "value1",
     *                      "field2" : "value2"
     *                  }
     *              },
     *              {
     *                  tableName : "TEST2",
     *                  values : {
     *                      "field1" : "value1",
     *                      "facetField" : [ "category", "subCategory", "subSubCategory" ]
     *                  }
     *              }
     *          ]
     * @param callback The callback function which has one argument containing the array of
     * ids of records inserted.
     * @param error The callback function which has one argument which contains the error if any
     */
    /*this.insertRecords = function (recordsInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_PUT_RECORDS,
                        data: JSON.stringify(recordsInfo["records"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg);
                        }
                    });
    };*/

    /**
     * Insert records to a specific table.
     * @param recordsInfo Records information containing the records array.
     *  e.g. recordsInfo = {
     *          tableName : "TEST",
     *          records : [
     *              {
     *                  values : {
     *                      "field1" : "value1",
     *                      "field2" : "value2"
     *                  }
     *              },
     *              {
     *                  values : {
     *                      "field1" : "value1",
     *                      "facetField" : [ "category", "subCategory", "subSubCategory" ]
     *                  }
     *              }
     *          ]
     * @param callback The callback function which has one argument containing the array of
     * ids of records inserted.
     * @param error The callback function which has one argument which contains the error if any
     */
    /*this.insertRecordsToTable = function (recordsInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_PUT_RECORDS_TO_TABLE + "&tableName=" +
                                                                                    recordsInfo["tableName"],
                        data: JSON.stringify(recordsInfo["records"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg);
                        }
                    });
    };*/

    /**
     * Search records in a given table using lucene queries.
     * @param queryInfo Query information which contains the table name and search parameters.
     *  e.g. queryInfo = {
     *          tableName : "TEST",
     *          searchParams : {
     *              query : "logFile : wso2carbon.log",
     *              start : 0,
     *              count : 10,
     *          }
     *      }
     * @param callback The callback function which has one argument containing the array of
     * matching records.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.searchCount = function (queryInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_SEARCH_COUNT + "&tableName=" + queryInfo["tableName"],
                        data: JSON.stringify(queryInfo["searchParams"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Returns the records in a given table using lucene queries.
     * @param queryInfo Query information which contains the table name and search parameters.
     *  e.g. queryInfo = {
     *          tableName : "TEST",
     *          searchParams : {
     *              query : "logFile : wso2carbon.log",
     *              start : 0,
     *              count : 10,
     *          }
     *      }
     * @param callback The callback function which has one argument containing the number of
     * matched records
     * @param error The callback function which has one argument which contains the error if any
     */
    this.search = function (queryInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_SEARCH + "&tableName=" + queryInfo["tableName"],
                        data: JSON.stringify(queryInfo["searchParams"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Returns the search count of records in a given table using lucene queries.
     * @param queryInfo Query information which contains the table name and search parameters.
     * e.g.
     * queryInfo = {
     * searchParams : {
            tableName:"TEST",
            groupByField:"single_valued_facet_field",
            aggregateFields:[
            {
                fieldName:"n",
                aggregate:"AVG",
                alias:avg"
            },
            {
                fieldName:"n",
                aggregate:"MAX",
                alias:"max"
            },
            {
                fieldName:"n",
                aggregate:"sum",
                alias:"sum"
            },
            {
                fieldName:"n",
                aggregate:"MIN",
                alias:"min"
            },
            {
                fieldName:"n",
                aggregate:"COUNT",
                alias:"count"
            }
            ]
         }
      }
     * @param callback The callback function which has one argument containing the number of
     * matched records
     * @param error The callback function which has one argument which contains the error if any
     */
    this.searchWithAggregates = function (queryInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_SEARCH_WITH_AGGREGATES + "&tableName=" + queryInfo["tableName"],
                        data: JSON.stringify(queryInfo["searchParams"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Sets the schema for a table.
     * @param schemaInfo The object which contains the schema information
     *  e.g. schemaInfo = {
     *          tableName : "TEST",
     *          schema : {
     *              columns : {
     *                  "column1" : {
     *                      "type" : "STRING",
     *                      "isIndex : true,
     *                      "isScoreParam" : false
     *                  }
     *              }
     *          }
     *      }
     * @param callback The callback function which has one argument containing the response message
     * @param error The callback function which has one argument which contains the error if any
     */
    this.setSchema = function (schemaInfo, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_SET_SCHEMA + "&tableName=" + schemaInfo["tableName"],
                        data: JSON.stringify(schemaInfo["schema"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg);
                        }
                    });
    };

    /**
     * Gets the schema of a table.
     * @param tableName the table name.
     * @param callback The callback function which has one argument containing the table schema.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.getSchema = function (tableName, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_GET_SCHEMA + "&tableName=" + tableName,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Returns if the underlying recordStore supports pagination.
     * @param callback The callback function which has one argument containing true/false.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.isPaginationSupported = function (recordStore, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_PAGINATION_SUPPORTED + "&recordStore=" + recordStore,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Waits till the indexing completes.
     * @param callback The callback function which has one argument which contains the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.waitForIndexing = function (callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_WAIT_FOR_INDEXING,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Waits till the indexing of a given completes.
     * @param tableName The tableName
     * @param callback The callback function which has one argument which contains the response message.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.waitForIndexing = function (tableName, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_WAIT_FOR_INDEXING + "&tableName=" + tableName,
                        type: HTTP_GET,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Returns the immediate sub categories of a facet field.
     * @param drilldownReq drilldown information.
     *  e.g. drillDownReq = {
     *          tableName : "TEST",
     *          drillDownInfo : {
     *              fieldName : "facetField1",
     *              categoryPath : [ "category", "subCategory"]
     *              query : "logFile : wso2carbon.log"
     *              scoreFunction : "sqrt(weight)"
     *          }
     *      }
     * @param callback The callback function which has one argument which contains the subcategories.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.drillDownCategories = function (drilldownReq, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_DRILLDOWN_CATEGORIES + "&tableName=" +
                                                                               drilldownReq["tableName"],
                        data: JSON.stringify(drilldownReq["drillDownInfo"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Returns the records which match the drill-down query given the table.
     * @param drillDownReq The object which contains the drillDown information.
     *  e.g. drillDownReq = {
     *          tableName : "TEST",
     *          drillDownInfo : {
     *              categories : [
     *               {
     *                  fieldName : "facetField1",
     *                  path : ["A", "B", "C"]
     *              },
     *              {
     *                  fieldName : "facetField2",
     *                  path : [ "X", "Y", "Z"]
     *              }]
     *              query : "field1 : value1",
     *              recordStart : 0,
     *              recordCount : 50,
     *              scoreFunction : "scoreParamField * 2"
     *          }
     * @param callback The callback function which has one argument which contains the matching records
     * @param error The callback function which has one argument which contains the error if any
     */
    this.drillDownSearch = function (drillDownReq, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_DRILLDOWN_SEARCH + "&tableName=" +
                                                                               drillDownReq["tableName"],
                        data: JSON.stringify(drillDownReq["drillDownInfo"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Returns number of the records which match the drill-down query given the table.
     * @param drillDownReq The object which contains the drillDown information.
     *  e.g. drillDownReq = {
     *          tableName : "TEST",
     *          drillDownInfo : {
     *              categories : [
     *               {
     *                  fieldName : "facetField1",
     *                  path : ["A", "B", "C"]
     *              },
     *              {
     *                  fieldName : "facetField2",
     *                  path : [ "X", "Y", "Z"]
     *              }]
     *              query : "field1 : value1",
     *              recordStart : 0,
     *              recordCount : 50,
     *              scoreFunction : "scoreParamField * 2"
     *          }
     * @param callback The callback function which has one argument which contains the count.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.drillDownSearchCount = function (drillDownReq, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_DRILLDOWN_SEARCH_COUNT + "&tableName="+
                                                                                  drillDownReq["tableName"],
                        data: JSON.stringify(drillDownReq["drillDownInfo"]),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Create a stream definition for.a event stream
     * @param streamDef The object which contains the stream definition.
     *  e.g. streamDef = {
     *          name : "TEST",
     *          version : "1.0.0",
     *          nickName : "test",
     *          description : "sample description"
     *          payloadData : {
     *              name : "STRING",
     *              married : "BOOLEAN",
     *              age : "INTEGER"
     *          },
     *          metaData : {
     *              timestamp : "LONG"
     *          },
     *          correlationData : {
     *
     *          },
     *          tags : []
     *      }
     * @param callback The callback function which has one argument which contains stream id.
     * @param error The callback function which has one argument which contains the error if any
     */
    this.addStreamDefinition = function (streamDef, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_ADD_STREAM_DEFINITION,
                        data: JSON.stringify(streamDef),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Publishes an event to a given stream.
     * @param event object containing event data.
     * e.g. eventData = {
     *          streamName : "TEST",
     *          streamVersion : "1.0.0",
     *          timestamp : 54326543254532, "optional"
     *          payloadData : {
     *          },
     *          metaData : {
     *          },
     *          correlationData : {
     *          },
     *          arbitraryDataMap : {
     *          }
     *      }
     * @param callback callback The callback function which has one argument which contains the response.
     * @param error The callback function which has one argument which contains the error if any.
     */
    this.publishEvent = function (event, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_PUBLISH_EVENTS,
                        data: JSON.stringify(event),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    };

    /**
     * Get the stream definition for a event stream.
     * @param requestData request containing the data of the event stream.
     *      e.g. eventStreamInfo = {
     *              name : "TEST",
     *              version" "1.0.0"
     *          }
     * @param callback callback The callback function which has one argument which contains the stream definition.
     * @param error error The callback function which has one argument which contains the error if any
     */
    this.getStreamDefinition = function (requestData, callback, error) {
        jQuery.ajax({
                        url: this.serverUrl + "?type=" + TYPE_GET_STREAM_DEFINITION,
                        data: JSON.stringify(requestData),
                        type: HTTP_POST,
                        success: function (data) {
                            callback(data);
                        },
                        error: function (msg) {
                            error(msg[RESPONSE_ELEMENT]);
                        }
                    });
    }
}

/**
 * Construct an AnalyticsClient object given the username, password and serverUrl.
 * @param username the username
 * @param password the password
 * @param svrUrl the server url
 * @returns {AnalyticsClient} AnalyticsClient object
 */
AnalyticsClient.prototype.init = function (username, password, svrUrl) {
    if (svrUrl == null) {
        this.serverUrl = "https://localhost:9443/portal/controllers/apis/analytics.jag";
    } else {
        this.serverUrl = svrUrl;
    }
    var authHeader = generateBasicAuthHeader(username, password);
    jQuery.ajaxSetup({
                         dataType: DATA_TYPE_JSON,
                         contentType: CONTENT_TYPE_JSON,
                         beforeSend: function (request) {
                             if (authHeader != null) {
                                 request.setRequestHeader(AUTHORIZATION_HEADER, authHeader);
                             }
                         }
                     });
    return this;
};

function generateBasicAuthHeader(username, password) {
    if (username != null && password != null) {
        return "Basic " + btoa(username + ":" + password);
    }
    return null;
}
