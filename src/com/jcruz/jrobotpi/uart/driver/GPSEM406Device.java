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

import com.jcruz.jrobotpi.i2c.I2CUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;
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
public class GPSEM406Device {

    private UARTConfig config;
    private UART uart = null;
    private static String nmea = "";

    /**
     * Define a UART device config to interface to Emic-2
     *
     * @throws IOException
     */
    public GPSEM406Device() throws IOException {
        config = new UARTConfig(DeviceConfig.DEFAULT, DeviceConfig.DEFAULT, 9600,
                DATABITS_8, PARITY_NONE, STOPBITS_1, FLOWCONTROL_NONE);

        uart = (UART) DeviceManager.open(UART.class, config);
        GPS_Switch_Mode_To_NMEA();
        uart.setEventListener(0, new MyUartListener());
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private void GPS_Switch_Mode_To_NMEA() {
        //try {

        byte[] data = hexStringToByteArray("A0A20018810201010001010105010101000100010001000100012580013AB0B3");
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        buffer.clear();
        try {
            uart.write(buffer);
             I2CUtils.I2Cdelay(10000);
            System.out.println("Configurado");

//            int checkSum;
//            
//            byte[] magicHead = new byte[]{
//                
//                (byte)0xA0, (byte)0xA2,     // start sequence
//                (byte)0x00, (byte)0x18      // payload length 0x18 = 24
//            };
//            
//            int magicPayload[] = {
//                0x81,           // Message ID for "switch to NMEA"
// 
//                0x02,           // means "do not change NMEA debug message mode"
// 
//                // the next bunch of fields work in pairs, first the time between messages, "period", then whether or
//                // not to send checksum on that message
//                //
//                // "period" is the number of seconds between times that the GPS repeats the message
//                // zero period is "do not send message
//                // then 0x00 for checksum off or 0x01 for checksum on
//                //
// 
//                0x01,           // GGA period
//                0x01,           // checksum = on
// 
//                0x01,           // GLL period
//                0x01,           // checksum = on
// 
//                0x00,           // GSA period
//                0x01,           // checksum = on
// 
//                0x05,           // GSV period
//                0x01,           // checksum = on
// 
//                0x00,           // RMC period
//                0x01,           // checksum = on
// 
//                0x00,           // VTG period
//                0x01,           // checksum = on
// 
//                0x00,           // MSS period
//                0x01,           // checksum = on
// 
//                0x00,           // unused (future message)
//                0x01,           // unused (checksum = on)
// 
//                0x00,           // ZDA period
//                0x01,           // checksum = on
// 
//                0x00,           // unused (future message)
//                0x01,           // unused (checksum = on)
// 
//                // baud rate in 2 bytes (9600 = 0x2580; 19200 = 0x4B00; etc. )
//                0x25,           // baud rate high byte
//                0x80            // baud rate low byte
//            };
//            
//            byte[] magicTail= new byte[] {
//                (byte)0xB0, (byte)0xB3      // end sequence
//            };
//            
//// send 4 byte header
//            //for (int index = 0; index < 4; index++) {
//            ByteBuffer buffer = ByteBuffer.allocateDirect(magicHead.length);
//            buffer.put(magicHead);
//            buffer.clear();
//            uart.write(buffer);
//            //}
// 
//// send message body, calculating checksum as we go
//            ByteBuffer buffer2 = ByteBuffer.allocateDirect(magicPayload.length);
//            checkSum = 0;
//            for (int index = 0; index < 24; index++) {
//                buffer2.put(index,(byte)magicPayload[index]);
//                checkSum = checkSum + magicPayload[index];
//                //Serial1.print(byte(magicPayload[index]));
//            }
//            checkSum = checkSum & (0x7FFF);
//            buffer2.clear();
//            uart.write(buffer2);
//            
//            ByteBuffer buffer3 = ByteBuffer.allocateDirect(2);
//            buffer3.put((byte)(checkSum >> 8));
//            buffer3.put((byte) (checkSum & 0xff));
//            buffer3.clear();
//            uart.write(buffer3);
//            
//            // send the 2 byte checksum
//            //Serial1.print(byte(checkSum >> 8));
//            //Serial1.print(byte(checkSum & 0xff));
//            
//            ByteBuffer buffer4 = ByteBuffer.allocateDirect(magicTail.length);
//            buffer4.put(magicTail);
//            buffer4.clear();
//            uart.write(buffer4);
//            
//// send the 2 byte tail
////        for (int index = 0; index < 2; index++) {
////                Serial1.print(byte(magicTail[index]));
////        }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
// 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String formatTime(String time) {
        if (time != null) {
            return time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
        } else {
            return "";
        }
    }

    private String formatLat(String[] pos) {
        if ((pos[0] != null) && (pos[1] != null)) {
            return pos[0].substring(0, 2) + "* " + pos[0].substring(2, 4) + "' " + pos[0].substring(5, 9) + "\" " + pos[1];
        } else {
            return "";
        }
    }

    private String formatLong(String[] pos) {
        if ((pos[0] != null) && (pos[1] != null)) {
            return pos[0].substring(0, 3) + "* " + pos[0].substring(3, 5) + "' " + pos[0].substring(6, 10) + "\" " + pos[1];
        } else {
            return "";
        }
    }

    private String formatAlt(String[] alt) {
        if ((alt[0] != null) && (alt[1] != null)) {
            return alt[0] + " " + alt[1];
        } else {
            return "";
        }
    }

    private void process(String message) {
        try {
            StringTokenizer tokens = new StringTokenizer(message, ",");
        // pull off the first token and check if it is the message we want
            //$GPGGA,130612.255,,,,,0,00,,,M,0.0,M,,00
            String time = null;
            String longitude[] = {null, null};
            String latitude[] = {null, null};
            String altitude[] = {null, null};
            tokens.nextToken(); // $GPGGA position
            // Next token is the time
            time = tokens.nextToken();
            latitude[0] = tokens.nextToken();
            latitude[1] = tokens.nextToken();
            if ((latitude[0].equals("0")) || (latitude[1].equals("00"))) {
                System.out.println("Time: " + formatTime(time) + " GPS no enlazado al satélite...");
            } else {
                longitude[0] = tokens.nextToken();
                longitude[1] = tokens.nextToken();
                // Skip the next three tokens
                tokens.nextToken(); // Position indicator
                tokens.nextToken(); // Satellites used
                tokens.nextToken(); // Horizontal Dilution of precision
                altitude[0] = tokens.nextToken();
                altitude[1] = tokens.nextToken();

                System.out.println("Time: " + formatTime(time) + "\n"
                        + " Latitude: " + formatLat(latitude) + " Longitude: " + formatLong(longitude) + "\n"
                        + " Altitude: " + formatAlt(altitude) + "\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * Listener responses from Emic-2 commands Change waitForEmic if received :
     * char
     */
    class MyUartListener implements UARTEventListener {

        @Override
        public synchronized void eventDispatched(UARTEvent event) {
            if (event.getID() == INPUT_DATA_AVAILABLE) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(10);
                //StringBuilder result = new StringBuilder();
                try {
                    int nrocar = uart.read(buffer);
                    char c;

                    for (int i = 0; i < nrocar; i++) {
                        c = (char) buffer.get(i);
                        //result.append(c);
                        nmea=nmea.concat(String.valueOf(c));
                    }

                    if (nmea.contains("\r\n")) {
                        //System.out.println(nmea);
                        if (nmea.contains("$GPGGA")) {
                            process(nmea);
                        }
                        nmea = "";
                    }; 
                    //else {
                    //    nmea = nmea.concat(result.toString());
                    //}

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    /**
     * Free Uart resource
     *
     */
    public void close() {
        try {
            uart.close();
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING, ex.getLocalizedMessage());
        }
    }

}
