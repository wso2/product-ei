# Migrate Passwords from the Previous Release

The following instructions guide you through migrating the encrypted passwords in older versions of WSO2 Enterprise Service Bus and WSO2 Enterprise Integrator to the latest WSO2 Enterprise Integrator (6.4.0 and later). In older product versions, passwords are encrypted using RSA algorithm. In the latest versions, OAEP scheme is used together with RSA encryption. Therefore in order to migrate from older versions to new versions, we have to migrate encrypted passwords.

## Before you begin

Take a backup of the existing database used by the older version. This backup is necessary in case the migration causes issues in the existing database.

## Instructions

1. Download latest EI product version and unzip it. 
1. Download migration.zip, unzip and copy migration folder to <EI_HOME>.
1. Copy **org.wso2.carbon.ei.migration-6.5.0.jar** to <EI_HOME>/dropins directory.
1. Update configurations in ```<EI_HOME>/migration/migration-conf.properties```.
                
    * keystore.identity.location - Please follow either way to add keystore properties
         * Add keystore file you have used in older version to ```<EI_HOME>/migration``` directory and update the keystore.identity.location as 
```migration/{keystore name}```
        
         * **Or** provide the absolute path of keystore file you have used in older version

    * keystore.identity.key.password - keystore password
    * admin.user.name - admin username

1. Stop the older product server
1. Start Enterprise Integrator 6.5.0 with following command to perform the password migration.

    a. Linux/Unix
    ```
    sh integrator.sh -Dmigrate
    ```
    b. Windows
    ```
    sh integrator.bat -Dmigrate
    ```

1. Once the migration is successful, stop the server and delete **org.wso2.carbon.ei.migration-6.5.0.jar** from 
<EI_HOME>/dropins directory.
1. Start the server using the appropriate command
a. Linux/Unix
    ```
    sh integrator.sh
    ```
    b. Windows
    ```
    sh integrator.bat
    ```