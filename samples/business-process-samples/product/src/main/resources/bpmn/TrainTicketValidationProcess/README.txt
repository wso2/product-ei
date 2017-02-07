Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk

1. Deploy TrainTicketValidationProcess.bar using the WSO2 BPS management console.
2. Login to the BPMN explorer as a customer using the customer/customer credentials.
3. Select the PROCESSES tab to view the task in the task list.
4. Click the Start button and fill in the form that appears with the order details.
5. A task notification will be displayed under the MY TASKS tab, showing which delivery option was taken.


This sample explains the use of a sub process.
1. A customer enters user name, platform Id, and destination in a form.
2. Once the details are completed, a sub process is invoked to validate the provided details.
3. Under ‘check ticket details’, the inspector user does the validating based on the following details: name, destination.
4. Under ‘check boarding details’, the inspector user does the validating based on the following details: name, platform id.
5. Once both these tasks are completed, the user can board the train. A user task will be available for customer user with successful departure.
6. If both these tasks are not completed within the allocated time (1 minute in this sample), the user will fail to board the train. A user task will be available for customer on failure info.