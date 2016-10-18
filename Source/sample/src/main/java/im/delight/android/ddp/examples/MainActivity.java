package im.delight.android.ddp.examples;

/*
 * Copyright (c) delight.im <info@delight.im>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import im.delight.android.ddp.ResultListener;
import java.util.Map;
import java.util.HashMap;
import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.db.memory.InMemoryDatabase;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity implements MeteorCallback {

	private Meteor mMeteor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// enable logging of internal events for the library
		Meteor.setLoggingEnabled(true);

		// create a new instance
		mMeteor = new Meteor(this, "ws://www.meteor.com/websocket", new InMemoryDatabase());

		// register the callback that will handle events and receive messages
		mMeteor.addCallback(this);

		// establish the connection
		mMeteor.connect();
	}

	@Override
	public void onConnect(final boolean signedInAutomatically) {
		System.out.println("Connected");
		System.out.println("Is logged in: "+mMeteor.isLoggedIn());
		System.out.println("User ID: "+mMeteor.getUserId());

		if (signedInAutomatically) {
			System.out.println("Successfully logged in automatically");
		}
		else {
			// sign up for a new account
			mMeteor.registerAndLogin("john-doe", "john.doe@example.com", "password1", new ResultListener() {

				@Override
				public void onSuccess(String result) {
					System.out.println("Successfully registered: "+result);
				}

				@Override
				public void onError(String error, String reason, String details) {
					System.out.println("Could not register: "+error+" / "+reason+" / "+details);
				}

			});

			// sign in to the server
			mMeteor.loginWithUsername("john-doe", "password1", new ResultListener() {

				@Override
				public void onSuccess(String result) {
					System.out.println("Successfully logged in: "+result);

					System.out.println("Is logged in: "+mMeteor.isLoggedIn());
					System.out.println("User ID: "+mMeteor.getUserId());
				}

				@Override
				public void onError(String error, String reason, String details) {
					System.out.println("Could not log in: "+error+" / "+reason+" / "+details);
				}

			});
		}

		// subscribe to data from the server
		String subscriptionId = mMeteor.subscribe("meetups");

		// unsubscribe from data again (usually done later or not at all)
		mMeteor.unsubscribe(subscriptionId);

		// insert data into a collection
		Map<String, Object> insertValues = new HashMap<String, Object>();
		insertValues.put("_id", "my-key");
		insertValues.put("some-number", 3);
		mMeteor.insert("my-collection", insertValues);

		// update data in a collection
		Map<String, Object> updateQuery = new HashMap<String, Object>();
		updateQuery.put("_id", "my-key");
		Map<String, Object> updateValues = new HashMap<String, Object>();
		updateValues.put("_id", "my-key");
		updateValues.put("some-number", 5);
		mMeteor.update("my-collection", updateQuery, updateValues);

		// remove data from a collection
		mMeteor.remove("my-collection", "my-key");

		// call an arbitrary method
		mMeteor.call("myMethod");
	}

	@Override
	public void onDisconnect() {
		System.out.println("Disconnected");
	}

	@Override
	public void onDataAdded(String collectionName, String documentID, String fieldsJson) {
		System.out.println("Data added to <"+collectionName+"> in document <"+documentID+">");
		System.out.println("    Added: "+fieldsJson);
	}

	@Override
	public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
		System.out.println("Data changed in <"+collectionName+"> in document <"+documentID+">");
		System.out.println("    Updated: "+updatedValuesJson);
		System.out.println("    Removed: "+removedValuesJson);
	}

	@Override
	public void onDataRemoved(String collectionName, String documentID) {
		System.out.println("Data removed from <"+collectionName+"> in document <"+documentID+">");
	}

	@Override
	public void onException(Exception e) {
		System.out.println("Exception");
		if (e != null) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		mMeteor.disconnect();
		mMeteor.removeCallback(this);
		// or
		// mMeteor.removeCallbacks();

		// ...

		super.onDestroy();
	}

	/*private void testDatabase() {
		// first Meteor#handleMessage has to be made public temporarily

		// mock some data that is being added
		mMeteor.handleMessage("{\"msg\":\"added\",\"collection\":\"people\",\"id\":\"al\",\"fields\":{\"name\":\"Alice\",\"age\":25,\"gender\":\"f\",\"location\":{\"country\":\"Japan\",\"region\":\"Kansai\"}}}");
		mMeteor.handleMessage("{\"msg\":\"added\",\"collection\":\"people\",\"id\":\"bo\",\"fields\":{\"name\":\"Bob\",\"age\":27,\"gender\":\"m\",\"location\":{\"country\":\"Spain\",\"region\":\"Andalusia\"}}}");
		mMeteor.handleMessage("{\"msg\":\"added\",\"collection\":\"people\",\"id\":\"ca\",\"fields\":{\"name\":\"Carol\",\"age\":29,\"gender\":null,\"location\":null}}");
		mMeteor.handleMessage("{\"msg\":\"added\",\"collection\":\"people\",\"id\":\"ev\",\"fields\":{\"name\":\"Eve\",\"age\":31,\"gender\":\"f\",\"location\":{\"country\":\"Australia\",\"region\":null}}}");
		mMeteor.handleMessage("{\"msg\":\"added\",\"collection\":\"settings\",\"id\":\"5h2iJyPMZmDTaSwGC\",\"fields\":{\"appId\":\"92Hn8HKvatWDPP22u\",\"endpoint\":\"http://www.example.com/\",\"clientDelay\":10000,\"enableSomething\":true}}");
		mMeteor.handleMessage("{\"msg\":\"added\",\"collection\":\"companies\",\"id\":\"31c53bca49616e773567920d\",\"fields\":{\"owner\":null,\"isInProgress\":true,\"Description\":\"Acme Inc. is a company\",\"Company\":\"Acme Inc.\",\"Location\":\"Some city, Some country\",\"Region\":\"SomeContinent/SomeOtherContinent\",\"Logo\":\"/assets/i/companies/default-logo.png\",\"Type\":\"Things\",\"Website\":\"http://www.example.com/\",\"prime\":false}}");
		mMeteor.handleMessage("{\"msg\":\"added\",\"collection\":\"versions\",\"id\":\"JyPMZ49616e7735\",\"fields\":{\"version\":\"ae976571be8a6999984eae9da24fc5d948ca80ac\",\"assets\":{\"allCss\":[{\"url\":\"/main.css\"}]}}}");
		mMeteor.handleMessage("{\"msg\":\"added\",\"collection\":\"events\",\"id\":\"2H7ZDva9nhL4F42im\",\"fields\":{\"coords\":{\"type\":\"Point\",\"coordinates\":[1.23,-2.345]},\"events\":[{\"eventId\":\"946221490\",\"eventName\":\"Meteor 101\",\"eventUrl\":\"http://www.example.com/946221490\",\"eventTime\":1000000000000,\"eventUTCOffset\":-3600000}],\"groupId\":2018074068,\"groupName\":\"Meteor 101 A\",\"groupUrlname\":\"Meteor-101-A\"}}");

		// mock some data that is being changed
		mMeteor.handleMessage("{\"msg\":\"changed\",\"collection\":\"people\",\"id\":\"ev\",\"fields\":{\"age\":23,\"location\":{\"region\":\"New South Wales\"}},\"cleared\":[\"gender\"]}");

		// mock some data that is being removed
		mMeteor.handleMessage("{\"msg\":\"removed\",\"collection\":\"people\",\"id\":\"ca\"}");

		// get a reference to the database
		final Database database = mMeteor.getDatabase();

		// wait a second
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// and then check what's in there

				// test the database operations
				System.out.println("Database#count() = " + database.count());
				System.out.println("Database#getCollectionNames() = " + Arrays.toString(database.getCollectionNames()));
				System.out.println("Database#getCollection(\"customers\") = " + database.getCollection("customers"));
				System.out.println("Database#getCollection(\"customers\").count() = " + database.getCollection("customers").count());
				System.out.println("Database#getCollection(\"people\") = " + database.getCollection("people"));

				// print a divider
				System.out.println("----------");

				// get a reference to a collection
				final Collection collection = database.getCollection("people");

				// test the collection operations
				System.out.println("Collection#getName() = " + collection.getName());
				System.out.println("Collection#count() = " + collection.count());
				System.out.println("Collection#getDocumentIds() = " + Arrays.toString(collection.getDocumentIds()));
				System.out.println("Collection#getDocument(\"jo\") = " + collection.getDocument("jo"));
				System.out.println("Collection#getDocument(\"al\") = " + collection.getDocument("al"));
				System.out.println("Collection#getDocument(\"ca\") = " + collection.getDocument("ca"));
				System.out.println("Collection#getDocument(\"ev\") = " + collection.getDocument("ev"));

				// print a divider
				System.out.println("----------");

				// get a reference to a document
				final Document document = collection.getDocument("al");

				// test the document operations
				System.out.println("Document#getId() = " + document.getId());
				System.out.println("Document#count() = " + document.count());
				System.out.println("Document#getFieldNames() = " + Arrays.toString(document.getFieldNames()));
				System.out.println("Document#getField(\"age\") = " + document.getField("age"));

				// print a divider
				System.out.println("----------");

				// test the query builder operations
				System.out.println("Collection#findOne() = " + collection.findOne());
				System.out.println("Collection#find(1) = " + Arrays.toString(collection.find(1)));
				System.out.println("Collection#find(2) = " + Arrays.toString(collection.find(2)));
				System.out.println("Collection#find(5) = " + Arrays.toString(collection.find(5)));
				System.out.println("Collection#find(1, 1) = " + Arrays.toString(collection.find(1, 1)));
				System.out.println("Collection#find(1, 2) = " + Arrays.toString(collection.find(1, 2)));
				System.out.println("Collection#find(2, 1) = " + Arrays.toString(collection.find(2, 1)));
				System.out.println("Collection#find(2, 2) = " + Arrays.toString(collection.find(2, 2)));
				System.out.println("Collection#find() = " + Arrays.toString(collection.find()));
				System.out.println("Collection#whereEqual(\"name\", \"Eve\").find() = " + Arrays.toString(collection.whereEqual("name", "Eve").find()));
				System.out.println("Collection#whereNotEqual(\"name\", \"Eve\").find() = " + Arrays.toString(collection.whereNotEqual("name", "Eve").find()));
				System.out.println("Collection#whereLessThan(\"age\", 27).find() = " + Arrays.toString(collection.whereLessThan("age", 27).find()));
				System.out.println("Collection#whereLessThanOrEqual(\"age\", 27).find() = " + Arrays.toString(collection.whereLessThanOrEqual("age", 27).find()));
				System.out.println("Collection#whereLessThan(\"age\", 25).find() = " + Arrays.toString(collection.whereLessThan("age", 25).find()));
				System.out.println("Collection#whereGreaterThan(\"age\", 23).find() = " + Arrays.toString(collection.whereGreaterThan("age", 23).find()));
				System.out.println("Collection#whereGreaterThanOrEqual(\"age\", 23).find() = " + Arrays.toString(collection.whereGreaterThanOrEqual("age", 23).find()));
				System.out.println("Collection#whereGreaterThan(\"age\", 25).find() = " + Arrays.toString(collection.whereGreaterThan("age", 25).find()));
				System.out.println("Collection#whereNull(\"location\").find() = " + Arrays.toString(collection.whereNull("location").find()));
				System.out.println("Collection#whereNotNull(\"location\").find() = " + Arrays.toString(collection.whereNotNull("location").find()));
				System.out.println("Collection#whereNotNull(\"gender\").whereLessThan(\"age\", 26).find() = " + Arrays.toString(collection.whereNotNull("gender").whereLessThan("age", 26).find()));
			}

		}, 1000);
	}*/

}
