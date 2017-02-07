Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk

1. Deploy PizzaOrderProcess.bar using the WSO2 BPS management console.
2. Log in to the BPMN Explorer using the admin/admin credentials.
3. Access the PROCESSES tab to view the task in the task list.
4. Click the Start button next to the manual task sample.
5. Navigate to the CLAIMABLE TASKS tab to open the task and claim it.
6. Once the above step is done, a task appears in the MY TASKS tab.
7. Access this task and fill the form with the required details.
8. Navigate to the CLAIMABLE TASKS tab to open the new task and fill out the order details form.
9. Claim that task and on completion, another new task confirming your delivery will be available under CLAIMABLE TASKS. You can claim this task and view the details to complete the process.
10. Once this is claimed, the following form is displayed for the Status of Order Delivery task.




The sample scenario is,
1. The user (admin) places an order via filling the form to order pizza.
2.Once the form is filled by the user, the order must be confirmed by filling another form.
3.In the Deliver Pizza task, the user has to fill the amount for the pizza order.
4.After completing the Deliver Pizza task by filling the amount, the order details are displayed to the user.
