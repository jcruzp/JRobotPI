/*
 * 
 */
package com.jcruz.jrobotpi.i2c;

import java.io.IOException;
import jdk.dio.i2cbus.I2CDevice;

/**
 * All supported commands to I2C comunication RPI - Due
 *
 * @author jcruz
 */
public enum Wii {

    /**
     * Read Group 1 Buttons states
     */
    READ_BUTTONS1(0x01),
    /**
     * Read Group 2 Buttons states
     */
    READ_BUTTONS2(0x02),
    /**
     * Read Wii Motion Battery status
     */
    READ_BATTERY(0x03),
    /**
     * Read X from NunChuk Joystick
     */
    READ_JOYX(0x05),
    /**
     * Read Y from NunChuk Joystick
     */
    READ_JOYY(0x06),
    /**
     * Read X Wii Remote Accelerometer
     */
    READ_ACCEL_WRX(0x0A),
    /**
     * Read Y Wii Remote Accelerometer
     */
    READ_ACCEL_WRY(0x0C),
    /**
     * Read Z Wii Remote Accelerometer
     */
    READ_ACCEL_WRZ(0x0E),
    /**
     * Read X NunChuk Accelerometer
     */
    READ_ACCEL_NCX(0x10),
    /**
     * Read Y NunChuk Accelerometer
     */
    READ_ACCEL_NCY(0x12),
    /**
     * Read Z NunChuk Accelerometer
     */
    READ_ACCEL_NCZ(0x14),
    /**
     * Read Pitch Wii Motion Plus Gyroscope
     */
    READ_GYRO_PITCH(0x16),
    /**
     * Read Roll Wii Motion Plus Gyroscope
     */
    READ_GYRO_ROLL(0x18),
    /**
     * Read Yaw Wii Motion Plus Gyroscope
     */
    READ_GYRO_YAW(0x1A),
    /**
     * On or Off Wii Remote Leds and Rumble
     */
    LEDS_RUMBLE(0x20);

    private final byte cmd;

    Wii(int cmd) {
        this.cmd = (byte) cmd;
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
     * @return
     * @throws IOException
     */
    public float readFloatArduino(I2CDevice device) throws IOException {
        return I2CUtils.readFloatArduino(device, this.cmd);
    }

    /**
     *
     * @param device
     * @return
     * @throws IOException
     */
    public short readShortArduino(I2CDevice device) throws IOException {
        return I2CUtils.readShortArduino(device, this.cmd);
    }

    /**
     *
     * @param device
     * @param value
     * @throws IOException
     */
    public void write(I2CDevice device, byte value) throws IOException {
        I2CUtils.write(device, this.cmd, value);
    }
}
