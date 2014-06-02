#include <SPI.h>
/*******************************************************************************************************************
 Due_I2C is a slave to I2C0 (address 0x04) and Master to I2C1. v1.2.3

 It implements two callbacks functions:
    OnRequest: Wii Remote Plus and NunChuk bluetooth interface to I2C0 bus.
    OnReceive: Dc Motors interface to I2C1 bus.

 See Due-I2C command Map at https://docs.google.com/file/d/0B9Bs86zcl8kSdXJfUEwzT284alU/preview and
 Programs Description at https://docs.google.com/file/d/0B9Bs86zcl8kSdDFjbkQ5N2NKcUk/preview for more information.

 I am using libraries from Kristian Lauszus(c) and Adafruit(c) see copyright above
 *******************************************************************************************************************/

// Wii Library is //
/* Copyright (C) 2012 Kristian Lauszus, TKJ Electronics. All rights reserved.

This software may be distributed and modified under the terms of the GNU
General Public License version 2 (GPL2) as published by the Free Software
Foundation and appearing in the file GPL2.TXT included in the packaging of
this file. Please note that GPL2 Section 2[b] requires that all works based
on this software must also be made publicly available under the terms of
the GPL2 ("Copyleft").

Contact information
-------------------

Kristian Lauszus, TKJ Electronics
Web      :  http://www.tkjelectronics.com
e-mail   :  kristianl@tkjelectronics.com
*/

// Adafruit Motor Shield Library and PWM Servo Driver is
/******************************************************************
This is the library for the Adafruit Motor Shield V2 for Arduino.
It supports DC motors & Stepper motors with microstepping as well
as stacking-support. It is *not* compatible with the V1 library!

It will only work with https://www.adafruit.com/products/1483

Adafruit invests time and resources providing this open
source code, please support Adafruit and open-source hardware
by purchasing products from Adafruit!

Written by Limor Fried/Ladyada for Adafruit Industries.
BSD license, check license.txt for more information.
All text above must be included in any redistribution.
******************************************************************/

#include <Wii.h>
#include <usbhub.h>
#ifdef dobogusinclude
#include <spi4teensy3.h>
#endif

#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include <utility/Adafruit_PWMServoDriver.h>

USB Usb;
//USBHub Hub1(&Usb); // Some dongles have a hub inside

BTD Btd(&Usb); // You have to create the Bluetooth Dongle instance like so
/* You can create the instance of the class in two ways */
WII Wii(&Btd, PAIR); // This will start an inquiry and then pair with your Wiimote - you only have to do this once
//WII Wii(&Btd); // After that you can simply create the instance like so and then press any button on the Wiimote


//**** DC Motors Controller Definitions *****************************
// Create the motor shield object with the default I2C address 0x60
Adafruit_MotorShield AFMS = Adafruit_MotorShield();

// Define all motors (4WD in my case)
Adafruit_DCMotor *motorfl = AFMS.getMotor(1); // M1 Motor front left
Adafruit_DCMotor *motorfr = AFMS.getMotor(2); // M2 Motor front right
Adafruit_DCMotor *motorbl = AFMS.getMotor(3); // M1 Motor back left
Adafruit_DCMotor *motorbr = AFMS.getMotor(4); // M2 Motor back right
//********************************************************************

//#define DEBUG_I2C
//#define DEBUG_CMD

//Due-I2C i2c address
#define I2C_ADDR 0x04

//Max number of arguments
#define I2C_MSG_ARGS_MAX 1

//Number of commands to Wii Motion Plus and NunChuk bluetooth interface to I2C bus.
#define I2C_NUM_COMMANDS 32

//All supported commands to I2C comunication RPI - Due
//Read Buttons states
#define I2C_CMD_READ_BUTTONS1   	0x01
#define I2C_CMD_READ_BUTTONS2   	0x02

//Read Wii Motion Battery status
#define I2C_CMD_READ_BATTERY    	0x03

//Read X and Y from NunChuk Joystick
#define I2C_CMD_READ_JOYX       	0x05
#define I2C_CMD_READ_JOYY       	0x06

//Read Wii Remote Accelerometer
#define I2C_CMD_READ_ACCEL_WRX   	0x0A
//#define I2C_CMD_READ_ACCEL_WRXL 	0x0B
#define I2C_CMD_READ_ACCEL_WRY   	0x0C
//#define I2C_CMD_READ_ACCEL_WRYL 	0x0D
#define I2C_CMD_READ_ACCEL_WRZ   	0x0E
//#define I2C_CMD_READ_ACCEL_WRZL 	0x0F

