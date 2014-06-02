/*
 * Code based in book Raspberry Pi Cookbook for Python Programmers Copyright © 2014 Packt Publishing
 * Author Tim Cox (thanks for excellent book)
 * I converted sonyc.py python script at chapter 9 to Java
 */
package com.jcruz.jrobotpi.gpio.driver;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 * Interface to Ultrasound HC-SR04 device
 *
 * @author JCruz
 */
public class HCSR04Device {

    private final int PULSE = 10000;        // #10us pulse 10.000 ns
    private final int SPEEDOFSOUND = 34029; // Speed Sound 34029 cm/s

    private GPIOPin trigger = null;
    private GPIOPin echo = null;

    /**
     * Inicialize GPIO to echo and trigger pins
     *
     * @param _trigger
     * @param _echo
     * @throws java.io.IOException
     */
    public HCSR04Device(int _trigger, int _echo) throws IOException {
        // define device for trigger pin at HCSR04
        trigger = (GPIOPin) DeviceManager.open(new GPIOPinConfig(
                0, _trigger, GPIOPinConfig.DIR_OUTPUT_ONLY, GPIOPinConfig.MODE_OUTPUT_PUSH_PULL,
                GPIOPinConfig.TRIGGER_NONE, false));
        // define device for echo pin at HCSR04
        echo = (GPIOPin) DeviceManager.open(new GPIOPinConfig(
                0, _echo, GPIOPinConfig.DIR_INPUT_ONLY, GPIOPinConfig.MODE_INPUT_PULL_UP,
                GPIOPinConfig.TRIGGER_NONE, false));

        try {
            Thread.sleep(500);  //wait for 0.5 seconds
        } catch (InterruptedException ex) {
            Logger.getLogger(HCSR04Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Send a pulse to HCSR04 and compute the echo to obtain distance
     *
     * @return distance in cm/s
     * @throws java.io.IOException
     */
    public double pulse() throws IOException {
        trigger.setValue(true);         //Send a pulse trigger must be 1 and 0 with a 10 us wait
        try {
            Thread.sleep(0, PULSE);         // wait 10 us
        } catch (InterruptedException ex) {
        }
        trigger.setValue(false);
        long starttime = System.nanoTime(); //ns
        long stop = starttime;
        long start = starttime;
        //echo will go 0 to 1 and I need save time for that. 2 seconds difference
        while ((!echo.getValue()) && (start < starttime + 1000000000L * 2)) {
            start = System.nanoTime();
        }
        while ((echo.getValue()) && (stop < starttime + 1000000000L * 2)) {
            stop = System.nanoTime();
        }
        long delta = (stop - start);
        long distance = delta * SPEEDOFSOUND;       // echo from 0 to 1 depending object distance 
        return distance / 2.0 / (1000000000L); // cm/s
    }

    /**
     * free device GPIO
     *
     * @throws IOException
     */
    public void close() throws IOException {
        trigger.close();
        echo.close();;
    }
}
