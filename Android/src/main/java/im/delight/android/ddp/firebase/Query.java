package im.delight.android.ddp.firebase;

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

import im.delight.android.ddp.firebase.util.Path;
import im.delight.android.ddp.ResultListener;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.firebase.util.ListenerPool;
import im.delight.android.ddp.firebase.ValueEventListener;

/** This class (and its subclass `Firebase`) are used to read data */
public class Query {

	/** The common root collection on the Meteor server */
	protected static final String COLLECTION_NAME = "database";
	/** The common root subscription on the Meteor server */
	private static final String SUBSCRIPTION_NAME = "node";
	/** The DDP protocol version to use (must be supported on the server) */
	protected static final String DDP_VERSION = "pre2";
	private static final MeteorCallback mClientCallback = new MeteorCallback() {

		@Override
		public void onConnect(final boolean signedInAutomatically) {
			reportConnectivityChange(true);
		}

		@Override
		public void onDisconnect(int code, String reason) {
			reportConnectivityChange(false);
		}

		@Override
		public void onDataAdded(final String collectionName, final String documentID, final String newValuesJson) {
			if (collectionName != null && collectionName.equals(COLLECTION_NAME)) {
				final DataSnapshot snapshot = DataSnapshot.fromPath(documentID);
				if (newValuesJson != null) {
					snapshot.setFieldsFromJson(newValuesJson);
				}

				// execute all value listeners attached to this location
				ListenerPool.ValueEvent valueListeners = ListenerPool.ValueEvent.getInstance(documentID);
				valueListeners.onDataChange(snapshot);

				// execute all child listeners attached to this location's parent
				ListenerPool.ChildEvent childListeners = ListenerPool.ChildEvent.getInstance(new Path(documentID).getParent());
				childListeners.onChildAdded(snapshot, null);
			}
		}

		@Override
		public void onDataChanged(final String collectionName, final String documentID, final String updatedValuesJson, final String removedValuesJson) {
			if (collectionName != null && collectionName.equals(COLLECTION_NAME)) {
				final DataSnapshot snapshot = DataSnapshot.fromPath(documentID);
				if (updatedValuesJson != null) {
					snapshot.setFieldsFromJson(updatedValuesJson);
				}
				if (removedValuesJson != null) {
					snapshot.removeFieldsFromJson(removedValuesJson);
				}

				// execute all value listeners attached to this location
				ListenerPool.ValueEvent valueListeners = ListenerPool.ValueEvent.getInstance(documentID);
				valueListeners.onDataChange(snapshot);

				// execute all child listeners attached to this location's parent
				ListenerPool.ChildEvent childListeners = ListenerPool.ChildEvent.getInstance(new Path(documentID).getParent());
				childListeners.onChildChanged(snapshot, null);
			}
		}

		@Override
		public void onDataRemoved(final String collectionName, final String documentID) {
			if (collectionName != null && collectionName.equals(COLLECTION_NAME)) {
				final DataSnapshot snapshot = DataSnapshot.fromPath(documentID);
				snapshot.remove();

				// execute all value listeners attached to this location's parent
				ListenerPool.ValueEvent valueListeners = ListenerPool.ValueEvent.getInstance(new Path(documentID).getParent());
				valueListeners.onDataChange(snapshot.getParent());

				// execute all child listeners attached to this location's parent
				ListenerPool.ChildEvent childListeners = ListenerPool.ChildEvent.getInstance(new Path(documentID).getParent());
				childListeners.onChildRemoved(snapshot.getParent());
			}
		}

		@Override
		public void onException(Exception e) {
			throw new RuntimeException(e);
		}

	};
	/** Reference to the Meteor client that backs this instance */
	protected static Meteor mClient;
	/** The base URI of the server that we are connecting to */
	protected static String mServerUri;
	/** The path of the current node that this instance references */
	protected String mPath;

	protected Query(final String url) {
		if (url == null) {
			throw new RuntimeException("Server URI may not be null");
		}

		// for the first instance save the base server URI
		if (mServerUri == null) {
			mServerUri = normalizeServerUri(url);
		}
		// for the first instance create a shared client
		if (mClient == null) {
			mClient = new Meteor(Firebase.getAndroidContext(), url, DDP_VERSION);
			mClient.setCallback(mClientCallback);
		}

		// all server URIs must have common prefix (same server)
		if (!url.startsWith(mServerUri)) {
			throw new RuntimeException("You can only create subsequent instances for the same server as the first instance");
		}

		// remove common server URI from location/path
		mPath = normalizeServerUri(url).replace(mServerUri, "");
	}

