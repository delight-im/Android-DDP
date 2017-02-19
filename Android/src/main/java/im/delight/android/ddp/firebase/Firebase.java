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

import android.content.Context;
import im.delight.android.ddp.firebase.util.Path;
import im.delight.android.ddp.firebase.util.Utilities;
import java.util.HashSet;
import java.util.Set;
import im.delight.android.ddp.ResultListener;
import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MongoDb;
import java.util.HashMap;
import java.util.Map;

/**
 * Client that clones the Firebase API for Android and is intended to become a complete drop-in replacement
 *
 * This lets you connect to Meteor server just as you would connect to Firebase with their official SDK
 */
public class Firebase extends Query {

	/** Provides notifications about operations that have completed on the server */
	public static interface CompletionListener {

		/**
		 * Method that will be triggered when an operation has completed on the server
		 *
		 * @param error the error's details (or `null` on success)
		 * @param ref the Firebase instance that the operation executed on
		 */
		public void onComplete(FirebaseError error, Firebase ref);

	}

	/** Provides notifications about the outcome of a certain operation */
	public static interface ResultHandler {

		/** Method that will be triggered if the operation succeeded */
		public void onSuccess();

		/**
		 * Method that will be triggered if the operation failed
		 *
		 * @param error the error that was returned
		 */
		public void onError(FirebaseError error);

	}

	private static Context mContext;

	/**
	 * Returns an instance referencing the given location
	 *
	 * @param url the location
	 */
	public Firebase(final String url) {
		super(url);
	}

	/**
	 * Returns a reference to a location relative to this one
	 *
	 * @param pathString the relative path to the new location that should be created (e.g. `someChild` or `someChild/someGrandChild`)
	 * @return a new Firebase instance for the child location
	 */
	public Firebase child(String pathString) {
		// validate the child's key
		if (pathString == null) {
			throw new RuntimeException("A child's key may not be null");
		}
		if (pathString.length() == 0) {
			throw new RuntimeException("A child's key may not be empty");
		}

		final String newPath = new Path(mPath).getChild(pathString);

		if (new Path(newPath).isSubscribable()) {
			if (pathString.startsWith(".")) {
				throw new RuntimeException("A child's key may not start with `.` (dot)");
			}
			if (pathString.startsWith("_")) {
				throw new RuntimeException("A child's key may not start with `_` (underscore)");
			}
		}

		// return an instance for the specified child location
		return new Firebase(mServerUri + newPath);
	}

	/**
	 * Returns the last part of this location's name
	 *
	 * @return the last part of the location's name
	 */
	public String getKey() {
		return new Path(mPath).getLastSegment();
	}

