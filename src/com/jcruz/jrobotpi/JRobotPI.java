/*
 * JRobotPI Java Firmaware v2.0.0
 * State Machine to control Robot.
 * Use I2C bus for all sensors data and send data to Arduino Due with a I2C controller
 * to read all wii remote buttons and activate DC Motors.
 */
package com.jcruz.jrobotpi;

import com.jcruz.jrobotpi.gpio.driver.HCSR04Device;
import com.jcruz.jrobotpi.gpio.driver.PIRDevice;
import com.jcruz.jrobotpi.http.driver.XivelyDevice;
import com.jcruz.jrobotpi.i2c.I2CUtils;
import com.jcruz.jrobotpi.i2c.driver.BMP180Device;
import com.jcruz.jrobotpi.i2c.driver.BMP180Mode;
import com.jcruz.jrobotpi.i2c.driver.HTU21DDevice;
import com.jcruz.jrobotpi.i2c.driver.Move;
import com.jcruz.jrobotpi.i2c.driver.PCA9685Device;
import com.jcruz.jrobotpi.i2c.driver.TPA2016Device;
import com.jcruz.jrobotpi.i2c.driver.VCNL4000Device;
import com.jcruz.jrobotpi.i2c.driver.WiiRemote;
import com.jcruz.jrobotpi.log.LoggingHandler;
import com.jcruz.jrobotpi.uart.driver.EMIC2Device;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.midlet.MIDlet;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 *
 * @author jcruz
 */
public class JRobotPI extends MIDlet {

    private LoggingHandler loggerHandler = LoggingHandler.getInstance();
    private HCSR04Device hcsr04 = null;
    private final int trigger = 23;
    private final int echo = 17;

    private PIRDevice pir = null;
    private final int triggerPir = 25;
    private boolean pirActivate = true;

    private XivelyDevice xively = null;
    private BMP180Device bmp180 = null;
    private HTU21DDevice htu21d = null;
    private WiiRemote wiiremote = null;
    private Move move = null;
    private PCA9685Device servo = null;
    private VCNL4000Device vcnl4000 = null;
    private EMIC2Device emic2 = null;
    private TPA2016Device tpa = null;

    private Timer task = null;

    private String[] emic2Msgs = {
        "S Emic 2 Ok.", //0
        "S Inicializing devices.", //1
        "S HCSR04 Ok.", //2
        "S BMP180 Ok.", //3
        "S HTU21D Ok.", //4
        "S Wii Remote Ok.", //5
        "S DC Motors Ok.", //6
        "S Servo Ok.", //7
        "S VCNL4000 Ok.", //8
        "S Xively Ok.", //9    
        "S PIR and your listener Ok.", //10
        "S Task to read devices created.",//11
        "S Close devices comunication.", //12
        "S Menu activated.", //13
        "S Menu deactivated.", //14
        "S Prepare to move.", //15
        "S Stop move.", //16
        "S Prepare to detect objects.", //17
        "S Stop searching objects.", //18
        "S Scanning.", //19
        "S Object detected at ", //20
        "S No Object detected.", //21
        "S PIR Activated", //22
        "S PIR Deactivated" //23
    };

    //Activate option menu
    private boolean menuOn = false;
    //Control menu options
    private int opcMenu = 1;
    private int opcMenuSave = 0;
    private final int nroOpcsMenu = 5;
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

    private double pitch = 0.0;
    private double roll = 0.0;

