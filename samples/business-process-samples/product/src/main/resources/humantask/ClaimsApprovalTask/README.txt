Creating the Required Roles and Users
========================================
 Before you run the sample, you must create two users with different roles.

To create the roles:
1. In the BPS management console, go to Users and Roles>Roles in the Configure menu and click Add New Role.
2. Enter regionalManagerRole for the role name and click Next.
3. Select All Permissions to give all permissions to the role created and click Finish (the users will be added later).
4. Repeat these steps to create another role called regionalClerksRole .

To create the users:
1. Go to Users and Roles>Users in the Configure menu, and then click Add New User.
2. First, we will create a user who will have the regionalManagerRole role.
3. Enter regionalManagerUser for the user name and enter any password. Click Next.
4. Select regionalManagerRole and click Finish.
5. Repeat these steps to create another user with username regionalClerkUser and assign the regionalClerksRole as the role.

You now have the two users and ready to deploy the sample.

Deploying the Sample
====================
You will now deploy the ClaimsApprovalTask sample by adding the tasks and process associated with the sample.
To add the tasks:
1. Go to Human Tasks>Add in the Main menu.
2. Browse to and select ClaimsApprovalTask.zip, which is located in <EI_HOME>/samples/business-process/humantask, and click Upload.
3. Click OK in the confirmation message and then refresh the page.
4. The ApproveClaim and ClaimApprovalReminder task definitions appear in the deployed task configurations list.

You can click a task definition to view it. The ApproveClaim task definition looks like this:

To add the process:
1. Go to Processes>Add>BPEL under the Main menu.
2. Browse to and select ClaimsApprovalProcess.zip, which is located in <EI_HOME>/samples/business-process/bpel, and click Upload.
3. Click OK in the confirmation message, wait a few moments, and then refresh the page.
4. The ClaimsApprovalProcess process is now deployed and appears in the Deployed Processes list.

Running the Sample
==================
You will now run the sample by taking the following steps:
1. Go to Processes>List>BPEL in the Main menu.
2. In the Process ID column, click the process ID for ClaimsApprovalProcess: {http://www.wso2.org/humantask/claimsapprovalprocess.bpel}ClaimsApprovalProcess-1
3. The Process Information screen appears, displaying information about this process.
4. If the Status is "Retired", click on Activate to activate the process.

In the WSDL Details section of the process information screen, select claimsApprovalPartnerLink from the Partner-Links list, and then click Try It.
A new browser window appears where you can replace the placeholder values with the values you want to send.
Replace the sample with the following values:
<body>
   <p:ClaimApprovalProcessInput xmlns:p="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">
      <xsd:custID xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">customerId</xsd:custID>
      <xsd:custFName xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">customerFName</xsd:custFName>
      <xsd:custLName xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">CustomerName</xsd:custLName>
      <xsd:amount xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">5000</xsd:amount>
      <xsd:region xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">reagon</xsd:region>
      <xsd:priority xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">1</xsd:priority>
   </p:ClaimApprovalProcessInput>
</body>

You can now view this process instance in the management console.
1. Go to Instances>BPEL in the Main menu.
2. Notice that the ClaimsApprovalProcess-1 instance has been created and is active.
3. Click the instance ID to view the activity flow. The flow of the process depends on the value we specified for the amount property. If the amount is less than or equal to 1000, the BPEL process handles the approval, so the process terminates without initiating the human task. If the amount is over 1000, as we specified above, the ClaimsApprovalTask is initiated, and the process waits until it receives approval from the human task activity. To view this task, you must log in as the user who is assigned this task.
4. Click Sign-out, and then log in as regionalClerkUser.
5. Go to Main -> Human Tasks -> List (be sure to click the link lower down in the left navigation pane, not the one in the Manage group).


The task appears in the My Tasks list.
In the Task ID column, click the link for the task. You can see a read only view of the task information.

To start and approve the task, you need to go to the human task explorer as explained in the "Running the sample" section of Claims Approval task with HT Renderings Sample.
Go to Main -> Business Processes -> Instances. You can see that the instance, which was previously waiting for approval, has now completed successfully.
