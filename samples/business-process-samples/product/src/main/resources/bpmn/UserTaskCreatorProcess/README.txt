Required pre-requisite to try out BPMN samples
================================================
Create a user with the following username and password: (kermit/kermit) in order to try out the provided samples. See adding users for information on how to do this. In addition to this, create the following users. These users do not require any specific role, a role with login and BPMN permissions is sufficient.
- client
- customer
- manager
- inspector
- clerk

1.Login to the management console as the admin, using admin/admin credentials.
2.Create two users ("user1" and "user2") and assign both to a role with admin permissions.
3.Navigate to Processes > Add > BPMN and upload the UserTaskCreatorProcess.bar file found in the <BPS_HOME>/repository/samples/bpmn/ directory.
4. Login as admin on the BPMN explorer using admin/admin credentials.
5. Select the Processes tab and start a few process instances of the ‘UserCreationProcess’ by giving ‘user1’ as the assignee.
6. Logout as admin and login as "user1" on the BPMN explorer using the relevant credentials.
7. Select the My Tasks tab. You will see a few tasks listed that are assigned to user1.
8. Select the Substitutions tab and click Add Substitute. Enter "user2" as the substitute user and enter start and end times.
9. Go back to the My Tasks tab. You will see that there are no tasks for user1 anymore. All the existing tasks that were assigned to "user1" have been substituted to "user2" due to your substitution request.
10. Logout as "user1" and login as "user2" and select the My Tasks tab. Note that there are now tasks with the name "User task for user1" assigned to "user2".
11. Login as admin and start a few task instances for user1 again. When you login as "user2", you will notice that these tasks that you just assigned to "user1" were assigned to "user2" instead. This is because, future tasks have also been reassigned according to the substitution record.
12. Login again as "user1" and select the Substitutions tab. Click on the Deactivate button. This will disable the existing substitution record. Now the substitution view will look as follows.
13. Login as admin and start a few task instances giving "user1" as the assignee again. Login as "user1" again and you will see that these tasks are now be available in the My Tasks tab of user1 since the substitution was disabled in the above step.
