# 5.1.2.1 Using Switch Mediator on given regular expressions

## When to use

If you want to route the messages based on the regular expression, you can do it by using switch mediator.
Basically you can retrieve a string by calling a Regular expression and it match with the source value. Then returned string within each case statement.

Given below is the syntax of how regular expression can be add to the switch mediators.

```
<switch source="<source_value>">
   <case regex="<regular_expression>">
      mediator+
   </case>+
   <default>
      mediator+
   </default>
</switch>

```

## Sample use case
Let's take an example of a client that could send stock quote requests , and receive and display the price of stock quote.
The client add the name of the stock name which he needs to ge the price of the requested stock.
So now the request payload includes the above details. When the user sends the request,
the message is routed to the relevant endpoint by using Switch/Filter mediator.
In this example, the client can configure from which element or the attribute that the mediator should read and call the back-end service.

Sample request payload

```
<m:GetStockPrice xmlns:m="http://www.example.org/stock">
   <m:StockName>IBM</m:StockName>
</m:GetStockPrice>

```

Following is a sample synapse configuration in EI.

```
   <resource methods="POST">
      <inSequence>
         <property xmlns:m="http://www.example.org/stock" name="StockName" expression="//m:GetStockPrice/m:StockName" scope="default" type="STRING"/>
         <switch source="get-property('StockName')">
            <case regex="IBM">
               <call>
                  <endpoint key="http://ei-backend.scenarios.wso2.org:9090/eiTests/XMLEndpoint"/>
               </call>
            </case>
            <default/>
         </switch>
         <respond/>
      </inSequence>
      <outSequence/>
      <faultSequence/>
   </resource>
</api>

```

## Pre-requisites

A REST client like cURL to invoke the ESB API.

## Deployment guidelines

We can simply deploy by copying the carbon composite application archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

**OR**

We can create the api in Management Console and deploy.

## Supported versions

This is supported in all the EI and ESB versions









