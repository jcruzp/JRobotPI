/* 
 * The MIT License
 *
 * Copyright 2014 Jose Cruz <joseacruzp@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to u se, copy, modify, merge, publish, distribute, sublicense, and/or sell
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
package com.jcruz.jrobotpi.i2c;

import jdk.dio.i2cbus.I2CDevice;

/**
 *
 * @author jcruz
 */
public enum SC16IS750 {
   
    /**
     *
     */
    XHR(0x00),

    /**
     *
     */
    FCR(0x02),

    /**
     *
     */
    LCR(0x03),

    /**
     *
     */
    MCR(0x04),
    
    /**
     *
     */
    LSR(0x05),
    
    /**
     *
     */
    MSR(0x06),
    
    /**
     *
     */
    TXLVL(0x08),
    
    /**
     *
     */
    RXLVL(0x09),
    
    /**
     *
     */
    DLL(0x00),
    
    /**
     *
     */
    DLH(0x01);
    
    /**
     *
     */
    public int cmd;

    private SC16IS750(int cmd) {
        // SC16IS740 expects a R/W  bit first (only for spi interface), 
        //followed by the 4 bit register address of the byte.
        // So shift the bits left by three bits:
        this.cmd = cmd << 3;
    }
    /**
     *
     * @param device
     * @return
     */
    public int read(I2CDevice device) {
        return I2CUtils.read(device, this.cmd);
    }

    /**
     *
     * @param device
     * @param value
     */
    public void write(I2CDevice device, byte value) {
        I2CUtils.write(device, (byte) this.cmd, value);
    }
}
