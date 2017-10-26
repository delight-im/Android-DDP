package im.delight.android.ddp;

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

import im.delight.android.ddp.db.DataStore;
import im.delight.android.ddp.db.Database;
import android.content.SharedPreferences;
import android.content.Context;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.Iterator;
import org.codehaus.jackson.map.ObjectMapper;
import java.util.UUID;
import java.util.Arrays;
import java.io.IOException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonNode;
import java.util.HashMap;
import java.util.Map;

/** Client that connects to Meteor servers implementing the DDP protocol */
public class Meteor {

	private static final String TAG = "Meteor";
	/** Supported versions of the DDP protocol in order of preference */
	private static final String[] SUPPORTED_DDP_VERSIONS = { "1", "pre2", "pre1" };
	/** The maximum number of attempts to re-connect to the server over WebSocket */
	private static final int RECONNECT_ATTEMPTS_MAX = 5;
	/** Instance of Jackson library's ObjectMapper that converts between JSON and Java objects (POJOs) */
	private static final ObjectMapper mObjectMapper = new ObjectMapper();
	/** The WebSocket connection that will be used for the data transfer */
	private WebSocket mWebSocket;
	/** The callback that handles messages and events received from the WebSocket connection */
	private final WebSocketListener mWebSocketListener;
	/** Map that tracks all pending Listener instances */
	private final Map<String, Listener> mListeners;
	/** Messages that couldn't be dispatched yet and thus had to be queued */
	private final Queue<String> mQueuedMessages;
	private final Context mContext;
	/** Whether logging should be enabled or not */
	private static boolean mLoggingEnabled;
	private String mServerUri;
	private String mDdpVersion;
	/** The number of unsuccessful attempts to re-connect in sequence */
	private int mReconnectAttempts;
	/** The callbacks that will handle events and receive messages from this client */
	protected final CallbackProxy mCallbackProxy = new CallbackProxy();
	private String mSessionID;
	private boolean mConnected;
	private String mLoggedInUserId;
	private final DataStore mDataStore;

	/**
	 * Returns a new instance for a client connecting to a server via DDP over websocket
	 *
	 * The server URI should usually be in the form of `ws://example.meteor.com/websocket`
	 *
	 * @param context a `Context` reference (e.g. an `Activity` or `Service` instance)
	 * @param serverUri the server URI to connect to
	 */
	public Meteor(final Context context, final String serverUri) {
		this(context, serverUri, (DataStore) null);
	}

	/**
	 * Returns a new instance for a client connecting to a server via DDP over websocket
	 *
	 * The server URI should usually be in the form of `ws://example.meteor.com/websocket`
	 *
	 * @param context a `Context` reference (e.g. an `Activity` or `Service` instance)
	 * @param serverUri the server URI to connect to
	 * @param dataStore the data store to write data to
	 */
	public Meteor(final Context context, final String serverUri, final DataStore dataStore) {
		this(context, serverUri, SUPPORTED_DDP_VERSIONS[0], dataStore);
	}

	/**
	 * Returns a new instance for a client connecting to a server via DDP over websocket
	 *
	 * The server URI should usually be in the form of `ws://example.meteor.com/websocket`
	 *
	 * @param context a `Context` reference (e.g. an `Activity` or `Service` instance)
	 * @param serverUri the server URI to connect to
	 * @param protocolVersion the desired DDP protocol version
	 */
	public Meteor(final Context context, final String serverUri, final String protocolVersion) {
		this(context, serverUri, protocolVersion, null);
	}

