/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jcruz.jrobotpi;

import com.jcruz.jrobotpi.gpio.driver.HCSR04Device;
import com.jcruz.jrobotpi.gpio.driver.PIRDevice;
import com.jcruz.jrobotpi.http.driver.XivelyDevice;
import com.jcruz.jrobotpi.i2c.I2CUtils;
import com.jcruz.jrobotpi.i2c.driver.BMP180Device;
import com.jcruz.jrobotpi.i2c.driver.BMP180Mode;
import com.jcruz.jrobotpi.i2c.driver.HMC5883LDevice;
import com.jcruz.jrobotpi.i2c.driver.HTU21DDevice;
import com.jcruz.jrobotpi.i2c.driver.Move;
import com.jcruz.jrobotpi.i2c.driver.PCA9685Device;
import com.jcruz.jrobotpi.i2c.driver.TPA2016Device;
import com.jcruz.jrobotpi.i2c.driver.VCNL4000Device;
import com.jcruz.jrobotpi.i2c.driver.WiiRemote;
import com.jcruz.jrobotpi.uart.driver.EMIC2Device;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 *
 * @author eve0002474
 */
public class Devices {
    
    public HCSR04Device hcsr04 = null;
    private final int trigger = 23;
    private final int echo = 17;

    public PIRDevice pir = null;
    private final int triggerPir = 25;
    private boolean pirActivate = true;

    public XivelyDevice xively = null;
    public BMP180Device bmp180 = null;
    public HTU21DDevice htu21d = null;
    public WiiRemote wiiremote = null;
    public Move move = null;
    public PCA9685Device servo = null;
    public VCNL4000Device vcnl4000 = null;
    public EMIC2Device emic2 = null;
    public TPA2016Device tpa = null;
    public HMC5883LDevice hmc = null;

    protected Timer task = null;

    private final String[] emic2Msgs = {
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
        "S PIR Deactivated", //23
        "S HMC5883L Ok. //24"    
    };

    public Devices() throws IOException {
        emic2 = new EMIC2Device(emic2Msgs);
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
        
        hmc = new HMC5883LDevice();
        hmc.SetScale(1.3F);
        hmc.SetMeasurementMode(HMC5883LDevice.Measurement.Continuous);
        emic2.write(emic2Msgs[24]);

        //Inicialize PIR motion detect
        pir = new PIRDevice(triggerPir);
        pir.setListener(new MyPinListener());
        emic2.write(emic2Msgs[10]);

//            //Inicialize audio amp
//            tpa = new TPA2016Device();
//            //Only use one channel
//            tpa.enableChannel(false, true);
//            //Set audio volume to max value
//            tpa.setGain((byte) 30);
//            tpa.close();
        
        //This task send each 10 seconds all sensors data to Xively site
        //https://xively.com/feeds/918735601
        task = new Timer();
        task.schedule(readDevices, 0, 10000);
        emic2.write(emic2Msgs[11]);
    }

    
    public void setPirActivate(boolean pirActivate) {
        this.pirActivate = pirActivate;
    }

    public boolean isPirActivate() {
        return pirActivate;
    }
    
    public void Close(){
        emic2.Msg(12);
        if (task != null) {
            task.cancel();
        }
        pir.removeListener();
        pir.close();
        hmc.close();
        vcnl4000.close();
        servo.close();
        move.close();
        wiiremote.close();
        htu21d.close();
        bmp180.close();
        hcsr04.close();
        emic2.close();
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

    private final TimerTask readDevices = new TimerTask() {

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
            short heading=(short) hmc.calculateHeading();
            xively.updateValue("Heading", String.valueOf(heading));

            //System.out.format("Temperature: %.2f C, %.2f F\n", celsius, fahrenheit);
            //System.out.format("Pressure: %.2f hPa, %.2f inHg\n\n", hectorPascal, inchesMercury);
        }

    };
    
}
