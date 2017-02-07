Setting up the sample
=====================
1. Set up the sample as mentioned in the Claims Approval Task Sample readme.txt.
2.Create the roles (regionalClerksRole and regionalManagerRole) as mentioned in the ClaimApprovalTask sample topic.
3.Create users for the respective roles and assign those users to the roles as mentioned in the ClaimApprovalTask sample topic.

The following configuration snippet has been added to the ApproveClaim task.

<htd:deadlines>
      <htd:startDeadline name="sendNotifications">
             <htd:documentation xml:lang="en-US">
                        if the claimed amount is less than 10000 - to the task's potential
                        owners to remind them or their todo.
             </htd:documentation>
             <htd:for>PT1M</htd:for>
             <htd:escalation name="reminder">
               <htd:condition>
                    <![CDATA[htd:getInput("ClaimApprovalRequest")/test10:amount <
                          10000]]>
               </htd:condition>
               <htd:toParts>
                       <htd:toPart name="firstname">
                           htd:getInput("ClaimApprovalRequest")/test10:cust/test10:firstname
                        </htd:toPart>
                        <htd:toPart name="lastname">
                            htd:getInput("ClaimApprovalRequest")/test10:cust/test10:lastname
                        </htd:toPart>
               </htd:toParts>
               <htd:localNotification reference="tns:ClaimApprovalReminder">
                        <htd:documentation xml:lang="en-US">
                                Reuse the predefined notification "ClaimApprovalReminder".
                                Overwrite the recipients with the task's potential owners.
                          </htd:documentation>
               </htd:localNotification>
             </htd:escalation>
        </htd:startDeadline>
</htd:deadlines>



Deploy the sample
=================
1.The ClaimsApprovalProcess.zip BPEL package has to be deployed as mentioned in the ClaimApprovalTask sample readme. This BPEL package can be found in the <EI_HOME>/wso2/business-process/repository/samples/bpel directory.
2. The ClaimsApprovalTaskWithDeadline.zip HumanTask package has to be deployed as mentioned in the ClaimApprovalTask sample readme. This HumanTask package can be found in the <EI_HOME>/wso2/business-process/repository/samples/humantask directory.

Running the sample
==================
This sample has to be run as already mentioned in the ClaimApprovalTask sample.
