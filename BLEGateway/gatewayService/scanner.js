"use strict";

const BLEScanner = require('./BLEScanner');
const CONFIG = require('./config.json');
const request = require('request');
let scanner;

// Scanner

function extractEventsFromHashTable(hashTable) {
    let keys = Object.keys(hashTable);
    let values = [];

    for (let k = 0; k < keys.length; k++) {
        values.push(hashTable[keys[k]]);
    }

    return values;
}

function startScanner() {
    scanner = new BLEScanner();
    scanner.scan(CONFIG.isProduction);
    
    if (CONFIG.isProduction) {
        scanner.onError = startScanner; // TODO: Check if it makes sense
    }

    // Run the scanner after an interval of time (e.g. 15 secondes)
    let intervalID = setInterval(runBLERequest, CONFIG.scanInterval);
}

function runBLERequest() { 
    let newData = scanner.getCollectionData();
    let events = extractEventsFromHashTable(newData);

    console.log("BLE data to send to backend:");
    console.log(events);
    console.log("");

    //TODO: send only unique records!!!!

    if (events.length) {
        request({
            url: CONFIG.backendURL,
            method: 'POST',
            json: events,
            headers: {'token': CONFIG.backendToken},
            }, 
        function(error, response, body) {
            if (error) {
                console.log("Cannot send event to backend, error: " + error);
                // TODO: store events not sent in DB!
            } else {
                console.log("Sent event to backend, body: " + body);
            }
        });
    }
}

startScanner();