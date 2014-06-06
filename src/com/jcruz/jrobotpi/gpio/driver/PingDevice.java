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

import java.io.IOException;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import static jdk.dio.gpio.GPIOPin.OUTPUT;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 *
 * Code based in book Raspberry Pi Cookbook for Python Programmers Copyright © 2014 Packt Publishing
 * Author Tim Cox (thanks for excellent book)
 * I converted sonyc.py python script at chapter 9 to Java
 * @author JCruz
 */
public class PingDevice {

    private final int SIGNAL = 18;         // Trigger GPIO18 pin 
    private final int PULSE = 5000;        // #5us pulse 5.000 ns
    private final int SPEEDOFSOUND = 34029; // Speed Sound 34029 cm/s

    private GPIOPin sigOut = null;
    private GPIOPin sigIn = null;

    /**
     *
     * @throws IOException
     */
    public PingDevice() throws IOException {

    }

    //Send a pulse to HCSR04 and compute the echo to obtain distance
    /**
     *
     * @return @throws IOException
     * @throws InterruptedException
     */
    public double pulse() throws IOException, InterruptedException {
        // define device for trigger pin at HCSR04
        sigOut = (GPIOPin) DeviceManager.open(SIGNAL);
        sigOut.setDirection(OUTPUT);

        sigOut.setValue(false);         //Send a pulse trigger must be 1 and 0 with a 5 us wait
        Thread.sleep(500);         // wait 5 us
        sigOut.setValue(true);
        //    Thread.sleep(0, 1000);         // wait 1 us
        //    sigOut.setValue(false); 
        sigOut.close();
        sigIn = (GPIOPin) DeviceManager.open(new GPIOPinConfig(
                0, SIGNAL, GPIOPinConfig.DIR_INPUT_ONLY, GPIOPinConfig.MODE_INPUT_PULL_UP,
                GPIOPinConfig.TRIGGER_NONE, false));

        long starttime = System.nanoTime(); //ns
        long stop = starttime;
        long start = starttime;

        //echo will go 0 to 1 and I need save time for that. 2 seconds difference
//        while ((!sigIn.getValue()) && (start < starttime + 1000000L  )) {
//            start = System.nanoTime();
//        }
//        while ((sigIn.getValue()) && (stop < starttime + 1000000L  )) {
//            stop = System.nanoTime();
//        }
        //read data or after 2 seconds exit
        //while ((!sigIn.getValue()) && (stop < starttime + 1000000000L  ))
        while (!sigIn.getValue());
        //while (sigIn.getValue())
        stop = System.nanoTime();

        long delta = (stop - start);
        double distance = delta / 1000.0 / 58.0;       // echo from 0 to 1 depending object distance 
        //sigOut.close();
        sigIn.close();
        return distance; // cm/s
    }

    //free device GPIO
    /**
     *
     * @throws IOException
     */
    public void close() throws IOException {
        sigIn.close();
        sigOut.close();
    }

    class MyPinListener implements PinListener {

        @Override
        public void valueChanged(PinEvent event) {
            System.out.println("Pin listener for pin 11 has been called!");
            System.out.println("Pin is now " + event.getValue());
        }
    }

}
