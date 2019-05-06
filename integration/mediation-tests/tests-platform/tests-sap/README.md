## How to Run SAP Adaptor Tests

The module tests-sap is written for testing of the SAP Adaptor of WSO2 Enterprise Integrator. 

## Prerequisites
- SAP login credentials to access the SAP support portal
- Active SAP system

## Steps Prior to Running Tests
Following are the steps that you need to follow prior to running the module.
1. Download the sapidoc3.jar and sapjco3.jar middleware libraries and copy them into `src/test/resources/artifacts/ESB/server/lib` directory.
2. Download the native SAP JCo library and copy it to the system path as explained under step 3 in [Installing WSO2 SAP Adaptor](https://docs.wso2.com/display/EI600/SAP+Integration#installing-wso2-sap-adapter) of the official [documentation for SAP integration](https://docs.wso2.com/display/EI600/SAP+Integration#InstallingWSO2SAPAdapter).
3. Replace configurations in ECD.dest and ESD.server files inside `src/test/resources/artifacts/ESB/server/conf/sap` with the configurations relevant to your SAP system.

## Running Tests

You can directly run **IDoc** tests after the steps mentioned above. However, to run BAPI tests, you might need to modify the payloads used in BAPI tests to use function modules that are supported by your SAP system.
