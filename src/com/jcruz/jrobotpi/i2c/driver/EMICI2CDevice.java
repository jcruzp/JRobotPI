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
package com.jcruz.jrobotpi.i2c.driver;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface to EMIC-2 sound synthetizer
 *
 * @author jcruz
 */
public class EMICI2CDevice {

    private SC16IS750Device sc;
       
    /**
     * Define all messages for Emic
     */
    private final String[] emic2Msgs = {
        "Emic 2 Ok.", //0
        "Inicializing devices.", //1
        "HCSR04 Ok.", //2
        "BMP180 Ok.", //3
        "HTU21D Ok.", //4
        "Wii Remote Ok.", //5
        "DC Motors Ok.", //6
        "Servo Ok.", //7
        "VCNL4000 Ok.", //8
        "Xively Ok.", //9    
        "PIR left and it listener Ok.", //10
        "Task to read devices created.",//11
        "Close devices comunication.", //12
        "Menu activated.", //13
        "Menu deactivated.", //14
        "Prepare to move.", //15
        "Stop move.", //16
        "Prepare to detect objects.", //17
        "Stop searching objects.", //18
        "Scanning.", //19
        "Object detected at ", //20
        "No Object detected.", //21
        "PIR Activated", //22
        "PIR Deactivated.", //23
        "HMC5883L Ok.", //24  
        "GPS Ok.", //25  
        "REST Server Ok.", //26    
        "PIR right and it listener Ok.", //27        
        "Flame sensor Ok.", //28            
        "Prepare to search flame.", //29
        "Stop searching flame.", //30
        "Alert Flame detected." //31
    };

    /**
     * Define a UART device config to interface to Emic-2
     *
     * @param emic2Msgs
     * @throws IOException
     */
    public EMICI2CDevice() throws IOException {
        //I2C Emic interface using SC16IS750
        sc = new SC16IS750Device();
    }
    
    /**
     *
     * @param msgnum
     */
    public void Msg(int msgnum) {
        write(emic2Msgs[msgnum]);
        Logger.getGlobal().log(Level.FINE,emic2Msgs[msgnum]);
    }
    
    /**
     *
     * @param num
     * @return
     */
    public String getMsg(int num) {
        return emic2Msgs[num];
    }

    /**
     *
     * @param cad String to send to Emic-2
     */
    public void write(String cad) {
        cad = "S " + cad.concat("\r\n");
        sc.write(cad);
        // Wait for response from Emic-2. It respond to all commands with :
        waitResponse(2);
    }
    
    /**
     *
     * @param cad
     */
    public void writeCommand(String cad) {
        cad = cad.concat("\r\n");
        sc.write(cad);
        // Wait for response from Emic-2. It respond to all commands with :
        waitResponse(5);
    }

    /**
     *
     * Wait for response from UART. It respond to all commands with car
     *
     * @param nrobytes
     */
    public void waitResponse(int nrobytes) {
        int work = 0;
        //wait two chars :\n
        while (work!=nrobytes) {
            work = sc.bytesToRead();
        }
        for(;0<work;work--)
        //while (0 < work) {
           //read it from input buffer  
           sc.read();
        //   work--;
        //}
    }
    
    /**
     * Free Uart resource
     *
     */
    public void close() {
        sc.close();
    }

}
