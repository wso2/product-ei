/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.humantask.samples.eventlistener;

import org.wso2.carbon.humantask.core.api.event.HumanTaskEventListener;
import org.wso2.carbon.humantask.core.api.event.TaskEventInfo;
import org.wso2.carbon.humantask.core.dao.TaskType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
Add following configuration to humantask.xml
        <TaskEventListeners>
            <ClassName>org.wso2.humantask.samples.eventlistener.SimpleEventListener</ClassName>
        </TaskEventListeners>

Copy humantask-eventlistener jar to repository/components/lib
*/

public class SimpleEventListener implements HumanTaskEventListener {
    private static final Log log = LogFactory.getLog(SimpleEventListener.class);

    @Override
    public void onEvent(TaskEventInfo taskEventInfo) {
        log.info("New " + taskEventInfo.getTaskInfo().getType() + " Generated at " + taskEventInfo.getTaskInfo().getModifiedDate());
        if (taskEventInfo.getTaskInfo().getType() == TaskType.TASK) {
            log.info("\tTask Name :" + taskEventInfo.getTaskInfo().getName());
            log.info("\tTask Subject :" + taskEventInfo.getTaskInfo().getSubject());
            log.info("\tTask Description :" + taskEventInfo.getTaskInfo().getDescription());
            log.info("\tTask Owner : " + taskEventInfo.getTaskInfo().getOwner());

        } else if (taskEventInfo.getTaskInfo().getType() == TaskType.NOTIFICATION) {
            log.info("\tNotification Name :" + taskEventInfo.getTaskInfo().getName());
            log.info("\tNotification Subject :" + taskEventInfo.getTaskInfo().getSubject());
            log.info("\tNotification Description :" + taskEventInfo.getTaskInfo().getDescription());
        }
    }
}