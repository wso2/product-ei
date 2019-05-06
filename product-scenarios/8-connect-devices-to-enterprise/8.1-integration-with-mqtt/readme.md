# 8.1-Integration with MQTT

## Business use case narrative


MQTT (Message Queue Telemetry Transport) is a lightweight messaging protocol designed for lightweight communication between
devices and computer systems. WSO2 EI can listen for MQTT messages sent from a device and also generate MQTT events for a device. 
Thus, WSO2 EI can be used to enrich MQTT messages. It is possible to get enrich values from different sources like 
an external service, database, or some static value. 

WSO2 EI can receive a MQTT message and duplicate it for multiple devices. It is possible to send these events to multiple devices 
in parallel or one after the other in an order. Thus WSO2 EI can be used to broadcast a MQTT message to multiple devices. 

Sometime you want to invoke a SOAP endpoint or a REST service when an event from a device is received. These usecases are
discussed under section "1-integrating-systems-that-communicate-in-heterogeneous-message-formats". 

Here it is discussed how to use EI as a MQTT subscriber and a MQTT publisher. Note that in the middle you can use 
mediators to transform/enrich/clone the messages. 



## Sub-Scenarios

- [8.1.1-EI-as-a-MQTT-subscriber](8.1.1-EI-as-a-MQTT-subscriber)
- [8.1.2-EI-as-a-MQTT-events-publisher](8.1.2-EI-as-a-MQTT-events-publisher)