    /**
     * Start App Midlet
     */
    @Override
    public void startApp() {
        try {
            loggerHandler.start();

            Logger.getGlobal().log(Level.INFO, "************************************");
            Logger.getGlobal().log(Level.INFO, "*     Starting JRobotPI v1.3.0...  *");
            Logger.getGlobal().log(Level.INFO, "************************************");

            emic2 = new EMIC2Device();
            I2CUtils.I2Cdelay(3000);
            emic2.write("");
            //emic2.write("W200");
            emic2.write("L0");
            emic2.write("N0");

            emic2.write(emic2Msgs[0]);
            emic2.write(emic2Msgs[1]);
            hcsr04 = new HCSR04Device(trigger, echo);
            emic2.write(emic2Msgs[2]);

            bmp180 = new BMP180Device();
            emic2.write(emic2Msgs[3]);

            htu21d = new HTU21DDevice();
            emic2.write(emic2Msgs[4]);

            wiiremote = new WiiRemote();
            emic2.write(emic2Msgs[5]);

            move = new Move();

            emic2.write(emic2Msgs[6]);
            servo = new PCA9685Device();
            servo.setPWMFreq(60);
            emic2.write(emic2Msgs[7]);

            vcnl4000 = new VCNL4000Device();
            emic2.write(emic2Msgs[8]);

            xively = new XivelyDevice();
            emic2.write(emic2Msgs[9]);

            //Inicialize PIR motion detect
            pir = new PIRDevice(triggerPir);
            pir.setListener(new JRobotPI.MyPinListener());
            emic2.write(emic2Msgs[10]);

            //Inicialize audio amp
            tpa = new TPA2016Device();
            //Only use one channel
            tpa.enableChannel(false, true);
            //Set audio volume to max value
            tpa.setGain((byte) 30);
            tpa.close();

            //This task send each 10 seconds all sensors data to Xively site
            //https://xively.com/feeds/918735601
            task = new Timer();
            task.schedule(readDevices, 0, 10000);
            emic2.write(emic2Msgs[11]);

            while (true) {
                I2CUtils.I2Cdelay(10);
                //Activate control options Home (menu) / B (move) / A (scan) / 1 (PIR)
                if (wiiremote.getButton2Press(WiiRemote.Button2Enum.HOME)) {
                    menuOn = !menuOn;
                    emic2.write(menuOn ? emic2Msgs[13] : emic2Msgs[14]);
                    opcMenu = 1;
                    opcMenuSave = 0;
                } else {
                    I2CUtils.I2Cdelay(10);
                    if (wiiremote.getButton1Press(WiiRemote.Button1Enum.B)) {
                        //if (menuMove) {
                        menuMove = !menuMove;
                        emic2.write(menuMove ? emic2Msgs[15] : emic2Msgs[16]);
                        move.moveStop();
                        pirActivate = !menuMove;

                    } else {
                        I2CUtils.I2Cdelay(10);
                        if (wiiremote.getButton1Press(WiiRemote.Button1Enum.A)) {
                            menuScan = !menuScan;
                            emic2.write(menuScan ? emic2Msgs[17] : emic2Msgs[18]);

                        } else {
                            I2CUtils.I2Cdelay(10);
                            if (wiiremote.getButton2Press(WiiRemote.Button2Enum.ONE)) {
                                pirActivate = !pirActivate;
                                emic2.write(pirActivate ? emic2Msgs[22] : emic2Msgs[23]);
                            }
                        }

                        //Process option Scan
                        if (menuScan) {
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
                                //emic2.write(emic2Msgs[19]);
                                //Read distance to object
                                distance = (int) hcsr04.pulse();
                                if ((distance > 0) && (distance < topCmts)) {
                                    emic2.write(emic2Msgs[20] + " " + distance + " centimeters.");
                                }
                                servoOffSave = servoOff;
                            }
                        }

                        //Process option Move
                        if (menuMove) {
                            //Inc Speed
                            if (wiiremote.getButton2Press(WiiRemote.Button2Enum.PLUS)) {
                                I2CUtils.I2Cdelay(10);
                                move.moveFaster(1);
                                //Dec Speed    
                            } else if (wiiremote.getButton2Press(WiiRemote.Button2Enum.MINUS)) {
                                I2CUtils.I2Cdelay(10);
                                move.moveSlower(1);
                            }
                            I2CUtils.I2Cdelay(100);
                            pitch = wiiremote.getWiimotePitch();
                            I2CUtils.I2Cdelay(100);
                            roll = wiiremote.getWiimoteRoll();
                            I2CUtils.I2Cdelay(100);
                            //Wiimote Pith control move backward and forward
                            if (pitch < 160) {
                                move.moveBackward();
                            } else if (pitch > 200) {
                                move.moveForward();
                            }

                            //Wiimote Roll control move left and right
                            if (roll < 140) {
                                move.moveLeft();
                            } else if (roll > 220) {
                                move.moveRight();
                            }
                        }
                        //Process option menu
                        if (menuOn) {
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
                                        emic2.write("SHumidity" + String.valueOf((int) htu21d.readHumidity()));
                                        break;
                                    case 3:
                                        emic2.write("SRaspberry PI Temperature" + String.valueOf((int) htu21d.readTemperature()));
                                        break;
                                    case 4:
                                        float[] result1 = bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION);

                                        emic2.write("STemperature" + String.valueOf((int) result1[0]));
                                        break;
                                    case 5:
                                        float[] result2 = bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION);
                                        emic2.write("SPressure" + String.valueOf((int) result2[1]));
                                        break;
                                }
                                opcMenuSave = opcMenu;
                            }
                        }
                        I2CUtils.I2Cdelay(10);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Check PIR Sensor for motion detect
    class MyPinListener implements PinListener {

        @Override
        public void valueChanged(PinEvent event) {
            if (pirActivate) {
                xively.updateValue("PIR_Sensor", event.getValue() ? "1" : "0");
                if (event.getValue()) {
                    xively.updateValue("PIR_Sensor", "0");
                }

            }
        }

    }

    private TimerTask readDevices = new TimerTask() {

        @Override
        public void run() {
            int celsius = 0, fahrenheit = 0, hectorPascal = 0, inchesMercury = 0;

            short amb = vcnl4000.readAmbientLight();
            xively.updateValue("Ambient_Light", String.valueOf(amb));

            xively.updateValue("Humidity", String.valueOf((int) htu21d.readHumidity()));

            xively.updateValue("RPI_Temperature", String.valueOf((int) htu21d.readTemperature()));

            float[] result = bmp180.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION);
            celsius = (int) result[0];
            xively.updateValue("Temperature", String.valueOf(celsius));
            //fahrenheit = BMP180Device.celsiusToFahrenheit(celsius);
            hectorPascal = (int) result[1];
            xively.updateValue("Pressure", String.valueOf(hectorPascal));
                //inchesMercury = BMP180Device.pascalToInchesMercury(hectorPascal);

            //System.out.format("Temperature: %.2f C, %.2f F\n", celsius, fahrenheit);
            //System.out.format("Pressure: %.2f hPa, %.2f inHg\n\n", hectorPascal, inchesMercury);
        }

    };

    /**
     *
     * @param unconditional
     */
    public void destroyApp(boolean unconditional) {
        emic2.write(emic2Msgs[12]);
        if (task != null) {
            task.cancel();
        }
        pir.removeListener();
        pir.close();
        vcnl4000.close();
        servo.close();
        move.close();
        wiiremote.close();
        htu21d.close();
        bmp180.close();
        hcsr04.close();
        emic2.close();
        if (loggerHandler != null) {
            loggerHandler.stop();
        }

    }
}
