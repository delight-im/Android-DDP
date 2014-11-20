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
