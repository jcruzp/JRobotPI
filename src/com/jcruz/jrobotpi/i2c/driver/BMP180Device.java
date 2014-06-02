package com.jcruz.jrobotpi.i2c.driver;

import com.jcruz.jrobotpi.i2c.BMP180;
import com.jcruz.jrobotpi.i2c.I2CRpi;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface to BMP180 device
 *
 * @author jcruz
 */
public class BMP180Device extends I2CRpi {

    // EEPROM registers - these represent calibration data
    private short AC1;
    private short AC2;
    private short AC3;
    private int AC4;
    private int AC5;
    private int AC6;
    private short B1;
    private short B2;
    private short MB;
    private short MC;
    private short MD;
    private static final int CALIB_BYTES = 22;

    // Uncompensated temperature
    private int UT;

    // Uncompensated pressure
    private int UP;

    // Variables common between temperature and pressure calculations
    private int B5;

    private static final int address = 0x77;                    // Device address
    //private static final int serialClock = 3400000;             // 3.4MHz Max clock
    //private static final int addressSizeBits = 7;               // Device address size in bits

    // Conversion Timing data - pressure conversion delays
    // are defined in the BMP180Mode enum
    private static final int tempConvTime = 5;                  // Max delay time of 4.5 ms

    // Address byte length
    private static final int subAddressSize = 1;                // Size of each address (in bytes)

    /**
     * Initialze the BPM085 Device
     *
     * @throws IOException
     */
    public BMP180Device() throws IOException {
        super(address);
        readCalibrationData();
    }

    /**
     * Read the calibration data from the device in one shot
     *
     * @throws IOException - If there is an I/O exception reading the device
     */
    private void readCalibrationData() throws IOException {
        // Read all of the calibration data into a byte array
        ByteBuffer calibData = ByteBuffer.allocateDirect(CALIB_BYTES);
        int result = device.read(BMP180.EEPROM_start.cmd, subAddressSize, calibData);
        if (result < CALIB_BYTES) {
            Logger.getGlobal().log(Level.SEVERE, "Error: {0} bytes read", result);
            return;
        }
        // Read each of the pairs of data as a signed short
        calibData.rewind();
        AC1 = calibData.getShort();
        AC2 = calibData.getShort();
        AC3 = calibData.getShort();

        // Unsigned short values
        byte[] data = new byte[2];
        calibData.get(data);
        AC4 = (((data[0] << 8) & 0xFF00) + (data[1] & 0xFF));
        calibData.get(data);
        AC5 = (((data[0] << 8) & 0xFF00) + (data[1] & 0xFF));
        calibData.get(data);
        AC6 = (((data[0] << 8) & 0xFF00) + (data[1] & 0xFF));

        // Signed sort values
        B1 = calibData.getShort();
        B2 = calibData.getShort();
        MB = calibData.getShort();
        MC = calibData.getShort();
        MD = calibData.getShort();

        // Debug Statements
        Logger.getGlobal().log(Level.FINE, "AC1 = {0}", AC1);
        Logger.getGlobal().log(Level.FINE, "AC2 = {0}", AC2);
        Logger.getGlobal().log(Level.FINE, "AC3 = {0}", AC3);
        Logger.getGlobal().log(Level.FINE, "AC4 = {0}", AC4);
        Logger.getGlobal().log(Level.FINE, "AC5 = {0}", AC5);
        Logger.getGlobal().log(Level.FINE, "AC6 = {0}", AC6);
        Logger.getGlobal().log(Level.FINE, "B1 = {0}", B1);
        Logger.getGlobal().log(Level.FINE, "B2 = {0}", B2);
        Logger.getGlobal().log(Level.FINE, "MB = {0}", MB);
        Logger.getGlobal().log(Level.FINE, "MC = {0}", MC);
        Logger.getGlobal().log(Level.FINE, "MD = {0}", MD);
    }

    /**
     * Read temperature and pressure from the device. Note that the pressure is
     * read following the temperature, as the pressure is calculated using date
     * from temperature calculation.
     *
     * @param mode BMP180Mode
     * @return float array, element 0 = temperature Celsius, element 1 =
     * pressure, hPa
     * @throws IOException - If there is an I/O exception reading the device
     */
    public float[] getTemperaturePressure(BMP180Mode mode) throws IOException {
        float[] result = new float[2];
        result[0] = getTemperature();
        result[1] = getPressure(mode);
        return result;
    }

    /**
     * Read the temperature (in Celsius) from the device
     *
     * @return Temperature in degrees Celsius
     * @throws IOException - If there is an I/O exception reading the device
     */
    private float getTemperature() throws IOException {
        Logger.getGlobal().log(Level.FINE, "Getting temperature");
        BMP180.controlRegister.write(device, (byte) BMP180.getTempCmd.cmd);

        // Delay before reading the temperature
        try {
            Thread.sleep(tempConvTime);
        } catch (InterruptedException ex) {
        }

        UT = BMP180.tempAddr.readShort(device);
        Logger.getGlobal().log(Level.FINE, "Uncompensated temperature: {0}", UT);

        // Calculate the actual temperature
        int X1 = ((UT - AC6) * AC5) >> 15;
        int X2 = (MC << 11) / (X1 + MD);
        B5 = X1 + X2;
        float celsius = (float) ((B5 + 8) >> 4) / 10;

        // Debug statements
        Logger.getGlobal().log(Level.FINE, "X1 = {0}", X1);
        Logger.getGlobal().log(Level.FINE, "X2 = {0}", X2);
        Logger.getGlobal().log(Level.FINE, "B5 = {0}", B5);
        Logger.getGlobal().log(Level.FINE, "True temperature: {0}C", celsius);

        return celsius;
    }