//Read NunChuk Accelerometer
#define I2C_CMD_READ_ACCEL_NCX  	0x10
//#define I2C_CMD_READ_ACCEL_NCXL 	0x11
#define I2C_CMD_READ_ACCEL_NCY  	0x12
//#define I2C_CMD_READ_ACCEL_NCYL 	0x13
#define I2C_CMD_READ_ACCEL_NCZ  	0x14
//#define I2C_CMD_READ_ACCEL_NCZL 	0x15

//Read Wii Motion Plus Gyroscope
#define I2C_CMD_READ_GYRO_PITCH 	0x16
//#define I2C_CMD_READ_GYRO_PITCHL 	0x17
#define I2C_CMD_READ_GYRO_ROLL   	0x18
//#define I2C_CMD_READ_GYRO_ROLLL 	0x19
#define I2C_CMD_READ_GYRO_YAW       0x1A
//#define I2C_CMD_READ_GYRO_YAWL 	        0x1B

//On or Off Wii Remote Leds and Rumble
#define I2C_CMD_LEDS_RUMBLE 	        0x20

//Start and Stop desired DC Motor
#define I2C_CMD_CONFIG_MOTORS     	0x30

//Set speed to DC Motor
#define I2C_CMD_SET_SPEED     		0x31

// List of all commands that Due-I2C support see Due-I2C command Map doc
uint8_t supportedI2Ccmd[] = {
  I2C_CMD_READ_BUTTONS1,
  I2C_CMD_READ_BUTTONS2,
  I2C_CMD_READ_BATTERY,
  I2C_CMD_READ_JOYX,
  I2C_CMD_READ_JOYY,
  I2C_CMD_READ_ACCEL_WRX,
  I2C_CMD_READ_ACCEL_WRY,
  I2C_CMD_READ_ACCEL_WRZ,
  I2C_CMD_READ_ACCEL_NCX,
  I2C_CMD_READ_ACCEL_NCY,
  I2C_CMD_READ_ACCEL_NCZ,
  I2C_CMD_READ_GYRO_PITCH,
  I2C_CMD_READ_GYRO_ROLL,
  I2C_CMD_READ_GYRO_YAW,
  I2C_CMD_LEDS_RUMBLE,
  I2C_CMD_CONFIG_MOTORS,
  I2C_CMD_SET_SPEED
};

// Save values from byte command that can be request from RPI
uint8_t command[I2C_NUM_COMMANDS];
// Save values from float command that can be request from RPI
union cmdFloat_tag {
  byte b[4];
  float fval;
} cmdFloat;
// Save values from short command that can be request from RPI
union cmdShort_tag {
  byte b[2];
  int16_t sval;
} cmdShort;


// Save speed receive from command 0x31 for use with command 0x30
uint8_t speedMotor = 0;
//If need set speed
boolean setspeed = false;
//What run argument was received
uint8_t setrun = 0;

// Command write to or read from
uint8_t i2cCmd = 255;
// Byte argument from commands
uint8_t i2cArg = 255;

//******************************************************************************
//Initialize Due-I2C like a Slave at 0x04 (I2C0) and Master (I2C1) and SPI Slave
//******************************************************************************
void setup() {
  //Define I2C sda and scl like Slave
  Wire.begin(I2C_ADDR);  // join i2c bus
  Wire.onRequest(requestEvent); // Read command event
  Wire.onReceive(receiveEvent); // Write command event
  Serial.begin(115200); // start serial for output

  Serial.print(F("\r\nAdafruit Motor shield v2 - DC Motor test!"));

  //Define I2C sda1 and scl1 like Master
  AFMS.begin();  // create with the default frequency 1.6KHz
  // Set the speed to start, from 0 (off) to 255 (max speed)
  motorfl->setSpeed(0);
  
  motorfr->setSpeed(0);
  motorbl->setSpeed(0);
  motorbr->setSpeed(0);

  Serial.println(F("\r\nDue-I2C v1.2.2 initialized and waiting for commands"));

  while (!Serial); // Wait for serial port to connect - used on Leonardo, Teensy and other boards with built-in USB CDC serial connection
  if (Usb.Init() == -1) {
    Serial.print(F("\r\nOSC did not start"));
    while (1); //halt
  }
  Serial.print(F("\r\nWii Remote Bluetooth Library Started"));
  fiilArray(); // Fill array with 0;
}



void loop() {
  Usb.Task();  // Read from dongle Bluetooth
  processCommand();  // Process all commands received or requested (received event o request event) by RPI
}

