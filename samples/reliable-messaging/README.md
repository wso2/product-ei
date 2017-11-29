# Reliable Messaging

This sample demonstrates how a message could be delivered to a downstream service reliably.A given message
will be sent to a http endpoint which will accept the message and place it into a message queue in the 
broker. The placed message will be consumed from the queue and will be routed to the relevant downstream 
service.

At an event where the downstream service is unavailable, the message will be persisted in the queue until the 
service becomes available.

# Environment Setup

1. Start message broker by running bin$ ./broker.sh
2. Deploy service by running bin$ ./integrator.sh 