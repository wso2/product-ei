Setting up the sample
=====================
1. Set up the sample as mentioned in the Claims Approval Task Sample readme.
2. Create the roles (regionalClerksRole and regionalManagerRole) as mentioned in the ClaimApprovalTask sample topic.
3. Create users for the respective roles and assign those users to the roles as mentioned in the ClaimApprovalTask sample topic.

Deploy the sample
=================
1.The ClaimsApprovalProcess.zip BPEL package has to be deployed as mentioned in the ClaimApprovalTask readme. This BPEL package can be found in the<EI_HOME>/samples/business-process/bpel directory.
2. If you have already installed the ClaimsApprovalTask.zip human task sample, un-deploy it.
3. Deploy the ClaimsApprovalTaskWithHTRenderings.zip sample. This HumanTask package can be found in the <EI_HOME>/samples/business-process/humantask directory.

Running the sample
=================
This sample has to be run as already mentioned in the ClaimApprovalTask sample until the step 6.  After the step 6, follow the steps below.
1. Login to the HumanTask Explorer (https://localhost:9443/humantask-explorer) as the "regionalClerkUser".
2. Go to the "My Tasks" tab of the HumanTask Explorer web app (https://localhost:9443/humantask-explorer/mytasks)  where there will be a task listed as following.
3. Click on the task row to open the task info page.
4. Click on the Start button. Then the rendered task information can be seen as following.
5. Check the checkbox for Approval Status and click on "Complete" button to complete the task or "Save" button to save the current info and to complete it at a later time. You can see the saved tasks from the "My Tasks" page under the "In Progress" filter.
Once you complete the task, it will be listed under "Completed" filter. The history, attachments and comments for the task can be seen in the tabs at the bottom of the page.

This is how you can use HT Renderings to display your task information.