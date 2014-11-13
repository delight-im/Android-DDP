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
