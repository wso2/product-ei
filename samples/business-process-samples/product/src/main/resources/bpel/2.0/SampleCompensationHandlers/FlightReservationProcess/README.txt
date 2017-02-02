This process depicts a simple travel planning process. 
1. It includes three sub tasks. They are
	a. Reserve Car
	b. Reserve Hotel
	c. Reserver Flight

2. When flight reservation failed, the faultHandler associated with it will trigger compensationHandlers associated with other subTasks ie. Reserve Hotel and Reserve Car.

Sample Input:
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:com="http://wso2.org/bps/samples/compensation">
   <soapenv:Header/>
   <soapenv:Body>
      <com:CompensationProcessRequest>
         <com:input>3</com:input>
      </com:CompensationProcessRequest>
   </soapenv:Body>
</soapenv:Envelope>

Note- This process is targetted to show how compensation works in WSO2 BPS. So the possible improvements using BPEL constructs were minimalized. So this process can be improved by modifying the business logic or to get Faults from partner services and compensate based on those faults.
