These two process artifacts; Async-Client.zip and Async-Server.zip contains two bpel processes called Client.bpel and Server.bpel. 
Client.bpel invokes Server.bpel asynchronously.
The correlation is maintained via a property called id transferred with each interaction.

Note - Please modify the endpoints which call from client and server to client in-order to work.
This process pair can be used to trouble-shoot message-correlation, long running processes etc. 
