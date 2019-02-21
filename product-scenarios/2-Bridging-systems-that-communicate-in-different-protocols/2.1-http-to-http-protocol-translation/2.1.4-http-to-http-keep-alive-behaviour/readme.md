# 2.1.4-Protocol translation of systems which has varring HTTP keep-alive behaviour

## When to use
Protocol translation of systems which has varring HTTP keep-alive behaviour.

## Sample use-case
--sample use-case

## Supported versions

## Pre-requisites

## Development guidelines

## REST API (if available)
N/A

## Deployment guidelines

## Test cases

| ID | Summary |
| ------------- | ------------- |
| 2.1.4.1  | Receive from keep-alive enabled upstream and deliver over to keep-alive enabled downstream    |
| 2.1.4.2  | Receive from keep-alive enabled upstream and deliver over to keep-alive disabled downstream   |
| 2.1.4.3  | Receive from keep-alive disabled upstream and deliver over to keep-alive disabled downstream  |
| 2.1.4.4  | Receive from keep-alive disabled upstream and deliver over to keep-alive enabled downstream   |

