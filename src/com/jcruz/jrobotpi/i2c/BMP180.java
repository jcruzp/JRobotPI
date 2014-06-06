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
 * Control BMP180 pressure and temperature sensor
 * 
 * Based in Oracle Article How to Become an Embedded Developer in Minutes
 * by Angela Caicedo http://www.oracle.com/technetwork/articles/java/ma14-new-to-java-embedded-2177732.html
 * 
 * And 
 *
 * Beyond Beauty: JavaFX, I2C, Parallax, Touch, Raspberry Pi, Gyroscopes and Much More. (Part I)
 * Angela Blog https://blogs.oracle.com/acaicedo/entry/beyond_beauty_javafx_i2c_parallax
 *
 * @author jcruz
 */
public enum BMP180 {

    /**
     * Temperature and Pressure Control Register Data
     */
    EEPROM_start(0xAA),
    /**
     * Control register address
     */
    controlRegister(0xF4),
    /**
     * Temperature read address
     */
    tempAddr(0xF6),
    /**
     * Pressure read address
     */
    pressAddr(0xF6),
    /**
     * Read temperature command
     */
    getTempCmd(0x2E),
    /**
     * Read pressure command
     */
    getPressCmd(0x34);

    /**
     * Set command value
     */
    public int cmd;

    private BMP180(int cmd) {
        this.cmd = cmd;
    }

    /**
     * read an int from BMP180 connected to I2C bus
     *
     * @param device
     * @return
     */
    public int read(I2CDevice device) {
        return I2CUtils.read(device, this.cmd);
    }

    /**
     * read a short from BMP180 connected to I2C Bus
     *
     * @param device
     * @return
     */
    public short readShort(I2CDevice device) {
        return I2CUtils.readShort(device, this.cmd);
    }

    /**
     * write to BMP180 connected to I2C bus
     *
     * @param device
     * @param value
     */
    public void write(I2CDevice device, byte value) {
        I2CUtils.write(device, (byte) this.cmd, value);
    }
}
