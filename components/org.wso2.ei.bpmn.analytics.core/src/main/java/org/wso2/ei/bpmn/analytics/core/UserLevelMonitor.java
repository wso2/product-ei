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
import org.wso2.ei.bpmn.analytics.core.utils.BPMNAnalyticsCoreUtils;
import org.wso2.ei.bpmn.analytics.core.models.AggregateQuery;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * UserLevelMonitor holds all the functionalities for the user level monitoring
 * eg:  1.) Involvement of different users in a given process
 *      2.) Total involved time of different users with a given process
 */
public class UserLevelMonitor {
	private static final Log log = LogFactory.getLog(UserLevelMonitor.class);

	/**
	 * perform query: SELECT assignUser, SUM(duration) AS totalInvolvedTime FROM
	 *                USER_INVOLVE_SUMMARY WHERE <date range> GROUP BY assignUser;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getTotalInvolvedTimeVsUserId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int userCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField sumField = new AggregateField();
				sumField.setFieldName(BPMNAnalyticsCoreConstants.DURATION);
				sumField.setAggregate(BPMNAnalyticsCoreConstants.SUM);
				sumField.setAlias(BPMNAnalyticsCoreConstants.TOTAL_INVOLVED_TIME);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(sumField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.USER_INVOLVE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.ASSIGN_USER);
				if (from != 0 && to != 0) {
					query.setQuery(BPMNAnalyticsCoreUtils.getDateRangeQuery(
							BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to));
				}
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Total Involved Time Vs User Id Result:" +
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
						String assignee =
								values.getJSONArray(BPMNAnalyticsCoreConstants.ASSIGN_USER).getString(0);
						double totalInvolvedTime =
								values.getDouble(BPMNAnalyticsCoreConstants.TOTAL_INVOLVED_TIME);
						table.put(assignee, totalInvolvedTime);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getDoubleValueSortedList(table, BPMNAnalyticsCoreConstants.ASSIGN_USER,
							                          BPMNAnalyticsCoreConstants.TOTAL_INVOLVED_TIME, order,
							                          userCount);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Total Involved Time Vs UserId UserLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Total Involved Time Vs User Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT assignUser, COUNT(*) AS completedTotalTasks FROM
	 *                USER_INVOLVE_SUMMARY_DATA WHERE <date range> GROUP BY assignUser;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getTotalCompletedTasksVsUserId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int userCount = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.COMPLETED_TOTAL_TASKS);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.USER_INVOLVE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.ASSIGN_USER);
				if (from != 0 && to != 0) {
					query.setQuery(BPMNAnalyticsCoreUtils.getDateRangeQuery(
							BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to));
				}
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Total Completed Tasks Vs User Id Result:" +
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
						String assignee =
								values.getJSONArray(BPMNAnalyticsCoreConstants.ASSIGN_USER).getString(0);
						int totalInvolvedTime =
								values.getInt(BPMNAnalyticsCoreConstants.COMPLETED_TOTAL_TASKS);
						table.put(assignee, totalInvolvedTime);
					}
					sortedResult = BPMNAnalyticsCoreUtils
							.getIntegerValueSortedList(table, BPMNAnalyticsCoreConstants.ASSIGN_USER,
							                           BPMNAnalyticsCoreConstants.COMPLETED_TOTAL_TASKS,
							                           order, userCount);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Total Completed Tasks Vs UserId UserLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Total Completed Tasks Vs User Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT processDefKey, SUM(duration) AS totalInvolvedTime FROM
	 *                USER_INVOLVE_SUMMARY WHERE <assignee> AND <date range> GROUP BY processDefKey;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getTotalInvolvedTimeVsProcessId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				String userId = filterObj.getString(BPMNAnalyticsCoreConstants.USER_ID);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int count = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField sumField = new AggregateField();
				sumField.setFieldName(BPMNAnalyticsCoreConstants.DURATION);
				sumField.setAggregate(BPMNAnalyticsCoreConstants.SUM);
				sumField.setAlias(BPMNAnalyticsCoreConstants.TOTAL_INVOLVED_TIME);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(sumField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.USER_INVOLVE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY);
				String queryStr = "assignee:" + "\"'" + userId + "'\"";
				if (from != 0 && to != 0) {
					queryStr += " AND " + BPMNAnalyticsCoreUtils
							.getDateRangeQuery(BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to);
				}
				query.setQuery(queryStr);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get Total Involved Time Vs Process Id Result:" +
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
						double totalInvolvedTime =
								values.getDouble(BPMNAnalyticsCoreConstants.TOTAL_INVOLVED_TIME);
						table.put(processDefKey, totalInvolvedTime);
					}
					sortedResult = BPMNAnalyticsCoreUtils.getDoubleValueSortedList(table,
					                                                               BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY,
					                                                               BPMNAnalyticsCoreConstants.TOTAL_INVOLVED_TIME,
					                                                               order, count);
				}
			}
		} catch (JSONException | XMLStreamException | IOException e) {
			log.error("BPMN Analytics Core - Total Involved Time Vs ProcessId UserLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Total Involved Time Vs Process Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT processDefKey, COUNT(*) AS totalInstanceCount FROM
	 *                USER_INVOLVE_SUMMARY WHERE <assignee> AND <date range> GROUP BY
	 *                processDefKey;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getTotalInvolvedInstanceCountVsProcessId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				long from = filterObj.getLong(BPMNAnalyticsCoreConstants.START_TIME);
				long to = filterObj.getLong(BPMNAnalyticsCoreConstants.END_TIME);
				String userId = filterObj.getString(BPMNAnalyticsCoreConstants.USER_ID);
				String order = filterObj.getString(BPMNAnalyticsCoreConstants.ORDER);
				int count = filterObj.getInt(BPMNAnalyticsCoreConstants.NUM_COUNT);

				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.TOTAL_INSTANCE_COUNT);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.USER_INVOLVE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY);
				String queryStr = "assignee:" + "\"'" + userId + "'\"";
				if (from != 0 && to != 0) {
					queryStr += " AND " + BPMNAnalyticsCoreUtils
							.getDateRangeQuery(BPMNAnalyticsCoreConstants.COLUMN_FINISHED_TIME, from, to);
				}
				query.setQuery(queryStr);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug(
							"Query to get Total Involved Instance Count Vs Process Id Result:" +
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
						int instanceCount = values.getInt(BPMNAnalyticsCoreConstants.TOTAL_INSTANCE_COUNT);
						table.put(processDefKey, instanceCount);
					}
					sortedResult = BPMNAnalyticsCoreUtils.getIntegerValueSortedList(table,
					                                                                BPMNAnalyticsCoreConstants.PROCESS_DEFINITION_KEY,
					                                                                BPMNAnalyticsCoreConstants.TOTAL_INSTANCE_COUNT,
					                                                                order, count);
				}
			}
		} catch (JSONException | IOException | XMLStreamException e) {
			log.error("BPMN Analytics Core - Total Involved Instance Count Vs ProcessId UserLevelMonitoring error.", e);
		}

		if (log.isDebugEnabled()) {
			log.debug("Total Involved Instance Count Vs Process Id Result:" + sortedResult);
		}

		return sortedResult;
	}

	/**
	 * perform query: SELECT taskDefinitionKey, COUNT(taskInstanceId) AS taskInstanceCount FROM
	 *                USER_INVOLVE_SUMMARY_DATA WHERE <assignee> GROUP BY taskDefinitionKey;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getUserLevelTaskInstanceCountVsTaskId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				String userId = filterObj.getString(BPMNAnalyticsCoreConstants.USER_ID);
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
				String queryStr="assignee:" + "\"'" + userId + "'\"";
				queryStr += " AND " + "processDefKey:" + "\"'" + processId + "'\"";
				query.setQuery(queryStr);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get User Level Task Instance Count Vs Task Id Result:" +
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
		} catch (IOException | JSONException | XMLStreamException e) {
			log.error("BPMN Analytics Core - User Level Task Instance Count Vs Task Id UserLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("User Level Task Instance Count Vs Task Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * perform query: SELECT taskDefinitionKey, AVG(duration) AS avgExecutionTime FROM
	 *                USER_INVOLVE_SUMMARY_DATA WHERE <assignee> GROUP BY taskDefinitionKey;
	 *
	 * @param filters is used to filter the result
	 * @return the result as a JSON string
	 */
	public String getUserLevelAvgExecuteTimeVsTaskId(String filters) {
		String sortedResult = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				JSONObject filterObj = new JSONObject(filters);
				String processId = filterObj.getString(BPMNAnalyticsCoreConstants.PROCESS_ID);
				String userId = filterObj.getString(BPMNAnalyticsCoreConstants.USER_ID);
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
				String queryStr="assignee:" + "\"'" + userId + "'\"";
				queryStr += " AND " + "processDefKey:" + "\"'" + processId + "'\"";
				query.setQuery(queryStr);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get User Level Avg Execution Time Vs Task Id Result:" +
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
			log.error("BPMN Analytics Core - User Level Avg Execution Time Vs Task Id UserLevelMonitoring error.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("User Level Avg Execution Time Vs Task Id Result:" + sortedResult);
		}
		return sortedResult;
	}

