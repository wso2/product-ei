# 2.1.3-Protocol translation of systems which has varring HTTP chunking behaviours

## When to use
Protocol translation of systems which has varring HTTP chunking behaviours.

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
| 2.1.3.1  | Receive from chunking supported upstream and deliver over to chunking supported downstream                                             |
| 2.1.3.2  | Receive from chunking supported upstream and deliver over to content-length required downstream                                        |
| 2.1.3.3  | Receive from a content-length required upstream and deliver over to content-length required downstream                                 |
| 2.1.3.4  | Receive from content-length required upstream and deliver over to chunking supported downstream                                        |
| 2.1.3.5  | Receive from a upstream which doesn't contain either content-length or transfer-encoding header in the request                         |
| 2.1.3.6  | Receive a response from the downstream which doesn't contain either content-length or transfer-encoding header in the response message |

