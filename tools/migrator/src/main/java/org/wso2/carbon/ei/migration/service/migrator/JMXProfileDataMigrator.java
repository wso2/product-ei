package org.wso2.carbon.ei.migration.service.migrator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.common.jmx.agent.profiles.Profile;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.ei.migration.MigrationClientException;
import org.wso2.carbon.ei.migration.internal.MigrationServiceDataHolder;
import org.wso2.carbon.ei.migration.service.Migrator;
import org.wso2.carbon.ei.migration.util.Constant;
import org.wso2.carbon.ei.migration.util.Utility;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Password transformation class for JMX Profile.
 */
public class JMXProfileDataMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(JMXProfileDataMigrator.class);

    private static final String PROFILE_SAVE_REG_LOCATION = "repository/components/org.wso2.carbon.publish.jmx.agent/";

    private Registry registry;
    private RegistryService registryService = MigrationServiceDataHolder.getRegistryService();

    @Override
    public void migrate() {
        migrateProfilePassword();
    }

    private void migrateProfilePassword() {
        log.info(Constant.MIGRATION_LOG + "Migration starting on JMX Profile.");

        Tenant[] tenants;
        //for super tenant
        try {
            migrateProfilePasswordForTenant(Constant.SUPER_TENANT_ID);
        } catch (MigrationClientException e) {
            log.error("Error while migrating profiles. for tenant '".concat(
                    String.valueOf(Constant.SUPER_TENANT_ID)).concat("'. "), e);
        }
        try {
            tenants = MigrationServiceDataHolder.getRealmService().getTenantManager().getAllTenants();
        } catch (UserStoreException e) {
            log.error("Error while migrating profiles. Tenant retrieving failed. ", e);
            return;
        }
        for (Tenant tenant : tenants) {
            try {
                migrateProfilePasswordForTenant(tenant.getId());
            } catch (MigrationClientException e) {
                log.error("Error while migrating profiles for tenant '".concat(
                        String.valueOf(tenant.getId())).concat("'. "), e);
            }
        }
    }

    private void migrateProfilePasswordForTenant(int tenantID) throws MigrationClientException {
        try {
            registry = registryService.getGovernanceSystemRegistry(tenantID);
            Collection profilesCollection = (Collection) registry.get(PROFILE_SAVE_REG_LOCATION);
            for (String profileName : profilesCollection.getChildren()) {
                Profile profile = getProfile(profileName);
                reEncryptProfileWithNewCipher(profile);
            }
        } catch (RegistryException e) {
            log.warn("error while obtaining the registry ", e);
        } catch (CryptoException e) {
            throw new MigrationClientException("error while encrypting the registry ", e);
        }
    }

    private void reEncryptProfileWithNewCipher(Profile profile) throws MigrationClientException, CryptoException,
            RegistryException {
        String reEncryptedValue = Utility.getNewEncryptedValue(profile.getPass());
        profile.setPass(reEncryptedValue);
        saveUpdatedProfile(profile);
    }


    private Profile getProfile(String profileName) throws MigrationClientException {
        ByteArrayInputStream byteArrayInputStream;
        try {
            //if the profile exists
            Resource res = registry.get(profileName);
            byteArrayInputStream = new ByteArrayInputStream((byte[]) res.getContent());
        } catch (RegistryException e) {
            throw new MigrationClientException("Unable to get profile : ".concat(profileName).concat(". "), e);
        }

        Profile profile;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Profile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            profile = (Profile) jaxbUnmarshaller.unmarshal(byteArrayInputStream);
        } catch (JAXBException e) {
            throw new MigrationClientException("JAXB unmarshalling exception has occurred while retrieving '".
                    concat(profileName).concat("' profile from registry"), e);
        }
        return profile;

    }

    /**
     * Write the profile configuration in registry
     *
     * @param profile
     * @throws MigrationClientException
     * @throws RegistryException
     */
    private void saveUpdatedProfile(Profile profile) throws MigrationClientException, RegistryException {
        String path = PROFILE_SAVE_REG_LOCATION + profile.getName();

        JAXBContext jaxbContext;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            jaxbContext = JAXBContext.newInstance(Profile.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(profile, byteArrayOutputStream);
        } catch (JAXBException e) {
            throw new MigrationClientException("JAXB unmarshalling exception has occurred while saving '".
                    concat(profile.getName()).concat("'."), e);
        }

        //replace the profile if it exists
        try {
            Resource res = registry.newResource();
            res.setContent(byteArrayOutputStream.toString());
            //delete the existing profile
            registry.beginTransaction();
            registry.delete(path);
            //save the new profile
            registry.put(path, res);
            registry.commitTransaction();
        } catch (RegistryException e) {
            registry.rollbackTransaction();
            throw new MigrationClientException("Error has occurred while trying to save '".concat(profile.getName())
                    .concat("' profile on registry. "), e);
        }
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            // Just log the exception. Do nothing.
            log.warn("Unable to close byte stream ...", e);
        }
    }
}