	/**
	 * Get user id list
	 *
	 * @return user id list as a JSON array string
	 */
	public String getUserList() {
		String userIdList = "";
		try {
			if (BPMNAnalyticsCoreUtils.isDASAnalyticsActivated()) {
				AggregateField countField = new AggregateField();
				countField.setFieldName(BPMNAnalyticsCoreConstants.ALL);
				countField.setAggregate(BPMNAnalyticsCoreConstants.COUNT);
				countField.setAlias(BPMNAnalyticsCoreConstants.COMPLETED_TOTAL_TASKS);

				ArrayList<AggregateField> aggregateFields = new ArrayList<>();
				aggregateFields.add(countField);

				AggregateQuery query = new AggregateQuery();
				query.setTableName(BPMNAnalyticsCoreConstants.USER_INVOLVE_TABLE);
				query.setGroupByField(BPMNAnalyticsCoreConstants.ASSIGN_USER);
				query.setAggregateFields(aggregateFields);

				if (log.isDebugEnabled()) {
					log.debug("Query to get User List Result:" +
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
						String userId =
								values.getJSONArray(BPMNAnalyticsCoreConstants.ASSIGN_USER).getString(0);
						JSONObject o = new JSONObject();
						o.put(BPMNAnalyticsCoreConstants.ASSIGN_USER, userId);
						resultArray.put(o);
					}
					userIdList = resultArray.toString();
				}

				if (log.isDebugEnabled()) {
					log.debug("User List Result:" + userIdList);
				}
			}
		} catch (Exception e) {
			log.error("BPMN Analytics Core - user id list error.", e);
		}
		return userIdList;
	}
}
