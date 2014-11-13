function showJson() {
	var target = $("#fullJson");
	var jsonStr = JSON.stringify(Database._collection._docs._map, null, 4);
	target.text(jsonStr);
}

$(document).ready(function () {
	$("#showJson").click(showJson);
});
