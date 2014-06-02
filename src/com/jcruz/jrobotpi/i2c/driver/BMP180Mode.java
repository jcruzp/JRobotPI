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