	/**
	 * Returns a new instance for a client connecting to a server via DDP over websocket
	 *
	 * The server URI should usually be in the form of `ws://example.meteor.com/websocket`
	 *
	 * @param context a `Context` reference (e.g. an `Activity` or `Service` instance)
	 * @param serverUri the server URI to connect to
	 * @param protocolVersion the desired DDP protocol version
	 * @param dataStore the data store to write data to
	 */
	public Meteor(final Context context, final String serverUri, final String protocolVersion, final DataStore dataStore) {
		if (!isVersionSupported(protocolVersion)) {
			throw new IllegalArgumentException("DDP protocol version not supported: "+protocolVersion);
		}

		if (context == null) {
			throw new IllegalArgumentException("The context reference may not be null");
		}

		// save the context reference
		mContext = context.getApplicationContext();

		// save the data store reference
		mDataStore = dataStore;

		// create a new handler that processes the messages and events received from the WebSocket connection
		mWebSocketListener = new WebSocketAdapter() {

			@Override
			public void onConnected(final WebSocket websocket, final Map<String, List<String>> headers) {
				log(TAG);
				log("  onOpen");

				mConnected = true;
				mReconnectAttempts = 0;

				initConnection(mSessionID);
			}

			@Override
			public void onDisconnected(final WebSocket websocket, final WebSocketFrame serverCloseFrame, final WebSocketFrame clientCloseFrame, final boolean closedByServer) {
				log(TAG);
				log("  onClose");

				final boolean lostConnection = mConnected;

				mConnected = false;

				if (lostConnection) {
					mReconnectAttempts++;

					if (mReconnectAttempts <= RECONNECT_ATTEMPTS_MAX) {
						// try to re-connect automatically
						reconnect();
					}
					else {
						disconnect();
					}
				}

				mCallbackProxy.onDisconnect();
			}

			@Override
			public void onTextMessage(final WebSocket websocket, final String text) {
				log(TAG);
				log("  onTextMessage");
				log("    payload == "+text);

				handleMessage(text);
			}

			@Override
			public void onStateChanged(final WebSocket websocket, final WebSocketState newState) {}

			@Override
			public void handleCallbackError(final WebSocket websocket, final Throwable cause) {
				mCallbackProxy.onException(new Exception(cause));
			}

			@Override
			public void onError(final WebSocket websocket, final WebSocketException cause) {
				mCallbackProxy.onException(new Exception(cause));
			}

		};

		// create a map that holds the pending Listener instances
		mListeners = new HashMap<String, Listener>();

		// create a queue that holds undispatched messages waiting to be sent
		mQueuedMessages = new ConcurrentLinkedQueue<String>();

		// save the server URI
		mServerUri = serverUri;

		// try with the preferred DDP protocol version first
		mDdpVersion = protocolVersion;

		// count the number of failed attempts to re-connect
		mReconnectAttempts = 0;
	}

	/** Attempts to establish the connection to the server */
	public void connect() {
		openConnection(false);
	}

	/**
	 * Returns whether this client is connected or not
	 *
	 * @return whether this client is connected
	 */
	public boolean isConnected() {
		return mConnected;
	}

	/** Manually attempt to re-connect if necessary */
	public void reconnect() {
		openConnection(true);
	}

	/**
	 * Opens a connection to the server over websocket
	 *
	 * @param isReconnect whether this is a re-connect attempt or not
	 */
	private void openConnection(final boolean isReconnect) {
		if (isReconnect) {
			if (mConnected) {
				initConnection(mSessionID);
				return;
			}
		}

		// create a new WebSocket connection for the data transfer
		try {
			mWebSocket = new WebSocketFactory().setConnectionTimeout(30000).createSocket(mServerUri);
		}
		catch (final IOException e) {
			mCallbackProxy.onException(e);
		}

		mWebSocket.setMissingCloseFrameAllowed(true);
		mWebSocket.setPingInterval(25 * 1000);
		mWebSocket.addListener(mWebSocketListener);
		mWebSocket.connectAsynchronously();
	}

	/**
	 * Establish the connection to the server as requested by the DDP protocol (after the websocket has been opened)
	 *
	 * @param existingSessionID an existing session ID or `null`
	 */
	private void initConnection(final String existingSessionID) {
		final Map<String, Object> data = new HashMap<String, Object>();

		data.put(Protocol.Field.MESSAGE, Protocol.Message.CONNECT);
		data.put(Protocol.Field.VERSION, mDdpVersion);
		data.put(Protocol.Field.SUPPORT, SUPPORTED_DDP_VERSIONS);

		if (existingSessionID != null) {
			data.put(Protocol.Field.SESSION, existingSessionID);
		}

		send(data);
	}

	/** Disconnect the client from the server */
	public void disconnect() {
		mConnected = false;
		mListeners.clear();
		mSessionID = null;

		if (mWebSocket != null) {
			try {
				mWebSocket.disconnect();
			}
			catch (Exception e) {
				mCallbackProxy.onException(e);
			}
		}
		else {
			throw new IllegalStateException("You must have called the 'connect' method before you can disconnect again");
		}
	}

	/**
	 * Sends a Java object (POJO) over the websocket after serializing it with the Jackson library
	 *
	 * @param obj the Java object to send
	 */
	private void send(final Object obj) {
		// serialize the object to JSON
		final String jsonStr = toJson(obj);

		if (jsonStr == null) {
			throw new IllegalArgumentException("Object would be serialized to `null`");
		}

		// send the JSON string
		send(jsonStr);
	}

