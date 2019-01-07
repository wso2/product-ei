# 5.3.1.1 Load Balance requests with Load Balance Endpoint Group

## When to use
   The load balance endpoint group is typically used when EI is receiving a high load of requests that a single 
   back-end might not be capable of handling. In such scenarios, multiple back-ends are added where the EI forwards the 
   request to either of those, depending on a predefined policy(e.g., roundRobin) in order to reduce the load on a 
   single back-end.
## Sample use-case
   Suppose a user need to simply load balance requests between 2 backend services.
   
   Following is a sample synapse configuration in EI.
   ```
   <resource methods="POST">
     <inSequence>
       <call>
         <endpoint name="{ep.name}">
           <loadbalance algorithm="org.apache.synapse.endpoints.algorithms.RoundRobin">
             <endpoint>
               <address uri="http://ei-backend.scenarios.wso2.org:9090/eiTests/JSONEndpoint"/>
             </endpoint>
             <endpoint>
               <address uri="http://ei-backend.scenarios.wso2.org:9090/eiTests/JSONEndpoint"/>
             </endpoint>
           </loadbalance>
         </endpoint>
       </call>
       <respond/>
     </inSequence>
     <outSequence/>
     <faultSequence/>
   </resource>
   ```
## Supported Versions
   Supported since ESB 4.9.0 and by all EI versions

## Test cases

| ID        | Summary                                                                                   |
| ----------|:-----------------------------------------------------------------------------------------:|
| 5.3.1.1   | Load balance requests with a simple loadbalance endpoint group with default configurations|
