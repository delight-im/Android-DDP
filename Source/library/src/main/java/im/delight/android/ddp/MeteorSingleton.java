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

import android.content.Context;

/** Provides a single access point to the `Meteor` class that can be used across `Activity` instances */
public class MeteorSingleton extends Meteor {

	private static MeteorSingleton mInstance;

	public synchronized static MeteorSingleton createInstance(final Context context, final String serverUri) {
		return createInstance(context, serverUri, null);
	}

	public synchronized static MeteorSingleton createInstance(final Context context, final String serverUri, final String protocolVersion) {
		if (mInstance != null) {
			throw new IllegalStateException("An instance has already been created");
		}

		if (protocolVersion == null) {
			mInstance = new MeteorSingleton(context, serverUri);
		}
		else {
			mInstance = new MeteorSingleton(context, serverUri, protocolVersion);
		}

		return mInstance;
	}

	public synchronized static MeteorSingleton getInstance() {
		if (mInstance == null) {
			throw new IllegalStateException("Please call `createInstance(...)` first");
		}

		return mInstance;
	}

	public synchronized static boolean hasInstance() {
		return mInstance != null;
	}

	public synchronized static void destroyInstance() {
		if (mInstance == null) {
			throw new IllegalStateException("Please call `createInstance(...)` first");
		}

		mInstance.disconnect();
		mInstance.removeCallbacks();
		mInstance = null;
	}

	private MeteorSingleton(final Context context, final String serverUri) {
		super(context, serverUri);
	}

	private MeteorSingleton(final Context context, final String serverUri, final String protocolVersion) {
		super(context, serverUri, protocolVersion);
	}

}
