package org.wso2.carbon.dataservices.extension;

import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.dataservices.core.DataServiceFault;
import org.wso2.carbon.dataservices.core.auth.AuthorizationProvider;
import org.wso2.carbon.dataservices.core.auth.AuthorizationRoleRetriever;

import java.util.Map;

/**
 * This extension will return hardcoded role arrays for testing.
 */
public class RoleRetriever implements AuthorizationProvider {
    private static final Log log = LogFactory.getLog(RoleRetriever.class);
    String userName;
    String userRole;

    @Override
    public String[] getUserRoles(MessageContext messageContext) throws DataServiceFault {
        log.info("External role retriever invoked returning roles");
        String[] roleArray = {"admin","sampleRole1","sampleRole2", userRole};
        return roleArray;
    }

    @Override
    public String[] getAllRoles() throws DataServiceFault {
        log.info("External role retriever invoked for get all roles");
        String[] roleArray = {"sampleRole1","sampleRole2","sampleRole3", userRole};
        return roleArray;
    }

    @Override
    public String getUsername(MessageContext messageContext) throws DataServiceFault {
        return userName;
    }

    @Override
    public void init(Map<String, String> paramMap) throws DataServiceFault {
        userName = paramMap.get("userName");
        userRole = paramMap.get("userRole");
    }
}

