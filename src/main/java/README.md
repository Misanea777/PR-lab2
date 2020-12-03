# Lab 2
### Author: Filipescu Mihail
#### Group: FAF-181

## Implementation:<br>
For the tranport layer I have Implemented error correction ( hamming code) and a very simple handshake for connection.
Hamming is added in the EnhancedUdp class/protocol and not in the actual TP protocol because reasons.
The TP contains obviously the logic for TP packet creation and digesting the packets that are recived.

The session protocol is somewhat similar to the TLS. Innitialy if connection is not secure it makes it secure via 3-way handshake that looks like this :
ClientHello -> server responds with ServerHello that contains diffie - hellman parameters(p, g and generated pubKey) -> client recives it generates pubKey and premaster
then responding with ClientFinHandshake that conains pubKey -> Server recives pubKey and generates its premaster. All the next messages are encrypred with AES.

This session protocol therefore can be of 2 types. Handshake and Data. Handshake is implemented as an separate protocol. 

The application protocol again can be of 2 types. Simple message tranmission and Control. Control is implemented as a separate protocol, and defines some control 
"actions" like client pressing the exit button. The controls are used

The Server(telephone) is a similar to a state machine. It has states like: idle, dialing, ringing and talking. The client makes control operations to change the server(telephone) to other states. For ex: innitially the phone is idle. The client can make CALL control to call the otther client. The server will change into dialing state and will inform the other phone(server) that it is called. Then the server2 will inform the client2 that it is called. The client 2 has 2 options : make a CALL or
EXIT Control. If CALL is made then the serve2 will inform the serv1(that will transition in talking state and inform client1 that they are talking).

There are other details that I will not cover, feel free to ask me.

All clients are running on 2 threads in order to make possible speaking and listening at the same time.

Server has 3 threads however only 2 are needed. One thread is for debugging.


