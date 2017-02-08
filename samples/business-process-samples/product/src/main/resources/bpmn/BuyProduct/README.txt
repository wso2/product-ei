Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk

1. Deploy BuyProduct.bar using the WSO2 BPS management console.
2. Log in to the BPMN Explorer using the admin/admin credentials.
3. Access the PROCESSES tab to view the task in the task list.
4. Click the Start button next to the receive task sample.
5. Once the above step is done, a task appears in the MY TASKS tab.
6. Fill the form associated with the task and click Complete.
7. After completing the first task, i.e., after filling the details of the product, send a message to the engine that triggers the process to continue after the receive task.
8. Get the execution ID of the process instance using:
9. GET bpmn/runtime/executions (https://localhost:9443/bpmn/runtime/executions)
10. Identify the exucution ID of the execution with the task id "waitTask".
11. After getting the execution ID, use this REST call to signal the specific execution:
12. PUT bpmn/runtime/executions/{executionId}
       Request body (signal an execution):
          { "action":"signal" }
13. Then a message will be sent to the engine, and the receive task will be triggered which will allow the process to continue further.
14. There will be a user task created after receiving the message.

This sample executes as follows:
1. The user (admin) fills the details of the product, including the product name and quantity.
2. The receive task waits for a signal. Once a signal is received by the receive task, the process will continue to the next task.
3. The user confirms/gets the product details