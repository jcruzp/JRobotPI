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

import com.jcruz.jrobotpi.i2c.I2CRpi;
import com.jcruz.jrobotpi.i2c.TPA2016;
import java.io.IOException;

/**
 * Interface to TPA2016 sound amplifier
 *
 * @author Jose Cuz
 */
public class TPA2016Device extends I2CRpi {

    private static final int TPA2016_I2CADDR = 0x58;

    /**
     *
     * @throws IOException
     */
    public TPA2016Device() throws IOException {
        super(TPA2016_I2CADDR);
    }

    /**
     * Commands to set ganancy of amplifier
     */
    public enum AGCComp {

        /**
         * Ganancy compensation off
         */
        AGC_OFF(0x00),
        /**
         * Ganancy compensation 2
         */
        AGC_2(0x01),
        /**
         * Ganancy compensation 4
         */
        AGC_4(0x02),
        /**
         * Ganancy compensation 8
         */
        AGC_8(0x03);

        /**
         * Read value for ganancy
         */
        public byte value;

        AGCComp(int value) {
            this.value = (byte) value;
        }
    }

    /**
     * Set the gain in dB!
     *
     * @param g
     */
    public void setGain(byte g) {
        if (g > 30) {
            g = 30;
        }
        if (g < -28) {
            g = -28;
        }

        TPA2016.GAIN.write(device, g);
    }

    /**
     * for querying the gain, returns in dB
     *
     * @return
     */
    public byte getGain() {

        return ((byte) TPA2016.GAIN.read(device));
    }

    /**
     * Turn on/off right and left channels
     *
     * @param r
     * @param l
     */
    public void enableChannel(boolean r, boolean l) {

        byte setup = (byte) TPA2016.SETUP.read(device);
        if (r) {
            setup |= TPA2016.SETUP_R_EN.cmd;
        } else {
            setup &= ~TPA2016.SETUP_R_EN.cmd;
        }
        if (l) {
            setup |= TPA2016.SETUP_L_EN.cmd;
        } else {
            setup &= ~TPA2016.SETUP_L_EN.cmd;
        }

        TPA2016.SETUP.write(device, setup);
    }

    /**
     * Set to OFF, 1:2, 1:4 or 1:8
     *
     * @param x
     */
    public void setAGCCompression(AGCComp x) {
        if (x.value > 3) {
            return; // only 2 bits!
        }
        byte agc = (byte) TPA2016.AGC.read(device);
        agc &= ~(0x03);  // mask off bottom 2 bits
        agc |= x.value;        // set the compression ratio.
        TPA2016.AGC.write(device, agc);
    }

    /**
     *
     * @param release
     */
    public void setReleaseControl(byte release) {
        if (release > 0x3F) {
            return; // only 6 bits!
        }
        TPA2016.REL.write(device, release);
    }

    /**
     *
     * @param attack
     */
    public void setAttackControl(byte attack) {
        if (attack > 0x3F) {
            return; // only 6 bits!
        }
        TPA2016.ATK.write(device, attack);
    }

    /**
     *
     * @param hold
     */
    public void setHoldControl(byte hold) {
        if (hold > 0x3F) {
            return; // only 6 bits!
        }
        TPA2016.HOLD.write(device, hold);
    }

    /**
     * Turn on power limiter
     *
     */
    public void setLimitLevelOn() {
        byte agc = (byte) TPA2016.AGCLIMIT.read(device);
        agc &= ~(0x80);  // mask off top bit
        TPA2016.AGCLIMIT.write(device, agc);
    }

    /**
     * Turn off power limiter
     *
     */
    public void setLimitLevelOff() {
        byte agc = (byte) TPA2016.AGCLIMIT.read(device);
        agc |= 0x80;  // turn on top bit
        TPA2016.AGCLIMIT.write(device, agc);
    }

    /**
     * Set limit levels
     *
     * @param limit
     */
    public void setLimitLevel(byte limit) {
        if (limit > 31) {
            return;
        }

        byte agc = (byte) TPA2016.AGCLIMIT.read(device);

        agc &= ~(0x1F);  // mask off bottom 5 bits
        agc |= limit;        // set the limit level.

        TPA2016.AGCLIMIT.write(device, agc);
    }

    /**
     *
     * @param x
     */
    public void setAGCMaxGain(byte x) {
        if (x > 12) {
            return; // max gain max is 12 (30dB)
        }
        byte agc = (byte) TPA2016.AGC.read(device);
        agc &= ~(0xF0);  // mask off top 4 bits
        agc |= (x << 4);        // set the max gain
        TPA2016.AGC.write(device, agc);
    }

}