	/**
	 * Sends a string over the websocket
	 *
	 * @param message the string to send
	 */
	private void send(final String message) {
		log(TAG);
		log("  send");
		log("    message == "+message);

		if (message == null) {
			throw new IllegalArgumentException("You cannot send `null` messages");
		}

		if (mConnected) {
			log("    dispatching");

			if (mWebSocket != null) {
				mWebSocket.sendText(message);
			}
			else {
				throw new IllegalStateException("You must have called the 'connect' method before you can send data");
			}
		}
		else {
			log("    queueing");
			mQueuedMessages.add(message);
		}
	}

	/**
	 * Adds a callback that will handle events and receive messages from this client
	 *
	 * @param callback the callback instance
	 */
	public void addCallback(MeteorCallback callback) {
		mCallbackProxy.addCallback(callback);
	}

	/**
	 * Removes a callback that was to handle events and receive messages from this client
	 *
	 * @param callback the callback instance
	 */
	public void removeCallback(MeteorCallback callback) {
		mCallbackProxy.removeCallback(callback);
	}

	/** Removes all callbacks that were to handle events and receive messages from this client */
	public void removeCallbacks() {
		mCallbackProxy.removeCallbacks();
	}

	/**
	 * Serializes the given Java object (POJO) with the Jackson library
	 *
	 * @param obj the object to serialize
	 * @return the serialized object in JSON format
	 */
	private String toJson(Object obj) {
		try {
			return mObjectMapper.writeValueAsString(obj);
		}
		catch (Exception e) {
			mCallbackProxy.onException(e);

			return null;
		}
	}

	private <T> T fromJson(final String json, final Class<T> targetType) {
		try {
			if (json != null) {
				final JsonNode jsonNode = mObjectMapper.readTree(json);

				return mObjectMapper.convertValue(jsonNode, targetType);
			}
			else {
				return null;
			}
		}
		catch (Exception e) {
			mCallbackProxy.onException(e);

			return null;
		}
	}

