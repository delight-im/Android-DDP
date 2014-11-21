package im.delight.android.ddp.firebase.examples;

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

import android.content.Context;
import im.delight.android.ddp.firebase.ServerValue;
import im.delight.android.ddp.firebase.ChildEventListener;
import im.delight.android.ddp.firebase.DataSnapshot;
import im.delight.android.ddp.firebase.ValueEventListener;
import java.util.Map;
import java.util.HashMap;
import android.widget.Toast;
import im.delight.android.ddp.firebase.FirebaseError;
import im.delight.android.ddp.firebase.Firebase.CompletionListener;
import im.delight.android.ddp.firebase.Firebase;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	private static final String SERVER_URL = "ws://example.meteor.com/websocket";
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();

		// set the Android context (which has no effect here and is only there for compatibility)
		Firebase.setAndroidContext(this);

		// create the first reference to the server's URL
		Firebase mainRef = new Firebase(SERVER_URL);

		// create another reference to a child node
		Firebase usersRef = mainRef.child("users");

		// write data to a child node
		usersRef.child("jane_doe").setValue("Jane Doe", 2.5f);

		// access a location directly from URL and remove the value
		new Firebase(SERVER_URL+"/some/outdated/child").removeValue();

		// watch the connection state
		Firebase connectedState = new Firebase(SERVER_URL+"/.info/connected");
		connectedState.addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot snapshot) {
				final boolean connected = snapshot.getValue(Boolean.class);
				final String status = connected ? "connected" : "disconnected";
				Toast.makeText(context, "Connectivity changed: "+status, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled(FirebaseError error) { }

		});

		// get the offset from local time to server time
		new Firebase(SERVER_URL+"/.info/serverTimeOffset").addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot snapshot) {
				Toast.makeText(context, "Server time offset received: "+snapshot.getValue(Long.class), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled(FirebaseError error) { }

		});

		// subscribe to updates in the `users` location
		usersRef.addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
				Toast.makeText(context, "Child added on reference `"+snapshot.getKey()+"`", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
				Toast.makeText(context, "Child changed on reference `"+snapshot.getKey()+"`", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onChildRemoved(DataSnapshot snapshot) {
				Toast.makeText(context, "Child removed on reference `"+snapshot.getKey()+"`", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
				Toast.makeText(context, "Child moved on reference `"+snapshot.getKey()+"`", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled(FirebaseError error) { }

		});

		// write several children at once in a given location
		Map<String, Object> johnDoeData = new HashMap<String, Object>();
		johnDoeData.put("score", 1024);
		johnDoeData.put("active", false);
		johnDoeData.put("last_online", ServerValue.TIMESTAMP);
		usersRef.child("john_doe").updateChildren(johnDoeData, new CompletionListener() {

			@Override
			public void onComplete(FirebaseError error, Firebase ref) {
				String response = "Listener on `"+ref.getKey()+"` completed "+(error != null ? "with an error" : "successfully");
				Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
			}

		});

		// write a node's priority only
		usersRef.child("john_doe").child("score").setPriority(500);

		// create an automatically named child with `push()`
		final Firebase pushedRef = usersRef.getParent().child("randomEntries").push();

		// read a plain Java object (POJO) from storage
		pushedRef.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot snapshot) {
				PersonBean personBean = snapshot.getValue(PersonBean.class);
				Toast.makeText(context, "PersonBean received: "+personBean, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled(FirebaseError error) { }

		});

		// write a plain Java object (POJO) to the storage that will be serialized automatically
		PersonBean personBean = new PersonBean();
		personBean.name = "John Doe";
		personBean.age = 42;
		personBean.location = "Example City";
		pushedRef.setValue(personBean);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// manually force the client to disconnect
		Firebase.goOffline();
	}

}
