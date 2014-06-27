/*
 * The MIT License
 *
 * Copyright 2014 jcruz.
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

import com.jcruz.jrobotpi.gpio.driver.DFR0076Device;
import com.jcruz.jrobotpi.i2c.I2CUtils;
import javax.microedition.midlet.MIDlet;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 *
 * @author jcruz
 */
public class TestFlame extends MIDlet{
 
    DFR0076Device flame;
    
    public void startApp() {
        //Inicialize Flame Sensor
         flame = new DFR0076Device(22);
        flame.setListener(new FlameSensor());
        
    }
    
    
    public void destroyApp(boolean unconditional) {
        flame.close();
    }
    
    private static int waitnext=1;
    
     //Check Flame Sensor and update Xively
    class FlameSensor implements PinListener {
        
        @Override
        public void valueChanged(PinEvent event) {
                          
                if (event.getValue() && --waitnext==0) {
                   System.out.println("Llama...");
                   waitnext=10;
                 }

            }
        }

    
}
