/* 
 * The MIT License
 *
 * Copyright 2014 Jose Cruz <joseacruzp@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.jcruz.jrobotpi;

import com.jcruz.jrobotpi.gpio.driver.HCSR04Device;
import com.jcruz.jrobotpi.gpio.driver.GPIODevice;
import com.jcruz.jrobotpi.http.driver.XivelyDevice;
import com.jcruz.jrobotpi.i2c.I2CUtils;
import com.jcruz.jrobotpi.i2c.Wii;
import com.jcruz.jrobotpi.i2c.driver.BMP180Device;
import com.jcruz.jrobotpi.i2c.driver.BMP180Mode;
import com.jcruz.jrobotpi.i2c.driver.EMICI2CDevice;
import com.jcruz.jrobotpi.i2c.driver.HMC5883LDevice;
import com.jcruz.jrobotpi.i2c.driver.HTU21DDevice;
import com.jcruz.jrobotpi.i2c.driver.Move;
import com.jcruz.jrobotpi.i2c.driver.PCA9685Device;
//import com.jcruz.jrobotpi.i2c.driver.TPA2016Device;
import com.jcruz.jrobotpi.i2c.driver.VCNL4000Device;
import com.jcruz.jrobotpi.i2c.driver.WiiRemote;
import com.jcruz.jrobotpi.uart.driver.GPSEM406Device;
import java.io.IOException;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;
import jdk.dio.i2cbus.I2CDevice;

/**
 *
 * @author eve0002474
 */
public class Devices {

    public HCSR04Device hcsr04 = null;
    private final int trigger = 23;
    private final int echo = 17;

    public GPIODevice pirl = null;
    private final int pinPirl = 25;
    public GPIODevice pirr = null;
    private final int pinPirr = 24;
    private boolean pirActivate = true;
    
    public GPIODevice flame = null;
    private final int flamepin = 22;

    public XivelyDevice xively = null;
    public static BMP180Device bmp180 = null;
    public static HTU21DDevice htu21d = null;
    public WiiRemote wiiremote = null;
    public Move move = null;
    public PCA9685Device servo = null;
    public static VCNL4000Device vcnl4000 = null;
    public EMICI2CDevice emic2 = null;
    // public TPA2016Device tpa = null;
    public static HMC5883LDevice hmc = null;
    public static GPSEM406Device gps = null;
    

    public final String[] emic2Msgs = {
        "S Emic 2 Ok.", //0
        "S Inicializing devices.", //1
        "S HCSR04 Ok.", //2
        "S BMP180 Ok.", //3
        "S HTU21D Ok.", //4
        "S Wii Remote Ok.", //5
        "S DC Motors Ok.", //6
        "S Servo Ok.", //7
        "S VCNL4000 Ok.", //8
        "S Xively Ok.", //9    
        "S PIR left and it listener Ok.", //10
        "S Task to read devices created.",//11
        "S Close devices comunication.", //12
        "S Menu activated.", //13
        "S Menu deactivated.", //14
        "S Prepare to move.", //15
        "S Stop move.", //16
        "S Prepare to detect objects.", //17
        "S Stop searching objects.", //18
        "S Scanning.", //19
        "S Object detected at ", //20
        "S No Object detected.", //21
        "S PIR Activated", //22
        "S PIR Deactivated.", //23
        "S HMC5883L Ok.", //24  
        "S GPS Ok.", //25  
        "S REST Server Ok.", //26    
        "S PIR right and it listener Ok.", //27        
        "S Flame sensor Ok.", //28            
        "S Prepare to search flame.", //29
        "S Stop searching flame.", //30
        "S Alert Flame detected." //31
    };
    
    /**
     * Enum used to read all sensors data
     */
    public enum Sensors {

        /**
         * Read Ambient Light
         */
        AmbientLight {
                    String getValue() {
                        return String.valueOf(vcnl4000.readAmbientLight());
                    }
                    
                    String getName(){
                        return ("Ambient_Light");
                    }
                },
        /**
         * Read Humidity
         */
        Humidity {
                    String getValue() {
                        return String.valueOf((int) htu21d.readHumidity());
                    }
                    
                     String getName(){
                        return ("Humidity");
                    }
                },
        
        /**
         * Read RPI_Temperature
         */
        RPI_Temperature {
                    String getValue() {
                        return String.valueOf((int) htu21d.readTemperature());
                    }
                    
                    String getName(){
                        return ("RPI_Temperature");
                    }
                },
        
        /**
         * Read Temperature
         */
        Temperature {
                    String getValue() {
                        return String.valueOf((int) bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION)[0]);
                    }
                    String getName(){
                        return ("Temperature");
                    }
                },
        
