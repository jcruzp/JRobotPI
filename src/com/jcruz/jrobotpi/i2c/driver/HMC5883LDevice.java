/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jcruz.jrobotpi.i2c.driver;

import com.jcruz.jrobotpi.i2c.HMC5883L;
import com.jcruz.jrobotpi.i2c.I2CRpi;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jcruz
 */
public class HMC5883LDevice extends I2CRpi {

    public enum Measurement {

        Continuous(0x00),
        SingleShot(0x01),
        Idle(0x03);
        /**
         * Read Measurement type
         */
        public byte value;

        Measurement(int value) {
            this.value = (byte) value;
        }

    }

    public class MagnetometerScaled {

        float XAxis;
        float YAxis;
        float ZAxis;
    };

    public class MagnetometerRaw {

        int XAxis;
        int YAxis;
        int ZAxis;
    };

    private static final int HMC5883L_ADDRESS = 0x1E;
    private float m_Scale = 0.0F;
    private int ErrorCode_1_Num=1;

    public HMC5883LDevice() throws IOException {
        super(HMC5883L_ADDRESS);
        m_Scale = 1;
    }

    public MagnetometerRaw ReadRawAxis() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(6);
        try {
            device.read(HMC5883L.DataRegBegin.cmd, 1, buffer);
        } catch (IOException ex) {
            Logger.getLogger(BMP180Device.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        // Read each of the pairs of data as a signed short
        buffer.rewind();
        byte[] data = new byte[6];
        buffer.get(data);
                
        MagnetometerRaw raw = new MagnetometerRaw();
        raw.XAxis = (data[0] << 8) | data[1];
        raw.ZAxis = (data[2] << 8) | data[3];
        raw.YAxis = (data[4] << 8) | data[5];
        return raw;
    }

    public MagnetometerScaled ReadScaledAxis() {
        MagnetometerRaw raw = ReadRawAxis();
        MagnetometerScaled scaled = new MagnetometerScaled();
        scaled.XAxis = raw.XAxis * m_Scale;
        scaled.ZAxis = raw.ZAxis * m_Scale;
        scaled.YAxis = raw.YAxis * m_Scale;
        return scaled;
    }

    public int SetScale(float gauss) {
        int regValue = 0x00;
        if (gauss == 0.88) {
            regValue = 0x00;
            m_Scale = 0.73F;
        } else if (gauss == 1.3) {
            regValue = 0x01;
            m_Scale = 0.92F;
        } else if (gauss == 1.9) {
            regValue = 0x02;
            m_Scale = 1.22F;
        } else if (gauss == 2.5) {
            regValue = 0x03;
            m_Scale = 1.52F;
        } else if (gauss == 4.0) {
            regValue = 0x04;
            m_Scale = 2.27F;
        } else if (gauss == 4.7) {
            regValue = 0x05;
            m_Scale = 2.56F;
        } else if (gauss == 5.6) {
            regValue = 0x06;
            m_Scale = 3.03F;
        } else if (gauss == 8.1) {
            regValue = 0x07;
            m_Scale = 4.35F;
        } else {
            return ErrorCode_1_Num;
        }
    
        // Setting is in the top 3 bits of the register.
        regValue = regValue << 5;
        HMC5883L.ConfigRegB.write(device, (byte) regValue);
        return 0;
    }

    public void SetMeasurementMode(Measurement mode) {
        HMC5883L.ModeReg.write(device, mode.value);
       // Write(ModeRegister, mode);
    }

//    public void HMC5883L::Write(int address, int data) {
//        Wire.beginTransmission(HMC5883L_Address);
//        Wire.write(address);
//        Wire.write(data);
//        Wire.endTransmission();
//    }
//
//    uint8_t * HMC5883L::Read(int address, int length) {
//        Wire.beginTransmission(HMC5883L_Address);
//        Wire.write(address);
//        Wire.endTransmission();
//
//        Wire.beginTransmission(HMC5883L_Address);
//        Wire.requestFrom(HMC5883L_Address, length);
//
//        uint8_t buffer[length];
// if (Wire.available() == length) {
//            for (uint8_t i = 0; i < length; i++) {
//                buffer[i] = Wire.read();
//            }
//        }
//        Wire.endTransmission();
//
//        return buffer;
//    }

//    char * HMC5883L::GetErrorText(int errorCode) {
//        if (ErrorCode_1_Num == 1) {
//            return ErrorCode_1;
//        }
//
//        return "Error not defined.";
//    }

}