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

Database = new Meteor.Collection("database");

if (Meteor.isServer) {
	// define the `Date.now()` function if it isn't defined yet
	if (!Date.now) {
		Date.now = function() {
			return new Date().getTime();
		};
	}

	// namespace for features that are specific to Firebase
	var Firebase = {};

	Firebase.upsert = function (query, update) {
		// process any placeholders that may be included in this update
		update = Firebase.processPlaceholders(update);
		// separate this update itself from possible child updates that may be included in the value
		update = Firebase.extractChildUpdates(update);

		// if child updates were included in the value execute those first
		if (update.children !== null) {
			var childId;
			// loop over all child paths/IDs with their associated updates
			for (var childKey in update.children) {
				if (update.children.hasOwnProperty(childKey)) {
					// construct the ID for the child path
					childId = query._id + "/" + childKey;
					// recursively execute an upsert for the current child
					Firebase.upsert({ "_id": childId }, { "_value": update.children[childKey] });
				}
			}
		}

		// execute the original update itself (with children removed from its value)
		return Database.update(query, { "$set": update.self }, { "upsert": true });
	};

	Firebase.processPlaceholders = function (update) {
		// if this update contains value that is an object
		if (typeof update._value === "object") {
			// if the value is a placeholder
			if (typeof update._value._placeholder === "string") {
				// if the placeholder is for the current timestamp
				if (update._value._placeholder === "timestamp") {
					// replace the value with the timestamp
					update._value = Date.now();
					// and return the modified update
					return update;
				}
			}
		}

		// otherwise just return the unchanged update
		return update;
	};

	Firebase.extractChildUpdates = function (update) {
		// if this update doesn't contain any value to be written
		if (typeof update._value === "undefined" || update._value === null) {
			// just return the unchanged update with no children
			return { "self": update, "children": {} };
		}

		// if the value from this update is not an object
		if (typeof update._value !== "object") {
			// it cannot include any child updates so just return the unchanged update
			return { "self": update, "children": {} };
		}

		// prepare a container holding the child updates by path/ID
		var childUpdates = {};

		// loop over all key/value pairs in the value
		for (var childKey in update._value) {
			if (update._value.hasOwnProperty(childKey)) {
				// save the extracted child update with its path/ID
				childUpdates[childKey] = update._value[childKey];
			}
		}

		// unset the value for the current path
		update._value = null;

		// return the modified update with its single child updates
		return { "self": update, "children": childUpdates };
	};

	// clients may subscribe to all events at once by supplying the name `auto`
	Meteor.publish("auto", function () {
		return Database.find({});
	});

	// clients may subscribe to individual data sets by supplying the name `node`
	// the first argument (`param[0]`) takes a `string` indicating the document ID to subscribe to
	// the second argument (`param[1]`) takes a `boolean` defining whether children should be included in the subscription or not
	Meteor.publish("node", function (documentID, includeChildren) {
		if (includeChildren) {
			var pathRegex = "^"+documentID+"($|/)";
			return Database.find({ "_id": { "$regex": pathRegex }});
		}
		else {
			return Database.find({ "_id": documentID });
		}
	});

	// define methods that will be callable from the client
	Meteor.methods({
		"getServerTime": function () {
			return Date.now();
		},
		"reset": function () {
			return Database.remove({});
		},
		"Firebase.upsert": function (query, update) {
			return Firebase.upsert(query, update);
		}
	});

	Meteor.startup(function () {
		// run setup commands if necessary
		/*if (Database.find().count() === 0) {
			Database.insert({"_id": "settings"});
		}*/
	});
}

if (Meteor.isClient) {
	// let the debugging console subscribe to all events at once
	Meteor.subscribe("auto");
}
