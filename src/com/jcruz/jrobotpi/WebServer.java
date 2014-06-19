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

import com.jcruz.jrobotpi.Devices.Sensors;
import com.oracle.json.Json;
import com.oracle.json.JsonBuilderFactory;
import com.oracle.json.JsonObjectBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * Simple WebServer for read values using REST
 *
 * @author Jose Cruz
 */
public class WebServer implements Runnable {

    private StreamConnectionNotifier scn = null;
    private Connection c;
    private volatile boolean shouldRun;

    public boolean isShouldRun() {
        return shouldRun;
    }

    /**
     * Start server. Creates http connection and starts thread.
     *
     * @return
     */
    public boolean start() {
        shouldRun = true;
        try {
            scn = (StreamConnectionNotifier) Connector.open("socket://:8000");
            new Thread(this).start();
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING, ex.getLocalizedMessage());
        }
        return true;
    }

    /**
     * Lite Rest Server
     */
    @Override
    public void run() {
        try {
            while (shouldRun) {
                StreamConnection sc = (StreamConnection) scn.acceptAndOpen();
                c = new Connection(sc);
                c.start();
                // service the connection in a separate thread 
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage());
        }
    }

    /**
     * Destroy. Cleanup everything.
     */
    public void stop() {
        shouldRun = false;
        try {
            if (scn != null) {
                scn.close();
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage());
        }
    }

    /**
     * Thread to handle client request.
     */
    class Connection extends Thread {

        private volatile StreamConnection client;

        public Connection(StreamConnection c) {
            client = c;
        }

        //Based on REST parameters return values in JSON format
        private JsonObjectBuilder restJsonValues(String str) {
            JsonObjectBuilder jsonvalue = null;
            JsonBuilderFactory factory = Json.createBuilderFactory(null);

            jsonvalue = factory.createObjectBuilder();

            if (str.contains(Sensors.AmbientLight.getName())) {
                jsonvalue.add(Sensors.AmbientLight.getName(), Sensors.AmbientLight.getValue());
            }
            if (str.contains(Sensors.Heading.getName())) {
                jsonvalue.add(Sensors.Heading.getName(), Sensors.Heading.getValue());
            }
            if (str.contains(Sensors.Humidity.getName())) {
                jsonvalue.add(Sensors.Humidity.getName(), Sensors.Humidity.getValue());
            }
            if (str.contains(Sensors.Pressure.getName())) {
                jsonvalue.add(Sensors.Pressure.getName(), Sensors.Pressure.getValue());
            }
            if (str.contains(Sensors.RPI_Temperature.getName())) {
                jsonvalue.add(Sensors.RPI_Temperature.getName(), Sensors.RPI_Temperature.getValue());
            }
            if (str.contains(Sensors.Temperature.getName())) {
                jsonvalue.add(Sensors.Temperature.getName(), Sensors.Temperature.getValue());
            }
            if (str.contains(Sensors.Latitude.getName())) {
                jsonvalue.add(Sensors.Latitude.getName(), Sensors.Latitude.getValue());
            }
            if (str.contains(Sensors.Longitude.getName())) {
                jsonvalue.add(Sensors.Longitude.getName(), Sensors.Longitude.getValue());
            }
            if (str.contains(Sensors.Altitude.getName())) {
                jsonvalue.add(Sensors.Altitude.getName(), Sensors.Altitude.getValue());
            }
            return jsonvalue;
        }

        //Send JSON response
        private void sendResponse(JsonObjectBuilder jsonvalue) {
            if (jsonvalue != null) {
                try (PrintStream out = new PrintStream(client.openOutputStream())) {
                    out.println("HTTP/1.0 200 OK");
                    out.println("Cache-Control: max-age=5");
                    out.println("Content-Type: application/json; charset=utf-8");
                    out.println("Server: JRobotPI");
                    // this blank line signals the end of the headers
                    out.println("");
                    out.println(jsonvalue.build().toString());
                    out.flush();
                } catch (IOException ex) {
                    Logger.getGlobal().log(Level.WARNING, ex.getLocalizedMessage());
                } finally {
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException ex) {
                            Logger.getGlobal().log(Level.WARNING, ex.getLocalizedMessage());
                        }
                    }
                }
            }
        }

        /**
         * Handles client request. REST Server
         */
        public void run() {
            String str;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    client.openInputStream()))) {
                str = in.readLine();
                System.out.println(str);

                if (str != null) {
                    if (str.contains(Sensors.Stop.getName())) {
                        shouldRun = false; // Stop WebServer Thread
                    } else {
                        sendResponse(restJsonValues(str));
                    }
                }

            } catch (IOException ex) {
                Logger.getGlobal().log(Level.WARNING, ex.getLocalizedMessage());
            }

        }

    }

}
