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
import jdk.dio.DeviceConfig;
import jdk.dio.DeviceManager;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

/**
 * Base definitions for create a device and its config
 *
 * @author Jose Cuz
 */
public class I2CRpi {

    private I2CDeviceConfig config;

    /**
     * Save device address establishing
     */
    public I2CDevice device = null;

    /**
     * Define device and config it
     *
     * @param i2cAddress
     * @throws IOException
     */
    public I2CRpi(int i2cAddress) throws IOException {
        config = new I2CDeviceConfig(DeviceConfig.DEFAULT,
                i2cAddress,
                DeviceConfig.DEFAULT,
                DeviceConfig.DEFAULT);

        device = (I2CDevice) DeviceManager.open(I2CDevice.class, config);
    }

    /**
     * free device resource
     *
     */
    public void close() {
        try {
            device.close();
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        }
    }
}