        /**
         * Read Pressure
         */
        Pressure {
                    String getValue() {
                        return String.valueOf((int) bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION)[1]);
                    }
                    String getName(){
                        return ("Pressure");
                    }
                },
        
        /**
         * Read Heading
         */
        Heading {
                    String getValue() {
                        return String.valueOf((int) hmc.calculateHeading());
                    }
                    String getName(){
                        return ("Heading");
                    }
                },
        /**
         * Read Latitude
         */
        Latitude {
                    String getValue() {
                        return gps.getLatitude();
                    }
                    String getName(){
                        return ("Latitude");
                    }
                },
        /**
         * Read Longitude
         */
        Longitude {
                    String getValue() {
                        return gps.getLongitude();
                    }
                    String getName(){
                        return ("Longitude");
                    }
                },
        
        /**
         * Read Altitude
         */
        Altitude {
                    String getValue() {
                        return gps.getAltitude();
                    }
                    String getName(){
                        return ("Altitude");
                    }
                },
        
        Stop {
            
                    String getValue() {
                        return "";
                    }
                    String getName(){
                        return ("Stop");
                    }
        
        };

        abstract String getValue();
        abstract String getName();
    };
    

    /**
     *
     * @throws IOException
     */
    public Devices() throws IOException {
        emic2 = new EMICI2CDevice(emic2Msgs);
        I2CUtils.I2Cdelay(3000);
        
        //emic2.write("");
        emic2.writeCommand("W200");
        emic2.writeCommand("L0");
        emic2.writeCommand("N0");

        emic2.Msg(0);
        emic2.Msg(1);
        hcsr04 = new HCSR04Device(trigger, echo);
        emic2.Msg(2);

        bmp180 = new BMP180Device();
        emic2.Msg(3);

        htu21d = new HTU21DDevice();
        emic2.Msg(4);

        wiiremote = new WiiRemote();
        emic2.write(emic2Msgs[5]);

        move = new Move();
        emic2.write(emic2Msgs[6]);
        
        servo = new PCA9685Device();
        servo.setPWMFreq(60);
        emic2.Msg(7);

        vcnl4000 = new VCNL4000Device();
        emic2.Msg(8);

        xively = new XivelyDevice();
        emic2.Msg(9);

        hmc = new HMC5883LDevice();
        hmc.SetScale(1.3F);
        hmc.SetMeasurementMode(HMC5883LDevice.Measurement.Continuous);
        emic2.Msg(24);

        //Inicialize PIR Left motion detect
        pirl = new GPIODevice(pinPirl);
        pirl.setListener(new MyPinListener());
        emic2.Msg(10);
        
        //Inicialize PIR Right motion detect
        pirr = new GPIODevice(pinPirr);
        pirr.setListener(new MyPinListener());
        emic2.Msg(27);
        
        //Inicialize Flame Sensor
        flame = new GPIODevice(flamepin);
        flame.setListener(new FlameSensor());
        emic2.Msg(28);
        
        
        gps=new GPSEM406Device();
        emic2.Msg(25);
        

//            //Inicialize audio amp
//            tpa = new TPA2016Device();
//            //Only use one channel
//            tpa.enableChannel(false, true);
//            //Set audio volume to max value
//            tpa.setGain((byte) 30);
//            tpa.close();
    }

    /**
     *
     * @param pirActivate
     */
    public void setPirActivate(boolean pirActivate) {
        this.pirActivate = pirActivate;
    }

    /**
     *
     * @return
     */
    public boolean isPirActivate() {
        return pirActivate;
    }

    /**
     *
     */
    public void Close() {
        emic2.Msg(12);
        pirl.removeListener();
        pirl.close();
        pirr.removeListener();
        pirr.close();
        flame.removeListener();
        flame.close();
        hmc.close();
        vcnl4000.close();
        servo.close();
        move.close();
        wiiremote.close();
        htu21d.close();
        bmp180.close();
        hcsr04.close();
        gps.close();
        emic2.close();
    }

    //Check PIR Sensor for motion detect
    class MyPinListener implements PinListener {

        @Override
        public void valueChanged(PinEvent event) {
            if (pirActivate) {
                xively.updateValue("PIR_Sensor", event.getValue() ? "1" : "0");
                if (event.getValue()) {
                    xively.updateValue("PIR_Sensor", "0");
                }

            }
        }

    }
    
    //Check Flame Sensor and update Xively
    class FlameSensor implements PinListener {

        @Override
        public void valueChanged(PinEvent event) {
            //if (pirActivate) {
                xively.updateValue("Flame_Sensor", event.getValue() ? "1" : "0");
                if (event.getValue()) {
                    xively.updateValue("Flame_Sensor", "0");
                }

            //}
        }

    }

}
