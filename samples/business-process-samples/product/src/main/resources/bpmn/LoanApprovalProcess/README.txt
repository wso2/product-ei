Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk


1. Deploy sample LoanApprovalProcess.bar using the WSO2 BPS management console.
2. Log in to the BPMN Explorer using the client/client credentials.
3. Select the PROCESSES tab to view the task in the task list.
4. Click the Start button next to the LoanApprovalProcess sample.
5. Fill in the required details (i.e., income and loan amount) and click Start.
6. If the loan amount is less than 50,000, login to the bpmn-explorer using the manager/manager credentials and select the MY TASKS tab. You will see the that another task has appeared on the list to inform the manager.
7. If the loan amount is more than 50,000, logout and login to the bpmn-explorer using the clerk/clerk credentials and select the MY TASKS tab.  You will see the that another task has appeared in the list to revise the loan amount.
8. After submitting revised amount, a task will get created to the manager to approve/reject the request.


In this sample scenario, a loan approval process is displayed.
1. The client user fills the required details (income and loan amount), which is then sent for confirmation.
2. At the exclusive gateway, if the loan amount is higher than 50,000, a request is sent to the clerk user to revise the loan amount. In this case, the user can revise and resubmit the loan application.
3. If the loan amount does not exceed 50,000, the "review application" task is triggered and the manager user can approve the loan application.