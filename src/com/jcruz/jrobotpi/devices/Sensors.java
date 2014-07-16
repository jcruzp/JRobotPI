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

package com.jcruz.jrobotpi.devices;

import com.jcruz.jrobotpi.i2c.driver.BMP180Mode;

/**
 * Enum used to read all sensors data
 */
public enum Sensors {
    /**
     * Read Ambient Light
     */
    AmbientLight("Ambient Light ", "Ambient_Light") {
        public String getValue() {
            return String.valueOf(Devices.vcnl4000.readAmbientLight());
        }
    }, /**
     * Read Humidity
     */ Humidity("Humidity ", "Humidity") {
        public String getValue() {
            return String.valueOf((int) Devices.htu21d.readHumidity());
        }
    }, /**
     * Read RPI_Temperature
     */ RPI_Temperature("Raspberry PI Temperature ", "RPI_Temperature") {
        public String getValue() {
            return String.valueOf((int) Devices.htu21d.readTemperature());
        }
    }, /**
     * Read Temperature
     */ Temperature("Temperature ", "Temperature") {
        public String getValue() {
            return String.valueOf((int) Devices.bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION)[0]);
        }
    }, /**
     * Read Pressure
     */ Pressure("Pressure ", "Pressure") {
        public String getValue() {
            return String.valueOf((int) Devices.bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION)[1]);
        }
    }, /**
     * Read Heading
     */ Heading("Heading ", "Heading") {
        public String getValue() {
            return String.valueOf((int) Devices.hmc.calculateHeading());
        }
    }, /**
     * Read Latitude
     */ Latitude("Latitude ", "Latitude") {
        public String getValue() {
            return Devices.gps.getLatitude();
        }
    }, /**
     * Read Longitude
     */ Longitude("Longitude ", "Longitude") {
        public String getValue() {
            return Devices.gps.getLongitude();
        }
    }, /**
     * Read Altitude
     */ Altitude("Altitude ", "Altitude") {
        public String getValue() {
            return Devices.gps.getAltitude();
        }
    }, /**
     * Stop REST command
     */ Stop("Stop", "") {
        public String getValue() {
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
    public abstract String getValue();

    /**
     * Get name, xively name and value from enum
     */
    private Sensors(String name, String xivelyName) {
        this.name = name;
        this.xivelyName = xivelyName;
    }
    
}
