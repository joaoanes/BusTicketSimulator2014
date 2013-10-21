var http = require("http");
var url = require("url");

function start(port, route, handle)
{
	function onRequest(request, response) { 
		var pathname = url.parse(request.url).pathname; 
		console.log("Request:  " + pathname);
		console.log(route);
		route(handle, pathname, response, request);
	}


	http.createServer(onRequest).listen(port); 
	console.log("Server running.");
}

exports.start = start;