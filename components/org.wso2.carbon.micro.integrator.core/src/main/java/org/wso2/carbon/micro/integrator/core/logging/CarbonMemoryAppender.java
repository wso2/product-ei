/* 
 * Copyright 2005,2006 WSO2, Inc. http://www.wso2.org
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
package org.wso2.carbon.micro.integrator.core.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.wso2.carbon.bootstrap.logging.LoggingBridge;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.logging.CircularBuffer;
import org.wso2.carbon.utils.logging.LoggingUtils;
import org.wso2.carbon.utils.logging.TenantAwareLoggingEvent;
import org.wso2.carbon.utils.logging.handler.TenantDomainSetter;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.LogRecord;

/**
 * This appender will be used to capture the logs and later send to clients, if
 * requested via the logging web service. This maintains a circular buffer, of
 * some fixed amount (say 100).
 */
public class CarbonMemoryAppender extends AppenderSkeleton implements LoggingBridge {

    private static final Log log = LogFactory.getLog(CarbonMemoryAppender.class);
    private CircularBuffer<TenantAwareLoggingEvent> circularBuffer;
    private int bufferSize = -1;
    private String columnList;

    public CarbonMemoryAppender() {
    }

    public CarbonMemoryAppender(CircularBuffer<TenantAwareLoggingEvent> circularBuffer) {
        this.circularBuffer = circularBuffer;
    }

    public String getColumnList() {
        return columnList;
    }

    public void setColumnList(String columnList) {
        this.columnList = columnList;
    }

    protected synchronized void append(LoggingEvent loggingEvent) {
        int tenantId = AccessController.doPrivileged(new PrivilegedAction<Integer>() {
            public Integer run() {
                return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
            }
        });

        String appName = CarbonContext.getThreadLocalCarbonContext().getApplicationName();
        if (appName == null) {
            appName = TenantDomainSetter.getServiceName();
        }
        Logger logger = Logger.getLogger(loggingEvent.getLoggerName());
        TenantAwareLoggingEvent tenantEvent;
        if (loggingEvent.getThrowableInformation() != null) {
            tenantEvent = new TenantAwareLoggingEvent(loggingEvent.fqnOfCategoryClass, logger,
                    loggingEvent.timeStamp, loggingEvent.getLevel(), loggingEvent.getMessage(),
                    loggingEvent.getThrowableInformation().getThrowable());
        } else {
            tenantEvent = new TenantAwareLoggingEvent(loggingEvent.fqnOfCategoryClass, logger,
                    loggingEvent.timeStamp, loggingEvent.getLevel(), loggingEvent.getMessage(),
                    null);
        }
        tenantEvent.setTenantId(Integer.toString(tenantId));
        tenantEvent.setServiceName(appName);
        if (circularBuffer != null) {
            circularBuffer.append(tenantEvent);
        }
    }


    public void close() {
        // do we need to do anything here. I hope we do not need to reset the
        // queue
        // as it might still be exposed to others
    }

    public boolean requiresLayout() {
        return true;
    }

    public CircularBuffer getCircularQueue() {
        return circularBuffer;
    }

    public void setCircularBuffer(CircularBuffer<TenantAwareLoggingEvent> circularBuffer) {
        this.circularBuffer = circularBuffer;
    }

    public void clearCircularBuffer() {
        this.circularBuffer.clear();
    }

    public void activateOptions() {
        if (bufferSize < 0) {
            if (circularBuffer == null) {
                this.circularBuffer = new CircularBuffer<TenantAwareLoggingEvent>();
            }
        } else {
            this.circularBuffer = new CircularBuffer<TenantAwareLoggingEvent>(bufferSize);
        }
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void push(LogRecord logRecord) {
        LoggingEvent loggingEvent = LoggingUtils.getLogEvent(logRecord);
        append(loggingEvent);
    }
}
