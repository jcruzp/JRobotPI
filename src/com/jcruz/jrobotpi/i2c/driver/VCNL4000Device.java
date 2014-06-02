/*
 * 
 */
package com.jcruz.jrobotpi.i2c.driver;

import com.jcruz.jrobotpi.i2c.I2CRpi;
import com.jcruz.jrobotpi.i2c.VCNL4000;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface to VCNL4000 proximity sensor
 *
 * @author Jose Cuz
 */
public class VCNL4000Device extends I2CRpi {

    /**
     * Set frequency for IR led
     */
    public enum Freq {

        /**
         * 3.125 MHZ
         */
        F3M125(0x00),
        /**
         * 1.5625 Mhz
         */
        F1M5625(0x01),
        /**
         * 781.25 Khz
         */
        F781K25(0x02),
        /**
         * 390.625 Khz
         */
        F390K625(0x03);

        /**
         * Read frequency value
         */
        public byte value;

        Freq(int value) {
            this.value = (byte) value;
        }
    }
    private static final Logger LOG = Logger.getLogger(VCNL4000Device.class.getName());

    private static final int VCNL4000_ADDRESS = 0x13;

    /**
     *
     * @throws IOException
     */
    public VCNL4000Device() throws IOException {
        super(VCNL4000_ADDRESS);

        byte rev = (byte) VCNL4000.PRODUCTID.read(device);
        if ((rev & 0xF0) != 0x10) {
            LOG.log(Level.SEVERE, "Sensor not found");
            return;
        }

        VCNL4000.IRLED.write(device, (byte) 20);        // set to 20 * 10mA = 200mA
        LOG.log(Level.INFO, "IR LED current = {0} mA", String.valueOf(VCNL4000.IRLED.read(device) * 10));

        //write8(VCNL4000_SIGNALFREQ, 3);
        LOG.log(Level.INFO, "Proximity measurement frequency = ");
        byte freq = (byte) VCNL4000.SIGNALFREQ.read(device);
        if (freq == Freq.F3M125.value) {
            LOG.log(Level.INFO, "3.125 MHz");
        }
        if (freq == Freq.F1M5625.value) {
            LOG.log(Level.INFO, "1.5625 MHz");
        }
        if (freq == Freq.F781K25.value) {
            LOG.log(Level.INFO, "781.25 KHz");
        }
        if (freq == Freq.F390K625.value) {
            LOG.log(Level.INFO, "390.625 KHz");
        }

        VCNL4000.PROXIMITYADJUST.write(device, (byte) 0x81);
        LOG.log(Level.INFO, "Proximity adjustment register = ");
        LOG.log(Level.INFO, String.valueOf(VCNL4000.PROXIMITYADJUST.read(device)));

        // arrange for continuous conversion
        //write8(VCNL4000_AMBIENTPARAMETER, 0x89);
    }

    /**
     *
     * @param cur
     * @throws IOException
     */
    public void setLEDcurrent(byte cur) throws IOException {
        if ((cur > 20) || (cur < 0)) {
            cur = 5; //# setting this to 50mA; online ppl report trouble with I2C bus at over 60mA
        }      //more here: http://forums.adafruit.com/viewtopic.php?f=19&t=24263&p=125769&hilit=vcnl#p125769
        VCNL4000.IRLED.write(device, cur);
    }

    /**
     * Activate continuous conversion
     *
     * @throws IOException
     */
    public void continuousConversionOn() throws IOException {

        VCNL4000.AMBIENTPARAMETER.write(device, (byte) 0x89);
    }

    /**
     * Deactivate continuous conversion
     *
     * @throws IOException
     */
    public void continuousConversionOff() throws IOException {
        VCNL4000.AMBIENTPARAMETER.write(device, (byte) 0x09);
    }

    /**
     * Set IR Frequency
     *
     * @param freq
     * @throws IOException
     */
    public void setSignalFreq(Freq freq) throws IOException {
        //# Setting the proximity IR test signal frequency. The proximity measurement is using a square IR 
        //# signal as measurement signal. Four different values are possible: 
        //# 00 = 3.125 MHz
        //# 01 = 1.5625 MHz
        //# 02 = 781.25 kHz (DEFAULT)
        //# 03 = 390.625 kHz
        VCNL4000.SIGNALFREQ.write(device, freq.value);
    }

    /**
     * Get IR IR Frequency
     *
     * @return @throws IOException
     */
    public int getSignalFreq() throws IOException {
        return VCNL4000.SIGNALFREQ.read(device);
    }

    /**
     * Adjust Proximity
     *
     * @throws IOException
     */
    public void setProximityAdjust() throws IOException {
        VCNL4000.PROXIMITYADJUST.write(device, (byte) 0x81);
    }

    /**
     * Get Proximity
     *
     * @return @throws IOException
     */
    public int getProximityAdjust() throws IOException {
        return VCNL4000.PROXIMITYADJUST.read(device);
    }

    /**
     *
     * @return proximity from object to device in cms
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    public short readProximity() throws IOException, InterruptedException {
        byte temp = (byte) VCNL4000.COMMAND.read(device);

        VCNL4000.COMMAND.write(device, (byte) (temp | VCNL4000.MEASUREPROXIMITY.cmd));

        while (true) {
            byte result = (byte) VCNL4000.COMMAND.read(device);
            //Serial.print("Ready = 0x"); Serial.println(result, HEX);
            if ((result & VCNL4000.PROXIMITYREADY.cmd) > 0) {
                short data = (short) (VCNL4000.PROXIMITYDATA.read(device) << 8);
                data = (short) (data | VCNL4000.PROXIMITYDATA2.read(device));
                return data;
            }
            Thread.sleep(10);
        }
    }

    /**
     *
     * @return Ambient light indicator
     * @throws java.io.IOException
     */
    public short readAmbientLight() throws IOException {
        // read ambient light!
        byte temp = (byte) VCNL4000.COMMAND.read(device);

        VCNL4000.COMMAND.write(device, (byte) (temp | VCNL4000.MEASUREAMBIENT.cmd));

        while (true) {
            byte result = (byte) VCNL4000.COMMAND.read(device);
            //Serial.print("Ready = 0x"); Serial.println(result, HEX);
            if ((result & VCNL4000.AMBIENTREADY.cmd) > 0) {

                short data = (short) (VCNL4000.AMBIENTDATA.read(device) << 8);
                data = (short) (data | VCNL4000.AMBIENTDATA2.read(device));
                return data;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(VCNL4000Device.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
