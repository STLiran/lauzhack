"use strict";

const CONFIG = require("./config.json");
const spawn = require('child_process').spawn;
const exec = require('child_process').exec;
const hash = require('object-hash');

function BLEScanner() {
    this.scanner = null;
    this.collection = {};
    this.buffer = "";
    this.gatewayID;

    this.scan = function(isProd) {
        // Test environment
        if (!isProd) {
            this.scanner = spawn('cat', ['./dataTest.dump']);
        }
        // Production environment
        else {
            exec("sudo pkill -f lescan; sudo pkill -f hcidump;", function(error, stdout, stderr) {
                if (error) {
                    console.error("Cannot kill all lescan snd hcidump processes! " + error);
                }
            });
            //TODO: hciconfig hci0 down and up
        
            exec('2>/dev/null 1>/dev/null sudo hcitool lescan --duplicates &', function(error, stdout, stderr) {
                if (error) {
                    console.error("Error when running hcitool lescan: " + error);
                    return;
                }
                //console.log("Stdout of hcitool lescan: " + stdout);
                //console.log("Stderr of hcitool lescan: " + stderr);
            });

            this.scanner = spawn('sudo', ['hcidump', '--raw']);
        } 
        this.scanner.stdout.on('data', this.newDataIn.bind(this));
        this.scanner.stderr.on('data', this.errorDataIn.bind(this));
        this.scanner.on('exit', this.handleExit.bind(this));
    }

    this.newDataIn = function(data) {
        data = this.buffer + data.toString();
        let packets = data.split(">");

        for (let j = 0; j < packets.length; j++) {
            if (packets[j]) {
                this.analysePacket(packets[j]);
            }
        }
    }

    this.analysePacket = function(packet) {
        // Cleaning the string (it is massive :), could be lighter)
        packet = packet.replace(/\r?\n|\r/g, '').replace(/\s/g, '').replace(/>/g, '');
        //console.log(packet);

        // Detect EM beacon format
        if (packet.indexOf('0F09454D426561636F6E') === 28) {
            let payload = this.identifyEMDevice(packet);         
            this.collection[hash.MD5(payload)] = payload;
        }
        // Detect iBeacon format https://developer.mbed.org/blog/entry/BLE-Beacons-URIBeacon-AltBeacons-iBeacon/
        else if (packet.indexOf("0201061AFF4C000215") === 28) {
            let payload = this.identifyBeacon(packet); 
            this.collection[hash.MD5(payload)] = payload;
        }

        //console.log("Peripheral added to collection:");
        //console.log(this.collection);
        //console.log("");
        //console.log("");
    }

    this.getGatewayID = function() {
        if (this.gatewayID) {
            return this.gatewayID;
        }

        let networkInterfacesDump = require('os').networkInterfaces();
        let eth0Config = networkInterfacesDump['wlan0'];

        if (eth0Config && eth0Config.constructor === Array) {
            let macAddress;
            for (let u = 0; u < eth0Config.length; u++) {
                if (eth0Config[u]['family'] === 'IPv4') {
                    macAddress = eth0Config[u]['mac'];
                }
            }
            if (macAddress) {
                this.gatewayID = macAddress;
                return macAddress;
            }
        }

        return CONFIG.dummyGatewayID;
    }

    this.identifyBeacon = function(packet) {
        let uuid = packet.substr(46, 32);
        let major = parseInt(packet.substr(78, 4), 16);
        let minor = parseInt(packet.substr(82, 4), 16);
        let iBeaconData = {
            gatewayID: this.getGatewayID(),
            UUID: uuid,
            major: major,
            minor: minor
        };

        //console.log("UUID : " + uuid);
        //console.log("Major : " + major, packet.substr(78, 4));
        //console.log("Minor : " + minor, packet.substr(82, 4));

        // Creation of event payload to send to backend
        // Types odf events: beacon, button, ibeacon (either beacon or button), temperature, movement or unknown

        // Test if we make a difference between beacons and buttons
        if (CONFIG.differentiateBeaconsAndButtons) {
            // Test if ibeacon is a button
            if(CONFIG.majorsForButtons.indexOf(major) !== -1) {
                iBeaconData.type = "button";
            } else {
                iBeaconData.type = "beacon";
            }
        } else {
            iBeaconData.type = "ibeacon";
        }

        return iBeaconData;
    }

    this.identifyEMDevice = function(packet) {
        let minor = packet.substr(48, 10);

        let minorStr = "";
        for (let i = 0; i < minor.length; i += 2) {
            minorStr += String.fromCharCode(parseInt(minor.substr(i, 2), 16));
        }

        minor  = parseInt(minorStr);
        let sensorData = packet.substr(68, 4);
        let sensorType = sensorData[0];
        let sensorName = "unknown";
        let sensorValue = 0;

        if (sensorType == 'B') {  
            sensorName = 'movement';
            let counterData = packet.substr(87, 3);
            sensorValue = parseInt(counterData, 16);
        } else if (sensorType == '4') {
            sensorName = 'temperature';
            sensorValue = parseInt(sensorData[1] + sensorData[2], 16) + (parseInt(sensorData[3], 16) * 1/16);
        }

        let iBeaconData = {
            gatewayID: this.getGatewayID(),
            type: sensorName,
            UUID: CONFIG.uuidForEMDevice,
            minor: minor,
            value: sensorValue
            //timestamp: Math.floor(Date.now() / 1000) // Maybe not a good idea
        };

        return iBeaconData;
    }

    this.getCollectionData = function() {
        let collectionData = this.collection;
        this.collection = {};
        return collectionData;
    }

    this.errorDataIn = function(data) {
        console.log('Error', data);
    }

    this.handleExit = function() {
        // TODO restart process on production
        if (this.onError) {
            this.onError();
        }
    }

    this.onError = null;
}

module.exports = BLEScanner;