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

import org.codehaus.jackson.JsonNode;

/** Constants used in the Distributed Data Protocol (DDP) */
public class Protocol {

	/** Constants defining message types in message objects' values */
	public static class Message {

		public static final String ADDED = "added";
		public static final String ADDED_BEFORE = "addedBefore";
		public static final String CHANGED = "changed";
		public static final String CONNECT = "connect";
		public static final String CONNECTED = "connected";
		public static final String FAILED = "failed";
		public static final String METHOD = "method";
		public static final String NOSUB = "nosub";
		public static final String PING = "ping";
		public static final String PONG = "pong";
		public static final String READY = "ready";
		public static final String REMOVED = "removed";
		public static final String RESULT = "result";
		public static final String SUBSCRIBE = "sub";
		public static final String UNSUBSCRIBE = "unsub";

	}

	/** Constants defining field names in message objects' keys */
	public static class Field {

		public static final String CLEARED = "cleared";
		public static final String COLLECTION = "collection";
		public static final String DETAILS = "details";
		public static final String ERROR = "error";
		public static final String FIELDS = "fields";
		public static final String ID = "id";
		public static final String MESSAGE = "msg";
		public static final String METHOD = "method";
		public static final String NAME = "name";
		public static final String PARAMS = "params";
		public static final String RANDOM_SEED = "randomSeed";
		public static final String REASON = "reason";
		public static final String RESULT = "result";
		public static final String SESSION = "session";
		public static final String SUBS = "subs";
		public static final String SUPPORT = "support";
		public static final String VERSION = "version";
		public static final String TOKEN = "token";

	}

	/** Wrapper and utility class to store errors from the DDP protocol */
	public static class Error {

		private final String mError;
		private final String mReason;
		private final String mDetails;

		private Error(final String error, final String reason, final String details) {
			mError = error;
			mReason = reason;
			mDetails = details;
		}

		public static Error fromJson(final JsonNode json) {
			final String error;
			if (json.has(Protocol.Field.REASON)) {
				final JsonNode errorJson = json.get(Protocol.Field.ERROR);
				if (errorJson.isTextual()) {
					error = errorJson.getTextValue();
				}
				else if (errorJson.isNumber()) {
					error = errorJson.getNumberValue().toString();
				}
				else {
					throw new RuntimeException("Unexpected data type of error.error");
				}
			}
			else {
				error = null;
			}

			final String reason;
			if (json.has(Protocol.Field.REASON)) {
				reason = json.get(Protocol.Field.REASON).getTextValue();
			}
			else {
				reason = null;
			}

			final String details;
			if (json.has(Protocol.Field.DETAILS)) {
				details = json.get(Protocol.Field.DETAILS).getTextValue();
			}
			else {
				details = null;
			}

			return new Error(error, reason, details);
		}

		public String getError() {
			return mError;
		}

		public String getReason() {
			return mReason;
		}

		public String getDetails() {
			return mDetails;
		}

	}

}