//**********************************
//Initialize command array
//**********************************
void fiilArray() {
  for (int i = 1; i <= I2C_NUM_COMMANDS; i++)
    command[i] = 0;
}

//**********************************
//Check if received command is valid
//**********************************
boolean validCommand(uint8_t cmd) {
  boolean fcnt = false;
  // validating command is supported by slave
  for (int i = 0; i < sizeof(supportedI2Ccmd); i++) {
    if (supportedI2Ccmd[i] == cmd) {
      fcnt = true;
      break;
    }
  }
  return fcnt;
}

//*******************************************
// Executes whenever data is request from RPI
//*******************************************
void requestEvent() {

  switch (i2cCmd) {
    case I2C_CMD_READ_ACCEL_WRX:  //Process read commands that required short return values
    case I2C_CMD_READ_ACCEL_WRY:
    case I2C_CMD_READ_ACCEL_WRZ:
    case I2C_CMD_READ_ACCEL_NCX:
    case I2C_CMD_READ_ACCEL_NCY:
    case I2C_CMD_READ_ACCEL_NCZ:
      Wire.write(cmdShort.b, 2);
      cmdShort.sval=0;
      break;

    case I2C_CMD_READ_GYRO_PITCH:  //Process read commands that required float return values
    case I2C_CMD_READ_GYRO_ROLL:
    case I2C_CMD_READ_GYRO_YAW:
      Wire.write(cmdFloat.b, 4);
      cmdFloat.fval=0;
      break;

    case I2C_CMD_READ_BUTTONS1:
    case I2C_CMD_READ_BUTTONS2:
    case I2C_CMD_READ_BATTERY:
    case I2C_CMD_READ_JOYX:
    case I2C_CMD_READ_JOYY:
      Wire.write(command[i2cCmd]); //Process read commands that required byte return values
      fiilArray(); // Fill array with 0;
      break;
  }
  i2cCmd = 255; //Read  command was processed
 
}

//***************************************************
// Executes whenever data is received from master
// This version only accept one arguments by command
// Or a command without arguments (read command)
//***************************************************
void receiveEvent(int howMany)
{
  i2cCmd = 255;
  i2cArg = 255;
  //Process all received commands
  while (Wire.available()) {
    i2cCmd = Wire.read();   // receive first byte - command assumed
#ifdef DEBUG_I2C
    Serial.print(F("\r\nCommand received: "));
    Serial.print(i2cCmd);
#endif
    //Check if valid command received
    if (validCommand(i2cCmd)) {
      if (Wire.available()) {  // receive rest of tramsmission from master assuming arguments to the command
        i2cArg = Wire.read();  // Array with all arguments received, if have arguments is a write command
#ifdef DEBUG_I2C
        Serial.print(F("\r\nArgument received: "));
        Serial.print(i2cArg);
#endif
      }  else {
#ifdef DEBUG_I2C
        Serial.print(F("\r\nError: Command without arguments"));
#endif
        i2cArg = 255;
        // Command without argument
        return;
      }
      //Command ok process it
    } else {
#ifdef DEBUG_I2C
      Serial.print(F("\r\nError: Command not supported"));
#endif
      i2cCmd = 255;
    }
  }

}