    /**
     * Read the barometric pressure (in hPa) from the device. The command to the
     * control register is calculated based on the mode.
     *
     * @param mode - A BMP180Mode enum
     * @return Pressure measures in hPa
     * @throws IOException - If there is an I/O exception reading the device
     */
    private float getPressure(BMP180Mode mode) throws IOException {
        Logger.getGlobal().log(Level.FINE, "Getting pressure");
        // The pressure command is calculated by the enum
        Logger.getGlobal().log(Level.FINE, "Mode: {0}", mode.name());
        Logger.getGlobal().log(Level.FINE, "Pressure Command: {0}", mode.getCommand());

        BMP180.controlRegister.write(device, mode.getCommand());

        // Delay before reading the pressure - use the value determined by the oversampling setting (mode)
        try {
            Thread.sleep(mode.getDelay());
        } catch (InterruptedException ex) {
        }

        // Read the uncompensated pressure value
        ByteBuffer uncompPress = ByteBuffer.allocateDirect(3);
        int result = device.read(BMP180.pressAddr.cmd, subAddressSize, uncompPress);
        if (result < 3) {
            Logger.getGlobal().log(Level.SEVERE, "Error: {0} bytes read", result);
            return 0;
        }

        // Get the uncompensated pressure as a three byte word
        uncompPress.rewind();
        byte[] data = new byte[3];
        uncompPress.get(data);
        UP = ((((data[0] << 16) & 0xFF0000) + ((data[1] << 8) & 0xFF00) + (data[2] & 0xFF)) >> (8 - mode.getOSS()));
        Logger.getGlobal().log(Level.FINE, "Uncompensated pressure: {0}", UP);

        // Calculate the true pressure
        int B6 = B5 - 4000;
        int X1 = (B2 * (B6 * B6) >> 12) >> 11;
        int X2 = AC2 * B6 >> 11;
        int X3 = X1 + X2;
        int B3 = ((((AC1 * 4) + X3) << mode.getOSS()) + 2) / 4;
        X1 = AC3 * B6 >> 13;
        X2 = (B1 * ((B6 * B6) >> 12)) >> 16;
        X3 = ((X1 + X2) + 2) >> 2;
        int B4 = (AC4 * (X3 + 32768)) >> 15;
        int B7 = (UP - B3) * (50000 >> mode.getOSS());

        // Debug statements
        Logger.getGlobal().log(Level.FINE, "B6 = {0}", B6);
        Logger.getGlobal().log(Level.FINE, "X1 = {0}", X1);
        Logger.getGlobal().log(Level.FINE, "X2 = {0}", X2);
        Logger.getGlobal().log(Level.FINE, "X3 = {0}", X3);
        Logger.getGlobal().log(Level.FINE, "B3 = {0}", B3);
        Logger.getGlobal().log(Level.FINE, "X1 = {0}", X1);
        Logger.getGlobal().log(Level.FINE, "X2 = {0}", X2);
        Logger.getGlobal().log(Level.FINE, "X3 = {0}", X3);
        Logger.getGlobal().log(Level.FINE, "B4 = {0}", B4);
        Logger.getGlobal().log(Level.FINE, "B7 = {0}", B7);

        int Pa;
        if (B7 < 0x80000000) {
            Pa = (B7 * 2) / B4;
        } else {
            Pa = (B7 / B4) * 2;
        }
        Logger.getGlobal().log(Level.FINE, "Pa = {0}", Pa);

        X1 = (Pa >> 8) * (Pa >> 8);
        X1 = (X1 * 3038) >> 16;
        X2 = (-7357 * Pa) >> 16;

        Pa += ((X1 + X2 + 3791) >> 4);

        // Debug statements
        Logger.getGlobal().log(Level.FINE, "X1 = {0}", X1);
        Logger.getGlobal().log(Level.FINE, "X2 = {0}", X2);
        Logger.getGlobal().log(Level.FINE, "Pa = {0}", Pa);

        return (float) (Pa) / 100;
    }

    /**
     * Calculate temperature in Fahrenheit based on a celsius temp
     *
     * @param temp - The temperature in degrees Celsius to convert to Fahrenheit
     * @return float - Temperature in degrees Fahrenheit, converted from Celsius
     */
    public static float celsiusToFahrenheit(float temp) {
        return (float) ((temp * 1.8) + 32);
    }

    /**
     * Calculate pressure in inches of mercury (inHg)
     *
     * @param pressure - The pressure in hPa
     * @return float - Pressure converted to inches Mercury (inHg)
     */
    public static float pascalToInchesMercury(float pressure) {
        return (float) (pressure * 0.0296);
    }

}
