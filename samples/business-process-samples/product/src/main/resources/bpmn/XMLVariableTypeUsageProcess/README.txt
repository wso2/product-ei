Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk

1.Login to the management console as the admin, using admin/admin credentials.
2.Navigate to Processes > Add > BPMN and upload the XMLVariableTypeUsageProcess.bar file found in the <EI_HOME>/samples/business-process/bpmn/ directory.
3.Login as admin on the BPMN explorer using admin/admin credentials.
4.Select the Processes tab and start a two process instance of the XMLVariableType process; one with the Pending Arrivals Count field set to 0 and another one with the Pending Arrivals Count set to 1.
5.Select Tasks>My Tasks tab and you will see that the process instance you started with a pending arrivals count of 1 will appear on the task list as a user task.

This sample is based on a bookstore to handle pending arrivals of books.

1.Get the list of pending arrivals..
2.If there are no pending arrivals, a message stating "No Pending Arrivals" is printed.
3.If there are pending arrivals, a user task is triggered and assigned to the admin to add a new book.
4.The following code snippet shows the above condition using the XML variable.
    {xmlDoc.xPath("/bookstore/pendingArrivals/text()") > 0}
5.The new book is added to the book list.
6.The last service task prints the book list.