	/**
	 * Returns a reference to the parent location (or `null` if this is the root)
	 *
	 * @return a Firebase instance for the parent location
	 */
	public Firebase getParent() {
		final String parentPath = new Path(mPath).getParent();
		if (parentPath != null) {
			return new Firebase(mServerUri + parentPath);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns a reference to the root location
	 *
	 * @return a Firebase instance for the root location
	 */
	@SuppressWarnings("static-method")
	public Firebase getRoot() {
		// return an instance for the root location
		return new Firebase(mServerUri + Path.getRoot());
	}

	/**
	 * Returns the current version name for this library
	 *
	 * @return the version name
	 */
	public static String getSdkVersion() {
		return Version.SDK_VERSION;
	}

	/** Manually connect the client and enable automatic re-connects */
	public static void goOnline() {
		mClient.reconnect();
	}

	/** Manually disconnect the client and prevent automatic re-connects */
	public static void goOffline() {
		mClient.disconnect();
	}

	/**
	 * Creates a new child with automatically defined name and returns reference to that new location
	 *
	 * @return the Firebase instance for the child location
	 */
	public Firebase push() {
		// automatically generate a unique ID for the new child
		final String autoChildID = Meteor.uniqueID();

		// return an instance referencing the new child
		return child(autoChildID);
	}

	/** Removes the value at the current location including all child locations */
	public void removeValue() {
		removeValue(null);
	}

	/**
	 * Removes the value at the current location including all child locations
	 *
	 * @param listener the listener that should be triggered when the operation completes
	 */
	public void removeValue(final CompletionListener listener) {
		mClient.remove(COLLECTION_NAME, mPath, createInternalListener(listener));
	}

	/**
	 * Sets the value at the current location to the given value
	 *
	 * Both primitive types and objects (will be serialized by the Jackson library) are allowed
	 *
	 * @param value the new value for this location
	 */
	public void setValue(final Object value) {
		setValue(value, Utilities.UNDEFINED, null);
	}

	/**
	 * Sets the value at the current location to the given value
	 *
	 * Both primitive types and objects (will be serialized by the Jackson library) are allowed
	 *
	 * @param value the new value for this location
	 * @param priority the priority value to set for this location
	 */
	public void setValue(final Object value, final Object priority) {
		setValue(value, priority, null);
	}

	/**
	 * Sets the value at the current location to the given value
	 *
	 * Both primitive types and objects (will be serialized by the Jackson library) are allowed
	 *
	 * @param value the new value for this location
	 * @param listener the listener that should be triggered when the operation completes
	 */
	public void setValue(final Object value, final CompletionListener listener) {
		setValue(value, Utilities.UNDEFINED, listener);
	}

	/**
	 * Sets the value at the current location to the given value
	 *
	 * Both primitive types and objects (will be serialized by the Jackson library) are allowed
	 *
	 * @param value the new value for this location
	 * @param priority the priority value to set for this location
	 * @param listener the listener that should be triggered when the operation completes
	 */
	public void setValue(Object value, final Object priority, final CompletionListener listener) {
		final Map<String, Object> query = new HashMap<String, Object>();
		query.put(MongoDb.Field.ID, mPath);

		final Map<String, Object> fields = new HashMap<String, Object>();
		if (value != Utilities.UNDEFINED) {
			fields.put(MongoDb.Field.VALUE, value);
		}
		if (priority != Utilities.UNDEFINED) {
			fields.put(MongoDb.Field.PRIORITY, priority);
		}

		upsert(query, fields, createInternalListener(listener));
	}

	/**
	 * Insert the given data into the specified collection or update any existing data
	 *
	 * @param query the query to select the document to update with
	 * @param data the list of keys and values that should be set
	 * @param listener the listener to call on success/error
	 */
	private static void upsert(final Map<String, Object> query, final Map<String, Object> data, final ResultListener listener) {
		mClient.call("Firebase.upsert", new Object[] { query, data }, listener);
	}

	public void setPriority(final Object priority) {
		setPriority(priority, null);
	}

	public void setPriority(final Object priority, final CompletionListener listener) {
		setValue(Utilities.UNDEFINED, priority, listener);
	}

	/**
	 * Updates the specified child keys to the given values
	 *
	 * @param children the children to update (with their new values)
	 */
	public void updateChildren(final Map<String, Object> children) {
		updateChildren(children, null);
	}

	/**
	 * Updates the specified child keys to the given values
	 *
	 * @param children the children to update (with their new values)
	 * @param listener the listener that should be triggered when results are available for the operation
	 */
	public void updateChildren(final Map<String, Object> children, final CompletionListener listener) {
		if (children == null) {
			throw new RuntimeException("Children passed to updateChildren(...) may not be null");
		}

		final Set<String> pendingResults = new HashSet<String>();

		// loop over all child entries
		for (final Map.Entry<String, Object> child : children.entrySet()) {
			// remember that we are waiting for a result from this single operation
			pendingResults.add(child.getKey());

			// write the data to the child location individually
			child(child.getKey()).setValue(child.getValue(), new CompletionListener() {

				@Override
				public void onComplete(FirebaseError error, Firebase ref) {
					// on sucess
					if (error == null) {
						// remove this child from the list of pending results
						pendingResults.remove(child.getKey());

						// if there are no more pending results
						if (pendingResults.size() == 0) {
							// notify callback about success
							if (listener != null) {
								listener.onComplete(null, Firebase.this);
							}
						}
					}
					// on failure
					else {
						// notify callback about failure
						if (listener != null) {
							listener.onComplete(error, Firebase.this);
						}
					}
				}

			});
		}
	}

	/**
	 * Creates an internal listener that calls the given public listener asynchronously
	 *
	 * @param listener the listener to call asynchronously
	 * @return the internal listener or `null`
	 */
	private ResultListener createInternalListener(final CompletionListener listener) {
		if (listener == null) {
			return null;
		}
		else {
			return new ResultListener() {

				@Override
				public void onSuccess(String result) {
					if (listener != null) {
						listener.onComplete(null, Firebase.this);
					}
				}

				@Override
				public void onError(String error, String reason, String details) {
					final int errorCode = 0; // TODO add appropriate error code
					final FirebaseError firebaseError = new FirebaseError(errorCode, error, reason);
					if (listener != null) {
						listener.onComplete(firebaseError, Firebase.this);
					}
				}

			};
		}
	}

	public OnDisconnect onDisconnect() {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	/**
	 * The context is not needed to this date so the value will just be ignored
	 *
	 * This method is for API compatibility only
	 *
	 * @param context the Android context to use
	 */
	public static void setAndroidContext(final Context context) {
		mContext = context.getApplicationContext();
	}

	protected static Context getAndroidContext() {
		if (mContext == null) {
			throw new RuntimeException("Please call `setAndroidContext()` first");
		}

		return mContext;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Firebase [mServerUri=");
		builder.append(mServerUri);
		builder.append(", mPath=");
		builder.append(mPath);
		builder.append("]");
		return builder.toString();
	}

}
