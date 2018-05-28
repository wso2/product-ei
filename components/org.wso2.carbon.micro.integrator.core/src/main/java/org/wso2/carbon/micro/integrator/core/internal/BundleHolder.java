package org.wso2.carbon.micro.integrator.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by madhawa on 5/28/18.
 */
public class BundleHolder {
    private static Log log = LogFactory.getLog(BundleHolder.class);
    private final Object pendingItemsLock = new Object();
    private final Map<String, String> pendingItemMap = new ConcurrentHashMap();

    void addPendingItem(String requiredItemName, String itemType) {
        Object var3 = this.pendingItemsLock;
        synchronized(this.pendingItemsLock) {
            if(log.isDebugEnabled()) {
                log.debug("Pending Item added : " + requiredItemName);
            }
            this.pendingItemMap.put(requiredItemName, itemType);
        }
    }

    void removePendingItem(String requiredItemName) {
        Object var2 = this.pendingItemsLock;
        synchronized(this.pendingItemsLock) {
            if(this.pendingItemMap.containsKey(requiredItemName)) {
                if(log.isDebugEnabled()) {
                    log.debug("Pending Item removed : " + requiredItemName);
                }

                this.pendingItemMap.remove(requiredItemName);
                if(this.pendingItemMap.isEmpty()) {
//                    this.initializeCarbon();
                }
            }

        }
    }
}
