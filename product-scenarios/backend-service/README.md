# wso2-eiTestService
This implements a backend service used in product-ei tests. Service has been implemented in [Ballerina lang](https://ballerina.io/). This 
is used to simulate a real backend in the test scenarios.

# wso2-eiTestService features
- Handles JSON payloads.
- Handles XML payloads.
- Handles CSV paylods.
- Simulate backend latencies.
- Provide responses with specified HTTP status codes.

# wso2-eiTestService deploying instructions
- Fork and clone the repository.
- Build the **testServices** ballerina module using the command 'ballerina build testServices'. This will create a testServices.balx file in the 
target directory.
- Run the .balx file using the command 'ballerina run testServices.balx'. 
- Service configurations(base path, port etc) can be modified in the testService.bal file.
