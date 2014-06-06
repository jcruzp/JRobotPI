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

import com.jcruz.jrobotpi.i2c.I2CDue;
import com.jcruz.jrobotpi.i2c.I2CUtils;
import com.jcruz.jrobotpi.i2c.MotorsDC;

/**
 * Base code to control all DC Motors
 *
 * @author Jose Cuz
 */
public class Motors4WD extends I2CDue {

    /**
     * Define move options
     */
    public enum CmdEnum {

        /**
         * Stop motor
         */
        RELEASE(16),
        /**
         * Move forward
         */
        FORWARD(32),
        /**
         * Move backward
         */
        BACKWARD(64),
        /**
         * set speed
         */
        SPEED(128);

        /**
         *
         */
        public int value;

        CmdEnum(int value) {
            this.value = value;
        }
    };

    /**
     * Define motors left
     */
    public final int MOTOR_LEFT = 0;

    /**
     * Define motors right
     */
    public final int MOTOR_RIGHT = 1;

    private final int differential = 0; // % faster left motor turns compared to right  

    /**
     * tables hold time in ms to rotate robot 360 degrees at various speeds this
     * enables conversion of rotation angle into timed motor movement The speeds
     * are percent of max speed Note: low cost motors do not have enough torque
     * at low speeds so the robot will not move below this value Interpolation
     * is used to get a time for any speed from MIN_SPEED to 100%
     */
    public final int MIN_SPEED = 60; // first table entry is 60% speed

    /**
     * each table entry is 10% faster speed
     */
    public final int SPEED_TABLE_INTERVAL = 10;

    /**
     *
     */
    public final int NBR_SPEEDS = 1 + (100 - MIN_SPEED) / SPEED_TABLE_INTERVAL;

    /**
     * speeds
     */
    public int speedTable[] = new int[]{60, 70, 80, 90, 100};

    /**
     * time
     */
    public int rotationTime[] = new int[]{5500, 3300, 2400, 2000, 1750};

    // left and right motor speeds stored here (0-100%)
    private int motorSpeed[] = new int[]{0, 0, 0, 0};

    /**
     *
     */
    public Motors4WD() {
        super();
    }

    //Conver motor number to binary number
    private int nroMotor(int motor) {
        return (1 << motor);
    }

    /**
     * Re-maps a number from one range to another. That is, a value of fromLow
     * would get mapped to toLow, a value of fromHigh to toHigh, values
     * in-between to values in-between, etc.
     *
     * @param x
     * @param in_min
     * @param in_max
     * @param out_min
     * @param out_max
     * @return
     */
    public long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    void motorBegin(int motor) {
        motorStop(motor);  // stop the front motor
        motorStop(motor + 1); // stop the rear motor
    }

    // speed range is 0 to 100 percent
    void motorSetSpeed(int motor, int speed) {
        if (motor == MOTOR_LEFT && speed > differential) {
            speed -= differential;
        }
        motorSpeed[motor] = speed;           // save the value

        int pwm = (int) map(speed, 0, 100, 0, 255);  // scale to PWM range
        //motors[motor].setSpeed(pwm) ;
        I2CUtils.I2Cdelay(50);
        MotorsDC.SET_SPEED.write(arduino, pwm);
        I2CUtils.I2Cdelay(50);
        MotorsDC.CONFIG_MOTORS.write(arduino, CmdEnum.SPEED.value + nroMotor(motor));
        I2CUtils.I2Cdelay(50);
        MotorsDC.SET_SPEED.write(arduino, pwm);
        I2CUtils.I2Cdelay(50);
        MotorsDC.CONFIG_MOTORS.write(arduino, CmdEnum.SPEED.value + nroMotor(motor + 2));
        I2CUtils.I2Cdelay(50);

        // motors[motor].setSpeed(pwm) ;
        // motors[motor+2].setSpeed(pwm) ;
    }

    void motorForward(int motor, int speed) {
        motorSetSpeed(motor, speed);
        MotorsDC.CONFIG_MOTORS.write(arduino, CmdEnum.FORWARD.value + nroMotor(motor));
        I2CUtils.I2Cdelay(50);
        MotorsDC.CONFIG_MOTORS.write(arduino, CmdEnum.FORWARD.value + nroMotor(motor + 2));
        I2CUtils.I2Cdelay(50);

        //motors[motor].run(FORWARD); 
        //motors[motor+2].run(FORWARD);   
    }

    void motorReverse(int motor, int speed) {
        motorSetSpeed(motor, speed);
        MotorsDC.CONFIG_MOTORS.write(arduino, CmdEnum.BACKWARD.value + nroMotor(motor));
        I2CUtils.I2Cdelay(50);
        MotorsDC.CONFIG_MOTORS.write(arduino, CmdEnum.BACKWARD.value + nroMotor(motor + 2));
        I2CUtils.I2Cdelay(50);

        //motors[motor].run(BACKWARD); 
        //motors[motor+2].run(BACKWARD);   
    }

    void motorStop(int motor) {
        motorSetSpeed(motor, 0);
        MotorsDC.CONFIG_MOTORS.write(arduino, CmdEnum.RELEASE.value + nroMotor(motor));
        I2CUtils.I2Cdelay(50);
        MotorsDC.CONFIG_MOTORS.write(arduino, CmdEnum.RELEASE.value + nroMotor(motor + 2));
        I2CUtils.I2Cdelay(50);

        //motors[motor].run(RELEASE);      // stopped
        //motors[motor+2].run(RELEASE);   
    }

//void motorBrake(int motor)
//{
//  motors[motor].run(BRAKE);      // stopped
//  motors[motor+2].run(BRAKE); 
//}
//    
//}
}
