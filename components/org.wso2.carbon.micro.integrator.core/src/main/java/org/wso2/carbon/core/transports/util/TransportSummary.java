/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
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

package org.wso2.carbon.core.transports.util;

/**
 *
 */
public class TransportSummary {
    private String protocol;
    private boolean isListenerActive;
    private boolean isSenderActive;
    private boolean isNonRemovable;

 	public boolean isNonRemovable() {
		return isNonRemovable;
	}

	public void setNonRemovable(boolean isNonRemovable) {
		this.isNonRemovable = isNonRemovable;
	}

	public TransportSummary() {
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean isListenerActive() {
        return isListenerActive;
    }

    public void setListenerActive(boolean listenerActive) {
        isListenerActive = listenerActive;
    }

    public boolean isSenderActive() {
        return isSenderActive;
    }

    public void setSenderActive(boolean senderActive) {
        isSenderActive = senderActive;
    }
}


