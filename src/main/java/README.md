# Lab 2
### Author: Filipescu Mihail
#### Group: FAF-181

## Implementation:<br>

### Transport Layer:<br>
For the tranport layer I have Implemented error correction ( hamming code) and a very simple handshake for connection.
Hamming is added in the EnhancedUdp class/protocol and not in the actual TP protocol because reasons.
The TP contains obviously the logic for TP packet creation and digesting the packets that are recived.

### Session Layer:<br>
The session protocol is somewhat similar to the TLS. Innitialy if connection is not secure it makes it secure via 3-way handshake that looks like this :
1. ClientHello -> 
2. server responds with ServerHello that contains diffie - hellman parameters(p, g and generated pubKey) -> 
3. client recives it generates pubKey and premaster then responding with ClientFinHandshake that conains pubKey -> 
4. Server recives pubKey and generates its premaster. 
All the next messages are encrypred with AES.

This session protocol therefore can be of 2 types. Handshake and Data. Handshake is implemented as an separate protocol. 

### Application Layer:<br>
The application protocol again can be of 2 types. Simple message tranmission and Control. Control is implemented as a separate protocol, and defines some control 
"actions" like client pressing the exit button or used to notify the other part about some event. 
There possbile controls:
1. CALL
2. EXIT
3. NOTIFY_CALL
4. NOTIFY_ACCEPT_CALL

Both the client and server can generate or process controls. 

## Client/Server logic:<br>
The Server(telephone) is a similar to a state machine. It has states like: idle, dialing, ringing and talking. The client makes control operations to change the server(telephone) to other states. For ex: innitially the phone is idle. The client can make CALL control to call the otther client. The server will change into dialing state and will inform the other phone(server) that it is called. Then the server2 will inform the client2 that it is called. The client 2 has 2 options : make a CALL or
EXIT Control. If CALL is made then the serve2 will inform the serv1(that will transition in talking state and inform client1 that they are talking). Serv2 will
also transition in talking state. in this state clients can talk.

So in general:
Client can generate CALL, EXIT Controls or some message. CLient can digest or "it cares" only NOTIFY_CALL and NOTIFY_ACCEPT_CALL.
Server can generate NOTIFY_CALL, NOTIFY_ACCEPT_CALL and EXIT. Server can digest all controls. It also pass messages if certain conditions are met.
 
There are other details that I will not cover, feel free to ask me.

All clients are running on 2 threads in order to make possible speaking and listening at the same time.

Server has 3 threads however only 2 are needed. One thread is for debugging.


