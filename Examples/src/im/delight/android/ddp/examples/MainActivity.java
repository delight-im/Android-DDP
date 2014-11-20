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

		// create a new instance (protocol version in second parameter is optional)
		mMeteor = new Meteor("ws://example.meteor.com/websocket");

		// register the callback that will handle events and receive messages
		mMeteor.setCallback(this);
	}

	@Override
	public void onConnect() {
		System.out.println("Connected");

		// subscribe to data from the server
		mMeteor.subscribe("publicMessages");

		// unsubscribe from data again
		mMeteor.unsubscribe("publicMessages");

		// insert data into a collection
		Map<String, Object> insertValues = new HashMap<String, Object>();
		insertValues.put("_id", "my-key");
		insertValues.put("some-number", 3);
		mMeteor.insert("my-collection", insertValues);

		// update data in a collection
		Map<String, Object> updateQuery = new HashMap<String, Object>();
		updateQuery.put("_id", "my-key");
		Map<String, Object> updateValues = new HashMap<String, Object>();
		insertValues.put("_id", "my-key");
		insertValues.put("some-number", 5);
		mMeteor.update("my-collection", updateQuery, updateValues);

		// remove data from a collection
		mMeteor.remove("my-collection", "my-key");

		// call an arbitrary method
		mMeteor.call("/my-collection/count");
	}

	@Override
	public void onDisconnect(int code, String reason) {
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
