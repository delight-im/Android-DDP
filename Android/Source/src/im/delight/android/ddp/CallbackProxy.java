package im.delight.android.ddp;

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

import android.os.Handler;
import android.os.Looper;
import java.util.LinkedList;
import java.util.List;

/** Wrapper that executes all registered callbacks on the correct thread behind the scenes */
public class CallbackProxy implements MeteorCallback {

	private final List<MeteorCallback> mCallbacks = new LinkedList<MeteorCallback>();
	private final Handler mUiHandler = new Handler(Looper.getMainLooper());

	public CallbackProxy() { }

	public void addCallback(final MeteorCallback callback) {
		mCallbacks.add(callback);
	}

	public void removeCallback(final MeteorCallback callback) {
		mCallbacks.remove(callback);
	}

	public void removeCallbacks() {
		mCallbacks.clear();
	}

	@Override
	public void onConnect(final boolean signedInAutomatically) {
		// iterate over all the registered callbacks
		for (final MeteorCallback callback : mCallbacks) {
			// if the callback exists
			if (callback != null) {
				// execute the callback on the main thread
				mUiHandler.post(new Runnable() {

					@Override
					public void run() {
						// run the proxied method with the same parameters
						callback.onConnect(signedInAutomatically);
					}

				});
			}
		}
	}

	@Override
	public void onDisconnect() {
		// iterate over all the registered callbacks
		for (final MeteorCallback callback : mCallbacks) {
			// if the callback exists
			if (callback != null) {
				// execute the callback on the main thread
				mUiHandler.post(new Runnable() {

					@Override
					public void run() {
						// run the proxied method with the same parameters
						callback.onDisconnect();
					}

				});
			}
		}
	}

	@Override
	public void onDataAdded(final String collectionName, final String documentID, final String newValuesJson) {
		// iterate over all the registered callbacks
		for (final MeteorCallback callback : mCallbacks) {
			// if the callback exists
			if (callback != null) {
				// execute the callback on the main thread
				mUiHandler.post(new Runnable() {

					@Override
					public void run() {
						// run the proxied method with the same parameters
						callback.onDataAdded(collectionName, documentID, newValuesJson);
					}

				});
			}
		}
	}

	@Override
	public void onDataChanged(final String collectionName, final String documentID, final String updatedValuesJson, final String removedValuesJson) {
		// iterate over all the registered callbacks
		for (final MeteorCallback callback : mCallbacks) {
			// if the callback exists
			if (callback != null) {
				// execute the callback on the main thread
				mUiHandler.post(new Runnable() {

					@Override
					public void run() {
						// run the proxied method with the same parameters
						callback.onDataChanged(collectionName, documentID, updatedValuesJson, removedValuesJson);
					}

				});
			}
		}
	}

	@Override
	public void onDataRemoved(final String collectionName, final String documentID) {
		// iterate over all the registered callbacks
		for (final MeteorCallback callback : mCallbacks) {
			// if the callback exists
			if (callback != null) {
				// execute the callback on the main thread
				mUiHandler.post(new Runnable() {

					@Override
					public void run() {
						// run the proxied method with the same parameters
						callback.onDataRemoved(collectionName, documentID);
					}

				});
			}
		}
	}

	@Override
	public void onException(final Exception e) {
		// iterate over all the registered callbacks
		for (final MeteorCallback callback : mCallbacks) {
			// if the callback exists
			if (callback != null) {
				// execute the callback on the main thread
				mUiHandler.post(new Runnable() {

					@Override
					public void run() {
						// run the proxied method with the same parameters
						callback.onException(e);
					}

				});
			}
		}
	}

	public ResultListener forResultListener(final ResultListener callback) {
		return new ResultListener() {

			@Override
			public void onSuccess(final String result) {
				// if the callback exists
				if (callback != null) {
					// execute the callback on the main thread
					mUiHandler.post(new Runnable() {

						@Override
						public void run() {
							// run the proxied method with the same parameters
							callback.onSuccess(result);
						}

					});
				}
			}

			@Override
			public void onError(final String error, final String reason, final String details) {
				// if the callback exists
				if (callback != null) {
					// execute the callback on the main thread
					mUiHandler.post(new Runnable() {

						@Override
						public void run() {
							// run the proxied method with the same parameters
							callback.onError(error, reason, details);
						}

					});
				}
			}

		};
	}

	public SubscribeListener forSubscribeListener(final SubscribeListener callback) {
		return new SubscribeListener() {

			@Override
			public void onSuccess() {
				// if the callback exists
				if (callback != null) {
					// execute the callback on the main thread
					mUiHandler.post(new Runnable() {

						@Override
						public void run() {
							// run the proxied method with the same parameters
							callback.onSuccess();
						}

					});
				}
			}

			@Override
			public void onError(final String error, final String reason, final String details) {
				// if the callback exists
				if (callback != null) {
					// execute the callback on the main thread
					mUiHandler.post(new Runnable() {

						@Override
						public void run() {
							// run the proxied method with the same parameters
							callback.onError(error, reason, details);
						}

					});
				}
			}

		};
	}

	public UnsubscribeListener forUnsubscribeListener(final UnsubscribeListener callback) {
		return new UnsubscribeListener() {

			@Override
			public void onSuccess() {
				// if the callback exists
				if (callback != null) {
					// execute the callback on the main thread
					mUiHandler.post(new Runnable() {

						@Override
						public void run() {
							// run the proxied method with the same parameters
							callback.onSuccess();
						}

					});
				}
			}

		};
	}

}
