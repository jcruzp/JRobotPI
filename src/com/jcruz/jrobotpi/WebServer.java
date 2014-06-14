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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * MIDlet working as simple personal WebServer. Currently this serves simple
 * HTTP GET operation.
 *
 * @author Kumar Mettu
 * @version 0.61
 */
public class WebServer implements Runnable {

    private StreamConnectionNotifier scn = null;
    private Connection c;
    private volatile boolean shouldRun;

    /**
     * Default constructor.
     */
    WebServer() {

    }
    
    /**
     * Start server. Creates datagram connection and starts thread.
     * @return 
     */
    public boolean start() {
        shouldRun=true;
        try {
            scn = (StreamConnectionNotifier) Connector.open("socket://:8000");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * This will be invoked when we start the MIDlet
     */
    public void run() {
        try {
            while (shouldRun) {
                StreamConnection sc = (StreamConnection) scn.acceptAndOpen();
                c = new Connection(sc);
                c.start();
                // service the connection in a separate thread 
            }
        } catch (IOException e) {
            e.printStackTrace();
            //No-op 
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
        } catch (Exception e) {
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

        /**
         * Handles client request.
         */
        public void run() {
            String str = "";
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    client.openInputStream()))) {
                str = in.readLine();
                System.out.println(str);
                str = in.readLine();
                System.out.println(str);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try (PrintStream out = new PrintStream(client.openOutputStream())) {
                String response
                        = "<HTML>"
                        + "<HEAD>"
                        + "<TITLE>Web Server</TITLE>"
                        + "<link rel='shortcut icon' href='data:image/x-icon;,' type='image/x-icon'>"
                        + "</HEAD>"
                        + "<BODY>Prueba Socket ME. <br>"
                        + "Thanks for Visiting.</BODY>"
                        + "</HTML>";
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: text/html");
                out.println("Server: Bot");
                // this blank line signals the end of the headers
                out.println("");
                out.println(response);
                out.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

    }

}
