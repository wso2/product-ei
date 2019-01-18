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

## Sample use-case


## Pre-requisites


## Development guidelines


## Deployment guidelines


## Test cases

| Test Case ID  |                        Test Case	               |                                Test Description                |
| ------------- | ------------------------------------------------ | ---------------------------------------------------------------|
| 5.1.2.1.1     | Switch messages based on a valid regex   |   |
| 5.1.2.1.2     | [N] Switch messages based on a Invalid regex |  |
| 5.1.2.1.3     | [N] Switch messages based on a empty regex   |  |
| 5.1.2.1.4     | [N] Switch messages based on a null regex  |  |
| 5.1.2.1.5     | Switch messages based on regex when multiple cases exists with a default case   |  |

**_Note_**
[N] Represent the Negative Test cases (incorrect behaviours that user can attempt)













