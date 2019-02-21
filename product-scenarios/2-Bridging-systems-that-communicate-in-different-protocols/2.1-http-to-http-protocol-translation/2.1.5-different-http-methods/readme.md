# 2.1.5-Protocol translation of HTTP systems which uses different HTTP methods

## When to use
Protocol translation of HTTP systems which uses different HTTP methods.

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
| 2.1.5.1  | Receive a GET request and deliver a GET request to the downstream    |
| 2.1.5.2  | Receive a GET request and deliver a POST request to the downstream   |
| 2.1.5.3  | Receive a GET request and deliver a PUT request to the downstream  |
| 2.1.5.4  | Receive a GET request and deliver a DELETE request to the downstream   |
| 2.1.5.5  | Receive a GET request and deliver a OPTIONS request to the downstream   |
| 2.1.5.6  | Receive a GET request and deliver a PATCH request to the downstream   |
| 2.1.5.7  | Receive a POST request and deliver a GET request to the downstream  |
| 2.1.5.8  | Receive a POST request and deliver a POST request to the downstream   |
| 2.1.5.9  | Receive a POST request and deliver a PUT request to the downstream   |
| 2.1.5.10 | Receive a POST request and deliver a DELETE request to the downstream   |
| 2.1.5.11  | Receive a POST request and deliver a OPTIONS request to the downstream  |
| 2.1.5.12  | Receive a POST request and deliver a PATCH request to the downstream  |
| 2.1.5.13  | Receive a PUT request and deliver a GET request to the downstream   |
| 2.1.5.14  | Receive a PUT request and deliver a POST request to the downstream   |
| 2.1.5.15 | Receive a PUT request and deliver a PUT request to the downstream   |
| 2.1.5.16  | Receive a PUT request and deliver a DELETE request to the downstream  |
| 2.1.5.17  | Receive a PUT request and deliver a OPTIONS request to the downstream  |
| 2.1.5.18 | Receive a PUT request and deliver a PATCH request to the downstream   |
| 2.1.5.19  | Receive a DELETE request and deliver a GET request to the downstream  |
| 2.1.5.20  | Receive a DELETE request and deliver a POST request to the downstream  |
| 2.1.5.21  | Receive a DELETE request and deliver a PUT request to the downstream    |
| 2.1.5.22 | Receive a DELETE request and deliver a DELETE request to the downstream   |
| 2.1.5.23  | Receive a DELETE request and deliver a OPTIONS request to the downstream  |
| 2.1.5.24  | Receive a DELETE request and deliver a PATCH request to the downstream   |
| 2.1.5.25  | Receive a OPTIONS request and deliver a GET request to the downstream   |
| 2.1.5.26  | Receive a OPTIONS request and deliver a POST request to the downstream  |
| 2.1.5.27  | Receive a OPTIONS request and deliver a PUT request to the downstream  |
| 2.1.5.28  | Receive a OPTIONS request and deliver a DELETE request to the downstream   |
| 2.1.5.29  | Receive a OPTIONS request and deliver a OPTIONS request to the downstream   |
| 2.1.5.30 | Receive a OPTIONS request and deliver a PATCH request to the downstream   |
| 2.1.5.31  | Receive a PATCH request and deliver a GET request to the downstream  |
| 2.1.5.32  | Receive a PATCH request and deliver a POST request to the downstream  |
| 2.1.5.33  | Receive a PATCH request and deliver a PUT request to the downstream   |
| 2.1.5.34  | Receive a PATCH request and deliver a DELETE request to the downstream   |
| 2.1.5.35 | Receive a PATCH request and deliver a OPTIONS request to the downstream   |
| 2.1.5.36  | Receive a PATCH request and deliver a PATCH request to the downstream  |
| 2.1.5.37  | Receive a POST request without the message body and deliver it to the downstream service  |
| 2.1.5.38 | Receive a PUT request without the message body and deliver it to the downstream service   |
| 2.1.5.39  | Receive a DELETE request with a message body and deliver it to the downstream service  |
