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

/**
 * Interface to EMIC-2 sound synthetizer
 *
 * @author jcruz
 */
public class EMICI2CDevice {

    private SC16IS750Device sc;
    private String[] emic2Msgs=null;

    /**
     * Define a UART device config to interface to Emic-2
     *
     * @param emic2Msgs
     * @throws IOException
     */
    public EMICI2CDevice(String[] emic2Msgs) throws IOException {
        //I2C Emic interface using SC16IS750
        sc = new SC16IS750Device();
        this.emic2Msgs=emic2Msgs;
    }
    
    /**
     *
     * @param msgnum
     */
    public void Msg(int msgnum) {
        write(emic2Msgs[msgnum]);
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
        cad = cad.concat("\r\n");
        sc.write(cad);
        // Wait for response from Emic-2. It respond to all commands with :
        waitResponse(2);
    }
    
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
     * @param car
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
