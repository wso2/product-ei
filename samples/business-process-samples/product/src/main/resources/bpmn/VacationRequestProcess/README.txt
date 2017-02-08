Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk

1. Deploy VacationRequestProcess.bar using the WSO2 BPS management console.
2. Login to the BPMN explorer as a customer using the clerk/clerk credentials.
3. Select the PROCESSES tab to view the task in the task list.
4. Click the Start button and fill in the form that appears with the order details.
5. A task notification will be displayed under the MY TASKS tab, showing which delivery option was taken.

This sample displays the use  of a user task.
1. A vacation request form that includes name, number of days and reason is filled and sent by the user clerk.
2. Next, the vacation request details are sent to the manager user for approval as a User Task for manager.
3. Log in as 'manager' and the task will be appeared in the 'My Tasks' tab of 'BPMN Explorer'.