	/**
	 * Called whenever a JSON payload has been received from the websocket
	 *
	 * @param payload the JSON payload to process
	 */
	private void handleMessage(final String payload) {
		final JsonNode data;

		try {
			data = mObjectMapper.readTree(payload);
		}
		catch (JsonProcessingException e) {
			mCallbackProxy.onException(e);

			return;
		}
		catch (IOException e) {
			mCallbackProxy.onException(e);

			return;
		}

		if (data != null) {
			if (data.has(Protocol.Field.MESSAGE)) {
				final String message = data.get(Protocol.Field.MESSAGE).getTextValue();

				if (message.equals(Protocol.Message.CONNECTED)) {
					if (data.has(Protocol.Field.SESSION)) {
						mSessionID = data.get(Protocol.Field.SESSION).getTextValue();
					}

					// initialize the new session
					initSession();
				}
				else if (message.equals(Protocol.Message.FAILED)) {
					if (data.has(Protocol.Field.VERSION)) {
						// the server wants to use a different protocol version
						final String desiredVersion = data.get(Protocol.Field.VERSION).getTextValue();

						// if the protocol version that was requested by the server is supported by this client
						if (isVersionSupported(desiredVersion)) {
							// remember which version has been requested
							mDdpVersion = desiredVersion;

							// the server should be closing the connection now and we will re-connect afterwards
						}
						else {
							throw new RuntimeException("Protocol version not supported: "+desiredVersion);
						}
					}
				}
				else if (message.equals(Protocol.Message.PING)) {
					final String id;

					if (data.has(Protocol.Field.ID)) {
						id = data.get(Protocol.Field.ID).getTextValue();
					}
					else {
						id = null;
					}

					sendPong(id);
				}
				else if (message.equals(Protocol.Message.ADDED) || message.equals(Protocol.Message.ADDED_BEFORE)) {
					final String documentID;

					if (data.has(Protocol.Field.ID)) {
						documentID = data.get(Protocol.Field.ID).getTextValue();
					}
					else {
						documentID = null;
					}

					final String collectionName;

					if (data.has(Protocol.Field.COLLECTION)) {
						collectionName = data.get(Protocol.Field.COLLECTION).getTextValue();
					}
					else {
						collectionName = null;
					}

					final String newValuesJson;

					if (data.has(Protocol.Field.FIELDS)) {
						newValuesJson = data.get(Protocol.Field.FIELDS).toString();
					}
					else {
						newValuesJson = null;
					}

					if (mDataStore != null) {
						mDataStore.onDataAdded(collectionName, documentID, fromJson(newValuesJson, Fields.class));
					}

					mCallbackProxy.onDataAdded(collectionName, documentID, newValuesJson);
				}
				else if (message.equals(Protocol.Message.CHANGED)) {
					final String documentID;

					if (data.has(Protocol.Field.ID)) {
						documentID = data.get(Protocol.Field.ID).getTextValue();
					}
					else {
						documentID = null;
					}

					final String collectionName;

					if (data.has(Protocol.Field.COLLECTION)) {
						collectionName = data.get(Protocol.Field.COLLECTION).getTextValue();
					}
					else {
						collectionName = null;
					}

					final String updatedValuesJson;

					if (data.has(Protocol.Field.FIELDS)) {
						updatedValuesJson = data.get(Protocol.Field.FIELDS).toString();
					}
					else {
						updatedValuesJson = null;
					}

					final String removedValuesJson;

					if (data.has(Protocol.Field.CLEARED)) {
						removedValuesJson = data.get(Protocol.Field.CLEARED).toString();
					}
					else {
						removedValuesJson = null;
					}

					if (mDataStore != null) {
						mDataStore.onDataChanged(collectionName, documentID, fromJson(updatedValuesJson, Fields.class), fromJson(removedValuesJson, String[].class));
					}

					mCallbackProxy.onDataChanged(collectionName, documentID, updatedValuesJson, removedValuesJson);
				}
				else if (message.equals(Protocol.Message.REMOVED)) {
					final String documentID;

					if (data.has(Protocol.Field.ID)) {
						documentID = data.get(Protocol.Field.ID).getTextValue();
					}
					else {
						documentID = null;
					}

					final String collectionName;

					if (data.has(Protocol.Field.COLLECTION)) {
						collectionName = data.get(Protocol.Field.COLLECTION).getTextValue();
					}
					else {
						collectionName = null;
					}

					if (mDataStore != null) {
						mDataStore.onDataRemoved(collectionName, documentID);
					}

					mCallbackProxy.onDataRemoved(collectionName, documentID);
				}
				else if (message.equals(Protocol.Message.RESULT)) {
					// check if we have to process any result data internally
					if (data.has(Protocol.Field.RESULT)) {
						final JsonNode resultData = data.get(Protocol.Field.RESULT);

						// if the result is from a previous login attempt
						if (isLoginResult(resultData)) {
							// extract the login token for subsequent automatic re-login
							final String loginToken = resultData.get(Protocol.Field.TOKEN).getTextValue();
							saveLoginToken(loginToken);

							// extract the user's ID
							mLoggedInUserId = resultData.get(Protocol.Field.ID).getTextValue();
						}
					}

					final String id;

					if (data.has(Protocol.Field.ID)) {
						id = data.get(Protocol.Field.ID).getTextValue();
					}
					else {
						id = null;
					}

					final Listener listener = mListeners.get(id);

					if (listener instanceof ResultListener) {
						mListeners.remove(id);

						final String result;

						if (data.has(Protocol.Field.RESULT)) {
							result = data.get(Protocol.Field.RESULT).toString();
						}
						else {
							result = null;
						}

						if (data.has(Protocol.Field.ERROR)) {
							final Protocol.Error error = Protocol.Error.fromJson(data.get(Protocol.Field.ERROR));
							mCallbackProxy.forResultListener((ResultListener) listener).onError(error.getError(), error.getReason(), error.getDetails());
						}
						else {
							mCallbackProxy.forResultListener((ResultListener) listener).onSuccess(result);
						}
					}
				}
				else if (message.equals(Protocol.Message.READY)) {
					if (data.has(Protocol.Field.SUBS)) {
						final Iterator<JsonNode> elements = data.get(Protocol.Field.SUBS).getElements();
						String subscriptionId;

						while (elements.hasNext()) {
							subscriptionId = elements.next().getTextValue();

							final Listener listener = mListeners.get(subscriptionId);

							if (listener instanceof SubscribeListener) {
								mListeners.remove(subscriptionId);

								mCallbackProxy.forSubscribeListener((SubscribeListener) listener).onSuccess();
							}
						}
					}
				}
				else if (message.equals(Protocol.Message.NOSUB)) {
					final String subscriptionId;

					if (data.has(Protocol.Field.ID)) {
						subscriptionId = data.get(Protocol.Field.ID).getTextValue();
					}
					else {
						subscriptionId = null;
					}

					final Listener listener = mListeners.get(subscriptionId);

					if (listener instanceof SubscribeListener) {
						mListeners.remove(subscriptionId);

						if (data.has(Protocol.Field.ERROR)) {
							final Protocol.Error error = Protocol.Error.fromJson(data.get(Protocol.Field.ERROR));
							mCallbackProxy.forSubscribeListener((SubscribeListener) listener).onError(error.getError(), error.getReason(), error.getDetails());
						}
						else {
							mCallbackProxy.forSubscribeListener((SubscribeListener) listener).onError(null, null, null);
						}
					}
					else if (listener instanceof UnsubscribeListener) {
						mListeners.remove(subscriptionId);

						mCallbackProxy.forUnsubscribeListener((UnsubscribeListener) listener).onSuccess();
					}
				}
			}
		}
	}

