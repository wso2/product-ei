# 5.1.1.3 Using Filter Mediator on given Xpath

## Business use case narrative

Filter mediator behaves similar to the if-else logic. Filter mediator filters a message based on an XPath, JSONPath or a regular expression.
There are two modes of operation

- Specifies the XPath (boolean expression), return true or false
- XPath will be matched against the regular expression return true or false

Given below is the syntax of this mediator.

```
<filter (source="xpath" regex="string") | xpath="xpath">
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
| 5.1.1.3.1     | Filter messages based on valid source Xpath   |   |
| 5.1.1.3.2     | [N] Filter messages based on Invalid source Xpath |  |
| 5.1.1.3.3     | [N] Filter messages based on null source Xpath   |  |
| 5.1.1.3.4     | [N] Filter messages based on empty source Xpath  |  |
| 5.1.1.3.5     | Filter messages based on a given Xpath by ignoring the case sensitivity   |  |

**_Note_**
[N] Represent the Negative Test cases (incorrect behaviours that user can attempt)