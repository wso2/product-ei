/*
 * Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.ei.businessprocess.utils.processcleanup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Class used to trigger the relevant SQL query according to the DB type
 */
public class DBQuery {
	private static final Log log = LogFactory.getLog(DBQuery.class);

	private String ODE_PARTNER_LINK;
	private String ODE_SCOPE;
	private String ODE_EVENT;
	private String ODE_CORSET_PROP;
	private String ODE_CORRELATION_SET;
	private String ODE_XML_DATA_PROP;
	private String ODE_XML_DATA;
	private String ODE_MEX_PROP;
	private String ODE_MESSAGE;
	private String ODE_MESSAGE_EXCHANGE;
	private String ODE_MESSAGE_ROUTE;
	private String GET_INSTANCES_IDS;
	private String SEARCH;
	private String ODE_PROCESS;
	private String ODE_PROCESS_INSTANCE;
	private String STORE_DU;
	private String STORE_PROCESS;

	DBQuery(String databaseURL, String bpsHome) {
		Properties prop = new Properties();

		try {
			String configPath = bpsHome + CleanupConstants.REPOSITORY + File.separator + CleanupConstants.CONF +
								File.separator + CleanupConstants.CLEANUP_PROPERTIES;
			prop.load(new FileInputStream(configPath));
		} catch (IOException e) {
			log.error("Configuration path error.", e);
			System.exit(1);
		}

		//for mysql, oracle and sqlserver the query is same
		if (databaseURL.contains(CleanupConstants.MY_SQL) ||
		    databaseURL.contains(CleanupConstants.ORACLE) ||
		    databaseURL.contains(CleanupConstants.SQL_SERVER) ||
		    databaseURL.contains(CleanupConstants.POSTGRE_SQL)) {

			SEARCH = "select distinct(s.PID) as PROCESS_ID, " +
			         "case when o.ID is null then \"-1\" else o.ID end as ID" +
			         ", s.VERSION, s.DU\n" +
			         "from STORE_PROCESS s left join ODE_PROCESS o\n" +
			         "on s.PID = o.PROCESS_ID\n" +
			         "where s.STATE != \"ACTIVE\" {0}\n" +
			         "and s.DU not in \n" +
			         "(select distinct(s.DU)\n" +
			         "from STORE_PROCESS s left join ODE_PROCESS o\n" +
			         "on s.PID = o.PROCESS_ID\n" +
			         "where s.STATE = \"ACTIVE\")";

			GET_INSTANCES_IDS = "select ID from ODE_PROCESS_INSTANCE where PROCESS_ID = \"{0}\"";

			ODE_EVENT = "DELETE FROM ODE_EVENT WHERE INSTANCE_ID = \"{0}\"";
			ODE_CORSET_PROP = "DELETE FROM ODE_CORSET_PROP WHERE CORRSET_ID IN " +
			                  "(SELECT cs.CORRELATION_SET_ID FROM ODE_CORRELATION_SET cs " +
			                  "WHERE cs.SCOPE_ID IN " +
			                  "(SELECT os.SCOPE_ID FROM ODE_SCOPE os WHERE os.PROCESS_INSTANCE_ID = \"{0}\"))";
			ODE_CORRELATION_SET = "DELETE FROM ODE_CORRELATION_SET WHERE SCOPE_ID IN " +
			                      "(SELECT os.SCOPE_ID FROM ODE_SCOPE os WHERE os.PROCESS_INSTANCE_ID = \"{0}\")";
			ODE_PARTNER_LINK = "DELETE FROM ODE_PARTNER_LINK WHERE SCOPE_ID IN " +
			                   "(SELECT os.SCOPE_ID FROM ODE_SCOPE os WHERE os.PROCESS_INSTANCE_ID = \"{0}\")";
			ODE_XML_DATA_PROP = "DELETE FROM ODE_XML_DATA_PROP WHERE XML_DATA_ID IN " +
			                    "(SELECT xd.XML_DATA_ID FROM ODE_XML_DATA xd WHERE xd.SCOPE_ID IN " +
			                    "(SELECT os.SCOPE_ID FROM ODE_SCOPE os WHERE os.PROCESS_INSTANCE_ID = \"{0}\"))";
			ODE_XML_DATA = "DELETE FROM ODE_XML_DATA WHERE SCOPE_ID IN " +
			               "(SELECT os.SCOPE_ID FROM ODE_SCOPE os WHERE os.PROCESS_INSTANCE_ID = \"{0}\")";
			ODE_SCOPE = "DELETE FROM ODE_SCOPE WHERE PROCESS_INSTANCE_ID = \"{0}\"";
			ODE_MEX_PROP = "DELETE FROM ODE_MEX_PROP WHERE MEX_ID IN " +
			               "(SELECT mex.MESSAGE_EXCHANGE_ID FROM ODE_MESSAGE_EXCHANGE mex WHERE mex.PROCESS_INSTANCE_ID = \"{0}\")";
			ODE_MESSAGE = "DELETE FROM ODE_MESSAGE WHERE MESSAGE_EXCHANGE_ID IN " +
			              "(SELECT mex.MESSAGE_EXCHANGE_ID FROM ODE_MESSAGE_EXCHANGE mex WHERE mex.PROCESS_INSTANCE_ID = \"{0}\")";
			ODE_MESSAGE_EXCHANGE =
					"DELETE FROM ODE_MESSAGE_EXCHANGE WHERE PROCESS_INSTANCE_ID = \"{0}\"";
			ODE_MESSAGE_ROUTE = "DELETE FROM ODE_MESSAGE_ROUTE WHERE PROCESS_INSTANCE_ID = \"{0}\"";
			ODE_PROCESS_INSTANCE = "DELETE FROM ODE_PROCESS_INSTANCE WHERE ID = \"{0}\"";

			ODE_PROCESS = "delete from ODE_PROCESS where id = \"{0}\"";

			STORE_DU = "delete from STORE_DU where NAME=\"{0}\"";
			STORE_PROCESS = "delete from STORE_PROCESS where DU=\"{0}\"";

		} else if (databaseURL.contains("h2")) {
			//todo: need to update the queries for H2 DB type
			System.out.println("H2 not yet supported.");
			System.exit(0);

		} else {
			System.out.println("Unsupported DB Type \n" + "or Invalid Driver Name!");
		}
	}

