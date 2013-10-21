var redis = require("redis"),
	client = redis.createClient();

	client.on("error", function (err) {
        console.log("Error " + err);
    });
/*
POST
username:string
pass:string
name:string
card_type:string
card_number:string
card_validity:date

!Assumes data is sane
*/
    function registerUser(user, response)
    {
    	debugger;
    	client.get("global:user_id", function(err, uid) {
		    if (uid == null)
		    {
			    client.set("global:user_id", 1);
			    console.log("No global user id? Already set to 1");
			    uid = "1";
	    	}
	    	if (err)
	    	{
	    		console.log("Error: " + err);
	    	}
	    	client.exists("username:" + user.username, function(err, reply) { 
	    		if (err == null)
	    		{
	    			console.log(err);
	    		}
	    		debugger;
	    		if (reply == 1)
	    		{
	    			console.log("Ooops, username " + user.username + " taken");
	    			handleReply(buildResponse(401, null, "Username taken"), response);
	    			return false;
	    		}
	    		else
	    		{
	    			registerUserCallback(user, uid, response);
	    			return;
	    		}
	    	});
	    });	

    }



/*
	GET
	uid:string
*/
function getTicketsById(uid, response)
{
	if (uid == null)
	{
		handleReply(buildResponse(500, null, "User id not present"), response);
	}
	client.smembers("user_id:" + uid + ":tickets", function(err, reply)
		{
			debugger;
			if (err)
				console.log(err);
			handleReply(buildResponse(200, JSON.stringify(reply), err ? "Oops!" : null), response);
		});
}


/*
	POST
	username:string
	password:string

	!Assumes data is sane
	!TODO password is plaintext
*/
    function loginUser(user, response)
    {
    	client.get("username:" + user.username, function(err, id) {
    		if (err != null)
    		{
    			console.log(err);
    			handleReply(buildResponse(500, null, "err"), response);
    			return;
    		}

    		if (id == null)
    		{
    			handleReply(buildResponse(401, null, "Username not found"), response);
    			return;
    		}
    		client.hget("user_id:" + id, "password", function(err, reply){
    			if (user.password != reply)
    			{
    				debugger;
    				handleReply(buildResponse(401, null, "Password doesn't match records"), response);
    				console.log("Pw: " + reply);
    				console.log("Actual pw: " + user.password);
    			}
    			var auth = Math.random().toString(36).slice(2);
    			client.hset("user_id:" + id, "api_key", auth);
    			client.hset("user_id:" + id, "api_date", new Date());
    			handleReply(buildResponse(200, {"auth" : auth}, null), response);
    		});
    	});

    }

    function registerUserCallback(user, userId, response)
    {
	    client.hmset("user_id:" + userId, {
	    	"username" : user.username,
	    	"password" : user.password, //TODO hash hash hash
	    	"name" : user.name,
	    	"card_type" : user.card_type,
	    	"card_number" : user.card_number,
	    	"card_validity" : user.card_validity,
	    	"api_key" : Math.random().toString(36).slice(2),
	    	"api_date" : new Date()
	    }, redis.print);

	    client.set("username:" + user.username, userId);

	    console.log("Added user " + "user_id:" + userId);
	    console.log("Username " + user.username);	
	    client.incr("global:user_id");
	    handleReply(buildResponse(200, null, null), response);
    }


    function handleReply(reply, response)
    {
    	response.writeHead(reply.status, {"Content-Type": "text/plain"}); 
    	debugger;

		response.write(JSON.stringify(reply.content)); 
		response.end();
    }

    function buildResponse(status, message, error)
    {
    	var results = {"status" : status, "content" : {}};
    	if (message)
    		results["content"] = message;
    	if (error)
    		results["content"]["error"] = error;

    	return results;
    }

    exports.loginUser = loginUser;
    exports.registerUser = registerUser;
    exports.getTicketsById = getTicketsById;