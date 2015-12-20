# Protocol

The client library communicates with Meteor servers over the [Distributed Data Protocol](https://www.meteor.com/ddp) (DDP).

## Debugging

### Initialization

 1. Run [this online tool](http://software.hixie.ch/utilities/js/websocket/) that you will use to connect to your *local* Meteor instance
 2. Enter the WebSocket URL (and leave the protocol field empty):

    `ws://localhost:3000/websocket`

 3. Initialize the DDP connection (using an older protocol that does not require `ping`/`pong` events):

    `{"msg":"connect","version":"pre1","support":["pre1"]}`

### Exemplary commands

 4. Create a new record (document) `users/jane` on the server:

    `{"msg":"method","method":"/database/update","params":[{"_id":"/users/jane"}, {"_id":"/users/jane","_priority":null,"_value":"Jane Doe"},{"upsert":true}],"id":"client-event-1"}`

 5. Subscribe to a single location or path (document) from the server:

    `{"msg":"sub","id":"subscription-1","name":"node","params":["/users", true]}`

 6. Delete a the document at a certain  (child of root collection) by ID:

    `{"msg":"method","method":"/database/remove","params":[{"_id":"/users/john"}],"id":"client-event-2"}`

 7. Get the current server time:

    `{"msg":"method","method":"getServerTime","params":[],"id":"client-event-3"}`

 8. Reset (clear) the whole database:

    `{"msg":"method","method":"reset","params":[],"id":"client-event-4"}`
