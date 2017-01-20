package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Class which Extends test server manager in DSS
 */
public class DSSTestServerManager extends TestServerManager {
    private Map<String,Object> paramMap = new HashMap<String,Object>(2);
    public DSSTestServerManager(AutomationContext context) {
        super(context);
    }

    public DSSTestServerManager(AutomationContext context, String carbonZip) {
        super(context, carbonZip);
    }

    public DSSTestServerManager(AutomationContext context, int portOffset) {
        super(context, portOffset);
    }

    public DSSTestServerManager(AutomationContext context, String carbonZip, Map<String, String> commandMap) {
        super(context, carbonZip, commandMap);
    }

    public void setParameter(String key, Object param){
        paramMap.put(key,param);
    }

    public Object getParameter(String key){
        return paramMap.get(key);
    }

    public void removeParameter(String key){
        paramMap.remove(key);
    }
}
