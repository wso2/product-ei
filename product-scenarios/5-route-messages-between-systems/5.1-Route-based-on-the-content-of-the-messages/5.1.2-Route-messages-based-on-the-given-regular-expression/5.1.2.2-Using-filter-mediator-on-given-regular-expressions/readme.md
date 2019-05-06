# 5.1.2.2 Using Filter Mediator on given regular expressions

## When to use

If you want to route the messages based on the regular expression, you can do it by using Filter mediator as well.
Filter mediator behaves as if-else logic. You can retrieve a string by calling a Regular expression and it match with the source value.
Then returned string within each case statement.

Given below is the syntax of how regular expression can be add to the switch mediators.

```
<filter regex="<regular_expression>" source="<source_value>">
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
| 5.1.2.1.1     | Filter messages based on a valid regex   |   |
| 5.1.2.1.2     | [N] Filter messages based on a Invalid regex |  |
| 5.1.2.1.3     | [N] Filter messages based on a empty regex   |  |
| 5.1.2.1.4     | [N] Filter messages based on a null regex  |  |
| 5.1.2.1.5     | Filter messages when multiple cases exists with different regular expressions   |  |

**_Note_**
[N] Represent the Negative Test cases (incorrect behaviours that user can attempt)