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
 * Definitio commands to control HTU21D humidity and temperature sensor
 *
 * @author jcruz
 */
public enum HTU21D {

    /**
     *
     */
    TRIGGER_TEMP_MEASURE_HOLD(0xE3),
    /**
     *
     */
    TRIGGER_HUMD_MEASURE_HOLD(0xE5),
    /**
     *
     */
    TRIGGER_TEMP_MEASURE_NOHOLD(0xF3),
    /**
     *
     */
    TRIGGER_HUMD_MEASURE_NOHOLD(0xF5),
    /**
     *
     */
    WRITE_USER_REG(0xE6),
    /**
     *
     */
    READ_USER_REG(0xE7),
    /**
     *
     */
    SOFT_RESET(0xFE);

    /**
     *
     */
    public int cmd;

    private HTU21D(int cmd) {
        this.cmd = cmd;
    }

    /**
     * read an int from HTU21D connected to I2C bus
     *
     * @param device
     * @return
     */
    public int read(I2CDevice device) {
        return I2CUtils.read(device, this.cmd);
    }

    /**
     * read a short from HTU21D connected to I2C Bus
     *
     * @param device
     * @return
     */
    public short readShort(I2CDevice device) {
        return I2CUtils.readShort(device, this.cmd);
    }

    /**
     * write to HTU21D connected to I2C bus
     *
     * @param device
     * @param value
     */
    public void write(I2CDevice device, byte value) {
        I2CUtils.write(device, (byte) this.cmd, value);
    }

}
