/*
 * 
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

    /**
     * Define a UART device config to interface to Emic-2
     *
     * @throws IOException
     */
    public EMIC2Device() throws IOException {
        config = new UARTConfig(DeviceConfig.DEFAULT, DeviceConfig.DEFAULT, 9600,
                DATABITS_8, PARITY_NONE, STOPBITS_1, FLOWCONTROL_NONE);

        uart = (UART) DeviceManager.open(UART.class, config);
        uart.setEventListener(0, new MyUartListener());

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
                    Logger.getLogger(EMIC2Device.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
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
            Logger.getLogger(EMIC2Device.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
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
            Logger.getLogger(EMIC2Device.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
    }

}
