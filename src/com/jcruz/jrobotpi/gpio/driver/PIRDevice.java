/*
 * 
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
public class PIRDevice {

    private GPIOPin pir = null;

    /**
     * Define GPIO pin to listen for move detected
     *
     * @param pirGPIO
     */
    public PIRDevice(int pirGPIO) {
        try {
            pir = (GPIOPin) DeviceManager.open(new GPIOPinConfig(
                    0, pirGPIO, GPIOPinConfig.DIR_INPUT_ONLY, GPIOPinConfig.MODE_INPUT_PULL_DOWN,
                    GPIOPinConfig.TRIGGER_RISING_EDGE, false));

            I2CUtils.I2Cdelay(3000);    //wait for 3 seconds
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        }
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
