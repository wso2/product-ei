# Migrate from the Previous Releases

The following instructions guide you through migrating the encrypted passwords in older versions of WSO2 Enterprise 
Service Bus and WSO2 Enterprise Integrator to the latest WSO2 Enterprise Integrator (6.4.0 and later). In older product 
versions, passwords are encrypted using RSA algorithm. In the latest versions, OAEP scheme is used together with RSA 
encryption. Therefore in order to migrate from older versions to new versions, we have to migrate encrypted passwords. 
Furthermore since we have done modifications to naming convention of scheduled message processor related tasks from 
EI-6.6.0 onwards, this would delete scheduled message processor related tasks before initializing server startup. However 
tasks would be re-scheduled according to new naming convention during server startup.

## Before you begin

Take a backup of the existing database used by the older version. This backup is necessary in case the migration causes 
issues in the existing database.

## Instructions

##### For migration from EI-6.5.0
1. Download latest EI product version and unzip it. 
1. Download migration.zip, unzip and copy migration folder to <EI_HOME>.
1. Copy **org.wso2.carbon.ei.migration-6.6.0.jar** to <EI_HOME>/dropins directory.
1. Update configurations in ```<EI_HOME>/migration/migration-conf.properties```.
    * admin.user.name - admin username
    
1. Comment other lines.
1. Stop the older product server
1. Start Enterprise Integrator 6.6.0 with following command to perform the password migration.

    a. Linux/Unix
    ```
    sh integrator.sh -Dmigrate.from.product.version=ei650
    ```
    b. Windows
    ```
    integrator.bat -Dmigrate.from.product.version=ei650
    ```
1. Once the migration is successful, stop the server and delete **org.wso2.carbon.ei.migration-6.6.0.jar** from 
<EI_HOME>/dropins directory.
1. Start the server using the appropriate command
a. Linux/Unix
    ```
    sh integrator.sh
    ```
    b. Windows
    ```
    integrator.bat
    ```
    
##### For migration from ESB

1. Download latest EI product version and unzip it. 
1. Download migration.zip, unzip and copy migration folder to <EI_HOME>.
1. Copy **org.wso2.carbon.ei.migration-6.6.0.jar** to <EI_HOME>/dropins directory.
1. Update configurations in ```<EI_HOME>/migration/migration-conf.properties```.
                
    * keystore.identity.location - Please follow either way to add keystore properties
         * Add keystore file you have used in older version to ```<EI_HOME>/migration``` directory and update the keystore.identity.location as 
```migration/{keystore name}```
        
         * **Or** provide the absolute path of keystore file you have used in older version

    * keystore.identity.key.password - keystore password
    * admin.user.name - admin username

1. Stop the older product server
1. Start Enterprise Integrator 6.6.0 with following command to perform the password migration. Provide the name of the 
product version as given below. 

    a. Linux/Unix
    ```
    sh integrator.sh -Dmigrate.from.product.version=esbxxx
    ```
    b. Windows
    ```
    integrator.bat -Dmigrate.from.product.version=esbxxx
    ```

    Eg : If you are migrating from ESB-5.0.0 use following command.
    
    a. Linux/Unix
    ```
    sh integrator.sh -Dmigrate.from.product.version=esb500
    ```
    b. Windows
    ```
    integrator.bat -Dmigrate.from.product.version=esb500
    ```

1. Once the migration is successful, stop the server and delete **org.wso2.carbon.ei.migration-6.6.0.jar** from 
<EI_HOME>/dropins directory.
1. Start the server using the appropriate command

    a. Linux/Unix
    ```
    sh integrator.sh
    ```
    b. Windows
    ```
    integrator.bat
    ```

Note : 

* If you are using multi-tenant architecture, please delete scheduled message processor tasks located at 
`/__system/governance/repository/components/org.wso2.carbon.tasks/definitions/{tenent.id}/ESB_TASK/` manually. Scheduled 
message processor tasks starts with prefix `MSMP_`

* If there are deactivated message processors in the existing product version, please note that they will be re-activated 
after the migration. 