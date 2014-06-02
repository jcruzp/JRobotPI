/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jcruz.jrobotpi.i2c;

import java.io.IOException;
import jdk.dio.i2cbus.I2CDevice;

/**
 * Define the commands to control speed and activate DC Motors
 *
 * @author Jose Cuz
 */
public enum MotorsDC {

    /**
     * Start and Stop desired DC Motor
     */
    CONFIG_MOTORS(0x30),
    /**
     * Set speed to DC Motor
     */
    SET_SPEED(0x31);

    private final byte cmd;

    MotorsDC(int cmd) {
        this.cmd = (byte) cmd;
    }

    /**
     * write to Arduino Due I2C for activate Motor Shield v2
     *
     * @param device
     * @param value
     * @throws IOException
     */
    public void write(I2CDevice device, int value) throws IOException {
        I2CUtils.write(device, this.cmd, (byte) value);
    }
}
