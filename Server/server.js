var http = require("http");
var url = require("url");

function start(port, route, handle)
{
	function onRequest(request, response) {
		var pathname = url.parse(request.url).pathname; 
		route(handle, pathname, response, request);
	}


	http.createServer(onRequest).listen(port); 
	console.log("Server running.");
}

exports.start = start;