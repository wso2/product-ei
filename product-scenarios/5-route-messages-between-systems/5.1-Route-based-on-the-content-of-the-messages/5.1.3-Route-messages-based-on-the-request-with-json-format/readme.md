# 5.1.3 Route messages based on the request with json format

## When to use

This approach can be used to route message into the relevant systems based on the JSON content by using mediators.
Switch and Filter mediators are the possible mediators that can be used.

Reference : [JSON Support](https://docs.wso2.com/display/EI600/JSON+Support)

### Switch Mediator
Switch mediator behaves similar to the conventional switch-case statements available in any programming language.
Basically you can retrieve a string by calling  JSON path query and manipulate your message by matching
the returned string within each case statement.

Given below is the syntax of this mediator.

```
<switch source="[json-eval(JSON Path)]">
   <case regex="string">
      mediator+
   </case>+
   <default>
      mediator+
   </default>
</switch>

```

Reference : [Switch Mediator](https://docs.wso2.com/display/EI610/Switch+Mediator)


### Filter Mediator

Filter mediator behaves similar to the if-else logic. Filter mediator filters a message based on JSONPath.
Given below is the syntax of this mediator.

```
<filter (source="json_path")>
  mediator+
</filter>

```

Reference : [Filter Mediator](https://docs.wso2.com/display/EI610/Filter+Mediator)


## Sample use-case


## Pre-requisites


## Development guidelines


## Deployment guidelines


## Test cases

| Test Case ID  |                        Test Case	               |                                Test Description                |
| ------------- | ------------------------------------------------ | ---------------------------------------------------------------|
| 5.1.3.1     | Switch messages based on given JSONpath with valid expression   |   |
| 5.1.3.2     | [N] Switch messages based on given JSONpath with Invalid expression |  |
| 5.1.3.3     | [N] Switch messages based on given JSONpath with null expression   |  |
| 5.1.3.4     | [N] Switch messages based on given JSONpath with empty expression  |  |
| 5.1.3.5     | Filter messages based on a given JSONpath   |   |
| 5.1.3.6     | Filter messages based on a given JSONpath when JSON array exists |  |
| 5.1.3.7     | Filter messages based on JSONpath when multiple cases exists with a default case   |  |

**_Note_**
[N] Represent the Negative Test cases (incorrect behaviours that user can attempt)