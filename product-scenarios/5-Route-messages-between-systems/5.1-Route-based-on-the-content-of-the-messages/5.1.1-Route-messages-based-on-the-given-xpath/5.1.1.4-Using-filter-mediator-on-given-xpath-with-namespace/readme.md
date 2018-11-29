# 5.1.1.4 Using Filter Mediator on given Xpath with namespace

## When to use

If you need to provide an Xpath that has namespace, you can add it as a namespace in Filter mediator.

Given below is the syntax of how to add the it with namespace.

```
   <filter (source="xpath" regex="string") && mlns:test="[namespace]" >
     mediator+
   </filter>

```

## Sample use-case


## Pre-requisites


## Development guidelines


## Deployment guidelines


## Test cases

| Test Case ID  |                        Test Case	               |                                Test Description                |
| ------------- | ------------------------------------------------ | ---------------------------------------------------------------|
| 5.1.1.4.1     | Filter messages based on a given Xpath with namespaces   |   |
| 5.1.1.4.2     | [N] Filter messages based on a given Xpath with Invalid namespaces |  |
| 5.1.1.4.3     | [N] Switch messages based on given valid Xpath with null namespace   |  |
| 5.1.1.4.4     | [N] Filter messages based on empty source Xpath  |  |
| 5.1.1.4.5     | [N] Switch messages based on given valid Xpath with empty namespace   |  |

**_Note_**
[N] Represent the Negative Test cases (incorrect behaviours that user can attempt)
