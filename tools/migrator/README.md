# Migrating artifact data from previous version.

The following instructions guide you through migrating the encrypted passwords in synapse artifacts
when we migrate the artifacts from older versions of WSO2 Enterprise Service Bus and
WSO2 Enterprise Integrator to the latest WSO2 Enterprise Integrator (which are released after 6.1.1).
In the old server’s artifacts, the encrypted passwords are encrypted by the RSA algorithm.
In the latest versions, new algorithm (RSA + OAEP) is used to encrypt.
Therefore, in order to work with the old artifacts on new pack, we have to migrate the encrypted passwords.

## Migrating the passwords in artifacts.

Please do the follow these steps before starting the migration.
1. Copy the org.wso2.carbon.ei.migration-1.0.0.jar file to the  <NEW_SERVER_HOME>/dropins directory.
2. Copy the wso2carbon.jks file from the <OLD_SERVER_HOME>/repository/resources/security folder and
paste them in <NEW_SERVER_HOME>/repository/resources/security folder.
3. Copy the artifacts to the corresponding directories in new server.

## Now you have configured everything.
Start the Enterprise Integrator with the following command to perform the data migration for all components.

* Linux/Unix:
sh integrator.sh -Dmigrate

* Windows:
integrator.bat -Dmigrate

***Note:*** If you want to migrate the password in JMX Server Profile, start the Analytics profile in migration mode
with -Deiprofile=Analytics

*Linux/Unix:
sh integrator.sh -Dmigrate -Deiprofile=Analytics

*Windows:
integrator.bat -Dmigrate -Deiprofile=Analytics

## Migrate active tenants only
Optional:If you have any disabled/inactive tenants in your WSO2 Enterprise Integrator and
you do not want to apply the migration into it, do the migration for active tenants only.
You can perform this by setting the ***ignoreInactiveTenants*** system property when starting the migration as mentioned below.

*Linux/Unix:
sh integrator.sh -Dmigrate -DignoreInactiveTenants=true

*Windows:
integrator.bat -Dmigrate -DignoreInactiveTenants=true
