/*
 * 
 */
package com.jcruz.jrobotpi.i2c.driver;

import com.jcruz.jrobotpi.i2c.I2CDue;
import com.jcruz.jrobotpi.i2c.I2CUtils;
import com.jcruz.jrobotpi.i2c.Wii;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.i2cbus.I2CDevice;

/**
 * Interface to Wii Remote control
 *
 * @author Jose Cuz
 */
public class WiiRemote extends I2CDue {

    /**
     * Group 2 of buttons at wii remote controller
     */
    public enum Button2Enum {

        /**
         * Button +
         */
        PLUS(16),
        /**
         * Button 2
         */
        TWO(2),
        /**
         * Button 1
         */
        ONE(1),
        /**
         * Button -
         */
        MINUS(4),
        /**
         * Button Home
         */
        HOME(8);

        /**
         * Read value for buttons group 2
         */
        public int value;

        Button2Enum(int value) {
            this.value = value;
        }
    };

    /**
     * Enum used to read the joystick on the Nunchuck.
     */
    public enum HatEnum {

        /**
         * Read the x-axis on the Nunchuck joystick.
         */
        HatX {
                    int getHat(I2CDevice arduino) throws IOException {
                        return Wii.READ_JOYX.read(arduino);
                    }
                },
        /**
         * Read the y-axis on the Nunchuck joystick.
         */
        HatY {
                    int getHat(I2CDevice arduino) throws IOException {
                        return Wii.READ_JOYY.read(arduino);
                    }
                };

        abstract int getHat(I2CDevice arduino) throws IOException;
    };

    /**
     * This enum is used to read all the different buttons on the different
     * controllers
     */
    public enum Button1Enum {

        /**
         * Button Up
         */
        UP(128),
        /**
         * Button Right
         */
        RIGHT(16),
        /**
         * Button Down
         */
        DOWN(64),
        /**
         * Button Left
         */
        LEFT(32),
        /**
         * Button Z
         */
        Z(2),
        /**
         * Button C
         */
        C(1),
        /**
         * Button B
         */
        B(4),
        /**
         * Button A
         */
        A(8);

        /**
         * Read value for buttons group 1
         */
        public int value;

        Button1Enum(int value) {
            this.value = value;
        }
    };

    /**
     * Enum used to turn on the LEDs on the different controllers.
     */
    public enum LEDEnum {

        /**
         * All Off
         */
        OFF(0),
        /**
         * Led 1
         */
        LED1(1),
        /**
         * Led 2
         */
        LED2(2),
        /**
         * Led 3
         */
        LED3(4),
        /**
         * Led 4
         */
        LED4(8),
        /**
         * Rumble
         */
        RUMBLE(16);

        /**
         * Read value from leds or rumble
         */
        public int value;

        LEDEnum(int value) {
            this.value = value;
        }
    };

    private boolean wiimoteConnected = true;
    private boolean nunchuckConnected = true;
    private boolean motionPlusConnected = true;

    private byte ledRumbleStatus = 0; // All off

    /**
     * Accelerometer values used to calculate pitch and roll.
     */
    private int accXwiimote, accYwiimote, accZwiimote;
    private int accXnunchuck, accYnunchuck, accZnunchuck;

    /**
     * Inicialize comunication with Arduino Due at I2C bus
     */
    public WiiRemote() {
        super();
    }

    /**
     *
     * @param b
     * @return Group 1 buttons pressed
     * @throws IOException
     */
    public boolean getButton1Press(Button1Enum b) throws IOException {
        return (Wii.READ_BUTTONS1.read(arduino) & b.value) == b.value;
    }

    /**
     *
     * @param b
     * @return Group 2 buttons pressed
     * @throws IOException
     */
    public boolean getButton2Press(Button2Enum b) throws IOException {
        return (Wii.READ_BUTTONS2.read(arduino) & b.value) == b.value;
    }

    /**
     * Used to read the joystick of the Nunchuck.
     *
     * @param a Either ::HatX or ::HatY.
     * @return Return the analog value in the range from approximately 25-230.
     * @throws java.io.IOException
     */
    public int getAnalogHat(HatEnum a) throws IOException {
        return a.getHat(arduino);
    }

    /**
     * Pitch calculated from the Wiimote. A complimentary filter is used if the
     * Motion Plus is connected.
     *
     * @return Pitch in the range from 0-360.
     * @throws java.io.IOException
     */
    public float getPitch() throws IOException {
        return Wii.READ_GYRO_PITCH.readFloatArduino(arduino);
    }

