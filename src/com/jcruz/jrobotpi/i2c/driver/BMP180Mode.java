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

/**
 * Relationship between Oversampling Setting and conversion delay (in ms) for
 * each Oversampling Setting contstant
 *
 * @author jcruz
 */
public enum BMP180Mode {

    /**
     * Ultra low power: 4.5 ms minimum conversion delay
     */
    ULTRA_LOW_POWER(0, 5),
    /**
     * Standard: 7.5 ms
     */
    STANDARD(1, 8),
    /**
     * High Resolution: 13.5 ms
     */
    HIGH_RESOLUTION(2, 14),
    /**
     * Ultra high Resolution: 25.5 ms
     */
    ULTRA_HIGH_RESOLUTION(3, 26);

    private final int oss;                                      // Oversample setting value
    private final int delay;                                    // Minimum conversion time in ms
    private static final byte getPressCmd = (byte) 0x34;        // Read pressure command
    private final byte cmd;                                     // Command byte to read pressure

    BMP180Mode(int oss, int delay) {
        this.oss = oss;
        this.delay = delay;
        this.cmd = (byte) (getPressCmd + ((oss << 6) & 0xC0));
    }

    /**
     *
     * @return the conversion delay (in ms) associated with this oversampling
     * setting
     */
    public int getDelay() {
        return delay;
    }

    /**
     *
     * @return the command to the control register for this oversampling setting
     */
    public byte getCommand() {
        return cmd;
    }

    /**
     *
     * @return Return this oversampling setting
     */
    public int getOSS() {
        return oss;
    }
}
