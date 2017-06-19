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
import org.wso2.ei.bpmn.analytics.core.clients.BPMNAnalyticsCoreRestClient;
import org.wso2.ei.bpmn.analytics.core.models.AggregateField;
import org.wso2.ei.bpmn.analytics.core.models.AggregateQuery;
import org.wso2.ei.bpmn.analytics.core.utils.BPMNAnalyticsCoreUtils;
import org.wso2.ei.bpmn.analytics.core.models.SearchQuery;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * TaskLevelMonitor holds all the functionalites for the task level monitoring
 */
public class TaskLevelMonitor {
	private static final Log log = LogFactory.getLog(TaskLevelMonitor.class);

	/**
	 * perform query: SELECT taskDefinitionKey, AVG(duration) AS avgExecutionTime FROM
	 *                USER_INVOLVE_SUMMARY_DATA GROUP BY taskDefinitionKey;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getAvgExecuteTimeVsTaskId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int taskCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField avgField = new AggregateField();
				avgField.setFieldName(BPMNAnalyticsCoreConstants.DURATION);
				avgField.setAggregate(BPMNAnalyticsCoreConstants.AVG);
				avgField.setAlias(BPMNAnalyticsCoreConstants.AVG_EXECUTION_TIME);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(avgField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.USER_INVOLVE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY);
				query.setQuery("processDefinitionId:" + "\"'" + processId + "'\"");
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Avg Execution Time Vs Task Id Result:" +
					          BPMNAnalyticsCoreUtils.getJSONString(query));
				}

				String result = BPMNAnalyticsCoreRestClient
						.post(BPMNAnalyticsCoreUtils
								      .getURL(BPMNAnalyticsCoreConstants.ANALYTICS_AGGREGATE),
						      BPMNAnalyticsCoreUtils.getJSONString(query));

				JSONArray unsortedResultArray = new JSONArray(result);
				Hashtable<String, Double> table = new Hashtable<>();

				if (unsortedResultArray.length() != 0) {
					for (int i = 0; i < unsortedResultArray.length(); i++) {
						JSONObject jsonObj = unsortedResultArray.getJSONObject(i);
						JSONObject values = jsonObj.getJSONObject(BPMNAnalyticsCoreConstants.VALUES);
						String taskDefKey =
								values.getJSONArray(BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY)
								      .getString(0);
						double avgExecTime =
								values.getDouble(BPMNAnalyticsCoreConstants.AVG_EXECUTION_TIME);
						table.put(taskDefKey, avgExecTime);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getDoubleValueSortedList(table, BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY,
							                          BPMNAnalyticsCoreConstants.AVG_EXECUTION_TIME, order,
							                          taskCount);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Avg Execution Time Vs TaskId TaskLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Avg Execution Time Vs Task Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT taskDefinitionKey, COUNT(taskInstanceId) AS taskInstanceCount FROM
	 *                USER_INVOLVE_SUMMARY_DATA GROUP BY taskDefinitionKey;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getTaskInstanceCountVsTaskId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int taskCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.USER_INVOLVE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY);
				query.setQuery("processDefinitionId:" + "\"'" + processId + "'\"");
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Task Instance Count Vs Task Id Result:" +
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
								values.getJSONArray(BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY)
								      .getString(0);
						int processInstanceCount =
								values.getInt(BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT);
						table.put(processDefKey, processInstanceCount);
					}
					sortedResult = BPMNAnalyticsCoreUtils.getIntegerValueSortedList(table,
					                                                                BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY,
					                                                                BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT,
					                                                                order,
					                                                                taskCount);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Task Instance Count Vs TaskId TaskLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Task Instance Count Vs Task Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT assignee, COUNT(taskInstanceId) AS taskInstanceCount FROM
	 *                TASK_USAGE_SUMMARY GROUP BY assignee;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getTaskInstanceCountVsUserId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				String taskId = filterObj.getString(BPMNAnalyticsCoreConstants.TASK_ID);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int taskCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				String queryStr = "taskDefinitionKey:" + "\"'" + taskId + "'\" AND " +"processDefinitionId:" + "\"'" + processId+ "'\"";

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.TASK_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.ASSIGN_USER);
				query.setQuery(queryStr);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Task Instance Count Vs User Id Result:" +
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
						String userId =
								values.getJSONArray(BPMNAnalyticsCoreConstants.ASSIGN_USER).getString(0);
						int taskInstanceCount =
								values.getInt(BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT);
						table.put(userId, taskInstanceCount);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getIntegerValueSortedList(table, BPMNAnalyticsCoreConstants.ASSIGN_USER,
							                           BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT,
							                           order, taskCount);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Task Instance Count Vs UserId TaskLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Task Instance Count Vs User Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT assignee, AVG(duration) AS avgExecutionTime FROM
	 *                TASK_USAGE_SUMMARY GROUP BY assignee;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getAvgWaitingTimeVsUserId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				String taskId = filterObj.getString(BPMNAnalyticsCoreConstants.TASK_ID);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int taskCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField avgField = new AggregateField();
				avgField.setFieldName(BPMNAnalyticsCoreConstants.DURATION);
				avgField.setAggregate(BPMNAnalyticsCoreConstants.AVG);
				avgField.setAlias(BPMNAnalyticsCoreConstants.AVG_WAITING_TIME);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(avgField);

				String queryStr = "taskDefinitionKey:" + "\"'" + taskId + "'\" AND " +"processDefinitionId:" + "\"'" + processId+ "'\"";

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.TASK_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.ASSIGN_USER);
				query.setQuery(queryStr);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Avg Waiting Time Vs User Id Result:" +
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
						double avgExecTime = values.getInt(BPMNAnalyticsCoreConstants.AVG_WAITING_TIME);
						table.put(userId, avgExecTime);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getDoubleValueSortedList(table, BPMNAnalyticsCoreConstants.ASSIGN_USER,
							                          BPMNAnalyticsCoreConstants.AVG_WAITING_TIME, order,
							                          taskCount);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Avg Waiting Time Vs UserId TaskLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Avg Waiting Time Vs User Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT DISTINCT taskInstanceId, duration FROM TASK_USAGE_SUMMARY
	 *                WHERE <taskDefinitionKey> AND <date range>
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getExecutionTimeVsTaskInstanceId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				String taskId = filterObj.getString(BPMNAnalyticsCoreConstants.TASK_ID);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int limit = filterObj.getInt(BPMNAnalyticsCoreConstants.LIMIT);

				SearchQuery searchQuery = new SearchQuery();
				searchQuery.setTableName(BPMNAnalyticsCoreConstants.TASK_USAGE_TABLE);
				String queryStr = "taskDefinitionKey:" + "\"'" + taskId + "'\" AND " +"processDefinitionId:" + "\"'" + processId+ "'\"";
				if (from != 0 && to != 0) {
					queryStr += " AND " + BPMNAnalyticsCoreUtils
							.getDateRangeQuery(BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to);
				}
				searchQuery.setQuery(queryStr);
				searchQuery.setStart(BPMNAnalyticsCoreConstants.MIN_COUNT);
				searchQuery.setCount(BPMNAnalyticsCoreConstants.MAX_COUNT);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Execution Time Vs Task Instance Id Result:" +
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
								values.getString(BPMNAnalyticsCoreConstants.TASK_INSTANCE_ID);
						double executionTime = values.getDouble(BPMNAnalyticsCoreConstants.DURATION);
						table.put(processDefKey, executionTime);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getDoubleValueSortedList(table, BPMNAnalyticsCoreConstants.TASK_INSTANCE_ID,
							                          BPMNAnalyticsCoreConstants.DURATION, order, limit);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Execution Time Vs Task InstanceId TaskLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Execution Time Vs Task Instance Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * Perform query: SELECT DISTINCT finishTime, COUNT(*) AS taskInstanceCount FROM TASK_USAGE_SUMMARY
	 *                WHERE <date range> AND <task id list> GROUP BY finishTime
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getDateVsTaskInstanceCount(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				JSONArray taskIdList = filterObj.getJSONArray(BPMNAnalyticsCoreConstants.TASK_ID_LIST);

				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.TASK_USAGE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.FINISHED_TIME);
				String queryStr = BPMNAnalyticsCoreUtils
						.getDateRangeQuery(BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to);

				queryStr +="AND " +"processDefinitionId:" + "\"'" + processId+ "'\"";

				if (taskIdList.length() != 0) {
					queryStr += " AND ";
					for (int i = 0; i < taskIdList.length(); i++) {
						if (i == 0) {
							queryStr +=
									"(taskDefinitionKey:" + "\"'" + taskIdList.getString(i) + "'\"";
						} else {
							queryStr += " OR " + "taskDefinitionKey:" + "\"'" +
							            taskIdList.getString(i) + "'\"";
						}
						if (i == taskIdList.length() - 1) {
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
						long completedTime = Long.parseLong(
								values.getJSONArray(BPMNAnalyticsCoreConstants.FINISHED_TIME).getString(0));
						int taskInstanceCount =
								values.getInt(BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT);
						table.put(completedTime, taskInstanceCount);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getLongKeySortedList(table, BPMNAnalyticsCoreConstants.FINISHED_TIME,
							                      BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Date Vs Task Instance Count TaskLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Date Vs Task Instance Count Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * Get task definition key list
	 *
	 * @return task definition key list as a JSON array string
	 */
	public String getTaskList() {
		String taskIdList = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.USER_INVOLVE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get the Task List Result:" +
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
						String taskDefKey =
								values.getJSONArray(BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY)
								      .getString(0);
						JSONObject o = new JSONObject();
						o.put(BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY, taskDefKey);
						resultArray.put(o);
					}
					taskIdList = resultArray.toString();
				}

				if (log.isDebugEnabled()) {
					log.debug("Task List Result:" + taskIdList);
				}
			}
		} catch (Exception e) {
			log.error("BPMN Analytics Core - task id list error.", e);
		}
		return taskIdList;
	}

	/**
	 * Get task definition key list for a selected process id
	 *
	 * @param filters is used to get process id as a filter
	 * @return task definition key list as a JSON array string
     */
	public String getProcessRelatedTaskList(String filters) {
		String taskIdList = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.TASK_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				String queryStr = "processDefinitionId:" + "\"'" + processId+ "'\"";
				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.USER_INVOLVE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY);
				query.setAggregateFields(aggregateFields);
				query.setQuery(queryStr);

				if (log.isDebugEnabled()) {
					log.debug("Query to get the Task List Result:" +
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
						String taskDefKey =
								values.getJSONArray(BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY)
										.getString(0);
						JSONObject o = new JSONObject();
						o.put(BPMNAnalyticsCoreConstants.TASK_DEFINITION_KEY, taskDefKey);
						resultArray.put(o);
					}
					taskIdList = resultArray.toString();
				}

				if (log.isDebugEnabled()) {
					log.debug("Task List Result:" + taskIdList);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - task id list error.", e);
		}
		return taskIdList;
	}
}
