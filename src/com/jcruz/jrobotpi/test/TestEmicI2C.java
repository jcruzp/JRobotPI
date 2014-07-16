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

import com.jcruz.jrobotpi.i2c.driver.EMICI2CDevice;
import com.jcruz.jrobotpi.log.LoggingHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author jcruz
 */
public class TestEmicI2C extends MIDlet {

    private LoggingHandler loggerHandler = LoggingHandler.getInstance();
    
    public final String[] emic2Msgs = {
        "S Emic 2 Ok.", //0
        "S Inicializing devices.", //1
        "S HCSR04 Ok.", //2
        "S BMP180 Ok.", //3
        "S HTU21D Ok.", //4
        "S Wii Remote Ok.", //5
        "S DC Motors Ok.", //6
        "S Servo Ok.", //7
        "S VCNL4000 Ok.", //8
        "S Xively Ok.", //9    
        "S PIR and your listener Ok.", //10
        "S Task to read devices created.",//11
        "S Close devices comunication.", //12
        "S Menu activated.", //13
        "S Menu deactivated.", //14
        "S Prepare to move.", //15
        "S Stop move.", //16
        "S Prepare to detect objects.", //17
        "S Stop searching objects.", //18
        "S Scanning.", //19
        "S Object detected at ", //20
        "S No Object detected.", //21
        "S PIR Activated", //22
        "S PIR Deactivated", //23
        "S HMC5883L Ok." //24   
    };

    @Override
    public void startApp() {
        loggerHandler.start();
        Logger.getGlobal().setLevel(Level.ALL);

        Logger.getGlobal().log(Level.INFO, "************************************");
        try {
            EMICI2CDevice sc = new EMICI2CDevice();
            
        sc.writeCommand("W200");
        sc.writeCommand("L0");
        sc.writeCommand("N0");
            sc.Msg(1);
            sc.Msg(2);
            sc.Msg(3);
            sc.Msg(4);
            
            sc.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void destroyApp(boolean unconditional) {
    }
}
