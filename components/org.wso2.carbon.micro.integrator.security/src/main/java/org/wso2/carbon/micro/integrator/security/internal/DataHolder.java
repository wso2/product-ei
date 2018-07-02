package org.wso2.carbon.micro.integrator.security.internal;

import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserStoreManager;

public class DataHolder {

    private RealmConfiguration realmConfig;
    private UserStoreManager userStoreManager;

    public static DataHolder instance = new DataHolder();

    public static DataHolder getInstance() {
        return instance;
    }

    public RealmConfiguration getRealmConfig() {
        return realmConfig;
    }

    public void setRealmConfig(RealmConfiguration realmConfig) {
        this.realmConfig = realmConfig;
    }

    public UserStoreManager getUserStoreManager() {
        return userStoreManager;
    }

    public void setUserStoreManager(UserStoreManager userStoreManager) {
        this.userStoreManager = userStoreManager;
    }
}
