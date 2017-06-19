/*
 *     Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */
package org.wso2.ei.bpmn.analytics.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.wso2.ei.bpmn.analytics.core.clients.BPMNAnalyticsCoreRestClient;
import org.wso2.ei.bpmn.analytics.core.models.AggregateField;
import org.wso2.ei.bpmn.analytics.core.models.AggregateQuery;
import org.wso2.ei.bpmn.analytics.core.models.SearchQuery;
import org.wso2.ei.bpmn.analytics.core.utils.BPMNAnalyticsCoreUtils;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * ProcessLevelMonitor holds all the functionalites for the process level monitoring
 */
public class ProcessLevelMonitor {
	private static final Log log = LogFactory.getLog(ProcessLevelMonitor.class);
	/**
	 * perform query: SELECT assignUser, COUNT(*) AS completedTotalTasks FROM
	 *                TASK_USAGE_SUMMARY_DATA WHERE <process id> AND <date range> GROUP BY assignUser;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getTotalCompletedTasksVsUserIdForProcess(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int userCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.COMPLETED_TOTAL_TASKS);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.TASK_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.ASSIGN_USER);

				String queryStr = "processDefinitionId:" + "\"'" + processId + "'\"";
				if (from != 0 && to != 0) {
					queryStr += " AND " + BPMNAnalyticsCoreUtils
							.getDateRangeQuery(BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to);
				}
				query.setQuery(queryStr);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Total Completed Tasks Vs User Id for process: " + processId + " | Result:"
							+ BPMNAnalyticsCoreUtils.getJSONString(query));
				}

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils.getURL(BPMNAnalyticsCoreConstants.ANALYTICS_AGGREGATE),
								BPMNAnalyticsCoreUtils.getJSONString(query));

				JSONArray unsortedResultArray = new JSONArray(result);
				Hashtable<String, Integer> table = new Hashtable<>();

				if (unsortedResultArray.length() != 0) {
					for (int i = 0; i < unsortedResultArray.length(); i++) {
						JSONObject jsonObj = unsortedResultArray.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						String assignee = values.getJSONArray(BPMNAnalyticsCoreConstants.ASSIGN_USER).getString(0);
						int totalInvolvedTasks = values.getInt(BPMNAnalyticsCoreConstants.COMPLETED_TOTAL_TASKS);
						table.put(assignee, totalInvolvedTasks);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getIntegerValueSortedList(table, BPMNAnalyticsCoreConstants.ASSIGN_USER,
									BPMNAnalyticsCoreConstants.COMPLETED_TOTAL_TASKS, order, userCount);
				}
			}

		} catch (XMLStreamException | JSONException | IOException e) {
			log.error("BPMN Analytics Core - Total Completed Tasks Vs UserId UserLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Total Completed Tasks Vs User Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT assignee, SUM(duration) AS totalExecutionTime FROM
	 *                TASK_USAGE_SUMMARY where <processId> GROUP BY assignee;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getTotalTimeVsUserIdForProcess(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int taskCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField avgField = new AggregateField();
				avgField.setFieldName(BPMNAnalyticsCoreConstants.DURATION);
				avgField.setAggregate(BPMNAnalyticsCoreConstants.SUM);
				avgField.setAlias(BPMNAnalyticsCoreConstants.TOTAL_INVOLVED_TIME);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(avgField);

				String queryStr = "processDefinitionId:" + "\"'" + processId+ "'\"";

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.TASK_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.ASSIGN_USER);
				query.setQuery(queryStr);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get total execution time Vs User Id for process. | Result:" +
							BPMNAnalyticsCoreUtils.getJSONString(query));
				}

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils.getURL(BPMNAnalyticsCoreConstants.ANALYTICS_AGGREGATE),
						      BPMNAnalyticsCoreUtils.getJSONString(query));

				JSONArray unsortedResultArray = new JSONArray(result);
				Hashtable<String, Double> table = new Hashtable<>();

				if (unsortedResultArray.length() != 0) {
					for (int i = 0; i < unsortedResultArray.length(); i++) {
						JSONObject jsonObj = unsortedResultArray.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						String userId =
								values.getJSONArray(BPMNAnalyticsCoreConstants.ASSIGN_USER).getString(0);
						double totalExecTime = values.getInt(BPMNAnalyticsCoreConstants.TOTAL_INVOLVED_TIME);
						table.put(userId, totalExecTime);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getDoubleValueSortedList(table, BPMNAnalyticsCoreConstants.ASSIGN_USER,
							                          BPMNAnalyticsCoreConstants.TOTAL_INVOLVED_TIME, order,
							                          taskCount);
				}
			}
		} catch (JSONException | XMLStreamException | IOException e) {
			log.error("BPMN Analytics Core - total execution time Vs UserId ProcessLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Total Waiting Time Vs User Id for process. Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT processDefinitionId, AVG(duration) AS avgExecutionTime FROM
	 *                PROCESS_USAGE_SUMMARY WHERE <date range> GROUP BY processDefinitionId;
	 *
	 * @param filters is a given date range represents as the JSON string
	 * @return the result as a JSON string
	 */
	public String getAvgExecuteTimeVsProcessId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int processCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField avgField = new AggregateField();
				avgField.setFieldName(BPMNAnalyticsCoreConstants.DURATION);
				avgField.setAggregate(BPMNAnalyticsCoreConstants.AVG);
				avgField.setAlias(BPMNAnalyticsCoreConstants.AVG_EXECUTION_TIME);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(avgField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.PROCESS_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY);
				if (from != 0 && to != 0) {
					query.setQuery(BPMNAnalyticsCoreUtils.getDateRangeQuery(
							BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to));
				}
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Avg Execution Time Vs ProcessId Result:" +
					          BPMNAnalyticsCoreUtils.getJSONString(query));
				}

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils.getURL(BPMNAnalyticsCoreConstants.ANALYTICS_AGGREGATE),
						      BPMNAnalyticsCoreUtils.getJSONString(query));

				JSONArray unsortedResultArray = new JSONArray(result);
				Hashtable<String, Double> table = new Hashtable<>();

				if (unsortedResultArray.length() != 0) {
					for (int i = 0; i < unsortedResultArray.length(); i++) {
						JSONObject jsonObj = unsortedResultArray.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						String processDefKey =
								values.getJSONArray(BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY)
								      .getString(0);
						double avgExecTime =
								values.getDouble(BPMNAnalyticsCoreConstants.AVG_EXECUTION_TIME);
						table.put(processDefKey, avgExecTime);
					}
					sortedResult = BPMNAnalyticsCoreUtils.getDoubleValueSortedList(table,
					                                                               BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY,
					                                                               BPMNAnalyticsCoreConstants.AVG_EXECUTION_TIME,
					                                                               order,
					                                                               processCount);
				}
			}
		} catch (JSONException | XMLStreamException | IOException e) {
			log.error("BPMN Analytics Core - Avg Execution Time Vs ProcessId ProcessLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Avg Execution Time Vs ProcessId Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT processDefinitionId, COUNT(processInstanceId) AS processInstanceCount
	 *                FROM PROCESS_USAGE_SUMMARY WHERE <date range> GROUP BY processDefinitionId;
	 *
	 * @param filters is a given date range represents as the JSON string
	 * @return the result as a JSON string
	 */
	public String getProcessInstanceCountVsProcessId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int processCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.PROCESS_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY);
				if (from != 0 && to != 0) {
					query.setQuery(BPMNAnalyticsCoreUtils.getDateRangeQuery(
							BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to));
				}
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Process Instance Count Vs ProcessId Result:" +
					          BPMNAnalyticsCoreUtils.getJSONString(query));
				}

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils.getURL(BPMNAnalyticsCoreConstants.ANALYTICS_AGGREGATE),
						      BPMNAnalyticsCoreUtils.getJSONString(query));

				JSONArray unsortedResultArray = new JSONArray(result);
				Hashtable<String, Integer> table = new Hashtable<>();

				if (unsortedResultArray.length() != 0) {
					for (int i = 0; i < unsortedResultArray.length(); i++) {
						JSONObject jsonObj = unsortedResultArray.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						String processDefKey =
								values.getJSONArray(BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY)
								      .getString(0);
						int processInstanceCount =
								values.getInt(BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT);
						table.put(processDefKey, processInstanceCount);
					}
					sortedResult = BPMNAnalyticsCoreUtils.getIntegerValueSortedList(table,
					                                                                BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY,
					                                                                BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT,
					                                                                order,
					                                                                processCount);
				}
			}
		} catch (JSONException | XMLStreamException | IOException e) {
			log.error("BPMN Analytics Core - Process Instance Count Vs ProcessId ProcessLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Process Instance Count Vs ProcessId Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT processVersion, AVG(duration) AS avgExecutionTime FROM
	 *                PROCESS_USAGE_SUMMARY WHERE <date range> AND <processId> GROUP BY
	 *                processVersion;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getAvgExecuteTimeVsProcessVersion(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				String processKey = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_KEY);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int processCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField avgField = new AggregateField();
				avgField.setFieldName(BPMNAnalyticsCoreConstants.DURATION);
				avgField.setAggregate(BPMNAnalyticsCoreConstants.AVG);
				avgField.setAlias(BPMNAnalyticsCoreConstants.AVG_EXECUTION_TIME);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(avgField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.PROCESS_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.PROCESS_VERSION);
				query.setQuery("processKeyName:" + "\"'" + processKey + "'\"");
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Avg Execution Time Vs Process Version Result:" +
					          BPMNAnalyticsCoreUtils.getJSONString(query));
				}

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils.getURL(BPMNAnalyticsCoreConstants.ANALYTICS_AGGREGATE),
						      BPMNAnalyticsCoreUtils.getJSONString(query));

				JSONArray unsortedResultArray = new JSONArray(result);
				Hashtable<String, Double> table = new Hashtable<>();

				if (unsortedResultArray.length() != 0) {
					for (int i = 0; i < unsortedResultArray.length(); i++) {
						JSONObject jsonObj = unsortedResultArray.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						String processVersion =
								values.getJSONArray(BPMNAnalyticsCoreConstants.PROCESS_VERSION)
								      .getString(0);
						double avgExecTime =
								values.getDouble(BPMNAnalyticsCoreConstants.AVG_EXECUTION_TIME);
						table.put(processVersion, avgExecTime);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getDoubleValueSortedList(table, BPMNAnalyticsCoreConstants.PROCESS_VERSION,
							                          BPMNAnalyticsCoreConstants.AVG_EXECUTION_TIME, order,
							                          processCount);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Avg Execution Time Vs Process Version ProcessLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Avg Execution Time Vs Process Version Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT processVersion, COUNT(processInstanceId) AS processInstanceCount
	 *                FROM PROCESS_USAGE_SUMMARY GROUP BY processVersion;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getProcessInstanceCountVsProcessVersion(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				String processKey = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_KEY);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int processCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.PROCESS_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.PROCESS_VERSION);
				query.setQuery("processKeyName:" + "\"'" + processKey + "'\"");
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Process Instance Count Vs Process Version Result:" +
					          BPMNAnalyticsCoreUtils.getJSONString(query));
				}

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils.getURL(BPMNAnalyticsCoreConstants.ANALYTICS_AGGREGATE),
						      BPMNAnalyticsCoreUtils.getJSONString(query));

				JSONArray unsortedResultArray = new JSONArray(result);
				Hashtable<String, Integer> table = new Hashtable<>();

				if (unsortedResultArray.length() != 0) {
					for (int i = 0; i < unsortedResultArray.length(); i++) {
						JSONObject jsonObj = unsortedResultArray.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						String processVersion =
								values.getJSONArray(BPMNAnalyticsCoreConstants.PROCESS_VERSION)
								      .getString(0);
						int processInstanceCount =
								values.getInt(BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT);
						table.put(processVersion, processInstanceCount);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getIntegerValueSortedList(table, BPMNAnalyticsCoreConstants.PROCESS_VERSION,
							                           BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT,
							                           order, processCount);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Process Instance Count Vs Process Version ProcessLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Process Instance Count Vs Process Version Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT DISTINCT processInstanceId, duration FROM PROCESS_USAGE_SUMMARY
	 *                WHERE <processId> AND <date range>
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getExecutionTimeVsProcessInstanceId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int limit = filterObj.getInt(BPMNAnalyticsCoreConstants.LIMIT);

				SearchQuery searchQuery = new SearchQuery();
				searchQuery.setTableName(BPMNAnalyticsCoreConstants.PROCESS_USAGE_TABLE);
				String queryStr = "processDefinitionId:" + "\"'" + processId + "'\"";
				if (from != 0 && to != 0) {
					queryStr += " AND " + BPMNAnalyticsCoreUtils
							.getDateRangeQuery(BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to);
				}
				searchQuery.setQuery(queryStr);
				searchQuery.setStart(BPMNAnalyticsCoreConstants.MIN_COUNT);
				searchQuery.setCount(BPMNAnalyticsCoreConstants.MAX_COUNT);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Execution Time Vs Process Instance Id Result:" +
					          BPMNAnalyticsCoreUtils.getJSONString(searchQuery));
				}

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils.getURL(BPMNAnalyticsCoreConstants.ANALYTICS_SEARCH),
						      BPMNAnalyticsCoreUtils.getJSONString(searchQuery));

				JSONArray unsortedResultArray = new JSONArray(result);
				Hashtable<String, Double> table = new Hashtable<>();

				if (unsortedResultArray.length() != 0) {
					for (int i = 0; i < unsortedResultArray.length(); i++) {
						JSONObject jsonObj = unsortedResultArray.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						String processDefKey =
								values.getString(BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_ID);
						double executionTime = values.getDouble(BPMNAnalyticsCoreConstants.DURATION);
						table.put(processDefKey, executionTime);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getDoubleValueSortedList(table, BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_ID,
							                          BPMNAnalyticsCoreConstants.DURATION, order, limit);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Execution Time Vs Process InstanceId ProcessLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Execution Time Vs Process Instance Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * Perform query: SELECT DISTINCT finishTime, COUNT(*) AS processInstanceCount FROM
	 *                PROCESS_USAGE_SUMMARY WHERE <date range> AND <process id list>
	 *                GROUP BY finishTime
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getDateVsProcessInstanceCount(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				boolean aggregateByMonth = filterObj.getBoolean(BPMNAnalyticsCoreConstants.AGGREGATE_BY_MONTH);
				JSONArray processIdList =
						filterObj.getJSONArray(BPMNAnalyticsCoreConstants.PROCESS_ID_LIST);

				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.PROCESS_USAGE_TABLE);
				if(aggregateByMonth) {
					query.setGroupByField(BPMNAnalyticsCoreConstants.MONTH);
				} else {
					query.setGroupByField(BPMNAnalyticsCoreConstants.FINISHED_TIME);
				}
				String queryStr = BPMNAnalyticsCoreUtils
						.getDateRangeQuery(BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to);

				if (processIdList.length() != 0) {
					queryStr += " AND ";
					for (int i = 0; i < processIdList.length(); i++) {
						if (i == 0) {
							queryStr +=
									"(processDefinitionId:" + "\"'" + processIdList.getString(i) +
									"'\"";
						} else {
							queryStr += " OR " + "processDefinitionId:" + "\"'" +
							            processIdList.getString(i) + "'\"";
						}
						if (i == processIdList.length() - 1) {
							queryStr += ")";
						}
					}
				}
				query.setQuery(queryStr);
				query.setAggregateFields(aggregateFields);

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils.getURL(BPMNAnalyticsCoreConstants.ANALYTICS_AGGREGATE),
						      BPMNAnalyticsCoreUtils.getJSONString(query));

				JSONArray unsortedResultArray = new JSONArray(result);
				Hashtable<Long, Integer> table = new Hashtable<>();

				if (unsortedResultArray.length() != 0) {
					for (int i = 0; i < unsortedResultArray.length(); i++) {
						JSONObject jsonObj = unsortedResultArray.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						long completedTime;
						if(aggregateByMonth) {
							completedTime = Long.parseLong(
									values.getJSONArray(BPMNAnalyticsCoreConstants.MONTH).getString(0));
						} else {
							completedTime = Long.parseLong(
									values.getJSONArray(BPMNAnalyticsCoreConstants.FINISHED_TIME)
									      .getString(0));
						}
						int processInstanceCount =
								values.getInt(BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT);
						table.put(completedTime, processInstanceCount);
					}
					if(aggregateByMonth) {
						sortedResult = BPMNAnalyticsCoreUtils
								.getLongKeySortedList(table, BPMNAnalyticsCoreConstants.MONTH,
								                      BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT);
					} else {
						sortedResult = BPMNAnalyticsCoreUtils
								.getLongKeySortedList(table, BPMNAnalyticsCoreConstants.FINISHED_TIME,
								                      BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT);
					}
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Date Vs Process Instance Count ProcessLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Date Vs Process Instance Count Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * Get process definition key list
	 *
	 * @return process definition key list as a JSON array string
	 */
	public String getProcessIdList() {
		String processIdList = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				AggregateField avgField = new AggregateField();
				avgField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				avgField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				avgField.setAlias(BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(avgField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.PROCESS_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get the Process Id List Result:" +
					          BPMNAnalyticsCoreUtils.getJSONString(query));
				}

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils.getURL(BPMNAnalyticsCoreConstants.ANALYTICS_AGGREGATE),
						      BPMNAnalyticsCoreUtils.getJSONString(query));

				JSONArray array = new JSONArray(result);
				JSONArray resultArray = new JSONArray();

				if (array.length() != 0) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonObj = array.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						String processDefKey =
								values.getJSONArray(BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY)
								      .getString(0);
						JSONObject o = new JSONObject();
						o.put(BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY, processDefKey);
						resultArray.put(o);
					}
					processIdList = resultArray.toString();
				}

				if (log.isDebugEnabled()) {
					log.debug("Process Id List Result:" + processIdList);
				}
			}
		} catch (Exception e) {
			log.error("BPMN Analytics Core - process id list error.", e);
		}
		return processIdList;
	}

	/**
	 * Get process key list
	 *
	 * @return process key list as a JSON array string
	 */
	public String getProcessKeyList() {
		String processKeyList = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				AggregateField avgField = new AggregateField();
				avgField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				avgField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				avgField.setAlias(BPMNAnalyticsCoreConstants.PROCESS_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(avgField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.PROCESS_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.PROCESS_KEY);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Process Key List Result:" +
					          BPMNAnalyticsCoreUtils.getJSONString(query));
				}

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils.getURL(BPMNAnalyticsCoreConstants.ANALYTICS_AGGREGATE),
						      BPMNAnalyticsCoreUtils.getJSONString(query));

				JSONArray array = new JSONArray(result);
				JSONArray resultArray = new JSONArray();

				if (array.length() != 0) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonObj = array.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						String processKey =
								values.getJSONArray(BPMNAnalyticsCoreConstants.PROCESS_KEY)
								      .getString(0);
						JSONObject o = new JSONObject();
						o.put(BPMNAnalyticsCoreConstants.PROCESS_KEY, processKey);
						resultArray.put(o);
					}
					processKeyList = resultArray.toString();
				}

				if (log.isDebugEnabled()) {
					log.debug("Process Key List Result:" + processKeyList);
				}
			}
		} catch (Exception e) {
			log.error("BPMN Analytics Core - process key list error.", e);
		}
		return processKeyList;
	}
}
