/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.ei.migration.service.migrator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.ei.migration.MigrationClientException;
import org.wso2.carbon.ei.migration.service.Migrator;
import org.wso2.carbon.ei.migration.service.dao.EventSinkDAO;
import org.wso2.carbon.ei.migration.util.Constant;
import org.wso2.carbon.ei.migration.util.Utility;
import org.wso2.carbon.event.sink.EventSink;
import org.wso2.carbon.event.sink.EventSinkException;

import java.util.List;

/**
 * Password transformation class for Event Sink.
 */
public class EventSinkMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(EventSinkMigrator.class);

    @Override
    public void migrate() {
        transformPasswordInAllEventSinks();
    }

    /**
     * This method will transform the event sink password encrypted with old encryption algorithm to new encryption
     * algorithm.
     */
    private void transformPasswordInAllEventSinks() {
        log.info(Constant.MIGRATION_LOG + "Password transformation starting on Event Sink.");
        List<EventSink> eventSinksList;
        try {
            eventSinksList = EventSinkDAO.getInstance().getAllEventSinks();
            if (eventSinksList.size() > 0) {
                this.transformPasswordFromOldToNewEncryption(eventSinksList);
            }
        } catch (EventSinkException | MigrationClientException e) {
            log.error("Password transformation failed with ERROR: " + e);
        }
    }

    /**
     * Transform the password in event sink mediator by new algorithm
     *
     * @param eventSinksList
     * @throws MigrationClientException
     */
    private void transformPasswordFromOldToNewEncryption(List<EventSink> eventSinksList)
            throws MigrationClientException {

        for (EventSink eventSink : eventSinksList) {
            try {
                String newEncryptedPassword = Utility.getNewEncryptedValue(eventSink.getPassword());
                if (StringUtils.isNotEmpty(newEncryptedPassword)) {
                    EventSink updatedEventSink = new EventSink(eventSink.getName(), eventSink.getUsername(),
                            newEncryptedPassword, eventSink.getReceiverUrlSet(), eventSink.getAuthenticationUrlSet());
                    EventSinkDAO.getInstance().writeEventSink(updatedEventSink);
                }
            } catch (Exception e) {
                throw new MigrationClientException(e.getMessage());
            }
        }
    }
}
