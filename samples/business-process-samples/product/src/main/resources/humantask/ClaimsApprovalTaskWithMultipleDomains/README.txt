Introduction
============

Human tasks provide the specification to define tasks performed by human beings. Then a human task processor can request 
the task owners to perform some tasks and act according to the response. Wso2 carbon supports the use of multiple User 
stores. So the BPS human task engine also should have the support to assign tasks to users in multiple user stores.

User guide
===========

Adding Users

In order to use this feature you should set up multiple user stores as described in Working with User Stores. Now you 
can add users to different domains (user stores). Among these user stores, one of them is considered as the primary store
and others are secondary stores. A user or a role in a secondary store should be referred using the following format,
		<domain name>/<username|rolename>

Assigning tasks to domain users
================================

When you assign a task inside your humanTask package or Process archive to a domain user you should use the above format.
Eg: Lets assume you have ‘ClerkUser’ in ‘abc.com’ domain and ‘managerUser’ in primary domain.
So If you are assigning any task to these users they should be referred as follows,

ClerkUser >> abc.com/ClerkUser
managerUser >> managerUser

Management console display these domain names in block letters. But, please note that these domain names are case insensitive, 
you can use any form. 

Running the sample
===================

Creating the Required Roles and Users
--------------------------------------

Before you run the sample, you must create two users with different roles. First, you should setup a secondary user store
with the domain name ‘abc.com’ using instructions in  Working with User Stores.

To create the roles:

In the BPS management console, go to Configure -> Users and Roles -> Roles, and then click Add New Role.
Enter regionalManagerRole for the role name, keep PRIMARY as the domain name and click Next.
Select All Permissions to give all permissions to this role to the role created and click finish (the users will be added later). 
Repeat these steps to create another role called regionalClerksRole, this time change the domain name to ‘abc.com’.

To create the users:

Go to Configure -> Users and Roles -> Users, and then click Add New User.

First, we will create a user who will have the regionalManagerRole role.
Enter regionalManagerUser for the user name, select PRIMARY for domain and enter any password, and then click Next.
Select regionalManagerRole and click Finish. 

Repeat these steps to create another user with username regionalClerkUser for the domain ‘abc.com’ and assign the regionalClerksRole as the role.

You now have the two users and ready to deploy the sample.

Deploying the Sample

You will now deploy the ClaimsApprovalTask sample by adding the tasks and process associated with the sample.

To add the tasks:

Go to Main -> Human Tasks -> Add.
Browse to and select ClaimsApprovalTaskWithMultipleDomains.zip, which is located in <BPS_HOME>/repository/samples/humantask, and click Upload.
Click OK in the confirmation message and then refresh the page.
The ApproveClaim and ClaimApprovalReminder task definitions appear in the deployed task configurations list. You can click a task definition to view it.

To add the process:

Go to Main -> Processes -> Add.
Browse to and select ClaimsApprovalProcess.zip, which is located in <BPS_HOME>/repository/samples/bpel, and click Upload.
Click OK in the confirmation message, wait a few moments, and then refresh the page.

The ClaimsApprovalProcess process is now deployed and appears in the Deployed Processes list.

Executing the Sample

You will now run the sample by taking the following steps:

Go to Main -> Processes -> List.
In the Process ID column, click the process ID for ClaimsApprovalProcess: {http://www.wso2.org/humantask/claimsapprovalprocess.bpel}ClaimsApprovalProcess-1 

The Process Information screen appears, displaying information about this process.
In the WSDL Details section of the process information screen, select claimsApprovalPartnerLink from the Partner-Links list, and then click Try It.

A new browser window appears where you can replace the placeholder values with the values you want to send.
Replace the sample with the following values:



<body>
<p:ClaimApprovalProcessInput xmlns:p="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">
<xsd:custIDxmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">customerId</xsd:custID>
      <xsd:custFName xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">customerFName</xsd:custFName>
<xsd:custLName xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">CustomerName</xsd:custLName>
      	<xsd:amount xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">5000</xsd:amount>
<xsd:region xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">reagon</xsd:region>
      <xsd:priority xmlns:xsd="http://www.wso2.org/humantask/claimsapprovalprocessservice.wsdl">1</xsd:priority>
   </p:ClaimApprovalProcessInput>
</body>

You can now view this process instance in the management console.

Go to Main -> Business Processes -> Instances.

Notice that the ClaimsApprovalProcess-1 instance has been created and is active.
Click the instance ID to view the activity flow. 

The flow of the process depends on the value we specified for the amount property. If the amount is less than 1000,
the BPEL process handles the approval, so the process terminates without initiating the human task. If the amount is
over 5000, as we specified above, the ClaimsApprovalTask is initiated, and the process waits until it receives approval 
from the human task activity. To view this task, you must log in as the user who is assigned this task.

Click Sign-out, and then log in as regionalClerkUser.

Go to Main -> Human Tasks -> List (be sure to click the link lower down in the left navigation pane, not the one in the Manage group).

The task appears in the My Tasks list.

In the Task ID column, click the link for the task.

Click Start to start the task.

To approve the request, click Approve in the Response section, and then click Complete.
Go to Main -> Business Processes -> Instances. You can see that the instance, which was previously waiting for approval,
has now completed successfully.

Conclusion

Business process server has the multiple user store support for Human tasks. Users only has to refer the users and roles
correctly with domain prefix, when they are in secondary domains.

