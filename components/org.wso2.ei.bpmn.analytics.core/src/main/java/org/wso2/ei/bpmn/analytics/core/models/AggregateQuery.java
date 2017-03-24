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
package org.wso2.ei.bpmn.analytics.core.models;

import java.util.ArrayList;

/**
 * AggregateQuery class is used to generate a query object
 */
public class AggregateQuery {
	private String tableName;
	private String groupByField;
	private String aggregateLevel;
	private String query;
	private ArrayList<AggregateField> aggregateFields;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getGroupByField() {
		return groupByField;
	}

	public void setGroupByField(String groupByField) {
		this.groupByField = groupByField;
	}

	public ArrayList<AggregateField> getAggregateFields() {
		return aggregateFields;
	}

	public void setAggregateFields(ArrayList<AggregateField> aggregateFields) {
		this.aggregateFields = aggregateFields;
	}

	public String getAggregateLevel() {
		return aggregateLevel;
	}

	public void setAggregateLevel(String aggregateLevel) {
		this.aggregateLevel = aggregateLevel;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
