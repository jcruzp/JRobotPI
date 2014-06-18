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
package com.jcruz.jrobotpi.uart.driver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceConfig;
import jdk.dio.DeviceManager;
import jdk.dio.uart.UART;
import jdk.dio.uart.UARTConfig;
import static jdk.dio.uart.UARTConfig.DATABITS_8;
import static jdk.dio.uart.UARTConfig.FLOWCONTROL_NONE;
import static jdk.dio.uart.UARTConfig.PARITY_NONE;
import static jdk.dio.uart.UARTConfig.STOPBITS_1;
import jdk.dio.uart.UARTEvent;
import static jdk.dio.uart.UARTEvent.INPUT_DATA_AVAILABLE;
import jdk.dio.uart.UARTEventListener;

/**
 * Interface to EMIC-2 sound synthetizer
 *
 * @author jcruz
 */
public class EMIC2Device {

    private UARTConfig config;
    private UART uart = null;
    private static boolean waitForEmic = true;
    private String[] emic2Msgs=null;

    /**
     * Define a UART device config to interface to Emic-2
     *
     * @param emic2Msgs
     * @throws IOException
     */
    public EMIC2Device(String[] emic2Msgs) throws IOException {
        config = new UARTConfig(DeviceConfig.DEFAULT, DeviceConfig.DEFAULT, 9600,
                DATABITS_8, PARITY_NONE, STOPBITS_1, FLOWCONTROL_NONE);

        uart = (UART) DeviceManager.open(UART.class, config);
        uart.setEventListener(0, new MyUartListener());
        this.emic2Msgs=emic2Msgs;
    }
    
    /**
     *
     * @param msgnum
     */
    public void Msg(int msgnum) {
        write(emic2Msgs[msgnum]);
        Logger.getGlobal().log(Level.INFO,emic2Msgs[msgnum].substring(2));
    }
    
    /**
     *
     * @param num
     * @return
     */
    public String getMsg(int num) {
        return emic2Msgs[num];
    }

    /**
     *
     * Listener responses from Emic-2 commands Change waitForEmic if received :
     * char
     */
    class MyUartListener implements UARTEventListener {

        @Override
        public void eventDispatched(UARTEvent event) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(5);
            if (event.getID() == INPUT_DATA_AVAILABLE) {
                try {
                    uart.read(buffer);
//                    System.out.println(buffer);
//                    for (int i=0;i<10;i++)
//                        System.out.println(buffer.get(i));
                    waitForEmic = !((buffer.get(1) == 0x3A) || (buffer.get(2) == 0x3A));
                } catch (IOException ex) {
                    Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
                }
            }
        }
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
            uart.write(buffer);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        }
        waitResponse();
    }

    /**
     *
     * Wait for response from Emic-2. It respond to all commands with :
     * character, all ok or ? invalid command. MyUartListener change waitForEmic
     * see it.
     */
    private void waitResponse() {
        while (waitForEmic);
        waitForEmic = true;
    }

    /**
     * Free Uart resource
     *
     */
    public void close() {
        try {
            uart.close();
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        }
    }

}
