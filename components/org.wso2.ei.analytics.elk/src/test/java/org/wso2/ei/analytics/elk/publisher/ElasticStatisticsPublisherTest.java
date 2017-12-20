/*
* Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.ei.analytics.elk.publisher;

import junit.framework.TestCase;
import org.apache.synapse.aspects.flow.statistics.publishing.PublishingEvent;
import org.apache.synapse.aspects.flow.statistics.publishing.PublishingFlow;
import org.junit.Assert;
import org.wso2.carbon.das.data.publisher.util.PublisherUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.SimpleTimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ElasticStatisticsPublisherTest extends TestCase {

    public void testProcess() throws Exception {

        PublishingFlow flow = new PublishingFlow();

        flow.setMessageFlowId("abcd1234");

        PublishingEvent event1 = new PublishingEvent();
        PublishingEvent event2 = new PublishingEvent();

        event1.setComponentName("TestProxy");
        event1.setComponentType("Proxy Service");
        event1.setFaultCount(0);
        event1.setStartTime(System.currentTimeMillis());

        event2.setComponentName("TestSequence");
        event2.setComponentType("Sequence");
        event2.setFaultCount(1);
        event2.setStartTime(System.currentTimeMillis());

        flow.addEvent(event1);
        flow.addEvent(event2);

        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();

        // for event1
        map1.put("flowid", "abcd1234");
        map1.put("host", PublisherUtil.getHostAddress());
        map1.put("type", "Proxy Service");
        map1.put("name", "TestProxy");

        long time1 = event1.getStartTime();
        Date date1 = new Date(time1);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate1 = dateFormat.format(date1);

        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        timeFormat.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        String formattedTime1 = timeFormat.format(date1);

        String timestampElastic1 = formattedDate1 + "T" + formattedTime1 + "Z";
        map1.put("@timestamp", timestampElastic1);

        map1.put("success", true);

        // for event2
        map2.put("flowid", "abcd1234");
        map2.put("host", PublisherUtil.getHostAddress());
        map2.put("type", "Sequence");
        map2.put("name", "TestSequence");

        long time2 = event2.getStartTime();
        Date date2 = new Date(time2);

        String formattedDate2 = dateFormat.format(date2);
        String formattedTime2 = timeFormat.format(date2);

        String timestampElastic2 = formattedDate2 + "T" + formattedTime2 + "Z";
        map2.put("@timestamp", timestampElastic2);

        map2.put("success", false);

        Queue<Map<String, Object>> queue = new ConcurrentLinkedQueue<Map<String, Object>>();
        queue.add(map1);
        queue.add(map2);

        ElasticStatisticsPublisher.process(flow);

        Assert.assertTrue(map1.equals(ElasticStatisticsPublisher.getAllMappingsQueue().poll()) &&
                map2.equals(ElasticStatisticsPublisher.getAllMappingsQueue().poll())
        );
    }
}