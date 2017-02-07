Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk

1. Deploy OrderApprovalProcess.bar using the WSO2 BPS management console.
2. Login to the BPMN explorer using customer/customer credentials.
3. Select the PROCESSES tab to view the process in the process list.
4. Click Start and fill in the form that appears with the order details
5. Logout and Login as the manager using manager/manager credentials.
6. Select the MY TASKS tab. You will see two approval tasks for the order and payment details, as seen below.
7. Enter the relevant details in each of the tasks.
8. Login to the management console and navigate to Instances>BPMN.
9. Click on the Instance ID of the Parallel Order Process.
10. You will now be able to view a more detailed BPMN process instance with its process state diagram which will display the current active position of the instance in red.


In this sample, an inclusive gateway is used to perform parallel branching.
1.The customer user starts the process and enters the following order details: username, order id, paid amount.
2.The 'accept payment details' task is sent to the manager user with the paid amount and username.
3.The 'confirm order details' task is sent to manager user with the order ID and username.
4.Once the above tasks are completed, the order is approved and sent for delivery. A task will be sent to the customer user to notify the order availability.