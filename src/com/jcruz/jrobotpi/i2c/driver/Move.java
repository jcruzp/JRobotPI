/*
 *
 */
package com.jcruz.jrobotpi.i2c.driver;

import com.jcruz.jrobotpi.i2c.I2CUtils;

/**
 * Define all movements for 4wd motors
 *
 * @author jcruz
 */
public class Move extends Motors4WD {

    /**
     * *** Global Defines ***
     */
    // defines for locations of sensors
    private final int SENSE_IR_LEFT = 0;
    private final int SENSE_IR_RIGHT = 1;

    // defines for directions
    private final int DIR_LEFT = 0;
    private final int DIR_RIGHT = 1;

    private final int DIR_CENTER = 2;

    // obstacles constants
    private final int OBST_NONE = 0;  // no obstacle detected
    private final int OBST_LEFT_EDGE = 1;  // left edge detected
    private final int OBST_RIGHT_EDGE = 2;  // right edge detected
    private final int OBST_FRONT_EDGE = 3;  // edge detect at both left & right sensors

    // move states:
    enum MoveEnum {

        MOV_LEFT, MOV_RIGHT, MOV_FORWARD, MOV_BACK, MOV_ROTATE, MOV_STOP
    };
    /**
     * ** End of Global Defines ***************
     */

    /**
     *
     * Drive: mid level movement functions
     */
    private MoveEnum moveState = MoveEnum.MOV_STOP;   // what robot is doing

    private int moveSpeed = 0;     // move speed stored here (0-100%)
    private int speedIncrement = 10; // percent to increase or decrease speed

    /**
     * Define movements to 4wd motors
     */
    public Move() {
        super();
    }

    /**
     * This is the low level movement state. it will differ from the command
     * state when the robot is avoiding obstacles
     *
     * @param newState
     */
    public void changeMoveState(MoveEnum newState) {
        if (newState != moveState) {
            //Serial.print("Changing move state from "); Serial.print( states[moveState]);
            //Serial.print(" to "); Serial.println(states[newState]);
            moveState = newState;
        }
    }

    /**
     * Stop move
     *
     */
    public void moveStop() {
        changeMoveState(MoveEnum.MOV_STOP);
        motorStop(MOTOR_LEFT);
        motorStop(MOTOR_RIGHT);
    }

//void moveBrake()
//{
//  changeMoveState(MoveEnum.MOV_STOP);
//  motorBrake(MOTOR_LEFT);
//  motorBrake(MOTOR_RIGHT);
//}
    /**
     * Set speed
     *
     * @param speed
     */
    public void moveSetSpeed(int speed) {
        motorSetSpeed(MOTOR_LEFT, speed);
        motorSetSpeed(MOTOR_RIGHT, speed);
        moveSpeed = speed; // save the value
    }

    /**
     * Decrement speed
     *
     * @param decrement
     */
    public void moveSlower(int decrement) {
        //Serial.print(" Slower: ");
//        if (moveSpeed >= speedIncrement + MIN_SPEED) {
//            moveSpeed -= speedIncrement;
//        } else {
//            moveSpeed = MIN_SPEED;
//        }
        moveSpeed -= speedIncrement;
        if (moveSpeed <= 0) {
            moveSpeed = 0;
        }
        moveSetSpeed(moveSpeed);
    }

    /**
     * Increment speed
     *
     * @param increment
     */
    public void moveFaster(int increment) {
        //Serial.print(" Faster: ");
        moveSpeed += speedIncrement;
        if (moveSpeed > 100) {
            moveSpeed = 100;
        }
        moveSetSpeed(moveSpeed);
    }

    /**
     *
     * @return Move state
     */
    public MoveEnum moveGetState() {
        return moveState;
    }

    /**
     * Inicialize motors and stop them
     *
     */
    public void moveBegin() {
        motorBegin(MOTOR_LEFT);
        motorBegin(MOTOR_RIGHT);
        moveStop();
    }

    /**
     * Move Left
     *
     */
    public void moveLeft() {
        changeMoveState(MoveEnum.MOV_LEFT);
        motorForward(MOTOR_LEFT, 0);
        motorForward(MOTOR_RIGHT, moveSpeed);
    }

    /**
     * Move Right
     *
     */
    public void moveRight() {
        changeMoveState(MoveEnum.MOV_RIGHT);
        motorForward(MOTOR_LEFT, moveSpeed);
        motorForward(MOTOR_RIGHT, 0);
    }

    /**
     * Move Forward
     *
     */
    public void moveForward() {
        changeMoveState(MoveEnum.MOV_FORWARD);
        motorForward(MOTOR_LEFT, moveSpeed);
        motorForward(MOTOR_RIGHT, moveSpeed);
    }

    /**
     * Move Backward
     *
     */
    public void moveBackward() {
        changeMoveState(MoveEnum.MOV_BACK);
        motorReverse(MOTOR_LEFT, moveSpeed);
        motorReverse(MOTOR_RIGHT, moveSpeed);
    }

