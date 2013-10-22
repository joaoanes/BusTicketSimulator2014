var redis = require("redis"),
	client = redis.createClient();

	client.on("error", function (err) {
        console.log("Error " + err);
    });

function route(handle, pathname, response, request)
{

	if ((typeof handle[pathname]) === 'function')
	{
		console.log("Routing to " + pathname);
		if (handle[pathname].auth)
			resolveAuthIssues(handle, pathname, response, request);
		else
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

function resolveAuthIssues(handle, pathname, response, request)
{
	console.log("Authenticating!");
	var url = require("url");
	var auth_code = url.parse(request.url, true).query["auth"];
	if (!auth_code)
		return rejectAuth(response);
	else

	client.get("auth:" + auth_code, function(err, reply){
		if (err)
		{
			console.log(err);
			return rejectAuth(response);
		}
		if (reply == null)
		{
			console.log("Bad auth with token " + auth_code);
			return rejectAuth(response);
		}
		multi = client.multi();
		multi.hget("user_id:" + reply, "api_key");
		multi.hget("user_id:" + reply, "api_date");
		multi.exec(function(err2, results)
		{
			if (err2)
			{
				console.log(err2);
				return rejectAuth(response);
			}

			//var finalDate = new Date(Date.parse(results[1]).getTime() + 30*60000);
			if (/*new Date() > finalDate*/ false)
			{
				rejectAuth(response);
				return client.set("auth:" + auth_code, null);
			}
			else
			{
				if (auth_code != results[0])
				{
					return rejectAuth(response);		
				}
				else
				{
					handle[pathname](response, request);
				}

			}


			debugger;
		});
	});		
	
}

function rejectAuth(response)
{
		response.writeHead(401, {"Content-Type": "text/plain"}); 
		response.write("{\"error\": Authentication failed }"); 
		response.end();
}

exports.route = route;