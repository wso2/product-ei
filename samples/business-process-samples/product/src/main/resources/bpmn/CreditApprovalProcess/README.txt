Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk

1. Deploy CreditApprovalProcess.bar using the WSO2 BPS management console.
2. Log in to the BPMN Explorer using the admin/admin credentials.
3. Select the PROCESSES tab to view the task in the task list.
4. Click the Start button next to the CallActivitiSample.
5. Select the MY TASKS tab. You will see that a task has appeared on the list.  Complete this task to obtain credit information.
6.Click the provided task and enter the required details (e.g., name and salary).
7.Select the MY TASKS tab. You will see that another task has now appeared on the list. Complete this task to verify the loan.
8.Once you have completed the task, another task will appear on the list. This task indicates whether the load was approved or rejected.


In this particular scenario, the CallActivitySample process invokes the independent LoanCheck process to verify the loan. All tasks in this scenario have been assigned to the admin (username:admin, password:admin) for the purpose of demonstrating the usage.

1.The user is given a form to fill that includes his/her name and salary.
2. Using the call activity, the loanCheck process is invoked, which checks whether the user is eligible for a loan.
(NOTE: Condition: If the salary of the user > 100000, the loan is approved. Otherwise, the loan is rejected.)
3. The user must verify the salary and the status of the loan.