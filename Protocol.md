# Protocol

The client library communicates with Meteor servers over the [Distributed Data Protocol](https://www.meteor.com/ddp) (DDP).

## Debugging

### Initialization

 1. Run [this online tool](http://software.hixie.ch/utilities/js/websocket/) that you will use to connect to your *local* Meteor instance
 1. Enter the WebSocket URL (and leave the protocol field empty):

    `ws://localhost:3000/websocket`

 1. Initialize the DDP connection (using an older protocol that does not require `ping`/`pong` events):

    `{"msg":"connect","version":"pre1","support":["pre1"]}`

### Exemplary commands

 * Create a new record (document) `users/jane` on the server:

   ```javascript
   {"msg":"method","method":"/database/update","params":[{"_id":"/users/jane"}, {"_id":"/users/jane","_priority":null,"_value":"Jane Doe"},{"upsert":true}],"id":"client-event-1"}
   ```

 * Subscribe to a single location or path (document) from the server:

   ```javascript
   {"msg":"sub","id":"subscription-1","name":"node","params":["/users", true]}
   ```

 * Delete a the document at a certain  (child of root collection) by ID:

   ```javascript
   {"msg":"method","method":"/database/remove","params":[{"_id":"/users/john"}],"id":"client-event-2"}
   ```

 * Call some arbitrary method defined on the server:

   ```javascript
   {"msg":"method","method":"myMethodName","params":[],"id":"client-event-3"}
   ```

### Verifying against a Meteor client app written in JavaScript

 * In order to look up the contents of any collection, execute the following JavaScript call on the console:

   ```javascript
   // var MyCollection = new Meteor.Collection("myCollection");
   JSON.stringify(MyCollection._collection._docs._map);
   ```
