# EnergyHarvester
Team 33: Energy Harvester Power Management App
The app is designed to moniter output from the HAMSTR via the WECS system

The app is made of the activites that handle most of the operation.
MainActivity.java
DatabaseHelper.java
BluetoothLeUart.java

The layout is comprised of the following file.
activity_main.xml

The activities are found in the app -> src -> main -> java -> com -> bobbylwillis -> energyharvester

MainActivity.java handles the majority of the app setup. 

The methods before manageBluetoothAvailabilty() in MainActivity.java constructs the graph and buttons.

View Database Button
Creates a toast window that shows the data from the current graph

Clear
Removes all data from current table

Add Data
adds data entry to current table

On/off toggle button
starts data collection. Bluetooth connection starts up when app is active. The toggle button switches a variable that controls whether data is inserted or not.

The graph is populated with data from the OnReceive method. It autopopulates depending on what the data is. 

Half of the activity handles the bluetooth connectivity. The bluetooth code in main uses methods from BluetoohLeUart.
OnReceive handles the data coming from the Bluefruit module. Data comes in the form of a flag byte then the voltage reading is split into bytes.


DatabaseHelper.java
Constructs a database of 4 tables( total, solar, thermal, piezo).

Each table has 5 columns. (ID, DateTime, Power, Voltage, Current)

If there are futher questions, please email blwillis@ncsu.edu

