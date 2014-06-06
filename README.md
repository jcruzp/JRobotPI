JRobotPI
========

https://sites.google.com/site/jrobotpi/

###JRobotPI.java

Java ME 8 Midlet program, is the firmware to control the robot and all sensors, dc motors, servos and utilities modules.

Implements a framework for developers make all robotic applications and extend libraries.

All sensors have an interface implemented in Java, some was converted from Phyton others from Arduino C code. 

Don´t have multiples layers for communicate with sensors only use java jdk.dio packages i2cbus, uart and gpio. All native java ME 8 clases was used.


The packages defined are:

**com.jcruz.jrobotpi.i2c** : enum classes with all commands supported by each i2c connected device or sensor.
**com.jcruz.jrobotpi.i2c.driver** : implemented all commands defined in enum classes.
**com.jcruz.jrobotpi.gpio.driver** : implemented commands defined for gpio connected devices.
**com.jcruz.jrobotpi.uart.driver** : implemented commands defined for uart connected devices.
**com.jcruz.jrobotpi.http** : enum classes with all commands supported by each http connected device or site.
**com.jcruz.jrobotpi.http.driver** : implemented all commands defined in enum classes.

Basicly we needs an enum class where with all defined or supported commands and a driver class that implements all commands.
With Netbeans IDE 8.0 I can install and run MIdlet JRobotPI.java directly to Raspberry PI, that have installed a Oracle Java ME Embedded 8 for Raspberry Pi Model B. 


For now i have interface in java to this devices:

|Device|Interface Type|
--------------|---------------|
|Emic 2 Text-to-Speech module|UART|
|Stereo 2.8W Class D Audio Amplifier - I2C Control AGC - TPA2016 will be change by Sparkfun Mono Audio Amp Breakout - TPA2005D1 to   avoid interference  (not develop yet *)|I2C|
|VCNL4000 Proximity/Light sensor|I2C|
|BMP180 Barometric Pressure/Temperature/Altitude Sensor|I2C|
|HTU21D Humidity Sensor Breakout|I2C|
|HMC5883L Digital Compass Module Triple Axis Magnetoresistive Sensor Module|I2C|
|Ultrasonic Ranging Detector Mod HC-SR04 Distance Sensor|GPIO|
|hc-sr501 PIR|GPIO|
|Adafruit 16-Channel 12-bit PWM/Servo Shield - I2C interface|I2C|
|EM406 - GPS receiver|UART (not develop yet *)|
|Arduino Due (Dc Motors and Wii Remote Control)|I2C|


I plan to work on this in the next days
More information see: https://sites.google.com/site/jrobotpi/hardware

All sensors send data to Xively site and in this site I have defined a trigger for PIR_Motion device that listener to changes and send notifications to my Iphone using Prowl for that.
https://xively.com/feeds/918735601


###Due_I2C.ino

Arduino DUE program for proxy to I2C (address 0x04)

It implements two callbacks functions
	
    OnRequest: Wii Remote Control and NunChuk bluetooth interface to I2C bus.
    OnReceive: Dc Motors interface to I2C bus.
	
For more info see 
	
    Block Diagram: https://sites.google.com/site/jrobotpi/hardware
    In the section Wii Remote Control Commands you can see all mapped buttons to commands.
	
    Schematics: https://sites.google.com/site/jrobotpi/hardware/schematics
