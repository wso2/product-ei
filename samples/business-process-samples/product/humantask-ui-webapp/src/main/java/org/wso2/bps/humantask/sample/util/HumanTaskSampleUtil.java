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
package org.wso2.bps.humantask.sample.util;

import org.wso2.carbon.humantask.stub.ui.task.client.api.types.TPresentationName;
import org.wso2.carbon.humantask.stub.ui.task.client.api.types.TPresentationSubject;

public class HumanTaskSampleUtil {

    /**
     * Returns the task presentation header
     * @param pSub
     * @param pName
     * @return
     */
	public static String getTaskPresentationHeader(TPresentationSubject pSub,
			TPresentationName pName) {
		String presentationName = "";
		if (pSub != null && pSub.getTPresentationSubject() != null) {
			presentationName = pSub.getTPresentationSubject();
		} else if (pName != null && pName.getTPresentationName() != null) {
			presentationName = pName.getTPresentationName();
		}
		return presentationName;
	}
}
