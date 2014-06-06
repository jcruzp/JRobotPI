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
package com.jcruz.jrobotpi.i2c;

import jdk.dio.i2cbus.I2CDevice;

/**
 * Commands to control VCNL4000 proximity sensor
 *
 * @author Jose Cuz
 */
public enum VCNL4000 {

    /**
     *
     */
    COMMAND(0x80),
    /**
     *
     */
    PRODUCTID(0x81),
    /**
     *
     */
    IRLED(0x83),
    /**
     *
     */
    AMBIENTPARAMETER(0x84),
    /**
     *
     */
    AMBIENTDATA(0x85),
    /**
     *
     */
    AMBIENTDATA2(0x86),
    /**
     *
     */
    PROXIMITYDATA(0x87),
    /**
     *
     */
    PROXIMITYDATA2(0x88),
    /**
     *
     */
    SIGNALFREQ(0x89),
    /**
     *
     */
    PROXIMITYADJUST(0x8A),
    /**
     *
     */
    MEASUREAMBIENT(0x10),
    /**
     *
     */
    MEASUREPROXIMITY(0x08),
    /**
     *
     */
    AMBIENTREADY(0x40),
    /**
     *
     */
    PROXIMITYREADY(0x20);

    /**
     *
     */
    public int cmd;

    private VCNL4000(int cmd) {
        this.cmd = cmd;
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