    /**
     *
     * function to check if robot can continue moving when taking evasive action
     * returns true if robot is not blocked when moving to avoid obstacles this
     * 'placeholder' version always returns true
     *
     * @return
     */
    public boolean checkMovement() {
        return true;
    }

    /**
     * check for obstacles while delaying the given duration in ms
     *
     * @param duration
     */
    public void movingDelay(long duration) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < duration) {
            // function in Look module checks for obstacle in direction of movement
            if (checkMovement() == false) {
                if (moveState != MoveEnum.MOV_ROTATE) // rotate is only valid movement
                {
                    //Serial.println("Stopping in moving Delay()");
                    //moveBrake();
                    moveStop();
                }
            }
        }
    }

    /**
     * *********** high level movement functions *************** moves in the
     * given direction at the current speed for the given duration in
     * milliseconds
     *
     * @param direction
     * @param duration
     */
    public void timedMove(MoveEnum direction, int duration) {
        //Serial.print("Timed move ");
        if (direction == MoveEnum.MOV_FORWARD) {
            //Serial.println("forward");
            moveForward();
        } else if (direction == MoveEnum.MOV_BACK) {
            //Serial.println("back");
            moveBackward();
        }
        //else
        //Serial.println("?");

        movingDelay(duration);
        moveStop();
    }

    /**
     *
     * functions to rotate the robot
     *
     * @param angle
     * @param speed
     * @return the time in milliseconds to turn the given angle at the given
     * speed
     */
    public long rotationAngleToTime(int angle, int speed) {
        long fullRotationTime; // time to rotate 360 degrees at given speed

        if (speed < MIN_SPEED) {
            return 0; // ignore speeds slower then the first table entry
        }
        angle = Math.abs(angle);

        if (speed >= 100) {
            fullRotationTime = rotationTime[NBR_SPEEDS - 1]; // the last entry is 100%
        } else {
            int index = (speed - MIN_SPEED) / SPEED_TABLE_INTERVAL; // index into speed and time tables
            int t0 = rotationTime[index];
            int t1 = rotationTime[index + 1];    // time of the next higher speed
            fullRotationTime = map(speed, speedTable[index], speedTable[index + 1], t0, t1);
            // Serial.print("index= ");  Serial.print(index); Serial.print(", t0 = ");  Serial.print(t0);  Serial.print(", t1 = ");  Serial.print(t1);
        }
        // Serial.print(" full rotation time = ");  Serial.println(fullRotationTime);
        long result = map(angle, 0, 360, 0, fullRotationTime);
        return result;
    }

    /**
     * rotate the robot from MIN_SPEED to 100% increasing by
     * SPEED_TABLE_INTERVAL
     *
     * @param direction
     * @param angle
     */
    public void calibrateRotationRate(int direction, int angle) {
        //Serial.print(locationString[direction]);
        //Serial.println(" calibration" );
        for (int speed = MIN_SPEED; speed <= 100; speed += SPEED_TABLE_INTERVAL) {

            //delay(1000);
            //blinkNumber(speed/10);
            if (direction == DIR_LEFT) {    // rotate left
                motorReverse(MOTOR_LEFT, speed);
                motorForward(MOTOR_RIGHT, speed);
            } else if (direction == DIR_RIGHT) {    // rotate right
                motorForward(MOTOR_LEFT, speed);
                motorReverse(MOTOR_RIGHT, speed);
            }
            //else
            //   Serial.println("Invalid direction");

            long time = rotationAngleToTime(angle, speed);

//    Serial.print(locationString[direction]);
//    Serial.print(": rotate ");
//    Serial.print(angle);
//    Serial.print(" degrees at speed ");
//    Serial.print(speed);
//    Serial.print(" for ");
//    Serial.print(time);
//    Serial.println("ms");
            I2CUtils.I2Cdelay((int) time);
            motorStop(MOTOR_LEFT);
            motorStop(MOTOR_RIGHT);
            I2CUtils.I2Cdelay(2000); // two second delay between speeds
        }
    }

    /**
     * Move Rotate
     *
     * @param angle
     */
    public void moveRotate(int angle) {
        changeMoveState(MoveEnum.MOV_ROTATE);
        //Serial.print("Rotating ");  Serial.println(angle);
        if (angle < 0) {
            //Serial.println(" (left)");
            motorReverse(MOTOR_LEFT, moveSpeed);
            motorForward(MOTOR_RIGHT, moveSpeed);
            angle = -angle;
        } else if (angle > 0) {
            //Serial.println(" (right)");
            motorForward(MOTOR_LEFT, moveSpeed);
            motorReverse(MOTOR_RIGHT, moveSpeed);
        }
        long ms = rotationAngleToTime(angle, moveSpeed);
        movingDelay(ms);
        //moveBrake();
        moveStop();
    }

}
