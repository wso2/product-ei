/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.bps.humantask.sample.manager;

import org.apache.axis2.databinding.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.bps.humantask.sample.clients.HumanTaskClientAPIServiceClient;
import org.wso2.bps.humantask.sample.util.HumanTaskSampleConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TaskManager extends HttpServlet {

    /**
     * This class was introduced to perform task operations based on request.
     */
    private static Log log = LogFactory.getLog(TaskManager.class);

    /**
     * Perform the task operations
     */
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        String backEndUrl = this.getServletContext().getInitParameter(HumanTaskSampleConstants.BACKEND_SERVER_URL);
        String sessionCookie = (String) req.getSession().getAttribute(HumanTaskSampleConstants.SESSION_COOKIE);
        HumanTaskClientAPIServiceClient humanTaskClientAPIServiceClient = new HumanTaskClientAPIServiceClient
                (sessionCookie, backEndUrl + HumanTaskSampleConstants.SERVICE_URL, null);
        String operation = req.getParameter("operation");
        String taskID = req.getParameter("taskID");

        if (log.isDebugEnabled()) {
            log.debug("Perform the task operation " + operation + " on Task ID " + taskID);
        }
        if (operation.equals("start")) {
            try {
                humanTaskClientAPIServiceClient.start(new URI(taskID));
            } catch (Exception ex) {
                String errMsg = "Failed to start the task";
                log.error(errMsg, ex);
            }
            req.getRequestDispatcher("Task.jsp?queryType=assignedToMe&taskId=" + taskID).forward(req, resp);

        } else if (operation.equals("complete")) {
            String payload = req.getParameter("payload");
            try {
                humanTaskClientAPIServiceClient.complete(new URI(taskID), payload);
            } catch (Exception ex) {
                String errMsg = "Failed to complete the task";
                log.error(errMsg, ex);
            }
            req.getRequestDispatcher("/Home.jsp?queryType=assignedToMe&pageNumber=0").forward(req, resp);

        } else if (operation.equals("stop")) {
            try {
                humanTaskClientAPIServiceClient.stop(new URI(taskID));
            } catch (Exception ex) {
                String errMsg = "Failed to stop the task";
                log.error(errMsg, ex);
            }
            req.getRequestDispatcher("Task.jsp?queryType=assignedToMe&taskId=" + taskID).forward(req, resp);

        }
    }
}
