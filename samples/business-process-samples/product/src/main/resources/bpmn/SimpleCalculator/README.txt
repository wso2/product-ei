Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk


1. Deploy SimpleCalculator.bar using the WSO2 BPS management console.
2. Login to the BPMN-explorer using clerk/clerk credentials.
3. Select the PROCESSES tab to view the deployed sample.
4. Click Start and fill the form that appears with the numbers that are to be calculated.
5. The result will be displayed under the MY TASKS tab as seen below.


In this sample scenario, a service task is executed to perform a calculation based on the user input.
1.The clerk user is provided with a form to input two numbers.
2.Once the user completes providing input, an addition calculation is performed by a service task (SimpleCalculator.addNumbers) and the final result is displayed.
3. Finally, the clerk user can confirm the provided result from the 'User Task' created.