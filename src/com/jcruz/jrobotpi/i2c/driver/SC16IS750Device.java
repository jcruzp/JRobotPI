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

import com.jcruz.jrobotpi.i2c.I2CRpi;
import com.jcruz.jrobotpi.i2c.I2CUtils;
import com.jcruz.jrobotpi.i2c.SC16IS750;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface to SC16IS750 I2C/SPI to UART I use I2C interface to connect Emic-2
 * to I2C 0x90 address and free a UART port to use with GPS.
 *
 * @author Jose Cruz <joseacruzp@gmail.com>
 */
public class SC16IS750Device extends I2CRpi {

    private static final int SC16IS750_write = 0x48;

    /**
     *
     * @throws IOException
     */
    public SC16IS750Device() throws IOException {
        super(SC16IS750_write);
        configUARTregs();
    }

    private void configUARTregs() {
        //Config UART
        Logger.getGlobal().log(Level.INFO, "Config UART of SC16IS750");

        //Line Control Register: Enable Writing DLH & DLL
        //& set no Parity, 1 stop bit, and 8 bit word length
        SC16IS750.LCR.write(device, (byte) 0b10000011);

        //Division registers DLL & DLH
        // Write '96' to get 9600 baud rate
        //Assumes you have the version with the ~14MHz crystal
        // (16x9600 = 153600 = 14.7456Mhz/96)
        SC16IS750.DLL.write(device, (byte) 96);
        SC16IS750.DLH.write(device, (byte) 00);

        //Line Control Register: Disnable Writing DLH & DLL
        //Same setup 
        SC16IS750.LCR.write(device, (byte) 0b00000011);

        //Modem Control Register
        //Normal Operating Mode
        SC16IS750.MCR.write(device, (byte) 0b00000000);

        //FIFO Control Register: Enable the FIFO and no other features
        SC16IS750.FCR.write(device, (byte) 0b00000111);

        I2CUtils.I2Cdelay(2000);

        Logger.getGlobal().log(Level.INFO, "FIFO Control Register: " + SC16IS750.FCR.read(device));
        Logger.getGlobal().log(Level.INFO, "Line Control Register: " + SC16IS750.LCR.read(device));
        Logger.getGlobal().log(Level.INFO, "Modem Control Register: " + SC16IS750.MCR.read(device));

    }

    /**
     *
     * @param cad String to send to Emic-2
     */
    public void write(String cad) {
        cad = cad.concat("\r\n");
        ByteBuffer buffer = ByteBuffer.allocateDirect(cad.length());
        buffer.put(cad.getBytes());
        buffer.clear();
        try {
            device.write(SC16IS750.XHR.cmd, 1, buffer);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING, ex.getLocalizedMessage());
        }

//        int work = 0;
//        while (work != 2) {
//            work = SC16IS750.RXLVL.read(device);
//        }
        while (SC16IS750.XHR.read(device) != 0x3A);
    }
}