	/**
	 * formats the String with the process name and returns the delete query
	 * @param name process name
	 * @return a sql query
	 */
	public String deleteFromStoreProcess(String name) {
		String sql = MessageFormat.format(STORE_PROCESS, name);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the filter and returns the search query
	public String getSearchQuery(String filter) {
		String sql = MessageFormat.format(SEARCH, filter);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the filter and returns the search query
	public String getSearchByNameQuery(String filter, String name) {
		String sql =
				MessageFormat.format(SEARCH, filter).concat(" and s.DU like \"%" + name + "%\"");
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the process id and returns the delete query
	public String deleteFromOdeProcess(String id) {
		String sql = MessageFormat.format(ODE_PROCESS, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the process name and returns the delete query
	public String deleteFromStoreDu(String name) {
		String sql = MessageFormat.format(STORE_DU, name);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the process id and returns the search query
	public String getInstancesSearchQuery(String processID) {
		String sql = MessageFormat.format(GET_INSTANCES_IDS, processID);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeProcessInstance(String id) {
		String sql = MessageFormat.format(ODE_PROCESS_INSTANCE, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeScope(String id) {
		String sql = MessageFormat.format(ODE_SCOPE, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdePartnerLink(String id) {
		String sql = MessageFormat.format(ODE_PARTNER_LINK, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeEvent(String id) {
		String sql = MessageFormat.format(ODE_EVENT, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeCorsetProp(String id) {
		String sql = MessageFormat.format(ODE_CORSET_PROP, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeCorrelationSet(String id) {
		String sql = MessageFormat.format(ODE_CORRELATION_SET, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeXmlDataProp(String id) {
		String sql = MessageFormat.format(ODE_XML_DATA_PROP, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeXmlData(String id) {
		String sql = MessageFormat.format(ODE_XML_DATA, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeMexProp(String id) {
		String sql = MessageFormat.format(ODE_MEX_PROP, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeMessage(String id) {
		String sql = MessageFormat.format(ODE_MESSAGE, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeMessageExchange(String id) {
		String sql = MessageFormat.format(ODE_MESSAGE_EXCHANGE, id);
		return sql.replaceAll("\"", "'");
	}

	//formats the String with the id and returns the delete query
	public String deleteFromOdeMessageRoute(String id) {
		String sql = MessageFormat.format(ODE_MESSAGE_ROUTE, id);
		return sql.replaceAll("\"", "'");
	}
}
