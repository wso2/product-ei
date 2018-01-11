# Online Holiday Packages Booking System.

This scenario is about an online Holiday Package Booking System.
Holiday Package Booking systems may provide the flights, hotels and vehicle rental facilities to a client as requested.
In this sample client provides 'departure date', 'return date', 'from where', 'to where' and the 'vehicle type' he needs in the request.
After analyzing given information from the system he will be provided with the details with the most suitable holiday package.

Service will send booking requests parallely to multiple airline companies, vehicle rental companies and hotels.
Service will include the vehicle from the first rental company to respond and the flight from the airline company which is offering cheapest price to the holiday package.
Moreover service will send requests to the nearby hotels and will include the nearest hotel into the package after analyzing responses coming from the hotels.

## Services

Our main focus in this sample is on Holiday Package Service.
However for simplicity other sample backend services are also written in ballerina.
Please note that these are dummy services implemented to mock a specific functionality.

### Backend Services

AirlineService, CarRentalService and HotelService contains resources which are responsible for sending flight details, car rental details and hotel details to the HolidayPackage service.

### Holiday Package Service

Holiday Package Service contains a single resource to handle all the above mentioned scenarios.
From this service multiple requests will be sent to the backend to retrieve details about flights, car rentals and hotels.
Requests will be sent parallely to relevant backend endpoints.

For example, a request containing departure date, return date, from, to will be sent to multiple flight reservation companies.
To send requests parallely, tasks will be delegated to parallely running threads using workers.
The airline company which is offering cheapest price will be taken into consideration to be included in the package.

A request containing vehicletype, from and to dates will be sent to multiple car rental companies in a similar way.
The car rental company which is first to respond will be included in the package.

For hotels also requests are sent in a similar way.
In here also responses coming from the hotels will be joined together to identify the hotel which is located in the nearest place to the requested location.

## How to run the sample

The services related to service-orchestration-with-parallel-processing could be executed in the following manner,

- Navigate to the directory <EI_HOME>/samples/service-orchestration-with-parallel-processing/services
- Execute the following,

```
samples/service-orchestration-with-parallel-processing/services$ ../../../bin/integrator.sh run samples/holiday/
```
or execute

```
bin$ ./integrator.sh ../samples/service-orchestration-with-parallel-processing/holidayPackage.balx
```

What is done from above 2 commands are quite similar. Select only one and execute it. It will deploy all the services used in our scenario.

## Invoke the service

Run

```
curl -v "http://localhost:9090/web/holiday/?depart=02/01/2018&returnDate=05/01/2018&from=colombo&to=changi&vehicleType=car&location=changi"
```
### Observations

Response received:

```
< HTTP/1.1 200 OK
< Content-Type: application/json
< Content-Length: 273
<
{
	"Flight": {
		"Company": "Asiana",
		"Departure Date": "02/01/2018",
		"From": "colombo",
		"To": "changi"
	},
	"Vehicle": {
		"Company": "DriveSG",
		"VehicleType": "car",
		"Price per Day": 50
	},
	"Hotel": {
		"Hotel": "elizabeth",
		"From Date": "02/01/2018",
		"To Date": "05/01/2018",
		"Distance to Location(Miles)": 5
	}
}
```

### Modifying the code

All the services related to this sample are located in <EI_HOME>/samples/service-orchestration-with-parallel-processing/services directory.

Following are the available ballerina program files.

* [CarRentalService.bal](services/samples/holiday/CarRentalService.bal)
* [AirlineService.bal](services/samples/holiday/AirlineService.bal)
* [HotelService.bal](services/samples/holiday/HotelService.bal)
* [HolidayPackage.bal](services/samples/holiday/HolidayPackage.bal)

First 3 bal files are dummy services which are used to mock the functionality of a backend service.

[HolidayPackage.bal](services/samples/holiday/holidayPackage.bal) is the main service which we focus on this sample.

We can simply edit the configurations as you prefer and execute the sample again with modified
content with the following command.

```
samples/service-orchestration-with-parallel-processing/services$ ../../../bin/integrator.sh samples/holiday
```

We can test the sample by executing the same set of steps given above.