	/**
	 * Returns whether the given JSON result is from a previous login attempt
	 *
	 * @param result the JSON result
	 * @return whether the result is from a login attempt (`true`) or not (`false`)
	 */
	private static boolean isLoginResult(final JsonNode result) {
		return result.has(Protocol.Field.TOKEN) && result.has(Protocol.Field.ID);
	}

	/**
	 * Returns whether the client is currently logged in as some user
	 *
	 * @return whether the client is logged in (`true`) or not (`false`)
	 */
	public boolean isLoggedIn() {
		return mLoggedInUserId != null;
	}

	/**
	 * Returns the ID of the user who is currently logged in
	 *
	 * @return the ID or `null`
	 */
	public String getUserId() {
		return mLoggedInUserId;
	}

	/**
	 * Returns whether the specified version of the DDP protocol is supported or not
	 *
	 * @param protocolVersion the DDP protocol version
	 * @return whether the version is supported or not
	 */
	public static boolean isVersionSupported(final String protocolVersion) {
		return Arrays.asList(SUPPORTED_DDP_VERSIONS).contains(protocolVersion);
	}

	/**
	 * Sends a `pong` over the websocket as a reply to an incoming `ping`
	 *
	 * @param id the ID extracted from the `ping` or `null`
	 */
	private void sendPong(final String id) {
		final Map<String, Object> data = new HashMap<String, Object>();

		data.put(Protocol.Field.MESSAGE, Protocol.Message.PONG);

		if (id != null) {
			data.put(Protocol.Field.ID, id);
		}

		send(data);
	}

	/**
	 * Sets whether logging of internal events and data flow should be enabled for this library
	 *
	 * @param enabled whether logging should be enabled (`true`) or not (`false`)
	 */
	public static void setLoggingEnabled(final boolean enabled) {
		mLoggingEnabled = enabled;
	}

	/**
	 * Logs a message if logging has been enabled
	 *
	 * @param message the message to log
	 */
	public static void log(final String message) {
		if (mLoggingEnabled) {
			System.out.println(message);
		}
	}

	/**
	 * Creates and returns a new unique ID
	 *
	 * @return the new unique ID
	 */
	public static String uniqueID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Insert given data into the specified collection
	 *
	 * @param collectionName the collection to insert the data into
	 * @param data the data to insert
	 */
	public void insert(final String collectionName, final Map<String, Object> data) {
		insert(collectionName, data, null);
	}

	/**
	 * Insert given data into the specified collection
	 *
	 * @param collectionName the collection to insert the data into
	 * @param data the data to insert
	 * @param listener the listener to call on success/error
	 */
	public void insert(final String collectionName, final Map<String, Object> data, final ResultListener listener) {
		call("/"+collectionName+"/insert", new Object[] { data }, listener);
	}

	/**
	 * Insert given data into the specified collection
	 *
	 * @param collectionName the collection to insert the data into
	 * @param query the query to select the document to update with
	 * @param data the list of keys and values that should be set
	 */
	public void update(final String collectionName, final Map<String, Object> query, final Map<String, Object> data) {
		update(collectionName, query, data, emptyMap());
	}

	/**
	 * Insert given data into the specified collection
	 *
	 * @param collectionName the collection to insert the data into
	 * @param query the query to select the document to update with
	 * @param data the list of keys and values that should be set
	 * @param options the list of option parameters
	 */
	public void update(final String collectionName, final Map<String, Object> query, final Map<String, Object> data, final Map<String, Object> options) {
		update(collectionName, query, data, options, null);
	}

