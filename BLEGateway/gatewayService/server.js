"use strict";
/*
const WIFIHandler = require('./WIFIHandler.js');
const CONFIG = require('./config.json');
const express = require('express');
let app = express();

// WIFI

app.use(express.static('public'));

var wHandler=  new WIFIHandler();
//wHandler.initialScan();

app.get('/connectionStatus', function(req, res) {
    res.send(wHandler.connectionState);
});

app.get('/setSSID/:ssid/:password', function (req, res) {
    wHandler.tryConnection(req.params.ssid, req.params.password);
    res.send('ok');
});

app.get('/setSSID/:ssid', function (req, res) {
    wHandler.tryConnection(req.params.ssid, null);
    res.send('ok');
});

app.get('/getSSID', function (req, res) {
  res.json(wHandler.knowSSID);
});

app.get('/*', function(req,res){
    res.redirect('/index.html');
});

app.listen(80, function () {
    console.log('Example app listening on port 80!');
});
*/