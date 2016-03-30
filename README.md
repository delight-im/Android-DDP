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
         compile 'com.github.delight-im:Android-DDP:v3.1.0'
     }
     ```

 * Add the Internet permission to your app's `AndroidManifest.xml`:

    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    ```

## Usage

 * Creating a new instance of the DDP client

   ```java
   public class MyActivity extends Activity implements MeteorCallback {

       private Meteor mMeteor;

       @Override
       protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);

           // ...

           // create a new instance
           mMeteor = new Meteor(this, "ws://example.meteor.com/websocket");

           // register the callback that will handle events and receive messages
           mMeteor.addCallback(this);

           // establish the connection
           mMeteor.connect();
       }

       public void onConnect(boolean signedInAutomatically) { }

       public void onDisconnect() { }

       public void onDataAdded(String collectionName, String documentID, String newValuesJson) {
           // parse the JSON and manage the data yourself (not recommended)
           // or
           // enable a database (see section "Using databases to manage data") (recommended)
       }

       public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
           // parse the JSON and manage the data yourself (not recommended)
           // or
           // enable a database (see section "Using databases to manage data") (recommended)
       }

       public void onDataRemoved(String collectionName, String documentID) {
           // parse the JSON and manage the data yourself (not recommended)
           // or
           // enable a database (see section "Using databases to manage data") (recommended)
       }

       public void onException(Exception e) { }

       @Override
       public void onDestroy() {
           mMeteor.disconnect();
           mMeteor.removeCallback(this);
           // or
           // mMeteor.removeCallbacks();

           // ...

           super.onDestroy();
       }

   }
   ```

 * Singleton access
   * Creating an instance at the beginning

     ```java
     MeteorSingleton.createInstance(this, "ws://example.meteor.com/websocket")
     // instead of
     // new Meteor(this, "ws://example.meteor.com/websocket")
     ```

   * Accessing the instance afterwards (across `Activity` instances)

     ```java
     MeteorSingleton.getInstance()
     // instead of
     // mMeteor
     ```

   * All other API methods can be called on `MeteorSingleton.getInstance()` just as you would do on any other `Meteor` instance, as documented here with `mMeteor`

 * Registering a callback

   ```java
   // MeteorCallback callback;
   mMeteor.addCallback(callback);
   ```

 * Unregistering a callback

   ```java
   mMeteor.removeCallbacks();
   // or
   // // MeteorCallback callback;
   // mMeteor.removeCallback(callback);
   ```

 * Inserting data into a collection

   ```java
   Map<String, Object> values = new HashMap<String, Object>();
   values.put("_id", "my-id");
   values.put("some-key", "some-value");

   mMeteor.insert("my-collection", values);
   // or
   // mMeteor.insert("my-collection", values, new ResultListener() { });
   ```

 * Updating data in a collection

   ```java
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

   ```java
   mMeteor.remove("my-collection", "my-id");
   // or
   // mMeteor.remove("my-collection", "my-id", new ResultListener() { });
   ```

 * Subscribing to data from the server

   ```java
   String subscriptionId = mMeteor.subscribe("my-subscription");
   // or
   // String subscriptionId = mMeteor.subscribe("my-subscription", new Object[] { arg1, arg2 });
   // or
   // String subscriptionId = mMeteor.subscribe("my-subscription", new Object[] { arg1, arg2 }, new SubscribeListener() { });
   ```

 * Unsubscribing from a previously established subscription

   ```java
   mMeteor.unsubscribe(subscriptionId);
   // or
   // mMeteor.unsubscribe(subscriptionId, new UnsubscribeListener() { });
   ```

 * Calling a custom method defined on the server

   ```java
   mMeteor.call("myMethod");
   // or
   // mMeteor.call("myMethod", new Object[] { arg1, arg2 });
   // or
   // mMeteor.call("myMethod", new ResultListener() { });
   // or
   // mMeteor.call("myMethod", new Object[] { arg1, arg2 }, new ResultListener() { });
   ```

 * Disconnect from the server

   ```java
   mMeteor.disconnect();
   ```

 * Creating a new account (requires `accounts-password` package)

   ```java
   mMeteor.registerAndLogin("john", "john.doe@example.com", "password", new ResultListener() { });
   // or
   // mMeteor.registerAndLogin("john", "john.doe@example.com", "password", profile, new ResultListener() { });
   ```

 * Signing in with an existing username (requires `accounts-password` package)

   ```java
   mMeteor.loginWithUsername("john", "password", new ResultListener() { });
   ```

 * Signing in with an existing email address (requires `accounts-password` package)

   ```java
   mMeteor.loginWithEmail("john.doe@example.com", "password", new ResultListener() { });
   ```

 * Check if the client is currently logged in (requires `accounts-password` package)

   ```java
   mMeteor.isLoggedIn();
   ```

 * Get the client's user ID (if currently logged in) (requires `accounts-password` package)

   ```java
   mMeteor.getUserId();
   ```

 * Logging out (requires `accounts-password` package)

   ```java
   mMeteor.logout();
   // or
   // mMeteor.logout(new ResultListener() { });
   ```

 * Checking whether the client is connected

   ```java
   mMeteor.isConnected();
   ```

 * Manually attempt to re-connect (if necessary)

   ```java
   mMeteor.reconnect();
   ```

## Using databases to manage data

### Enabling a database

Pass an instance of `Database` to the constructor. Right now, the only subclass provided as a built-in database is `InMemoryDatabase`. So the code for the constructor becomes:

```java
mMeteor = new Meteor(this, "ws://example.meteor.com/websocket", new InMemoryDatabase());
```

After that change, all data received from the server will automatically be parsed, updated and managed for you in the built-in database. That means no manual JSON parsing!

So whenever you receive data notifications via `onDataAdded`, `onDataChanged` or `onDataRemoved`, that data has already been merged into the database and can be retrieved from there. In these callbacks, you can thus ignore the parameters containing JSON data and instead get the data from your database.

### Accessing the database

```java
Database database = mMeteor.getDatabase();
```

This method call and most of the following method calls can be chained for simplicity.

### Getting a collection from the database by name

```java
// String collectionName = "myCollection";
Collection collection = mMeteor.getDatabase().getCollection(collectionName);
```

### Retrieving the names of all collections from the database

```java
String[] collectionNames = mMeteor.getDatabase().getCollectionNames();
```

### Fetching the number of collections from the database

```java
int numCollections = mMeteor.getDatabase().count();
```

### Getting a document from a collection by ID

```java
// String documentId = "wjQvNQ6sGjzLMDyiJ";
Document document = mMeteor.getDatabase().getCollection(collectionName).getDocument(documentId);
```

### Retrieving the IDs of all documents from a collection

```java
String[] documentIds = mMeteor.getDatabase().getCollection(collectionName).getDocumentIds();
```

### Fetching the number of documents from a collection

```java
int numDocuments = mMeteor.getDatabase().getCollection(collectionName).count();
```

### Querying a collection for documents

Any of the following method calls can be chained and combined in any way to select documents via complex queries.

```java
// String fieldName = "age";
// int fieldValue = 62;
Query query = mMeteor.getDatabase().getCollection(collectionName).whereEqual(fieldName, fieldValue);
```

```java
// String fieldName = "active";
// int fieldValue = false;
Query query = mMeteor.getDatabase().getCollection(collectionName).whereNotEqual(fieldName, fieldValue);
```

```java
// String fieldName = "accountBalance";
// float fieldValue = 100000.00f;
Query query = mMeteor.getDatabase().getCollection(collectionName).whereLessThan(fieldName, fieldValue);
```

```java
// String fieldName = "numChildren";
// long fieldValue = 3L;
Query query = mMeteor.getDatabase().getCollection(collectionName).whereLessThanOrEqual(fieldName, fieldValue);
```

```java
// String fieldName = "revenue";
// double fieldValue = 0.00;
Query query = mMeteor.getDatabase().getCollection(collectionName).whereGreaterThan(fieldName, fieldValue);
```

```java
// String fieldName = "age";
// int fieldValue = 21;
Query query = mMeteor.getDatabase().getCollection(collectionName).whereGreaterThanOrEqual(fieldName, fieldValue);
```

```java
// String fieldName = "address";
Query query = mMeteor.getDatabase().getCollection(collectionName).whereNull(fieldName);
```

```java
// String fieldName = "modifiedAt";
Query query = mMeteor.getDatabase().getCollection(collectionName).whereNotNull(fieldName);
```

Any query can be executed by a `find` or `findOne` call. The step of first creating the `Query` instance can be skipped if you chain the calls to execute the query immediately.

```java
Document[] documents = mMeteor.getDatabase().getCollection(collectionName).find();
```

```java
// int limit = 30;
Document[] documents = mMeteor.getDatabase().getCollection(collectionName).find(limit);
```

```java
// int limit = 30;
// int offset = 5;
Document[] documents = mMeteor.getDatabase().getCollection(collectionName).find(limit, offset);
```

```java
Document document = mMeteor.getDatabase().getCollection(collectionName).findOne();
```

Chained together, these calls may look as follows, for example:

```java
Document document = mMeteor.getDatabase().getCollection("users").whereNotNull("lastLoginAt").whereGreaterThan("level", 3).findOne();
```

### Getting a field from a document by name

```java
// String fieldName = "age";
Object field = mMeteor.getDatabase().getCollection(collectionName).getDocument(documentId).getField(fieldName);
```

### Retrieving the names of all fields from a document

```java
String[] fieldNames = mMeteor.getDatabase().getCollection(collectionName).getDocument(documentId).getFieldNames();
```

### Fetching the number of fields from a document

```java
int numFields = mMeteor.getDatabase().getCollection(collectionName).getDocument(documentId).count();
```

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
