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
package org.wso2.ei.bps.analytics.core.bpmn.utils;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.ei.bps.analytics.core.bpmn.BPMNAnalyticsCoreConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;

import static org.wso2.ei.bps.analytics.core.bpmn.internal.BPMNAnalyticsCoreServerHolder.getInstance;

/**
 * BPMNAnalyticsCoreUtils class is used to keep= the functions which are useful for the monitor classes.
 */
public class BPMNAnalyticsCoreUtils {
    private static final Log log = LogFactory.getLog(BPMNAnalyticsCoreUtils.class);

    // Make the constructor private, since it is a utility class
    private BPMNAnalyticsCoreUtils() {
    }

    /**
     * Build the lucene query format for the date range
     *
     * @param columnName to hold the column name for the task finishedTime in the table
     * @param from       is the long value of a given date
     * @param to         is the long value of a given date
     * @return a query string in the format of lucene
     */
    public static String getDateRangeQuery(String columnName, long from, long to) {
        return columnName + " : [" + from + " TO " + to + "]";
    }

    /**
     * Convert query object as a JSON String
     *
     * @param query to hold the query object
     * @return a JSON String
     */
    public static String getJSONString(Object query) {
        return new Gson().toJson(query);
    }

    /**
     * Round a double value to two decimal points
     *
     * @param value  double value
     * @param places number of decimals
     * @return rounded decimal value
     */
    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    /**
     * Check BPMN Analytics configured or not
     *
     * @return true if the BPMN Analytics URL is configured
     */
    public static boolean isDASAnalyticsActivated() {
        return getInstance().getBPMNAnalyticsCoreServer().getBPSAnalyticsConfiguration().isAnalyticsDashboardEnabled();
    }

    /**
     * Build and return the DAS rest API urls
     *
     * @param path hold the relative path to a particular webservice
     * @return the base url of DAS
     */
    public static String getURL(String path) {
        return getInstance().getBPMNAnalyticsCoreServer().getBPSAnalyticsConfiguration().getAnalyticsServerURL() + "/" + path;
    }

