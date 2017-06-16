/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.deployer.common;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

public class DirChangeCallback implements EventCallback{

    @Override
    public void exec(WatchKey watchKey) {
        if (watchKey != null) {
            System.out.println("DirChangeCallback TRIGGERED : ");
            for (WatchEvent<?> watchEvent: watchKey.pollEvents()) {
                WatchEvent.Kind kind =  watchEvent.kind();
                System.out.println("Event name : " + kind.name() + " , File name : " + watchEvent.context());
            }
            watchKey.reset();
        } else {
            System.out.println("No Change");
        }
    }
}
