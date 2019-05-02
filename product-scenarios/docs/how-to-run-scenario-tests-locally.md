## How to Run Scenario Tests Locally

`product-scenarios` directory holds the main scenarios that can be expected of an integration system and how each of those integration patterns can be achieved using WSO2 Enterprise Integrator. Each scenario contains the description of the scenario, along with the approaches that can be followed inorder to accomplish the scenario using WSO2 EI and test cases to verify the scenario.

### Data Bucket

The important point to remember when running scenario tests is that there should be a WSO2EI instance deployed and running prior to running the test cases. This brings in the capability of running EI instances locally as well as remotely, hence making it essential to define the service/backend URLs in advance. The directory which holds such configurations that define the deployment of the EI instance is known as the `DATA BUCKET` and it is necessary for it to contain the file, `deployment.properties` which contains all required URLs. You can have a look at [deployment.properties](../deployment.properties) to see a sample configuration for a setup that runs locally.

### Test.sh

The shell script file [test.sh](../test.sh) is responsible for triggering scenario tests. It triggers a maven build for all the scenarios upon execution of the file.
 
### Steps to Run Tests against WSO2 Enterprise Integrator

1. Copy lib jars located in [scenario-commons](../scenarios-commons/src/main/resources/lib) into `<EI_HOME>/lib` directory.

2. Start WSO2 EI. If you are running tests against a released version of WSO2 IE, please get a wum update prior to running tests, in order to avoid failure of tests added for fixes after the release.

3. Update [deployment.properties](../deployment.properties) with the relevant URLs (You can skip this step if EI is being tested locally).

4. Execute [test.sh](../test.sh) as follows:
```
cd ../
./test.sh --input-dir <absolute_path_of_the_current_directory>/
```
Here, the absolute path of the current directory is provided as the input directory since deployment.properties file resides inside it.

### Steps to Run Tests against WSO2 Micro Integrator

WSO2 Micro Integrator is slightly different from WSO2 Enterprise Integrator in the following ways:
1. Micro Integrator does not support hot deployment. 
2. Micro Integrator does not provide the admin services that are shipped with Enterprise Integrator.

These 2 differences causes the scenario tests to be run against Micro Integrator in a lot different way.

1. Modify the function `runTestProfile()` in test.sh to execute the profile, `artifacts` in addition to the existing one, and execute the script.
```
cd ../
./test.sh --input-dir <absolute_path_of_the_current_directory>/
```
2. Copy all the C-Apps inside `<product-scenarios>/capps/target/` to `<MI_HOME>/repository/deployment/server/carbonapps`

3. Copy lib jars located in [scenario-commons](../scenarios-commons/src/main/resources/lib) into `<MI_HOME>/lib` directory.

4. Start WSO2 Micro Integrator.

5. Update [deployment.properties](../deployment.properties) with the relevant URLs (You can skip this step if MI is being tested locally).

6. Modify the method `onExecutionStart()` of `TestPrepExecutionListener` to neither login, nor deploy C-Apps.

7. Modify the `init` of `ScenarioTestBase` to not login.

8. Undo the changes done in step 1.

9. Find the `invocation.uuid` used for creating C-Apps by analyzing the logs. E.g., a log line of the format `number of invocations for ${invocation.uuid}-interval` will get printed when scheduled tasks run. Modify the method `runTestProfile()` of test.sh to use this uuid when building the project.
 
10. Execute the script again to run the tests against the configured MI instance.
