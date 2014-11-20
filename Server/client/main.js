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

function showJson() {
	showJsonCustom(Database._collection._docs._map);
}

function showJsonCustom(obj) {
	var target = $("#fullJson");
	var jsonStr = JSON.stringify(obj, null, 4);
	target.text(jsonStr);
}

function resetDatabase() {
	Meteor.call("reset");
	showJsonCustom({});
}

$(document).ready(function () {
	$("#showJson").click(showJson);
	$("#resetDatabase").click(resetDatabase);
});
