/*
*Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.ei.businessprocess.integration.common.clients.humantasks;

import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.NCName;
import org.apache.axis2.databinding.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.ei.businessprocess.integration.common.clients.AuthenticateStubUtil;
import org.wso2.carbon.humantask.stub.ui.task.client.api.*;
import org.wso2.carbon.humantask.stub.ui.task.client.api.types.*;

public class HumanTaskClientApiClient {

    private static final Log log = LogFactory.getLog(HumanTaskClientApiClient.class);

    private final static String serviceName = "HumanTaskClientAPIAdmin";
    private HumanTaskClientAPIAdminStub humanTaskClientAPIAdminStub;

    public HumanTaskClientApiClient(String serviceEndPoint, String sessionCookie) throws AxisFault {
        final String humantaskClientAPIServiceURL = serviceEndPoint + serviceName;
        humanTaskClientAPIAdminStub = new HumanTaskClientAPIAdminStub(humantaskClientAPIServiceURL);
        AuthenticateStubUtil.authenticateStub(sessionCookie, humanTaskClientAPIAdminStub);
    }

    public HumanTaskClientApiClient(String serviceEndPoint, String username, String password) throws AxisFault {
        final String humantaskClientAPIServiceURL = serviceEndPoint + serviceName;
        humanTaskClientAPIAdminStub = new HumanTaskClientAPIAdminStub(humantaskClientAPIServiceURL);
        AuthenticateStubUtil.authenticateStub(username, password, humanTaskClientAPIAdminStub);
    }


    public TTaskSimpleQueryResultSet simpleQuery(final TSimpleQueryInput tSimpleQueryInput) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.simpleQuery(tSimpleQueryInput);
        } catch (Exception e) {
            String error_String = "Simple Query Operation Failed";
            log.error(error_String, e);
            throw e;
        }
    }

    public void stop(final URI taskId) throws Exception {
        try {
            humanTaskClientAPIAdminStub.stop(taskId);
        } catch (Exception e) {
            String error_String = "Unable to stop task " + taskId;
            log.error(error_String, e);
            throw e;
        }
    }

    public void resume(final URI taskId) throws Exception {
        try {
            humanTaskClientAPIAdminStub.resume(taskId);
        } catch (Exception e) {
            String error_String = "Unable to resume task " + taskId;
            log.error(error_String, e);
            throw e;
        }
    }

    public void setOutput(final URI taskIdURI, final NCName ncName, final Object o) throws Exception {
        try {
            humanTaskClientAPIAdminStub.setOutput(taskIdURI, ncName, o);
        } catch (Exception e) {
            String error_String = "Unable to setOutput for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void suspend(final URI taskId) throws Exception {
        try {
            humanTaskClientAPIAdminStub.suspend(taskId);
        } catch (Exception e) {
            String error_String = "Unable to suspend task " + taskId;
            log.error(error_String, e);
            throw e;
        }
    }

    public TUser[] getAssignableUserList(final URI taskIdURI) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.getAssignableUserList(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to get Assignable User List for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void updateComment(final URI taskIdURI, final URI commentId, final String s) throws Exception {
        try {
            humanTaskClientAPIAdminStub.updateComment(taskIdURI, commentId, s);
        } catch (Exception e) {
            String error_String = "Unable to update comment for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public TTaskAbstract loadTask(final URI taskIdURI) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.loadTask(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to load task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void skip(final URI taskIdURI) throws Exception {
        try {
            humanTaskClientAPIAdminStub.skip(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to skip task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void start(final URI taskId) throws Exception {
        try {
            humanTaskClientAPIAdminStub.start(taskId);
        } catch (Exception e) {
            String error_String = "Unable to start task " + taskId;
            log.error(error_String, e);
            throw e;
        }
    }

    public void fail(final URI taskIdURI, final TFault tFault) throws Exception {
        try {
            humanTaskClientAPIAdminStub.fail(taskIdURI, tFault);
        } catch (Exception e) {
            String error_String = "Unable to fail task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void activate(final URI taskIdURI) throws Exception {
        try {
            humanTaskClientAPIAdminStub.activate(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to activate task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public URI addComment(final URI taskIdURI, final String commentString) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.addComment(taskIdURI, commentString);
        } catch (Exception e) {
            String error_String = "Unable to add Comment for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void deleteComment(final URI taskIdURI, final URI commentId) throws Exception {
        try {
            humanTaskClientAPIAdminStub.deleteComment(taskIdURI, commentId);
        } catch (Exception e) {
            String error_String = "Unable to delete Comment " + commentId + " for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void delegate(final URI taskId, final TOrganizationalEntity delegatee) throws Exception {
        try {
            humanTaskClientAPIAdminStub.delegate(taskId, delegatee);
        } catch (Exception e) {
            String error_String = "Unable to delegate for task " + taskId;
            log.error(error_String, e);
            throw e;
        }
    }

    public TComment[] getComments(URI taskIdURI) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.getComments(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to comments for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public boolean addAttachment(final URI taskId, final String name, final String accessType, final String contentType, final Object attachment)
            throws Exception {
        try {
            return humanTaskClientAPIAdminStub.addAttachment(taskId, name, accessType, contentType, attachment);
        } catch (Exception e) {
            String error_String = "Unable to add attachment for task " + taskId;
            log.error(error_String, e);
            throw e;
        }
    }

    public TAttachmentInfo[] getAttachmentInfos(final URI taskId) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.getAttachmentInfos(taskId);
        } catch (Exception e) {
            String error_String = "Unable to add attachment for task " + taskId;
            log.error(error_String, e);
            throw e;
        }
    }

    public void remove(final URI taskId) throws Exception {
        try {
            humanTaskClientAPIAdminStub.remove(taskId);
        } catch (Exception e) {
            String error_String = "Unable to remove task " + taskId;
            log.error(error_String, e);
            throw e;
        }
    }

    public TTaskAuthorisationParams loadAuthorisationParams(final URI taskIdURI) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.loadAuthorisationParams(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to load AuthorisationParams for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public TTaskEvents loadTaskEvents(final URI taskIdURI) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.loadTaskEvents(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to load Task Events for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public Object getInput(final URI taskIdURI, final NCName inputIdentifier) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.getInput(taskIdURI, inputIdentifier);
        } catch (Exception e) {
            String error_String = "Unable to get Input for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void complete(final URI taskIdURI, final String outputStr) throws Exception {
        try {
            humanTaskClientAPIAdminStub.complete(taskIdURI, outputStr);
        } catch (Exception e) {
            String error_String = "Unable to complete task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void claim(final URI taskIdURI) throws Exception {
        try {
            humanTaskClientAPIAdminStub.claim(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to claim task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void setFault(final URI taskIdURI, final TFault tFault) throws Exception {
        try {
            humanTaskClientAPIAdminStub.setFault(taskIdURI, tFault);
        } catch (Exception e) {
            String error_String = "Unable to set fault for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public String getTaskDescription(final URI taskIdURI, final String contentTypeStr) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.getTaskDescription(taskIdURI, contentTypeStr);
        } catch (Exception e) {
            String error_String = "Unable to get Task Description for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void deleteAttachment(URI taskId, URI attachmentId) throws Exception {
        try {
            humanTaskClientAPIAdminStub.deleteAttachment(taskId, attachmentId);
        } catch (Exception e) {
            String error_String = "Unable to delete attachment " + attachmentId + " for task " + taskId;
            log.error(error_String, e);
            throw e;
        }
    }

    public void nominate(final URI taskIdURI, final TOrganizationalEntity nomineeOrgEntity) throws Exception {
        try {
            humanTaskClientAPIAdminStub.nominate(taskIdURI, nomineeOrgEntity);
        } catch (Exception e) {
            String error_String = "Unable to nominate OrgEntity for task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void deleteOutput(final URI taskIdURI) throws Exception {
        try {
            humanTaskClientAPIAdminStub.deleteOutput(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to delete Output for Task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void deleteFault(final URI taskIdURI) throws Exception {
        try {
            humanTaskClientAPIAdminStub.deleteFault(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to delete fault for Task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public Object getOutput(final URI taskIdURI, final NCName partNCName) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.getOutput(taskIdURI, partNCName);
        } catch (Exception e) {
            String error_String = "Unable to getOutput for Task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void release(final URI taskId) throws Exception {
        try {
            humanTaskClientAPIAdminStub.release(taskId);
        } catch (Exception e) {
            String error_String = "Unable to release Task " + taskId;
            log.error(error_String, e);
            throw e;
        }
    }

    public TFault getFault(URI taskIdURI) throws Exception {
        try {
            return humanTaskClientAPIAdminStub.getFault(taskIdURI);
        } catch (Exception e) {
            String error_String = "Unable to get Fault message for Task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

    public void setPriority(final URI taskIdURI, final TPriority tPriority) throws Exception {
        try {
            humanTaskClientAPIAdminStub.setPriority(taskIdURI , tPriority);
        } catch (Exception e) {
            String error_String = "Unable to get Fault message for Task " + taskIdURI;
            log.error(error_String, e);
            throw e;
        }
    }

}
