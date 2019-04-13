## 5.1.1.1-Using-switch-mediator-on-given-xpath

| Test Case ID  |                        Test Case	               |                                Test Description                |      Status    |
| ------------- | ------------------------------------------------ | ---------------------------------------------------------------| -------------- |
| 5.1.1.1.1     | Switch messages based on given Xpath with valid case name   | **_Given_:** Valid xml/json/csv request payload has be to sent. **_When_:** Add valid Xpath name as a source name in the switch mediator. **_Then_:** Valid response message should be logged when the client request is routed to the relevant endpoint. |   Automated  |
| 5.1.1.1.2     | [N] Switch messages based on given Xpath with Invalid case name | **_Given_:** Valid xml/json/csv request payload has be to sent. **_When_:** Add Invalid Xpath name as a source name in the switch mediator. **_Then_:** Error message should be return from the server.|   Automated  |
| 5.1.1.1.3     | Switch messages based on Xpath when multiple cases exists with a default case  | **_Given_:** Valid xml/json/csv request payload has be to sent. **_When_:** Add multiple default cases in switch mediator with valid Xpath name as a source name. **_Then_:** Valid response message should be return from the server.|   Not Started  |
| 5.1.1.1.4     | Switch messages based on a given Xpath by ignoring the case sensitivity  | **_Given_:** Valid xml/json/csv request payload has be to sent. **_When_:** Add valid Xpath name with case sensitivity in the switch mediator . **_Then_:** Valid response message should be logged when the client request is routed to the relevant endpoint. |   Not Started  |

**_Note_** 
- [N] Represent the Negative Test cases (Incorrect behaviours that user can attempt)
