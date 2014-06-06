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
package com.jcruz.jrobotpi.test;

import com.jcruz.jrobotpi.gpio.driver.HCSR04Device;
import com.jcruz.jrobotpi.i2c.driver.WiiRemote;
import com.jcruz.jrobotpi.i2c.driver.WiiRemote.Button2Enum;
import com.jcruz.jrobotpi.i2c.driver.BMP180Device;
import com.jcruz.jrobotpi.i2c.driver.BMP180Mode;
import com.jcruz.jrobotpi.gpio.driver.PIRDevice;
import com.jcruz.jrobotpi.http.driver.XivelyDevice;
import com.jcruz.jrobotpi.i2c.driver.PCA9685Device;
import com.jcruz.jrobotpi.i2c.driver.HTU21DDevice;
import com.jcruz.jrobotpi.i2c.driver.TPA2016Device;
import com.jcruz.jrobotpi.i2c.driver.VCNL4000Device;
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
 * @author Jose Cuz
 */
public class TestJRobotPI extends MIDlet {

    WiiRemote wiiremote=null;
    
    private void TestWiiRemote() {
        try {
            System.out.println("I2C Sender, parmeter [Paket size] [Loop count]");
            System.out.println("get bus 1");
            // get I2C bus instance
            //final I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);

//            I2CDeviceConfig config = new I2CDeviceConfig(DeviceConfig.DEFAULT,
//                    0x04,
//                    DeviceConfig.DEFAULT,
//                    DeviceConfig.DEFAULT);
//            I2CDevice arduino = (I2CDevice) DeviceManager.open(I2CDevice.class, config);
            System.out.println("get device with id 4");
            //I2CDevice arduino = bus.getDevice(0x04);
//		for (int i = 0; i < buffer.length; i++) {
//			buffer[i] = (byte) i;
//		}

//		try {
//			System.out.println(arduino.read());
//		} catch (Exception e) {
//			//bus.close();			
//			e.printStackTrace();
//		}
            wiiremote = new WiiRemote();

//            MotorsDC.SET_SPEED.write(wiiremote.arduino, 255);
//            MotorsDC.CONFIG_MOTORS.write(wiiremote.arduino, 0xAF);
//
//            Thread.sleep(1000);
//
//            MotorsDC.SET_SPEED.write(wiiremote.arduino, 50);
//            MotorsDC.CONFIG_MOTORS.write(wiiremote.arduino, 0x8F);
//
//            Thread.sleep(1000);
//
//            MotorsDC.SET_SPEED.write(wiiremote.arduino, 100);
//            MotorsDC.CONFIG_MOTORS.write(wiiremote.arduino, 0xCF);
//
//            Thread.sleep(1000);
//
//            ByteBuffer txBuf = ByteBuffer.wrap(new byte[4]);
//
//            txBuf.put(0, (byte) 49);
//            txBuf.put(1, (byte) 255);
//            txBuf.put(2, (byte) 48);
//            txBuf.put(3, (byte) 0xAF);
//            wiiremote.arduino.write(txBuf);
//
//            Thread.sleep(1000);
//
//            //txBuf = ByteBuffer.wrap(new byte[4]);
//            txBuf.clear();
//            txBuf.put(0, (byte) 49);
//            txBuf.put(1, (byte) 50);
//            txBuf.put(2, (byte) 48);
//            txBuf.put(3, (byte) 0x8F);
//            wiiremote.arduino.write(txBuf);
//
//            Thread.sleep(1000);
//
//            txBuf.clear();
//            txBuf.put(0, (byte) 49);
//            txBuf.put(1, (byte) 100);
//            txBuf.put(2, (byte) 48);
//            txBuf.put(3, (byte) 0xCF);
//            wiiremote.arduino.write(txBuf);
//
//            Thread.sleep(1000);
//
//            System.out.println("Rumble");
//            ByteBuffer txBuf2 = ByteBuffer.allocateDirect(2);
//            //txBuf = ByteBuffer.wrap(new byte[2]);
//
//            txBuf2.put(0, (byte) 32);
//            txBuf2.put(1, (byte) 16);
//            wiiremote.arduino.write(txBuf2);
//
//            Thread.sleep(1000);
//
//            System.out.println("Led 4");
//            txBuf2.clear();
//            txBuf2.put(0, (byte) 32);
//            txBuf2.put(1, (byte) 4);
//            wiiremote.arduino.write(txBuf2);
//
//            Thread.sleep(1000);

            System.out.println("Esperando para leer...");

//		Thread.sleep(100);
//		try {
//			System.out.println(arduino.read());
//		} catch (Exception e) {
//			//bus.close();			
//			e.printStackTrace();
//		}
            //int accelX;
            //byte[] accel = new byte[2];
            //Thread.sleep(1000);
            //byte[] accelData = new byte[1];
            // ByteBuffer rxBuf = ByteBuffer.allocateDirect(1);
            //  txBuf = ByteBuffer.wrap(new byte[2]);
            while (true) {
//                rxBuf.clear();
//                arduino.read((byte) 10, 1, rxBuf);
//                accelX = ((rxBuf.get(0) << 8) & 0xFF00);
//                rxBuf.clear();
//                arduino.read((byte) 11, 1, rxBuf);
//                accelX+= (rxBuf.get(0) & 0xFF);

                //
                //System.out.println("Acelerometro X:" + wiiremote.getWiimotePitch());
                //System.out.println("Hat X:" + wiiremote.getAnalogHat(WiiRemote.HatEnum.HatX));
                //System.out.println("Hat Y:" + wiiremote.getAnalogHat(WiiRemote.HatEnum.HatY));
                //rxBuf.clear();
                //arduino.read((byte) 2, 1, rxBuf);
                //int val1=Wii.READ_BUTTONS1.read(arduino);
                
                if (wiiremote.getButton2Press(Button2Enum.ONE)) 
                    wiiremote.setLedOn(WiiRemote.LEDEnum.LED1);
                else
                    
                if (wiiremote.getButton2Press(Button2Enum.TWO)) 
                    wiiremote.setLedOn(WiiRemote.LEDEnum.LED2); 
                else
                 
                if (wiiremote.getButton2Press(Button2Enum.MINUS)) 
                    wiiremote.setLedOn(WiiRemote.LEDEnum.LED3);
                else
                 
                if (wiiremote.getButton2Press(Button2Enum.HOME)) 
                    wiiremote.setLedOn(WiiRemote.LEDEnum.LED4);
                else
//                } else {
                    wiiremote.setAllOff();
//                }

//                System.out.println("Gyroscope P:" + wiiremote.getPitch());
//                System.out.println("Gyroscope R:" + wiiremote.getRoll());
//                System.out.println("Gyroscope Y:" + wiiremote.getYaw());
                System.out.println("Pitch P:" + wiiremote.getWiimotePitch());
                System.out.println("Roll R:" + wiiremote.getWiimoteRoll());
//                System.out.println("Pitch P:" + wiiremote.getNunchuckPitch());
//                Thread.sleep(10);
//                System.out.println("Roll R:" + wiiremote.getNunchuckRoll());

                // Get the new position
                //int pos = rxBuf.position(); // 6
                //System.out.println("Posicion:" + rxBuf.get(0));
                //System.out.println(rxBuf.get(0));
//                if ((val1 <= 16) || (wiiremote.getButton2Press(Button2Enum.ONE))) {
//                    System.out.println(val1);
//                    txBuf.clear();
//                    txBuf.put(0, (byte) 32);
//                    txBuf.put(1, (byte) val1);
//                    arduino.write(txBuf);
//                    //Thread.sleep(1);
//                }
                Thread.sleep(10);
            }

//		for (int i = 0; i < loops; i++) {
//			System.out.println("send buffer now");
//
//			long l = System.currentTimeMillis();
//			// write(int address, byte[] buffer, int offset, int size) throws
//			// IOException
//			arduino.write(buffer, 0, buffer.length);
//			long needed = System.currentTimeMillis() - l;
//			// arduino.write((byte)65);
//
//			System.out.println("done in " + needed + "ms");
//		}
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    PCA9685Device servo = null;

    private void TestServo() {
        try {

//           for (short i = 150; i < 600; i++) {
//                servo.setPWM((byte) 0, (short) 0, i);
//            }
//
//            Thread.sleep(1000);
//
//            for (short i = 600; i < 150; i--) {
//               servo.setPWM((byte) 0, (short) 0, i);
//            }
            while (true){
            servo.setPWM((byte) 8, (short) 0, (short) 100);
            Thread.sleep(1000);
            servo.setPWM((byte) 8, (short) 0, (short) 350);
            Thread.sleep(1000);
            servo.setPWM((byte) 8, (short) 0, (short) 600);
            Thread.sleep(2000);
            }
            //           servo.close();
        } catch (InterruptedException ex) {
            Logger.getLogger(TestJRobotPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private BMP180Device bmp180Device;
    private Timer task = null;

    private void TestBMP180() {
        try {
            // For full debug messages, uncomment these lines
            //Handler handler = new ConsoleHandler();
            //handler.setLevel(Level.FINE);
            //Logger.getGlobal().addHandler(handler);
            //Logger.getGlobal().setLevel(Level.FINE);
            bmp180Device = new BMP180Device();
            // Start the task and run it every five seconds
            task = new Timer();
            task.schedule(report, 0, 5000);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private TimerTask report = new TimerTask() {

        @Override
        public void run() {
            float celsius = 0, fahrenheit = 0, hectorPascal = 0, inchesMercury = 0;
            float[] result = bmp180Device.getTemperaturePressure(BMP180Mode.ULTRA_HIGH_RESOLUTION);
            celsius = result[0];
            fahrenheit = BMP180Device.celsiusToFahrenheit(celsius);
            hectorPascal = result[1];
            inchesMercury = BMP180Device.pascalToInchesMercury(hectorPascal);
            System.out.format("Temperature: %.2f C, %.2f F\n", celsius, fahrenheit);
            System.out.format("Pressure: %.2f hPa, %.2f inHg\n\n", hectorPascal, inchesMercury);
        }

    };

    HCSR04Device sensor;
    //PingDevice sensor;

    private void TestSoundSensor() {
        try {
            //sensor = new PingDevice();
            sensor = new HCSR04Device(23,17);
            while (true) {
                System.out.println(sensor.pulse());
                Thread.sleep(4000);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    PIRDevice pir;

    private void TestPIR() {
        pir = new PIRDevice(25);
        pir.setListener(new MyPinListener());
    }

    class MyPinListener implements PinListener {

        @Override
        public void valueChanged(PinEvent event) {
            XivelyDevice xdev = new XivelyDevice();
            xdev.updateValue("PIR_Sensor", event.getValue()? "1": "0");
            if (event.getValue()) 
               xdev.updateValue("PIR_Sensor", "0");
            System.out.println("Pin listener for pin 7 has been called!");
            System.out.println("Pin 7 is now " + event.getValue());

        }

    }

    VCNL4000Device vcnl;

    private void TestVCNL() throws IOException, InterruptedException {
        vcnl = new VCNL4000Device();
        short amb = vcnl.readAmbientLight();
        Thread.sleep(2000);
        short prox = vcnl.readProximity();
        System.out.println("Ambiente: " + amb);
        System.out.println("Proximidad: " + prox);

        vcnl.setSignalFreq(VCNL4000Device.Freq.F390K625);
        System.out.println("Feq: " + vcnl.getSignalFreq());
        //vcnl.close();
    }

    HTU21DDevice htu21d;

    private void TestHTU21D() throws IOException, InterruptedException {

        System.out.println("Temp:" + htu21d.readTemperature());
        System.out.println("Humity:" + htu21d.readHumidity());
        Thread.sleep(2000);
    }

    EMIC2Device emic2;
    TPA2016Device tpa;

    private void TestEMIC2() throws IOException, InterruptedException {
        emic2 = new EMIC2Device(null);
       
        Thread.sleep(3000);
        emic2.write("");
        emic2.write("W200");
        emic2.write("L0");
        emic2.write("N0");
        //emic2.waitResponse();
        emic2.write("S EMIC 2 begin");
        //emic2.waitResponse();
        emic2.write("S EMIC 2 Is inicializing");
        //emic2.write("D1");
        //emic2.write("D2");
        //emic2.waitResponse();
        //System.out.println("Fin...");

    }

    /**
     *
     */
    @Override
    public void startApp() {
        System.out.println("Pruebas v3.0");
        try {
//             tpa=new TPA2016Device();
//             tpa.enableChannel(false, true);
//          tpa.setGain((byte)-20);
//          tpa.close();
            
            //TestEMIC2();
           // TestPIR();
            servo = new PCA9685Device();
            servo.setPWMFreq(60);
            //TestSoundSensor();
            while (true)
                    TestServo();
            //TestWiiRemote();
            
           //  htu21d=new HTU21DDevice();
            // while(true)
            // TestHTU21D();
            //TestBMP180();
//            TestVCNL();
//            
//            while (true){
//            short amb=vcnl.readAmbientLight();
//        Thread.sleep(2000);
//        short prox=vcnl.readProximity();
//        System.out.println("Ambiente: "+ amb);
//        System.out.println("Proximidad: "+prox);
//            }
            //  try {
            //TestPIR();
            // TestWiiRemote();
            //servo = new PCA9685Device();

            //servo.setPWMFreq(60);
            // } catch (IOException ex) {
            //      Logger.getLogger(TestJRobotPI.class.getName()).log(Level.SEVERE, null, ex);
            //  } catch (InterruptedException ex) {
            //      Logger.getLogger(TestJRobotPI.class.getName()).log(Level.SEVERE, null, ex);
            //  }
            //while (true)
            //TestServo();
            // TestBMP180();
            //TestSoundSensor();
        } catch (IOException ex) {
            Logger.getLogger(TestJRobotPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    public void pauseApp() {
    }

    /**
     *
     * @param unconditional
     */
    public void destroyApp(boolean unconditional) {

        pir.close();
        wiiremote.close();
        emic2.close();
    }
}
