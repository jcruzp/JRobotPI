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
package com.jcruz.jrobotpi.test;

import com.jcruz.jrobotpi.i2c.HMC5883L;
import com.jcruz.jrobotpi.i2c.I2CUtils;
import com.jcruz.jrobotpi.i2c.driver.HMC5883LDevice;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author jcruz
 */
public class TestHMC5883 extends MIDlet {

    @Override
    public void startApp() {
        try {
            HMC5883LDevice hmc = new HMC5883LDevice();
            hmc.SetScale(1.3F);
            hmc.SetMeasurementMode(HMC5883LDevice.Measurement.Continuous);
            while (true) {
                System.out.println(hmc.calculateHeading());
                I2CUtils.I2Cdelay(2000);
                HMC5883LDevice.MagnetometerRaw values = hmc.ReadRawAxis();
                System.out.print("X:" + values.XAxis);
                System.out.print(" Y:" + values.YAxis);
                System.out.println(" Z:" + values.ZAxis);
            }

        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getMessage());
        }

    }

    @Override
    public void destroyApp(boolean unconditional) {
    }
}
