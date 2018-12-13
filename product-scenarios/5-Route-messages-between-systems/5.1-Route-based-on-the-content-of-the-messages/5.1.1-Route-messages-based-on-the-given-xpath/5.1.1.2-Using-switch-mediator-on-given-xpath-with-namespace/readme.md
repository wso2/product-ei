# 5.1.1.2 Using switch mediator on given xpath with namespace

## When to use

If you need to provide an Xpath that has namespace, you can add it as a namespace in switch mediator.

Given below is the syntax of how to add the it with namespace.

```
<switch source="[XPath|json-eval(JSON Path)]" && xmlns:test="[namespace]">
   <case regex="string">
      mediator+
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
| 5.1.1.2.1     | Switch messages based on given valid Xpath with valid namespace   | **_Given_:** Valid xml/json/csv request payload has be to sent. **_When_:** Add valid Xpath name as a source name with a valid namespace in the switch mediator. **_Then_:** Valid response message should be logged when the client request is routed to the relevant endpoint. |
| 5.1.1.2.2     | [N] Switch messages based on given valid Xpath with Invalid namespace | **_Given_:** Valid 
xml/json/csv request payload has be to sent. **_When_:** Add valid Xpath name as a source name with invalid namespace
 in the switch mediator. **_Then_:** Error message should be return from the server.|
| 5.1.1.2.3     | [N] Switch messages based on given valid Xpath with null namespace    | **_Given_:** Valid 
xml/json/csv request payload has be to sent. **_When_:** Add valid Xpath name as a source name with null namespace in
 the switch mediator. **_Then_:** Error message should be return from the server.|
| 5.1.1.2.4     | [N] Switch messages based on given valid Xpath with empty namespace    | **_Given_:** Valid 
xml/json/csv request payload has be to sent. **_When_:** Add valid Xpath name as a source name with empty namespace 
in the switch mediator. **_Then_:** Error message should be return from the server.|

**_Note_**
[N] Represent the Negative Test cases (incorrect behaviours that user can attempt)

