var http = require("http");
var port = 8080;

var url = require("url");
var server = require("./server");
var router = require("./router");
var users = require("./users");
var redis = require("./redis");

var handle = {'/login': users.login, '/register' : users.register, '/tickets/user': users.getTickets, '/tickets/buy': users.buyTickets}; 

server.start(port, router.route, handle);