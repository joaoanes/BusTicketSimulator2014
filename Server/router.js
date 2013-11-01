var redis = require("redis"),
	client = redis.createClient();

	client.on("error", function (err) {
        console.log("Error " + err);
    });

function route(handle, pathname, response, request)
{

	if ((typeof handle[pathname]) === 'function')
	{	
		console.log(" ///  ROUTING " + pathname + " \\\\\\");
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
		response.write("404!"); 
		response.end();

}

function resolveAuthIssues(handle, pathname, response, request)
{
	
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
			var finalDate = Date.parse(results[1]) + 86400000; //24 hours
			if (new Date().getTime() > finalDate)
			{
				return rejectAuth(response, ": auth key expired");
			}
			else
			{
				if (auth_code != results[0])
				{
					return rejectAuth(response);		
				}
				else
				{
					request._authorized = reply; //_authorized now has the uid of the original user. Useful!
					console.log("Authenticated as " + reply +"!");
					handle[pathname](response, request);
				}

			}
		});
	});		
	
}

function rejectAuth(response, err)
{
		response.writeHead(401, {"Content-Type": "text/plain"});

		response.write(JSON.stringify({"error" : "Authentication failed" + (err ? " " + err : "")})); 
		response.end();
}

exports.route = route;