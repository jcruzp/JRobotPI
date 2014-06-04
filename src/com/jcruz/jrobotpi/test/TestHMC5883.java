/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
            Logger.getLogger(TestHMC5883.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void destroyApp(boolean unconditional) {
    }
}