//***************************************
//Process all commands from I2C0 bus RPI
//***************************************
void processCommand() {
  //if Wii Remote was inicialized process all Wii and Motors commands
  if ((Wii.wiimoteConnected) && (i2cCmd != 255)) {
#ifdef DEBUG_CMD
    Serial.print(F("\r\nCommand: "));
    Serial.print(i2cCmd);
    Serial.print(F("\r\nRead value: "));
    Serial.print(command[i2cCmd]);
#endif


    switch (i2cCmd)
    {
      case I2C_CMD_READ_BUTTONS1:
        //Read Wii Remote first group buttons
        command[I2C_CMD_READ_BUTTONS1] =  Wii.getButtonPress(UP)   * 128 +
                                          Wii.getButtonPress(DOWN) * 64 +
                                          Wii.getButtonPress(LEFT) * 32 +
                                          Wii.getButtonPress(RIGHT) * 16 +
                                          Wii.getButtonPress(A)    * 8 +
                                          Wii.getButtonPress(B)    * 4;

        //Read Nunchuk Buttons Z and C
        command[I2C_CMD_READ_BUTTONS1] += Wii.nunchuckConnected ?  (Wii.getButtonPress(Z) * 2 + Wii.getButtonPress(C)) : 0;
        break;

      case I2C_CMD_READ_BUTTONS2:
        //Read Wii Remote second group buttons
        command[I2C_CMD_READ_BUTTONS2] =  Wii.getButtonPress(PLUS) * 16 +
                                          Wii.getButtonPress(HOME) * 8 +
                                          Wii.getButtonPress(MINUS) * 4 +
                                          Wii.getButtonPress(TWO)  * 2 +
                                          Wii.getButtonPress(ONE);
        break;

      case I2C_CMD_READ_BATTERY:
        //Read Battery status
        command[I2C_CMD_READ_BATTERY] = Wii.getBatteryLevel();
        break;

      case I2C_CMD_READ_JOYX:
        //Read Nunchuk Joystick X
        command[I2C_CMD_READ_JOYX] = Wii.nunchuckConnected ? Wii.getAnalogHat(HatX) : 0;
        break;

      case I2C_CMD_READ_JOYY:
        //Read Nunchuk Joystick X
        command[I2C_CMD_READ_JOYY] = Wii.nunchuckConnected ? Wii.getAnalogHat(HatY) : 0;
        break;

        //Read Wii Remote Accelerometer
      case I2C_CMD_READ_ACCEL_WRX:
        cmdShort.sval = Wii.accXwiimote;
        break;

      case I2C_CMD_READ_ACCEL_WRY:
        cmdShort.sval = Wii.accYwiimote;
        break;

      case I2C_CMD_READ_ACCEL_WRZ:
        cmdShort.sval = Wii.accZwiimote;
        break;

        //Read Nunchuk Accelerometer
      case I2C_CMD_READ_ACCEL_NCX:
        cmdShort.sval = Wii.accXnunchuck;
        break;

      case I2C_CMD_READ_ACCEL_NCY:
        cmdShort.sval = Wii.accYnunchuck;
        break;

      case I2C_CMD_READ_ACCEL_NCZ:
        cmdShort.sval = Wii.accZnunchuck;
        break;

        //Read Wii Motion Plus Gyroscope
      case I2C_CMD_READ_GYRO_PITCH:
        cmdFloat.fval = Wii.getPitch();
        break;

      case I2C_CMD_READ_GYRO_ROLL:
        cmdFloat.fval = Wii.getRoll();
        break;

      case I2C_CMD_READ_GYRO_YAW:
        cmdFloat.fval = Wii.motionPlusConnected ? Wii.getYaw() : 0.0;
        break;

        //Command Set Wii Remote Leds and Rumble
      case I2C_CMD_LEDS_RUMBLE:
        if (i2cArg != 255) {
          Wii.setAllOff(); //All Leds and Rumble Off
          if (i2cArg & B00000001) Wii.setLedOn(LED1);
          if (i2cArg & B00000010) Wii.setLedOn(LED2);
          if (i2cArg & B00000100) Wii.setLedOn(LED3);
          if (i2cArg & B00001000) Wii.setLedOn(LED4);
          if (i2cArg & B00010000) Wii.setRumbleOn();
        }
        //avoid repeat write command in the loop
        i2cCmd = 255;
        i2cArg = 255;
        break;

        //Command Activate DC Motors
      case I2C_CMD_CONFIG_MOTORS:
        if (i2cArg != 255) {
          setspeed = false;                             //If need set speed
          setrun = 0;                                   //What run argument was received
          if (i2cArg & B10000000) setspeed = true;      //If setSpeed
          if (i2cArg & B01000000) setrun = BACKWARD;    //If move backward
          if (i2cArg & B00100000) setrun = FORWARD;     //If move forward
          if (i2cArg & B00010000) setrun = RELEASE;     //If release

          if (i2cArg & B00000001) {
            if (setspeed) motorfl->setSpeed(speedMotor);    //Change speed
            if (setrun > 0) motorfl->run(setrun);           //Change move direction or only the speed
          }
          if (i2cArg & B00000010) {
            if (setspeed) motorfr->setSpeed(speedMotor);
            if (setrun > 0) motorfr->run(setrun);
          }
          if (i2cArg & B00000100) {
            if (setspeed) motorbl->setSpeed(speedMotor);
            if (setrun > 0) motorbl->run(setrun);
          }
          if (i2cArg & B00001000) {
            if (setspeed) motorbr->setSpeed(speedMotor);
            if (setrun > 0) motorbr->run(setrun);
          }
          //avoid repeat command in the loop
          i2cCmd = 255;
          i2cArg = 255;
        }
        break;

        //Command Set Motors Speed
      case I2C_CMD_SET_SPEED:
        if (i2cArg != 255) speedMotor = i2cArg;
        //avoid repeat command in the loop
        i2cCmd = 255;
        i2cArg = 255;
        break;
    }
  }
}

//**********************************************************************************************************

