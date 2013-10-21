function route(handle, pathname, response, request)
{

	if ((typeof handle[pathname]) === 'function')
	{
		debugger;
		console.log("Routing to " + pathname);
		handle[pathname](response, request);
	}
	else
	{
		defaultFun(response);
		console.log("that was of typeof " + typeof handle[pathname]);
	}


}

function defaultFun(response){

	console.log("No nothin!");

		response.writeHead(404, {"Content-Type": "text/plain"}); 
		response.write("fiel not fond!"); 
		response.end();

}

exports.route = route;