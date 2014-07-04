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

import com.jcruz.jrobotpi.gpio.driver.DFR0076Device;
import com.jcruz.jrobotpi.gpio.driver.HCSR04Device;
import com.jcruz.jrobotpi.gpio.driver.GPIODevice;
import com.jcruz.jrobotpi.http.driver.XivelyDevice;
import com.jcruz.jrobotpi.i2c.I2CUtils;
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

/**
 *
 * @author eve0002474
 */
public class Devices {

    /**
     * Define HCSR04 Device object
     */
    public HCSR04Device hcsr04 = null;
    private final int trigger = 23;
    private final int echo = 17;

    /**
     * Define PIR Left object
     */
    public GPIODevice pirl = null;
    private final int pinPirl = 25;

    /**
     * Define PIR Right object
     */
    public GPIODevice pirr = null;
    private final int pinPirr = 24;
    private boolean pirActivate = false;

    /**
     * Define DFR0076 Device object
     */
    public DFR0076Device flame = null;
    private final int flamepin = 22;
    private boolean flameActivate = false;

    /**
     * Xively Device object
     */
    public XivelyDevice xively = null;

    /**
     * Define BMP180 Device object
     */
    public static BMP180Device bmp180 = null;

    /**
     * Define HTU21D Device object
     */
    public static HTU21DDevice htu21d = null;

    /**
     * Define WiiRemote object
     */
    public WiiRemote wiiremote = null;

    /**
     * Define Move object
     */
    public Move move = null;

    /**
     * Define PCA9685 Device object
     */
    public PCA9685Device servo = null;

    /**
     * Define VCNL4000 Device object
     */
    public static VCNL4000Device vcnl4000 = null;

    /**
     * Define EMICI2C Device object
     */
    public EMICI2CDevice emic2 = null;
    // public TPA2016Device tpa = null;

    /**
     * Define HMC5883L Device object
     */
    public static HMC5883LDevice hmc = null;

    /**
     * Define GPSEM406 Device object
     */
    public static GPSEM406Device gps = null;

    /**
     * Define all messages for Emic
     */
    public final String[] emic2Msgs = {
        "Emic 2 Ok.", //0
        "Inicializing devices.", //1
        "HCSR04 Ok.", //2
        "BMP180 Ok.", //3
        "HTU21D Ok.", //4
        "Wii Remote Ok.", //5
        "DC Motors Ok.", //6
        "Servo Ok.", //7
        "VCNL4000 Ok.", //8
        "Xively Ok.", //9    
        "PIR left and it listener Ok.", //10
        "Task to read devices created.",//11
        "Close devices comunication.", //12
        "Menu activated.", //13
        "Menu deactivated.", //14
        "Prepare to move.", //15
        "Stop move.", //16
        "Prepare to detect objects.", //17
        "Stop searching objects.", //18
        "Scanning.", //19
        "Object detected at ", //20
        "No Object detected.", //21
        "PIR Activated", //22
        "PIR Deactivated.", //23
        "HMC5883L Ok.", //24  
        "GPS Ok.", //25  
        "REST Server Ok.", //26    
        "PIR right and it listener Ok.", //27        
        "Flame sensor Ok.", //28            
        "Prepare to search flame.", //29
        "Stop searching flame.", //30
        "Alert Flame detected." //31
    };

    /**
     * Enum used to read all sensors data
     */
    public enum Sensors {

        /**
         * Read Ambient Light
         */
        AmbientLight("Ambient Light ", "Ambient_Light") {
                    String getValue() {
                        return String.valueOf(vcnl4000.readAmbientLight());
                    }
                },
        /**
         * Read Humidity
         */
        Humidity("Humidity ", "Humidity") {
                    String getValue() {
                        return String.valueOf((int) htu21d.readHumidity());
                    }
                },
        /**
         * Read RPI_Temperature
         */
        RPI_Temperature("Raspberry PI Temperature ", "RPI_Temperature") {
                    String getValue() {
                        return String.valueOf((int) htu21d.readTemperature());
                    }
                },
        /**
         * Read Temperature
         */
        Temperature("Temperature ", "Temperature") {
                    String getValue() {
                        return String.valueOf((int) bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION)[0]);
                    }
                },
        /**
         * Read Pressure
         */
        Pressure("Pressure ", "Pressure") {
                    String getValue() {
                        return String.valueOf((int) bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION)[1]);
                    }
                },
        /**
         * Read Heading
         */
        Heading("Heading ", "Heading") {
                    String getValue() {
                        return String.valueOf((int) hmc.calculateHeading());
                    }
                },
        /**
         * Read Latitude
         */
        Latitude("Latitude ", "Latitude") {
                    String getValue() {
                        return gps.getLatitude();
                    }
                },
        /**
         * Read Longitude
         */
        Longitude("Longitude ", "Longitude") {
                    String getValue() {
                        return gps.getLongitude();
                    }
                },
        /**
         * Read Altitude
         */
        Altitude("Altitude ", "Altitude") {
                    String getValue() {
                        return gps.getAltitude();
                    }
                },
        /**
         * Stop REST command
         */
        Stop("Stop", "") {
                    String getValue() {
                        return "";
                    }
                };

        /**
         * Name string
         */
        public String name;

        /**
         * Xively and REST name string
         */
        public String xivelyName;

        /**
         * Value from sensor
         */
        abstract String getValue();

        /**
         * Get name, xively name and value from enum
         */
        private Sensors(String name, String xivelyName) {
            this.name = name;
            this.xivelyName = xivelyName;
        }

    };

    /**
     * Create all object devices
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
        emic2.Msg(5);

        move = new Move();
        emic2.Msg(6);

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
//TODO Define PIRDevice with pin parameter and move listeners to that
        //Inicialize PIR Left motion detect
        pirl = new GPIODevice(pinPirl);
        pirl.setListener(new PirSensor());
        emic2.Msg(10);

        //Inicialize PIR Right motion detect
        pirr = new GPIODevice(pinPirr);
        pirr.setListener(new PirSensor());
        emic2.Msg(27);

        //Inicialize Flame Sensor
        flame = new DFR0076Device(flamepin);
        //flame.setListener(new FlameSensor());
        emic2.Msg(28);

        gps = new GPSEM406Device();
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
     * Activate PIR sensors
     *
     * @param pirActivate
     */
    public void setPirActivate(boolean pirActivate) {
        this.pirActivate = pirActivate;
    }

    /**
     * Detect if PIRs are activated
     *
     * @return
     */
    public boolean isPirActivate() {
        return pirActivate;
    }

    /**
     * Detect if Flame sensor is activated
     *
     * @return
     */
    public boolean isFlameActivate() {
        return flameActivate;
    }

    /**
     * Activate flame sensor
     *
     * @param flameActivate
     */
    public void setFlameActivate(boolean flameActivate) {
        this.flameActivate = flameActivate;
    }

    /**
     * Close all object from devices
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
    class PirSensor implements PinListener {

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
            if (flameActivate) {
                //xively.updateValue("Flame_Sensor", event.getValue() ? "1" : "0");

                if (event.getValue()) {
                    emic2.Msg(31);

                    //xively.updateValue("Flame_Sensor", "0");
                }

            }
        }

    }

}
