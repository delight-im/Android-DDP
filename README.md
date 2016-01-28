# Android-DDP

This library implements the [Distributed Data Protocol](https://www.meteor.com/ddp) (DDP) from Meteor for clients on Android.

Connect your native Android apps, written in Java, to apps built with the [Meteor](https://www.meteor.com/) framework and build real-time features.

## Motivation

 * Have you built a web application with Meteor?
   * Using this library, you can build native Android apps that can talk to your Meteor server and web application.
 * Are you primarily an Android developer (who has never heard of Meteor)?
   * With "Android-DDP", you can use a Meteor server as your backend for real-time applications on Android.
 * Doesn't Meteor provide built-in features for Android app development already?
   * With Meteor's built-in features, your Android app will be written in HTML, CSS and JavaScript, wrapped in a `WebView`. It will not be a *native* app.
   * By using this library, however, you can write native Android apps in Java while still using Meteor as your real-time backend.

## Requirements

 * Android 2.3+

## Installation

 * Add this library to your project
   * Declare the Gradle repository in your root `build.gradle`

     ```gradle
     allprojects {
         repositories {
             maven { url "https://jitpack.io" }
         }
     }
     ```

   * Declare the Gradle dependency in your app module's `build.gradle`

     ```gradle
     dependencies {
         compile 'com.github.delight-im:Android-DDP:v2.1.0'
     }
     ```

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

       public void onDisconnect() { }

       public void onDataAdded(String collectionName, String documentID, String newValuesJson) { }

       public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) { }

       public void onDataRemoved(String collectionName, String documentID) { }

       public void onException(Exception e) { }

   }
   ```

 * Singleton access
   * Creating an instance at the beginning

     ```
     MeteorSingleton.createInstance(this, "ws://example.meteor.com/websocket")
     // instead of
     // new Meteor(this, "ws://example.meteor.com/websocket")
     ```

   * Accessing the instance afterwards (across `Activity` instances)

     ```
     MeteorSingleton.getInstance()
     // instead of
     // mMeteor
     ```

   * Registering a callback

     ```
     MeteorSingleton.getInstance().setCallback(this);
     // instead of
     // mMeteor.setCallback(this);
     ```

   * Unregistering a callback

     `MeteorSingleton.getInstance().unsetCallback(this);`

   * All other API methods can be called on `MeteorSingleton.getInstance()` just as you would do on any other `Meteor` instance, as documented here with `mMeteor`

 * Inserting data into a collection

   ```
   Map<String, Object> values = new HashMap<String, Object>();
   values.put("_id", "my-id");
   values.put("some-key", "some-value");

   mMeteor.insert("my-collection", values);
   // or
   // mMeteor.insert("my-collection", values, new ResultListener() { });
   ```

 * Updating data in a collection

   ```
   Map<String, Object> query = new HashMap<String, Object>();
   query.put("_id", "my-id");

   Map<String, Object> values = new HashMap<String, Object>();
   values.put("some-key", "some-value");

   mMeteor.update("my-collection", query, values);
   // or
   // mMeteor.update("my-collection", query, values, options);
   // or
   // mMeteor.update("my-collection", query, values, options, new ResultListener() { });
   ```

 * Deleting data from a collection

   ```
   mMeteor.remove("my-collection", "my-id");
   // or
   // mMeteor.remove("my-collection", "my-id", new ResultListener() { });
   ```

 * Subscribing to data from the server

   ```
   String subscriptionId = mMeteor.subscribe("my-subscription");
   // or
   // String subscriptionId = mMeteor.subscribe("my-subscription", new Object[] { arg1, arg2 });
   // or
   // String subscriptionId = mMeteor.subscribe("my-subscription", new Object[] { arg1, arg2 }, new SubscribeListener() { });
   ```

 * Unsubscribing from a previously established subscription

   ```
   mMeteor.unsubscribe(subscriptionId);
   // or
   // mMeteor.unsubscribe(subscriptionId, new UnsubscribeListener() { });
   ```

 * Calling a custom method defined on the server

   ```
   mMeteor.call("myMethod");
   // or
   // mMeteor.call("myMethod", new Object[] { arg1, arg2 });
   // or
   // mMeteor.call("myMethod", new ResultListener() { });
   // or
   // mMeteor.call("myMethod", new Object[] { arg1, arg2 }, new ResultListener() { });
   ```

 * Disconnect from the server

   `mMeteor.disconnect()`

 * Creating a new account (requires `accounts-password` package)

   ```
   mMeteor.registerAndLogin("john", "john.doe@example.com", "password", new ResultListener() { });
   // or
   // mMeteor.registerAndLogin("john", "john.doe@example.com", "password", profile, new ResultListener() { });
   ```

 * Signing in with an existing username (requires `accounts-password` package)

   `mMeteor.loginWithUsername("john", "password", new ResultListener() { });`

 * Signing in with an existing email address (requires `accounts-password` package)

   `mMeteor.loginWithEmail("john.doe@example.com", "password", new ResultListener() { });`

 * Check if the client is currently logged in (requires `accounts-password` package)

   `mMeteor.isLoggedIn()`

 * Get the client's user ID (if currently logged in) (requires `accounts-password` package)

   `mMeteor.getUserId()`

 * Logging out (requires `accounts-password` package)

   ```
   mMeteor.logout();
   // or
   // mMeteor.logout(new ResultListener() { });
   ```

 * Checking whether the client is connected

   `mMeteor.isConnected()`

 * Manually attempt to re-connect (if necessary)

   `mMeteor.reconnect()`

## Contributing

All contributions are welcome! If you wish to contribute, please create an issue first so that your feature, problem or question can be discussed.

## Dependencies

 * [TubeSock](https://github.com/firebase/TubeSock) — [Firebase](https://github.com/firebase) — [MIT License](http://firebase.mit-license.org/)
 * [Jackson Core](https://github.com/FasterXML/jackson-core) — [FasterXML](https://github.com/FasterXML) — [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
 * [Jackson ObjectMapper](https://github.com/FasterXML/jackson-databind) — [FasterXML](https://github.com/FasterXML) — [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Further reading

 * [DDP — Specification](https://github.com/meteor/meteor/blob/devel/packages/ddp/DDP.md)
 * [Jackson — Documentation](http://wiki.fasterxml.com/JacksonDocumentation)

## Disclaimer

This project is neither affiliated with nor endorsed by Meteor.

## License

```
Copyright (c) delight.im <info@delight.im>

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
