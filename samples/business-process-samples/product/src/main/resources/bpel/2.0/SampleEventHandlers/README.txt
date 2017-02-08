There are three operations, exposed on the SampleEventHandlerProcess.bpel via "SampleEventHandlerProcess" portType. They are
1. createInstance - creates and instance. This operation need an "id" which will act as the correlation-id. Also it requires a "input" which will maintain a counter.
2. eventGenerator - Generate events, so the event-handler defined in BPEL process get executed. Once it trigerred the counter will be substract by one.
3. completeInstance - completes the instance. Here the client need to provide the relavant "id" aka the correlation-id to determine which instance to be completed.

This sample is capable of checking the performance in event-handlers and verify it's functionality by setting a counter value to a high level and invoking the bpel process cocurrently to check the reliability of the event handler.
