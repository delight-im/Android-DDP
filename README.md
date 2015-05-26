# Android-DDP

This library implements the [Distributed Data Protocol](https://www.meteor.com/ddp) (DDP) from Meteor for clients on Android.

Connect your native Android apps, written in Java, to apps built with the [Meteor](https://www.meteor.com/) framework and build real-time features.

## Installation

 * Add this library to your project
   * Include one of the [JARs](Android/JARs) in your `libs` folder
   * or
   * Copy the Java package to your project's source folder
   * or
   * Create a new library project from this repository and reference it in your project
 * Add the Internet permission to your app's `AndroidManifest.xml`:

    `<uses-permission android:name="android.permission.INTERNET" />`

## Usage

 * Creating a new instance of the DDP client

   ```
   public class MyActivity extends Activity implements MeteorCallback {
   
       private Meteor mMeteor;
   
       @Override
	   protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
		   
		   // ...
		   
		   mMeteor = new Meteor(this, "ws://example.meteor.com/websocket");
		   mMeteor.setCallback(this);
	   }
	   
	   public void onConnect(boolean signedInAutomatically) { }
	   
	   public void onDisconnect(int code, String reason) { }
	   
	   public void onDataAdded(String collectionName, String documentID, String newValuesJson) { }
	   
	   public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) { }
	   
	   public void onDataRemoved(String collectionName, String documentID) { }
	   
	   public void onException(Exception e) { }
   
   }
   ```

 * Inserting data into a collection

   ```
   Map<String, Object> values = new HashMap<String, Object>();
   values.put("_id", "my-id");
   values.put("some-key", "some-value");
   
   mMeteor.insert("my-collection", values);
   ```

 * Updating data in a collection

   ```
   Map<String, Object> query = new HashMap<String, Object>();
   query.put("_id", "my-id");
   
   Map<String, Object> values = new HashMap<String, Object>();
   values.put("some-key", "some-value");
   
   mMeteor.update("my-collection", query, values);
   ```

 * Deleting data from a collection

   `mMeteor.remove("my-collection", "my-id");`

 * Subscribing to data from the server

   `String subscriptionId = mMeteor.subscribe("my-subscription");`

 * Unsubscribing from a previously established subscription

   `mMeteor.unsubscribe(subscriptionId);`

 * Calling a custom method defined on the server

   `mMeteor.call("myMethod");`

 * Disconnect from the server

   `mMeteor.disconnect()`

 * Creating a new account (requires `accounts-password` package)

   `mMeteor.registerAndLogin("john", "john.doe@example.com", "password", new ResultListener() { });`

 * Signing in with an existing username (requires `accounts-password` package)

   `mMeteor.loginWithUsername("john", "password", new ResultListener() { });`

 * Signing in with an existing email address (requires `accounts-password` package)

   `mMeteor.loginWithEmail("john.doe@example.com", "password", new ResultListener() { });`

 * Checking whether the client is connected

   `mMeteor.isConnected()`

 * Manually attempt to re-connect (if necessary)

   `mMeteor.reconnect()`

## Contributing

All contributions are welcome! If you wish to contribute, please create an issue first so that your feature, problem or question can be discussed.

## Dependencies

 * [Autobahn|Android](http://autobahn.ws/android/gettingstarted.html#add-jars-to-your-project) — [Tavendo](https://github.com/tavendo/AutobahnAndroid) — [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
 * [Jackson Core](http://autobahn.ws/android/gettingstarted.html#add-jars-to-your-project) — [FasterXML](https://github.com/FasterXML/jackson-core) — [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
 * [Jackson ObjectMapper](http://autobahn.ws/android/gettingstarted.html#add-jars-to-your-project) — [FasterXML](https://github.com/FasterXML/jackson-core) — [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Further reading

 * [DDP — Specification](https://github.com/meteor/meteor/blob/devel/packages/ddp/DDP.md)
 * [Jackson — Documentation](http://wiki.fasterxml.com/JacksonDocumentation)
 * [Autobahn|Android — API documentation](http://autobahn.ws/android/_gen/packages.html)

## Disclaimer

This project is neither affiliated with nor endorsed by Meteor or Firebase.

## License

```
Copyright 2014 www.delight.im <info@delight.im>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
