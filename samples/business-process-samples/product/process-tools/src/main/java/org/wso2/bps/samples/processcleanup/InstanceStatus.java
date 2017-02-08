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
package org.wso2.bps.samples.processcleanup;

import java.util.HashMap;

//Class for mapping instance status
public class InstanceStatus {

    public HashMap<String, String> state = new HashMap<String, String>();

    public InstanceStatus() {
        state.put("ACTIVE", "20");
        state.put("COMPLETED", "30");
        state.put("SUSPENDED", "50");
        state.put("FAILED", "40");
        state.put("TERMINATED", "60");
    }
}
