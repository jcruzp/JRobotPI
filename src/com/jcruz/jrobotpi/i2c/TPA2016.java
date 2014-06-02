/*
 * 
 */
package com.jcruz.jrobotpi.i2c;

import java.io.IOException;
import jdk.dio.i2cbus.I2CDevice;

/**
 * Commands to control TPA2016 sound amplifier
 *
 * @author Jose Cuz
 */
public enum TPA2016 {

    /**
     *
     */
    SETUP(0x1),
    /**
     *
     */
    SETUP_R_EN(0x80),
    /**
     *
     */
    SETUP_L_EN(0x40),
    //SETUP_SWS(0x20),
    //SETUP_R_FAULT(0x10),
    //SETUP_L_FAULT(0x08),
    //SETUP_THERMAL(0x04),
    //SETUP_NOISEGATE(0x01),

    /**
     *
     */
    AGCLIMIT(0x6),
    /**
     *
     */
    HOLD(0x4),
    /**
     *
     */
    ATK(0x2),
    /**
     *
     */
    REL(0x3),
    /**
     *
     */
    GAIN(0x5),
    /**
     *
     */
    AGC(0x7);

    /**
     *
     */
    public int cmd;

    private TPA2016(int cmd) {
        this.cmd = cmd;
    }

    /**
     *
     * @param device
     * @return
     * @throws IOException
     */
    public int read(I2CDevice device) throws IOException {
        return I2CUtils.read(device, this.cmd);
    }

    /**
     *
     * @param device
     * @param value
     * @throws IOException
     */
    public void write(I2CDevice device, byte value) throws IOException {
        I2CUtils.write(device, (byte) this.cmd, value);
    }

}
