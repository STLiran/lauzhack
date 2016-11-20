"use strict";

const exec = require('child_process').exec;
const CONFIG = require("./config.json");
const request = require('request');
const fs = require('fs');

var WIFIHandler = function() {
    this.ssid = null;
    this.pwd = null;
    this.connectionState = 'TEST';
    this.knowSSID = {};
    this.wifiScan = 0;
    this.shouldScan = true;

    this.initialScan = function() {
        console.log("WIFIHandler: Initial scan");
        this.refreshSSID();
        this.wifiScan = CONFIG.nbOfWiFiScans;
        setTimeout(this.runNextScan.bind(this), 2000);
    }

    this.saveCredentials = function() {
        fs.writeFile(__dirname + '/__id', JSON.stringify({
            ssid : this.ssid,
            pwd : this.pwd
        }));    
    }

    this.runNextScan = function() {
        console.log("WIFIHandler: Run next scan");
        this.refreshSSID();
        if (this.wifiScan > 0) {
            setTimeout(this.runNextScan.bind(this), CONFIG.wifiScansInterval);
        } else {
            //setTimeout(this.runNextScan.bind(this), 300000);
            this.stopAP();
            this.startAP();
        }
    }

    this.refreshSSID = function() {
        console.log("WIFIHandler: Refresh SSID");
        if (!this.shouldScan) {
            return;
        }

        this.wifiScan--;
        this.stopAP();

        exec("sudo wlan0 up && sudo iwlist wlan0 scan", function (error, stdout, stderr) {
            console.log("Scanning SSID");
            console.log(stdout);
            var items = stdout.split('Cell ');
            items.splice(0, 1);
            var ssids = [];

            for (var i = 0; i < items.length; ++i) {
                var itemLines = items[i].split('\n');
                if (itemLines.length > 6) {
                    var ssid = itemLines[5].split(":")[1];
                    var encryption = itemLines[4].split(":")[1];
                    var quality = itemLines[3].trim().split('=')[1].split(' ')[0];
                    var address = itemLines[0].trim().split('Address:')[1].trim();
                    console.log(ssid, 'encryption : ', encryption, quality);
                    ssids.push({
                        ssid : ssid.replace(/\"/g, ''),
                        encryption : encryption,
                        quality : quality,
                        address : address
                    })
                }
            }

            for (var i = 0; i < ssids.length; ++i) {
                if (!this.knowSSID[ssids[i].address]) {
                    this.knowSSID[ssids[i].address] = ssids[i];
                } else {
                    this.knowSSID[ssids[i].address] = ssids[i];
                }
            }
            this.startAP();
        }.bind(this));
    }

    this.startAP = function() {
        console.log("Starting AP");
        exec("sudo killall wpa_suplicant;\
            sudo killall dhclient;\
            sudo ifconfig wlan0 up;\
            sudo ifconfig wlan0 0;\
            sudo service hostapd start;\
            sudo ifconfig wlan0 192.168.1.1;\
            printf 'domain ael.li\nnameserver 192.168.1.1\n' | sudo tee /etc/resolv.conf;\
            sudo service dnsmasq restart;\
            sudo service isc-dhcp-server start;", 
        function (error, stdout, stderr) {
            console.log(stdout);
            if (error) {
                console.log(error);
            }
            console.log(stderr);
        });
    }

    this.stopAP = function(cb) {
        console.log("Stopping AP");
        var _cb = cb;
        exec("sudo service hostapd stop;\
            sudo service isc-dhcp-server stop;\
            printf 'domain ael.li\nnameserver 8.8.8.8\n' | sudo tee /etc/resolv.conf;\
            sudo ifconfig wlan0 0;\
            sudo ifconfig wlan0 down;\
            sudo service dnsmasq stop",
        function (error, stdout, stderr) {
            console.log(stdout);
            if (error) {
                console.log(error);
            }
            console.log(stderr);
            if (_cb) {
                _cb();
            }
        });
    }

    this.tryConnection = function(ssid, pwd) {
        console.log("Trying to connect to WIFI with ssid/password: " + ssid + " " + pwd);
        this.shouldScan = false;
        this.connectionState = 'TEST';
        this.ssid = ssid;
        this.pwd = pwd;

        this.stopAP(function() {
            console.log("Stopping AP");
            var cmd = "";

            if (this.pwd) {
                cmd = "sudo wpa_passphrase \"" + this.ssid + "\" \"" + this.pwd + "\" | sudo tee /etc/wpa_supplicant.conf;\
                sudo wpa_supplicant -B -i wlan0 -c/etc/wpa_supplicant.conf -D nl80211;";
            } else {
                var conf = 'network={\nssid="SSID"\nkey_mgmt=NONE\n}';
                fs.writeFileSync('/etc/wpa_supplicant.conf', conf.replace('SSID', this.ssid));
                cmd = "sudo wpa_supplicant -B -i wlan0 -c/etc/wpa_supplicant.conf -D nl80211;"
            }

            cmd += 'sudo dhclient wlan0;'
            console.log('execute :', cmd);

            exec(cmd, function (error, stdout, stderr) {
                console.log('==========');
                console.log(cmd);
                console.log(stdout);
                console.log('==========');

                if (error) {
                    console.log(error);
                    console.log(stderr);
                }
                
                if (stdout.indexOf('failed to connect') != -1 || error) {
                    this.shouldScan = true;
                    this.connectionState = 'FAIL';
                    this.startAP();
                    return;
                }

                request('http://www.google.com', function (error, response, body) {
                    console.log('Return From request to axa.com')
                    if (error) {
                        this.connectionState = 'FAIL';
                        this.shouldScan = true;
                    }
                    else {
                        this.connectionState = 'SUCCESS';
                        this.saveCredentials();
                        this.shouldScan = false;
                    }
                    //this.startAP(); //??????
                }.bind(this));
            }.bind(this));
        }.bind(this));
    }
}

module.exports = WIFIHandler;