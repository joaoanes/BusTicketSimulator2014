var redis = require('./redis.js'),
qs = require('querystring');
var url = require("url");

function login(response, req)
{
	if (req.method=="POST")
	{	
		var body = '';
        req.on('data', function (data) {
            body += data;
        });
        req.on('end', function () {
        	debugger;
		
            var POST = qs.parse(body);
            redis.loginUser(POST, response);

        });
	    
	}
	else
	{

    	var postHTML =
		  '<html><head><title>Really basic login form</title></head>' +
		  '<body>' +
		  '<form name="FORM" method="post">' +
		  'Username: <input name="username"><br>' +
		  'Password: <input name="password"><br>' +
		  '<input type="submit">' +
		  '</form>' +
		  '</body></html>';
		response.writeHead(200); 
    	response.write(postHTML); 
		response.end();
	}
}

function register(response, req)
{
      	if (req.method=="POST")
	{	
		var body = '';
        req.on('data', function (data) {
            body += data;
        });
        req.on('end', function () {
        	
            var POST = qs.parse(body);
            debugger;
            redis.registerUser(POST, response);

        });
	    
	}
	else
	{

    	var postHTML =
		  '<html><head><title>Really basic register form</title></head>' +
		  '<body>' +
		  '<form name="FORM" method="post">' +
		  'Username: <input name="username"><br>' +
		  'Password: <input name="password"><br>' +
		  'Name: <input name="name"><br>' +
		  'Card type: <input name="card_type"><br>' +
		  'Card validity: <input name="card_validity"><br>' +
		  'Card number: <input name="card_number"><br>' +
		  '<input type="submit">' +
		  '</form>' +
		  '</body></html>';
		response.writeHead(200); 
    	response.write(postHTML); 
		response.end();
	}
	
}

function getTicketsByUser(response, req)
{
	redis.getTicketsById(req._authorized, response);
}

function buyTickets(response, req)
{
	  	if (req.method=="POST")
	{	
		var body = '';
        req.on('data', function (data) {
            body += data;
        });
        req.on('end', function () {
        	
            var POST = qs.parse(body);
            //(uid, ticket_n_1, ticket_n_2, ticket_n_3, ticket_type, confirm, response)
            redis.buyTickets(req._authorized, POST["t_num_1"], POST["t_num_2"], POST["t_num_3"], POST["confirm"], response);
            

        });
	    
	}
	else
	{

    	var postHTML =
		  '<html><head><title>Really basic ticket buy form</title></head>' +
		  '<body>' +
		  '<form name="FORM" method="post">' +
		  'Ticket number 1: <input name="t_num_1"><br>' +
		  'Ticket number 2: <input name="t_num_2"><br>' +
		  'Ticket number 3: <input name="t_num_3"><br>' +
		  '<input type="checkbox" name="confirm">' +
		  '<input type="submit">' +
		  '</form>' +
		  '</body></html>';
		response.writeHead(200); 
    	response.write(postHTML); 
		response.end();
	}
}

buyTickets.auth = true;
getTicketsByUser.auth = true;

function split(data){
    var splits = data.split('&');
    var hash = [];
    console.log(splits.length);
    for (i = 0; i < splits.length; i++)
    {
        var iSplit = splits[i].split('=');
        hash[iSplit[0]] = iSplit[1];
    }
    return hash;
}

exports.login = login;
exports.register = register;
exports.getTickets = getTicketsByUser;
exports.buyTickets = buyTickets;