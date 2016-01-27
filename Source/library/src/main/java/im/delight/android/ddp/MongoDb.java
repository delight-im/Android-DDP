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

/** Constants used in MongoDB (the database behind Meteor) */
public class MongoDb {

	/** Constants defining field names in documents */
	public static class Field {

		public static final String ID = "_id";
		public static final String VALUE = "_value";
		public static final String PRIORITY = "_priority";

	}

	/** Constants defining modifiers that can be used in requests */
	public static class Modifier {

		public static final String SET = "$set";

	}

	/** Constants definining options that may be sent along with requests */
	public static class Option {

		public static final String UPSERT = "upsert";

	}

}
