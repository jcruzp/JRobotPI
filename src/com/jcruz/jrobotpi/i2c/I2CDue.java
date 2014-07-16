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
package com.jcruz.jrobotpi.i2c;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;
import static jdk.dio.i2cbus.I2CDeviceConfig.ADDR_SIZE_7;

/**
 * Define the device interface to Arduino Due to use with wii remote control and
 * DC motors controls
 *
 * @author Jose Cuz
 */
public class I2CDue {

    private final int i2cdueAddress = 0x04;  //I2C address from Arduino Due
    private I2CDeviceConfig config;

    /**
     * Save device pointer to Arduino Due I2C address
     */
    public static I2CDevice arduino = null;

    /**
     * Define the connection to Arduino Due
     * 
     */
    //TODO Change to Singleton Object Pattern 
    public I2CDue() {
        if (arduino == null) {
            config = new I2CDeviceConfig(1,
                    i2cdueAddress,
                    ADDR_SIZE_7,
                    100000);

            try {
                arduino = (I2CDevice) DeviceManager.open(I2CDevice.class, config);
            } catch (IOException ex) {
                Logger.getGlobal().log(Level.WARNING,ex.getMessage());
            }

        }
    }

    /**
     * Free connection to Arduino Due
     *
     */
    public void close() {
        try {
            arduino.close();
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getMessage());
        }
    }
}