	/**
	 * Insert given data into the specified collection
	 *
	 * @param collectionName the collection to insert the data into
	 * @param query the query to select the document to update with
	 * @param data the list of keys and values that should be set
	 * @param options the list of option parameters
	 * @param listener the listener to call on success/error
	 */
	public void update(final String collectionName, final Map<String, Object> query, final Map<String, Object> data, final Map<String, Object> options, final ResultListener listener) {
		call("/"+collectionName+"/update", new Object[] { query, data, options }, listener);
	}

	/**
	 * Insert given data into the specified collection
	 *
	 * @param collectionName the collection to insert the data into
	 * @param documentID the ID of the document to remove
	 */
	public void remove(final String collectionName, final String documentID) {
		remove(collectionName, documentID, null);
	}

	/**
	 * Insert given data into the specified collection
	 *
	 * @param collectionName the collection to insert the data into
	 * @param documentId the ID of the document to remove
	 * @param listener the listener to call on success/error
	 */
	public void remove(final String collectionName, final String documentId, final ResultListener listener) {
		final Map<String, Object> query = new HashMap<String, Object>();
		query.put(MongoDb.Field.ID, documentId);

		call("/"+collectionName+"/remove", new Object[] { query }, listener);
	}

	/**
	 * Sign in the user with the given username and password
	 *
	 * Please note that this requires the `accounts-password` package
	 *
	 * @param username the username to sign in with
	 * @param password the password to sign in with
	 * @param listener the listener to call on success/error
	 */
	public void loginWithUsername(final String username, final String password, final ResultListener listener) {
		login(username, null, password, listener);
	}

	/**
	 * Sign in the user with the given email address and password
	 *
	 * Please note that this requires the `accounts-password` package
	 *
	 * @param email the email address to sign in with
	 * @param password the password to sign in with
	 * @param listener the listener to call on success/error
	 */
	public void loginWithEmail(final String email, final String password, final ResultListener listener) {
		login(null, email, password, listener);
	}

	/**
	 * Sign in the user with the given username or email address and the specified password
	 *
	 * Please note that this requires the `accounts-password` package
	 *
	 * @param username the username to sign in with (either this or `email` is required)
	 * @param email the email address to sign in with (either this or `username` is required)
	 * @param password the password to sign in with
	 * @param listener the listener to call on success/error
	 */
	private void login(final String username, final String email, final String password, final ResultListener listener) {
		final Map<String, Object> userData = new HashMap<String, Object>();

		if (username != null) {
			userData.put("username", username);
		}
		else if (email != null) {
			userData.put("email", email);
		}
		else {
			throw new IllegalArgumentException("You must provide either a username or an email address");
		}

		final Map<String, Object> authData = new HashMap<String, Object>();
		authData.put("user", userData);
		authData.put("password", password);

		call("login", new Object[] { authData }, listener);
	}

	/**
	 * Attempts to sign in with the given login token
	 *
	 * @param token the login token
	 * @param listener the listener to call on success/error
	 */
	private void loginWithToken(final String token, final ResultListener listener) {
		final Map<String, Object> authData = new HashMap<String, Object>();
		authData.put("resume", token);

		call("login", new Object[] { authData }, listener);
	}

	public void logout() {
		logout(null);
	}

	public void logout(final ResultListener listener) {
		call("logout", new Object[] { }, new ResultListener() {

			@Override
			public void onSuccess(final String result) {
				// remember that we're not logged in anymore
				mLoggedInUserId = null;

				// delete the last login token which is now invalid
				saveLoginToken(null);

				if (listener != null) {
					mCallbackProxy.forResultListener(listener).onSuccess(result);
				}
			}

			@Override
			public void onError(final String error, final String reason, final String details) {
				if (listener != null) {
					mCallbackProxy.forResultListener(listener).onError(error, reason, details);
				}
			}

		});
	}

	/**
	 * Registers a new user with the specified username, email address and password
	 *
	 * This method will automatically login as the new user on success
	 *
	 * Please note that this requires the `accounts-password` package
	 *
	 * @param username the username to register with (either this or `email` is required)
	 * @param email the email address to register with (either this or `username` is required)
	 * @param password the password to register with
	 * @param listener the listener to call on success/error
	 */
	public void registerAndLogin(final String username, final String email, final String password, final ResultListener listener) {
		registerAndLogin(username, email, password, null, listener);
	}

