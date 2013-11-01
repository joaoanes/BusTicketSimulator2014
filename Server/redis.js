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

		var firstMulti = client.multi();

		firstMulti.smembers("user_id:" + uid + ":tickets:1");
		firstMulti.smembers("user_id:" + uid + ":tickets:2");
		firstMulti.smembers("user_id:" + uid + ":tickets:3");

		firstMulti.exec(function(err, allTicketsReply)
		{
			var allTickets = new Array();
			for (var k = 0; k < allTicketsReply.length; ++k)
			{
				for (var r = 0; r < allTicketsReply[k].length; ++r)
				{
					allTickets.push(allTicketsReply[k][r]);
				}
			}

			if (err)
				console.log(err);
			var multi = client.multi();
			for (var i = 0; i < allTickets.length; ++i)
			{
				multi.hgetall("ticket_id:" + allTickets[i]);
			}
			multi.exec(function(err, replies){
				debugger;
				for (var j = 0; j < replies.length; ++j)
				{
					replies[j]["id"] = allTickets[i]	;
					if (replies[j]["validated"] == null)
						replies[j]["validated"] = "null";
					if (replies[j]["bus"] == null)
						replies[j]["bus"] = "null";
				}
				handleReply(buildResponse(200, {"tickets" : replies}, err ? "Oops! " + err : null), response);
			});

			
		});
	}


	function buyTickets(uid, ticket_n_1, ticket_n_2, ticket_n_3, confirm, response)
	{
		client.hgetall("global:ticket_prices", function(err, prices)
		{
			if (prices == null)
			{
				prices = {};
				prices[0] = 25;
				prices[1] = 35;
				prices[2] = 45;
			}
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
				var tickets1 = new Array();
				var tickets2 = new Array();
				var tickets3 = new Array();
			
				var t3counter = parseInt(ticket_n_3);
				var t2counter = parseInt(ticket_n_2);
				var t1counter = parseInt(ticket_n_1);
				var t3counter_c = t3counter;
				var t2counter_c = t2counter;
				var t1counter_c = t1counter;
				var extraTicket = null;
				var extra = (t1counter + t2counter + t3counter) > 10;
				if (extra)
				{
					if (t1counter != 0)
						{++t1counter; extraTicket = "T1";}
					else if (t2counter != 0)
						{++t2counter; extraTicket = "T2";}
					else if (t3counter != 0)
						{++t3counter; extraTicket = "T3";}
				}

				var t3counter_c = t3counter;
				var t2counter_c = t2counter;
				var t1counter_c = t1counter;
				var shasum;

				var allTickets = parseInt(tid) + parseInt(ticket_n_1) + parseInt(ticket_n_2) + parseInt(ticket_n_3) + (extra ? 1 : 0);


				for (; tid < allTickets; ++tid)
				{
					shasum = crypto.createHash('sha1');
					shasum.update("" + tid, 'ascii');
					if (t3counter > 0)
					{
						tickets3.push(shasum.digest('hex'));
						--t3counter;
					}
					else if (t2counter > 0)
					{
						tickets2.push(shasum.digest('hex'));
						--t2counter;
					}	
					else if (t1counter > 0)
					{
						tickets1.push(shasum.digest('hex'));
						--t1counter;
					}		

				}
				t3counter = t3counter_c;
				t2counter = t2counter_c;
				t1counter = t1counter_c;

				var multi = client.multi();
				var ticket;
				var ticket_arr = new Array();

				processTickets(multi, uid, tickets1, "T1", ticket_arr);
				processTickets(multi, uid, tickets2, "T2", ticket_arr);
				processTickets(multi, uid, tickets3, "T3", ticket_arr);

				if (confirm)
				{
					multi.exec(redis.print);
					client.incrby("global:ticket_id", t1counter+t2counter+t3counter, redis.print);
					return handleReply(buildResponse(200, {"tickets" : ticket_arr}, null), response);
				}
				else
				{
					var last = {};
					var checkoutPrice = prices[0]*t1counter + prices[1]*t2counter + prices[2]*t3counter;
					last["price"] = checkoutPrice;
					if (t1counter > 0)
						last["T1"] = t1counter;
					if (t2counter > 0)
						last["T2"] = t2counter;
					if (t3counter > 0)
						last["T3"] = t3counter;
					if (extra)
						last["extra"] = extraTicket;
					
					debugger;
					return handleReply(buildResponse(200, last, null), response);
				}
				
			});
}	);
}

function processTickets(multi, uid, tickets, ticket_type, ticket_arr)
{
	if (tickets.length == 0)
		return;

	for (var i = 0; i < tickets.length; ++i)
	{
		multi.sadd("user_id:" + uid + ":tickets:" + ticket_type.slice(-1), tickets[i], redis.print);
		ticket = {};
		ticket["type"] = ticket_type;
		ticket["uid"] = uid;
		multi.hmset("ticket_id:" + tickets[i], ticket, redis.print);
		ticket["validated"] = "null";
		ticket["bus"] = "null";
		ticket["id"] = tickets[i];
		ticket_arr.push({"uid" : uid, "type" : ticket_type, "id" : tickets[i], "bus" : "null", "validated" : "null"});
	}
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
	exports.buyTickets = buyTickets;

