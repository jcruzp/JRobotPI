/*
 * 
 */
package com.jcruz.jrobotpi.i2c;

import java.io.IOException;
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
     * @throws IOException
     */
    public int read(I2CDevice device) throws IOException {
        return I2CUtils.read(device, this.cmd);
    }

    /**
     * read a short from HTU21D connected to I2C Bus
     *
     * @param device
     * @return
     * @throws IOException
     */
    public short readShort(I2CDevice device) throws IOException {
        return I2CUtils.readShort(device, this.cmd);
    }

    /**
     * write to HTU21D connected to I2C bus
     *
     * @param device
     * @param value
     * @throws IOException
     */
    public void write(I2CDevice device, byte value) throws IOException {
        I2CUtils.write(device, (byte) this.cmd, value);
    }

}