    /**
     * Roll calculated from the Wiimote. A complimentary filter is used if the
     * Motion Plus is connected.
     *
     * @return Roll in the range from 0-360.
     * @throws java.io.IOException
     */
    public float getRoll() throws IOException {
        return Wii.READ_GYRO_ROLL.readFloatArduino(arduino);
    }

    /**
     * This is the yaw calculated by the gyro.
     *
     * <B>NOTE:</B> This angle will drift a lot and is only available if the
     * Motion Plus extension is connected.
     *
     * @return The angle calculated using the gyro.
     * @throws java.io.IOException
     */
    public float getYaw() throws IOException {
        return Wii.READ_GYRO_YAW.readFloatArduino(arduino);
    }

    /**
     * Used to set all LEDs and rumble off.
     *
     * @throws java.io.IOException
     */
    public void setAllOff() throws IOException {
        ledRumbleStatus = 0;
        Wii.LEDS_RUMBLE.write(arduino, (byte) LEDEnum.OFF.value);
    }

    /**
     * Turn off rumble.
     *
     * @throws java.io.IOException
     */
    public void setRumbleOff() throws IOException {
        ledRumbleStatus &= (byte) (~LEDEnum.RUMBLE.value);
        Wii.LEDS_RUMBLE.write(arduino, ledRumbleStatus);
    }

    /**
     * Turn on rumble.
     *
     * @throws java.io.IOException
     */
    public void setRumbleOn() throws IOException {
        ledRumbleStatus |= (byte) (LEDEnum.RUMBLE.value);
        Wii.LEDS_RUMBLE.write(arduino, ledRumbleStatus);
    }

    /**
     * Turn the specific ::LEDEnum off.
     *
     * @param a The ::LEDEnum to turn off.
     * @throws java.io.IOException
     */
    public void setLedOff(LEDEnum a) throws IOException {
        ledRumbleStatus &= (byte) (~a.value);
        Wii.LEDS_RUMBLE.write(arduino, ledRumbleStatus);
    }

    /**
     * Turn the specific ::LEDEnum on.
     *
     * @param a The ::LEDEnum to turn on.
     * @throws java.io.IOException
     */
    public void setLedOn(LEDEnum a) throws IOException {
        ledRumbleStatus |= (byte) (a.value);
        Wii.LEDS_RUMBLE.write(arduino, ledRumbleStatus);
    }

    /**
     * Return the battery level of the Wiimote.
     *
     * @return The battery level in the range 0-255.
     * @throws java.io.IOException
     */
    public int getBatteryLevel() throws IOException {
        return Wii.READ_BATTERY.read(arduino);
    }

    /**
     * Pitch and roll calculated from the accelerometer inside the Wiimote.
     *
     * @return
     * @throws java.io.IOException
     */
    public double getWiimotePitch() throws IOException {
        accYwiimote = Wii.READ_ACCEL_WRY.readShortArduino(arduino);
        I2CUtils.I2Cdelay(1);
        accZwiimote = Wii.READ_ACCEL_WRZ.readShortArduino(arduino);
        return Math.toDegrees((Math.atan2(accYwiimote, accZwiimote) + Math.PI));
    }

    /**
     *
     * @return @throws IOException
     */
    public double getWiimoteRoll() throws IOException {
        accXwiimote = Wii.READ_ACCEL_WRX.readShortArduino(arduino);
        I2CUtils.I2Cdelay(1);
        accZwiimote = Wii.READ_ACCEL_WRZ.readShortArduino(arduino);
        return Math.toDegrees((Math.atan2(accXwiimote, accZwiimote) + Math.PI));
    }

    /**
     * Pitch and roll calculated from the accelerometer inside the Nunchuck.
     *
     * @return
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public double getNunchuckPitch() throws IOException {
        accYnunchuck = Wii.READ_ACCEL_NCY.readShortArduino(arduino);
        I2CUtils.I2Cdelay(1);
        accZnunchuck = Wii.READ_ACCEL_NCZ.readShortArduino(arduino);
        return Math.toDegrees((Math.atan2(accYnunchuck, accZnunchuck) + Math.PI));
    }

    /**
     *
     * @return @throws IOException
     * @throws InterruptedException
     */
    public double getNunchuckRoll() throws IOException {
        accXnunchuck = Wii.READ_ACCEL_NCX.readShortArduino(arduino);
        I2CUtils.I2Cdelay(1);
        accZnunchuck = Wii.READ_ACCEL_NCZ.readShortArduino(arduino);
        return Math.toDegrees((Math.atan2(accXnunchuck, accZnunchuck) + Math.PI));
    }

}
