/*
 * 
 */
package com.jcruz.jrobotpi.gpio.driver;

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
     * @throws IOException
     */
    public PIRDevice(int pirGPIO) throws IOException {
        pir = (GPIOPin) DeviceManager.open(new GPIOPinConfig(
                0, pirGPIO, GPIOPinConfig.DIR_INPUT_ONLY, GPIOPinConfig.MODE_INPUT_PULL_DOWN,
                GPIOPinConfig.TRIGGER_RISING_EDGE, false));

        try {
            Thread.sleep(3000);  //wait for 3 seconds
            //System.out.println("PIR is ok...");
        } catch (InterruptedException ex) {
            Logger.getLogger(PIRDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Defined listener to pir GPIO pin. Pin change value for some time depends
     * config by PIR
     *
     * @param pirListener
     * @throws IOException
     */
    public void setListener(PinListener pirListener) throws IOException {
        pir.setInputListener(pirListener);
    }

    /**
     * Free PIR GPIO
     *
     * @throws IOException
     */
    public void close() throws IOException {
        pir.close();
    }
}
