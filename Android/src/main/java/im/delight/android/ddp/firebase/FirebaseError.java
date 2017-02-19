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

import java.lang.String;

/** Wrapper for an error description that will be passed to callbacks when an operation failed */
public class FirebaseError {

	private final int mCode;
	private final String mMessage;
	private final String mDetails;

	public FirebaseError(int code, String message) {
		this(code, message, null);
	}

	public FirebaseError(int code, String message, String details) {
		mCode = code;
		mMessage = message;
		mDetails = (details == null ? "" : details);
	}

	public static FirebaseError fromException(Throwable e) {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	public int getCode() {
		return mCode;
	}

	public String getDetails() {
		return mDetails;
	}

	public String getMessage() {
		return mMessage;
	}

	public FirebaseException toException() {
		return new FirebaseException("Firebase error: " + mMessage);
	}

	@Override
	public String toString() {
		return "FirebaseError: " + mMessage;
	}

}
