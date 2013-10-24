var redis = require("redis"),
	client = redis.createClient(),
	crypto = require("crypto");

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
			if (err)
				console.log(err);
			var multi = client.multi();
			for (var i = 0; i < reply.length; ++i)
			{
				multi.hgetall("ticket_id:" + reply[i]);
			}
			multi.exec(function(err, replies){
				for (var i = 0; i < replies.length; ++i)
				{
					replies[i]["id"] = reply[i];
					if (replies[i]["validated"] == null)
						replies[i]["validated"] = "null";
					if (replies[i]["bus"] == null)
						replies[i]["bus"] = "null";
				}
				handleReply(buildResponse(200, {"tickets" : replies}, err ? "Oops! " + err : null), response);
			});

			
		});
}


	function buyTickets(uid, ticket_n, ticket_type, response)
	{
		client.get("global:ticket_id", function(err, tid) {
		    if (tid == null)
		    {
			    client.set("global:ticket_id", 1, redis.print);
			    console.log("No global ticket id? Already set to 1");
			    tid = "1";
	    	}
	    	if (err)
	    	{
	    		console.log(err);
	    		return handleReply(buildResponse(500, null, err), response);
	    	}
	    	var tickets = new Array();
	    	var allTickets = parseInt(tid) + parseInt(ticket_n);

			var shasum;

	    	for (; tid < allTickets; ++tid)
	    	{
	    		shasum = crypto.createHash('sha1');
	    		shasum.update("" + tid, 'ascii');
	    		tickets.push(shasum.digest('hex'));
	    	}
	    	var multi = client.multi();
	    	var ticket;
	    	var ticket_arr = new Array();
	    	for (var i = 0; i < tickets.length; ++i)
	    	{
	    		multi.sadd("user_id:" + uid + ":tickets", tickets[i], redis.print);
	    		ticket = {};
	    		ticket["type"] = ticket_type;
	    		ticket["uid"] = uid;
	    		multi.hmset("ticket_id:" + tickets[i], ticket, redis.print);
	    		ticket["validated"] = "null";
	    		ticket["bus"] = "null";
	    		ticket["id"] = tickets[i];
	    		ticket_arr.push({"uid" : uid, "type" : ticket_type, "id" : tickets[i], "bus" : "null", "validated" : "null"});
	    	}
	    	multi.exec(redis.print);
	    	client.incrby("global:ticket_id", ticket_n, redis.print);
	    	return handleReply(buildResponse(200, {"tickets" : ticket_arr}, null), response);
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
    				handleReply(buildResponse(401, null, "Password doesn't match records"), response);
    				console.log("Pw: " + reply);
    				console.log("Actual pw: " + user.password);
    			}
    			var auth = Math.random().toString(36).slice(2);
    			client.set("auth:" + auth, id, function(err){ if (err) console.log(err); });
    			client.hset("user_id:" + id, "api_key", auth);
    			client.hset("user_id:" + id, "api_date", new Date());
    			handleReply(buildResponse(200, {"auth" : auth}, null), response);
    		});
    	});

    }

    function registerUserCallback(user, userId, response)
    {
    	var auth = Math.random().toString(36).slice(2);
	    client.hmset("user_id:" + userId, {
	    	"username" : user.username,
	    	"password" : user.password, //TODO hash hash hash
	    	"name" : user.name,
	    	"card_type" : user.card_type,
	    	"card_number" : user.card_number,
	    	"card_validity" : user.card_validity,
	    	"api_key" : auth,
	    	"api_date" : new Date()
	    }, redis.print);

	    client.set("username:" + user.username, userId);

	    console.log("Added user " + "user_id:" + userId);
	    console.log("Username " + user.username);	
	    client.incr("global:user_id");
	    client.set("auth:" + auth, userId, redis.print);
	    var arr = {"auth" : auth};
	    handleReply(buildResponse(200, arr, null), response);
    }



    function handleReply(reply, response)
    {
    	response.writeHead(reply.status, {"Content-Type": "application/json"}); 
    	

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
    exports.buyTickets = buyTickets;

  