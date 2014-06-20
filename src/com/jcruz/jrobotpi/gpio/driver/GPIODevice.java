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
package com.jcruz.jrobotpi.gpio.driver;

import com.jcruz.jrobotpi.i2c.I2CUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.gpio.PinListener;

/**
 * Controls PIR Sensor
 *
 * @author Jose Cuz
 */
public class GPIODevice {

    private GPIOPin pir = null;

    /**
     * Define GPIO pin to listen for move detected
     *
     * @param pinGPIO
     */
    public GPIODevice(int pinGPIO) {
        try {
            pir = (GPIOPin) DeviceManager.open(new GPIOPinConfig(
                    0, pinGPIO, GPIOPinConfig.DIR_INPUT_ONLY, GPIOPinConfig.MODE_INPUT_PULL_DOWN,
                    GPIOPinConfig.TRIGGER_RISING_EDGE, false));

            I2CUtils.I2Cdelay(3000);    //wait for 3 seconds
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        }
    }

    public GPIOPin getPir() {
        return pir;
    }

    
    /**
     * Defined listener to pir GPIO pin. Pin change value for some time depends
     * config by PIR
     *
     * @param pirListener
     */
    public void setListener(PinListener pirListener) {
        try {
            pir.setInputListener(pirListener);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        }
    }
    
    /**
     * Remove listener for GPIO pin of PIR
     */
    public void removeListener() {
        try {
            pir.setInputListener(null);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        }
    }

    /**
     * Free PIR GPIO
     *
     */
    public void close() {
        try {
            pir.close();
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        }
    }
}
