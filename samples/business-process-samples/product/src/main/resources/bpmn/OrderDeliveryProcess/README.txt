Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk

1. Deploy OrderDeliveryProcess.bar using the WSO2 BPS management console.
2. Login to the BPMN explorer as a customer using the customer/customer credentials.
3. Select the PROCESSES tab to view the task in the task list.
4. Click the Start button and fill in the form that appears with the order details.
5. A task notification will be displayed under the MY TASKS tab, showing which delivery option was taken.


In this sample scenario,
1. The customer user sends an order request with details such as order name and summary.
2. This data is filtered from the script task.
3. If the summary field contains ‘local’, the order request will be sent to the 'local delivery' task.
4. Finally, the customer user will get a notified task that his order was sent to delivery.