	/**
	 * Registers a new user with the specified username, email address and password
	 *
	 * This method will automatically login as the new user on success
	 *
	 * Please note that this requires the `accounts-password` package
	 *
	 * @param username the username to register with (either this or `email` is required)
	 * @param email the email address to register with (either this or `username` is required)
	 * @param password the password to register with
	 * @param profile the user's profile data, typically including a `name` field
	 * @param listener the listener to call on success/error
	 */
	public void registerAndLogin(final String username, final String email, final String password, final HashMap<String, Object> profile, final ResultListener listener) {
		if (username == null && email == null) {
			throw new IllegalArgumentException("You must provide either a username or an email address");
		}

		final Map<String, Object> accountData = new HashMap<String, Object>();

		if (username != null) {
			accountData.put("username", username);
		}

		if (email != null) {
			accountData.put("email", email);
		}

		accountData.put("password", password);

		if (profile != null) {
			accountData.put("profile", profile);
		}

		call("createUser", new Object[] { accountData }, listener);
	}

	/**
	 * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
	 *
	 * @param methodName the name of the method to call, e.g. `/someCollection.insert`
	 */
	public void call(final String methodName) {
		call(methodName, null, null);
	}

	/**
	 * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
	 *
	 * @param methodName the name of the method to call, e.g. `/someCollection.insert`
	 * @param params the objects that should be passed to the method as parameters
	 */
	public void call(final String methodName, final Object[] params) {
		call(methodName, params, null);
	}

	/**
	 * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
	 *
	 * @param methodName the name of the method to call, e.g. `/someCollection.insert`
	 * @param listener the listener to trigger when the result has been received or `null`
	 */
	public void call(final String methodName, final ResultListener listener) {
		call(methodName, null, listener);
	}

	/**
	 * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
	 *
	 * @param methodName the name of the method to call, e.g. `/someCollection.insert`
	 * @param params the objects that should be passed to the method as parameters
	 * @param listener the listener to trigger when the result has been received or `null`
	 */
	public void call(final String methodName, final Object[] params, final ResultListener listener) {
		callWithSeed(methodName, null, params, listener);
	}

	/**
	 * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
	 *
	 * @param methodName the name of the method to call, e.g. `/someCollection.insert`
	 * @param randomSeed an arbitrary seed for pseudo-random generators or `null`
	 */
	public void callWithSeed(final String methodName, final String randomSeed) {
		callWithSeed(methodName, randomSeed, null, null);
	}

	/**
	 * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
	 *
	 * @param methodName the name of the method to call, e.g. `/someCollection.insert`
	 * @param randomSeed an arbitrary seed for pseudo-random generators or `null`
	 * @param params the objects that should be passed to the method as parameters
	 */
	public void callWithSeed(final String methodName, final String randomSeed, final Object[] params) {
		callWithSeed(methodName, randomSeed, params, null);
	}

	/**
	 * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
	 *
	 * @param methodName the name of the method to call, e.g. `/someCollection.insert`
	 * @param randomSeed an arbitrary seed for pseudo-random generators or `null`
	 * @param params the objects that should be passed to the method as parameters
	 * @param listener the listener to trigger when the result has been received or `null`
	 */
	public void callWithSeed(final String methodName, final String randomSeed, final Object[] params, final ResultListener listener) {
		// create a new unique ID for this request
		final String callId = uniqueID();

		// save a reference to the listener to be executed later
		if (listener != null) {
			mListeners.put(callId, listener);
		}

		final Map<String, Object> data = new HashMap<String, Object>();

		data.put(Protocol.Field.MESSAGE, Protocol.Message.METHOD);
		data.put(Protocol.Field.METHOD, methodName);
		data.put(Protocol.Field.ID, callId);

		if (params != null) {
			data.put(Protocol.Field.PARAMS, params);
		}

		if (randomSeed != null) {
			data.put(Protocol.Field.RANDOM_SEED, randomSeed);
		}

		send(data);
	}

	/**
	 * Subscribes to a specific subscription from the server
	 *
	 * @param subscriptionName the name of the subscription
	 * @return the generated subscription ID (must be used when unsubscribing)
	 */
	public String subscribe(final String subscriptionName) {
		return subscribe(subscriptionName, null);
	}

	/**
	 * Subscribes to a specific subscription from the server
	 *
	 * @param subscriptionName the name of the subscription
	 * @param params the subscription parameters
	 * @return the generated subscription ID (must be used when unsubscribing)
	 */
	public String subscribe(final String subscriptionName, final Object[] params) {
		return subscribe(subscriptionName, params, null);
	}

