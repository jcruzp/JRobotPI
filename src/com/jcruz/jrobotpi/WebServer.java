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
import javax.microedition.midlet.MIDlet;

/**
 * MIDlet working as simple personal WebServer. Currently this serves simple
 * HTTP GET operation.
 *
 * @author Kumar Mettu
 * @version 0.61
 */
public class WebServer extends MIDlet {

    private StreamConnectionNotifier scn = null;
    

    /**
     * Default constructor.
     */
    public WebServer() {

    }

    /**
     * This will be invoked when we start the MIDlet
     */
    public void startApp() {

        try {
            scn = (StreamConnectionNotifier) Connector.open("socket://:8000");

            while (true) {
                StreamConnection sc = (StreamConnection) scn.acceptAndOpen();

                Connection c = new Connection(sc);
                c.start();

                // service the connection in a separate thread 
            }

        } catch (IOException e) {
            //Handle Exceptions any other way you like. 
            //No-op 
        }
    }

    /**
     * Pause, discontinue ....
     */
    public void pauseApp() {
        try {
            if (scn != null) {
                scn.close();
            }
        } catch (Exception e) {
        }

    }

    /**
     * Destroy. Cleanup everything.
     */
    public void destroyApp(boolean unconditional) {
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

        public Connection(StreamConnection c) {
            client = c;
        }

        /**
         * Handles client request.
         */
        public void run() {
            String str;
            PrintStream out = null;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        client.openInputStream()));
                str = in.readLine();
                System.out.println(str);
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                out = new PrintStream(client.openOutputStream());
                String response
                        = "<HTML>"
                        + "<HEAD>"
                        + "<TITLE>Web Server</TITLE>"
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

            } catch (IOException ioe) {
            } finally {
                try {

                    if (out != null) {
                        out.close();
                    }
                    if (client != null) {
                        client.close();
                    }
                } catch (IOException ioee) {
                    //Handle Exceptions any other way you like. 
                    //No-op 
                }
            }
        }

    }
    private StreamConnection client;

}
