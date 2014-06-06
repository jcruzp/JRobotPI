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
package com.jcruz.jrobotpi;

import com.jcruz.jrobotpi.i2c.I2CUtils;
import com.jcruz.jrobotpi.i2c.driver.BMP180Mode;
import com.jcruz.jrobotpi.i2c.driver.WiiRemote;
import java.io.IOException;

/**
 *
 * @author eve0002474
 */
public class MenuWiiRemote extends Devices {

    //Activate option menu
    private boolean menuOn = false;
    //Control menu options
    private int opcMenu = 1;
    private int opcMenuSave = 0;
    private final int nroOpcsMenu = 6;
    //Activate option move
    private boolean menuMove = false;
    //Activate option scan
    private boolean menuScan = false;
    //Control servo movement
    private short servoOff = 100;
    private short servoOffSave = 1;
    //Save read distance to objects
    private int distance = 0;
    //Top distance in cmts to object, above that no objects
    private final int topCmts = 100;

    //Define move forward and backward with wii remote
    private double pitch = 0.0;
    //Define move left and right with nunchuk
    private double rollNunchuk = 0.0;
    //Deactivate menu
    private boolean stopMenu = false;

    /**
     * Initialize all devices and menu for them
     *
     * @throws IOException
     */
    public MenuWiiRemote() throws IOException {
        super();
    }

    /**
     * Start Menu for WiiRemote keys
     */
    public void Start() {
        while (!stopMenu) {
            I2CUtils.I2Cdelay(10);
            //Menu Options Home (menu) / B (move) / A (scan) / 1 (PIR)
            keyMenu();
            //Process all menu options
            processMenu();
        }
    }

    /**
     * Stop Menu
     */
    public void Stop() {
        //Stop menu
        stopMenu = true;
        //Close all devices
        Close();
    }

    //Activate control options Home (menu) / B (move) / A (scan) / 1 (PIR)
    private void keyMenu() {
        I2CUtils.I2Cdelay(10);
        if (wiiremote.getButton2Press(WiiRemote.Button2Enum.HOME)) {
            menuOn = !menuOn;
            emic2.Msg(menuOn ? 13 : 14);
            opcMenu = 1;
            opcMenuSave = 0;
        } else if (wiiremote.getButton1Press(WiiRemote.Button1Enum.B)) {
            menuMove = !menuMove;
            emic2.Msg(menuMove ? 15 : 16);
            move.moveStop(); //Stop motors
            move.moveSetSpeed(0); //Inicialize velocity
            setPirActivate(!menuMove); //if menumove is active deactivate PIR
        } else if (wiiremote.getButton1Press(WiiRemote.Button1Enum.A)) {
            menuScan = !menuScan;
            emic2.Msg(menuScan ? 17 : 18);
        } else if (wiiremote.getButton2Press(WiiRemote.Button2Enum.ONE)) {
            setPirActivate(!isPirActivate());
            emic2.Msg(isPirActivate() ? 22 : 23);
        }

    }

    private void scanMenu() {
        //Move servo left
        if (wiiremote.getButton1Press(WiiRemote.Button1Enum.LEFT)) {
            servoOff = (short) Math.max(100, servoOff - 50);
            //MOve servo right    
        } else if (wiiremote.getButton1Press(WiiRemote.Button1Enum.RIGHT)) {
            servoOff = (short) Math.min(600, servoOff + 50);
        }
        //If move to new position
        if (servoOff != servoOffSave) {

            servo.setPWM((byte) 0, (short) 0, servoOff);
            //Emic2Msg(emic2Msgs[19]);
            //Read distance to object
            distance = (int) hcsr04.pulse();
            if ((distance > 0) && (distance < topCmts)) {
                emic2.write(emic2.getMsg(20) + " " + distance + " centimeters.");
            }
            servoOffSave = servoOff;
        }

    }

    private void moveMenu() {
        //Process option Move
        //Inc Speed
        if (wiiremote.getButton2Press(WiiRemote.Button2Enum.PLUS)) {
            I2CUtils.I2Cdelay(10);
            move.moveFaster(1);
            //Dec Speed    
        } else if (wiiremote.getButton2Press(WiiRemote.Button2Enum.MINUS)) {
            I2CUtils.I2Cdelay(10);
            move.moveSlower(1);
        }
        I2CUtils.I2Cdelay(10);
        pitch = wiiremote.getWiimotePitch();
//                            I2CUtils.I2Cdelay(50);
//                            roll = wiiremote.getWiimoteRoll();
        I2CUtils.I2Cdelay(10);
//                            pitchNunchuk = wiiremote.getNunchuckPitch();
//                            I2CUtils.I2Cdelay(50);
        rollNunchuk = wiiremote.getNunchuckRoll();
        //I2CUtils.I2Cdelay(10);

        //Wiimote Pith control move backward and forward
        if (pitch < 160) {
            move.moveBackward();
        //Wiimote Nunchuk Roll control move left and right    
        } else if (pitch > 200) {
            if (rollNunchuk < 160) {
                move.moveLeft();
            } else if (rollNunchuk > 220) {
                move.moveRight();
            } else
                move.moveForward();
            
            
        }
    }

    private void homeMenu() {
        //Process option menu
        if (wiiremote.getButton1Press(WiiRemote.Button1Enum.UP)) {
            opcMenu = Math.max(1, opcMenu - 1);
        } else if (wiiremote.getButton1Press(WiiRemote.Button1Enum.DOWN)) {
            opcMenu = Math.min(nroOpcsMenu, opcMenu + 1);
        }
        //Emic-2 read for each option its value
        if (opcMenu != opcMenuSave) {
            switch (opcMenu) {
                case 1:
                    short amb = vcnl4000.readAmbientLight();
                    emic2.write("SAmbient Light" + String.valueOf(amb));
                    break;
                case 2:
                    float[] result2 = bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION);
                    emic2.write("SPressure" + String.valueOf((int) result2[1]));
                    break;
                case 3:
                    emic2.write("SHumidity" + String.valueOf((int) htu21d.readHumidity()));
                    break;
                case 4:
                    emic2.write("SRaspberry PI Temperature" + String.valueOf((int) htu21d.readTemperature()));
                    break;
                case 5:
                    float[] result1 = bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION);
                    emic2.write("STemperature" + String.valueOf((int) result1[0]));
                    break;
                case 6:
                    emic2.write("SHeading" + String.valueOf((int) hmc.calculateHeading()) + " Degrees");
                    break;
            }
            opcMenuSave = opcMenu;
        }

    }

    //Process option Scan
    private void processMenu() {
        if (menuScan) {
            scanMenu();
        }
        if (menuMove) {
            moveMenu();
        }
        if (menuOn) {
            homeMenu();
        }
    }

}
