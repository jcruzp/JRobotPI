/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jcruz.jrobotpi.i2c;

import jdk.dio.i2cbus.I2CDevice;

/**
 *
 * @author jcruz
 */
public enum HMC5883L {
   
    /**
     *
     */
    ConfigRegA(0x00),

    /**
     *
     */
    ConfigRegB(0x01),

    /**
     *
     */
    ModeReg(0x02),

    /**
     *
     */
    DataRegBegin(0x03);
    
    /**
     *
     */
    public int cmd;

    private HMC5883L(int cmd) {
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