	/**
	 * Subscribes to a specific subscription from the server
	 *
	 * @param subscriptionName the name of the subscription
	 * @param params the subscription parameters
	 * @param listener the listener to call on success/error
	 * @return the generated subscription ID (must be used when unsubscribing)
	 */
	public String subscribe(final String subscriptionName, final Object[] params, final SubscribeListener listener) {
		// create a new unique ID for this request
		final String subscriptionId = uniqueID();

		// save a reference to the listener to be executed later
		if (listener != null) {
			mListeners.put(subscriptionId, listener);
		}

		final Map<String, Object> data = new HashMap<String, Object>();

		data.put(Protocol.Field.MESSAGE, Protocol.Message.SUBSCRIBE);
		data.put(Protocol.Field.NAME, subscriptionName);
		data.put(Protocol.Field.ID, subscriptionId);

		if (params != null) {
			data.put(Protocol.Field.PARAMS, params);
		}

		send(data);

		// return the generated subscription ID
		return subscriptionId;
	}

	/**
	 * Unsubscribes from the subscription with the specified name
	 *
	 * @param subscriptionId the ID of the subscription
	 */
	public void unsubscribe(final String subscriptionId) {
		unsubscribe(subscriptionId, null);
	}

	/**
	 * Unsubscribes from the subscription with the specified name
	 *
	 * @param subscriptionId the ID of the subscription
	 * @param listener the listener to call on success/error
	 */
	public void unsubscribe(final String subscriptionId, final UnsubscribeListener listener) {
		// save a reference to the listener to be executed later
		if (listener != null) {
			mListeners.put(subscriptionId, listener);
		}

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put(Protocol.Field.MESSAGE, Protocol.Message.UNSUBSCRIBE);
		data.put(Protocol.Field.ID, subscriptionId);

		send(data);
	}

	/**
	 * Creates an empty map for use as default parameter
	 *
	 * @return an empty map
	 */
	private static Map<String, Object> emptyMap() {
		return new HashMap<String, Object>();
	}

	/**
	 * Saves the given login token to the preferences
	 *
	 * @param token the login token to save
	 */
	private void saveLoginToken(final String token) {
		final SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putString(Preferences.Keys.LOGIN_TOKEN, token);
		editor.apply();
	}

	/**
	 * Retrieves the last login token from the preferences
	 *
	 * @return the last login token or `null`
	 */
	private String getLoginToken() {
		return getSharedPreferences().getString(Preferences.Keys.LOGIN_TOKEN, null);
	}

	/**
	 * Returns a reference to the preferences for internal use
	 *
	 * @return the `SharedPreferences` instance
	 */
	private SharedPreferences getSharedPreferences() {
		return mContext.getSharedPreferences(Preferences.FILE_NAME, Context.MODE_PRIVATE);
	}

	private void initSession() {
		// get the last login token
		final String loginToken = getLoginToken();

		// if we found a login token that might work
		if (loginToken != null) {
			// try to sign in with that token
			loginWithToken(loginToken, new ResultListener() {

				@Override
				public void onSuccess(final String result) {
					announceSessionReady(true);
				}

				@Override
				public void onError(final String error, final String reason, final String details) {
					// clear the user ID since automatic sign-in has failed
					mLoggedInUserId = null;

					// discard the token which turned out to be invalid
					saveLoginToken(null);

					announceSessionReady(false);
				}

			});
		}
		// if we didn't find any login token
		else {
			announceSessionReady(false);
		}
	}

	/**
	 * Announces that the new session is now ready to use
	 *
	 * @param signedInAutomatically whether we have already signed in automatically (`true`) or not (`false)`
	 */
	private void announceSessionReady(final boolean signedInAutomatically) {
		// run the callback that waits for the connection to open
		mCallbackProxy.onConnect(signedInAutomatically);

		// try to dispatch queued messages now
		String queuedMessage = null;
		while ((queuedMessage = mQueuedMessages.poll()) != null) {
			send(queuedMessage);
		}
	}

	/**
	 * Returns the data store that was set in the constructor and that contains all data received from the server
	 *
	 * @return the data store or `null`
	 */
	public DataStore getDataStore() {
		return mDataStore;
	}

	/**
	 * Returns the database that was set in the constructor and that contains all data received from the server
	 *
	 * @return the database or `null`
	 */
	public Database getDatabase() {
		if (mDataStore instanceof Database) {
			return (Database) mDataStore;
		}
		else {
			return null;
		}
	}

}
