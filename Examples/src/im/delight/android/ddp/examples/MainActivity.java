package im.delight.android.ddp.examples;

/**
 * Copyright 2014 www.delight.im <info@delight.im>
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

		// create a new instance (protocol version in second parameter is optional)
		mMeteor = new Meteor(this, "ws://android-ddp-meteor.meteor.com/websocket");

		// register the callback that will handle events and receive messages
		mMeteor.setCallback(this);
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
		String subscriptionId = mMeteor.subscribe("publicMessages");

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
		super.onDestroy();
		mMeteor.disconnect();
	}

}
