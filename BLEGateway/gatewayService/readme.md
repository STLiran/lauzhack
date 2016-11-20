# BLE Gateway

## Pi installation

	apt-get install bluetooth bluez blueman pi-bluetooth
	sudo reboot
	
	hciconfig
	# in Tmux
	sudo hcitool lescan --duplicates
	sudo hcidump --raw
	

## Wifi Stack

  http://askubuntu.com/questions/16584/how-to-connect-and-disconnect-to-a-network-manually-in-terminal
  sudo iwlist wlan0 scan

  	iwconfig wlan0 essid CYREX key PASSWORD
 	dhclient

  scan 

  	sudo iwlist wlan0 scan
  
  
down AP adhoc

		sudo ifconfig wlan0 down
		sudo iwconfig wlan0 essid "AXA アーユーOK?"  mode ad-hoc
		sudo ifconfig wlan0 up
		sudo ifconfig wlan0 192.168.1.1 netmask 255.255.255.0
  
  DHCP SERVER
  
  		sudo apt-get install isc-dhcp-server
  		

sudo nano -w /etc/dhcp/dhcpd.conf

subnet 10.0.0.0 netmask 255.255.255.0 {
  range dynamic-bootp 10.0.0.5 10.0.0.10;
  option routers 10.0.0.1;
}



sudo ifconfig wlan0 10.0.0.1

sudo iptables -t nat -A PREROUTING -p tcp -m tcp --dport 80 -j DNAT --to-destination 10.0.0.1

 iw wlan0 set power_save off

 apt-get install hostapd
  https://frillip.com/using-your-raspberry-pi-3-as-a-wifi-access-point-with-hostapd/


nano /etc/hostapd/hostapd.conf

      # This is the name of the WiFi interface we configured above
      interface=wlan0

      # Use the nl80211 driver with the brcmfmac driver
      driver=nl80211

      # This is the name of the network
      ssid="AXA アーユーOK?"

      # Use the 2.4GHz band
      hw_mode=g

      # Use channel 6
      channel=6

      # Enable 802.11n
      ieee80211n=1

      # Enable WMM
      wmm_enabled=1

      # Enable 40MHz channels with 20ns guard interval
      ht_capab=[HT40][SHORT-GI-20][DSSS_CCK-40]

      # Accept all MAC addresses
      macaddr_acl=0


sudo service hostapd stop




sudo apt-get install dnsmasq
http://sirlagz.net/2013/08/23/how-to-captive-portal-on-the-raspberry-pi/