    /**
     * Get authorization header
     *
     * @return encoded auth header
     */
    public static String getAuthorizationHeader() {
        String userName = getInstance().getBPMNAnalyticsCoreServer().getBPSAnalyticsConfiguration().getAnalyticsServerUsername();
        String password = getInstance().getBPMNAnalyticsCoreServer().getBPSAnalyticsConfiguration().getAnalyticsServerPassword();
        if (userName != null && password != null) {
            return BPMNAnalyticsCoreConstants.AUTH_BASIC_HEADER + " " + DatatypeConverter
                    .printBase64Binary((userName + ":" + password).getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    /**
     * Get sorted list (sort by double type values)
     *
     * @param table is a hash table to keep the result as key-value pairs
     * @param key1  is the name for the first value of the JSON object
     * @param key2  is the name for the second value for the JSON object
     * @param order is to get the top or bottom results
     * @param count is to limit the number of results
     * @return a sorted list as a JSON array string
     * @throws JSONException
     */
    public static String getDoubleValueSortedList(Hashtable<String, Double> table, String key1,
                                                  String key2, String order, int count)
            throws JSONException {
        //Transfer as List and sort it
        ArrayList<Map.Entry<String, Double>> l = new ArrayList(table.entrySet());
        Collections.sort(l, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Collections.reverse(l);
        JSONArray array = new JSONArray();
        for (int i = 0; i < l.size(); i++) {
            JSONObject o = new JSONObject();
            o.put(key1, l.get(i).getKey());
            o.put(key2, round(l.get(i).getValue(), 2));
            array.put(o);
        }

        //if count exceeds the array length, then assign the array length to the count variable
        if (count > array.length()) {
            count = array.length();
        }

        JSONArray arrayPortion = new JSONArray();
        if (order.equalsIgnoreCase(BPMNAnalyticsCoreConstants.TOP)) {
            for (int i = array.length() - count; i < array.length(); i++) {
                arrayPortion.put(array.get(i));
            }
        } else if (order.equalsIgnoreCase(BPMNAnalyticsCoreConstants.BOTTOM)) {
            for (int i = 0; i < count; i++) {
                arrayPortion.put(array.get(i));
            }
        }
        return arrayPortion.toString();
    }

    /**
     * Get sorted list (sort by int type values)
     *
     * @param table is a hash table to keep the result as key-value pairs
     * @param key1  is the name for the first value of the JSON object
     * @param key2  is the name for the second value for the JSON object
     * @param order is to get the top or bottom results
     * @param count is to limit the number of results
     * @return a sorted list as a JSON array string
     * @throws JSONException
     */
    public static String getIntegerValueSortedList(Hashtable<String, Integer> table, String key1,
                                                   String key2, String order, int count)
            throws JSONException {
        ArrayList<Map.Entry<String, Integer>> l = new ArrayList(table.entrySet());
        Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Collections.reverse(l);
        JSONArray array = new JSONArray();
        for (int i = 0; i < l.size(); i++) {
            JSONObject o = new JSONObject();
            o.put(key1, l.get(i).getKey());
            o.put(key2, l.get(i).getValue());
            array.put(o);
        }

        //if count exceeds the array length, then assign the array length to the count variable
        if (count > array.length()) {
            count = array.length();
        }

        JSONArray arrayPortion = new JSONArray();
        if (order.equalsIgnoreCase(BPMNAnalyticsCoreConstants.TOP)) {
            for (int i = array.length() - count; i < array.length(); i++) {
                arrayPortion.put(array.get(i));
            }
        } else if (order.equalsIgnoreCase(BPMNAnalyticsCoreConstants.BOTTOM)) {
            for (int i = 0; i < count; i++) {
                arrayPortion.put(array.get(i));
            }
        }
        return arrayPortion.toString();
    }

    /**
     * Get sorted list (sort by long type keys)
     *
     * @param table is a hash table to keep the result as key-value pairs
     * @param key1  is the name for the first value of the JSON object
     * @param key2  is the name for the second value for the JSON object
     * @return a sorted list as a JSON array string
     * @throws JSONException
     */
    public static String getLongKeySortedList(Hashtable<Long, Integer> table, String key1,
                                              String key2) throws JSONException {
        ArrayList<Map.Entry<Long, Integer>> l = new ArrayList(table.entrySet());
        Collections.sort(l, new Comparator<Map.Entry<Long, Integer>>() {
            public int compare(Map.Entry<Long, Integer> o1, Map.Entry<Long, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        Collections.reverse(l);
        JSONArray array = new JSONArray();
        for (int i = 0; i < l.size(); i++) {
            JSONObject o = new JSONObject();
            o.put(key1, l.get(i).getKey());
            o.put(key2, l.get(i).getValue());
            array.put(o);
        }
        return array.toString();
    }

    /**
     * Convert given datetime string to date
     *
     * @param time is the long value of a date
     * @return date as a String (eg: 2015-11-12)
     */
    private static String dateFormatter(long time) {
        String date = new Date(time).toString();
        String[] dateArray = date.split(BPMNAnalyticsCoreConstants.SPACE_SEPARATOR);
        try {
            Date dateMonth = new SimpleDateFormat(BPMNAnalyticsCoreConstants.MONTH_FORMAT, Locale.ENGLISH)
                    .parse(dateArray[1]);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateMonth);
            int month = cal.get(Calendar.MONTH) + 1;
            String dateString = dateArray[5] + BPMNAnalyticsCoreConstants.DATE_SEPARATOR + month +
                    BPMNAnalyticsCoreConstants.DATE_SEPARATOR + dateArray[2];
            DateFormat df = new SimpleDateFormat(BPMNAnalyticsCoreConstants.DATE_FORMAT_WITHOUT_TIME);
            return df.format(df.parse(dateString));
        } catch (ParseException e) {
            String errMsg = "Date format parse exception.";
            log.error(errMsg, e);
        }
        return null;
    }
}
