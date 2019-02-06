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

















