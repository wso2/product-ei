# 5.1.5 Route messages based on the given endpoint

## When to use

The Message Endpoint EIP encapsulates the messaging system inside an application. It then customizes a general messaging
API for a specific application and task. Therefore, you can change the message API just by changing the endpoint code.
This improves the maintainability of applications.

This is an approach of how these endpoint can be add with a routing mediators in WSO2 Enterprise integrator.
Given below is the syntax of how we can call an endpoint inside a switch mediator.
Inside switch mediator there is a Send mediator that routes the message to the endpoint indicated by the address URI.

```
<switch source="<source_value>">
   <case regex="<regular_expression>">
      <send>
         <endpoint key="<endpoint>"/>
      </send>
   </case>+
   <default>
      mediator+
   </default>
</switch>

```

## Sample use-case


## Pre-requisites


## Development guidelines


## Deployment guidelines


## Test cases

| Test Case ID  |                        Test Case	               |                                Test Description                |
| ------------- | ------------------------------------------------ | ---------------------------------------------------------------|
| 5.1.5.1       | Switch messages based on given endpoint with valid key   |   |
| 5.1.5.2       | [N] Switch messages based on given endpoint with Invalid key |  |
| 5.1.5.3       | [N] Switch messages based on given endpoint with null key   |  |
| 5.1.5.4       | [N] Switch messages based on given endpoint with empty key  |  |
| 5.1.5.5       | Switch messages based on given endpoint with valid uri   |   |
| 5.1.5.6       | [N] Switch messages based on given endpoint with Invalid uri |  |
| 5.1.5.7       | [N] Switch messages based on given endpoint with null uri   |  |
| 5.1.5.8       | [N] Switch messages based on given endpoint with empty uri  |  |

**_Note_**
[N] Represent the Negative Test cases (incorrect behaviours that user can attempt)