	/**
	 * In order to prevent ambiguous and duplicate path names every server URI is normalized
	 *
	 * @param serverUri the server URI to normalize
	 * @return the normalized URI
	 */
	protected static String normalizeServerUri(String serverUri) {
		if (serverUri == null) {
			return null;
		}
		if (serverUri.endsWith(Path.SEPARATOR)) {
			serverUri = serverUri.substring(0, serverUri.length() - 1);
		}
		return serverUri;
	}

	private static ValueEventListener requestServerTime(final ValueEventListener listener) {
		mClient.call("getServerTime", new ResultListener() {

			@Override
			public void onSuccess(String result) {
				if (listener != null) {
					long serverTimeOffset;
					try {
						final long serverTime = Long.parseLong(result);
						serverTimeOffset = serverTime - System.currentTimeMillis();
					}
					catch (Exception e) {
						throw new RuntimeException("Couldn't parse server time offset: "+result);
					}

					final DataSnapshot snapshot = DataSnapshot.fromPath(Path.Special.SERVER_TIME_OFFSET);
					snapshot.setValue(serverTimeOffset);
					listener.onDataChange(snapshot);
				}
			}

			@Override
			public void onError(String error, String reason, String details) {
				final int errorCode = 0; // TODO add appropriate error code
				final FirebaseError firebaseError = new FirebaseError(errorCode, error, reason);
				if (listener != null) {
					listener.onCancelled(firebaseError);
				}
			}

		});

		return listener;
	}

	/**
	 * Subscribes to the specified location and all child nodes
	 *
	 * @param path the location
	 */
	private static void subscribeToData(final String path) {
		mClient.subscribe(SUBSCRIPTION_NAME, new Object[] { path, true });
	}

	/**
	 * Adds a callback listening for changes at this location
	 *
	 * @param listener the listener to trigger when the data at this location changes
	 * @return the listener that was passed in (reference must be kept to remove it later)
	 */
	public ValueEventListener addValueEventListener(final ValueEventListener listener) {
		if (mPath == null) {
			throw new RuntimeException("Path may not be null when adding a listener");
		}

		if (mPath.equals(Path.Special.SERVER_TIME_OFFSET)) {
			return requestServerTime(listener);
		}
		else {
			ListenerPool.ValueEvent.getInstance(mPath).add(listener);

			if (new Path(mPath).isSubscribable()) {
				subscribeToData(mPath);
			}

			return listener;
		}
	}

	/**
	 * Adds a callback retrieving data from this location *once*
	 *
	 * @param listener the listener to trigger once with the data from this location
	 */
	public void addListenerForSingleValueEvent(ValueEventListener listener) {
		addValueEventListener(listener); // FIXME run this one only once
	}

	/**
	 * Adds a callback listening for changes in this location's child nodes
	 *
	 * @param listener the listener to trigger when the data at any of this location's child nodes changes
	 * @return the listener that was passed in (reference must be kept to remove it later)
	 */
	public ChildEventListener addChildEventListener(ChildEventListener listener) {
		if (mPath == null) {
			throw new RuntimeException("Path may not be null when adding a listener");
		}

		ListenerPool.ChildEvent.getInstance(mPath).add(listener);

		if (new Path(mPath).isSubscribable()) {
			subscribeToData(mPath);
		}

		return listener;
	}

	/**
	 * Removes the given listener from this location
	 *
	 * @param listener the event listener to remove
	 */
	public void removeEventListener(EventListener listener) {
		if (listener instanceof ValueEventListener) {
			ListenerPool.ValueEvent.getInstance(mPath).remove(listener);
		}
		if (listener instanceof ChildEventListener) {
			ListenerPool.ChildEvent.getInstance(mPath).remove(listener);
		}
	}

	private static void reportConnectivityChange(final boolean connected) {
		ListenerPool.ValueEvent listenerGroup = ListenerPool.ValueEvent.getInstance(Path.Special.CONNECTED_STATE);
		final DataSnapshot snapshot = DataSnapshot.fromPath(Path.Special.CONNECTED_STATE);
		snapshot.setValue(connected);
		listenerGroup.onDataChange(snapshot);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Query [mServerUri=");
		builder.append(mServerUri);
		builder.append(", mPath=");
		builder.append(mPath);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(final Object other) {
		return ((other instanceof Query)) && (toString().equals(other.toString()));
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
