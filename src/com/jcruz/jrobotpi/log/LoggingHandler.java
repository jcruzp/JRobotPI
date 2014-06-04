/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 */
package com.jcruz.jrobotpi.log;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;

/**
 * Redirects log messages to remote console. Logging handler accepts TCP
 * connections on port 23 (Telnet) and redirects log messages to these
 * connections. Only one connection is accepted at a time -- when new client
 * connects, previous one gets disconnected. Before first client connects,
 * messages are directed to
 * <code>System.out</code>
 *
 * @see Logger
 */
public class LoggingHandler extends StreamHandler implements Runnable {

    private static final String PREFIX = "Logging: ";
    
    private static final int LOGGING_PORT = 23; // well-known telnet port
    
    private ServerSocketConnection servSocket;
    private SocketConnection socket;
    private Thread thread;
    
    private volatile boolean shouldRun;
    
    private static volatile LoggingHandler instance;
    
    private LoggingHandler() {}
    
    public static synchronized LoggingHandler getInstance() {
        if (instance == null) {
            instance = new LoggingHandler();
        }
        return instance;
    }

    /**
     * Start handler. Output is directed to
     * <code>System.out</code> and handler is attached to Global Logger; After
     * that, listening thread is started.
     *
     * @see #run
     */
    public void start() {
        setOutputStream(System.out);
        Logger.getGlobal().addHandler(this);
        try {
            servSocket = (ServerSocketConnection) Connector.open("socket://:" + LOGGING_PORT);
            shouldRun = true;
            thread = new Thread(this);
            thread.start();
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, PREFIX + "{0}", ex.getMessage());
        }
    }

    /**
     * Stop handler. Network socket is closed and listening thread is shut down.
     */
    public void stop() {
        synchronized (this) {
            if (socket != null) {
                try {
                    close();
                    socket.close();
                    servSocket.close();
                } catch (IOException ex) {
                    Logger.getGlobal().log(Level.SEVERE, PREFIX + "{0}", ex.getMessage());
                }
            }
            shouldRun = false;
        }
        try {
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Listening thread. Accepts connections. When new connection is accepted,
     * previous one is closed (if it existed) and output is redirected to the
     * new connection.
     */
    @Override
    public void run() {
        while (shouldRun) {
            try {
                // block until connection is requested by peer
                SocketConnection newSocket =
                        (SocketConnection) servSocket.acceptAndOpen();

                synchronized (this) {
                    if (shouldRun) {
                        OutputStream outStream = newSocket.openOutputStream();

                        // close previous connection...
                        if (socket != null) {
                            close();
                            socket.close();
                        }
                        // ...and replace with new one.
                        socket = newSocket;
                        setOutputStream(outStream);
                    }
                }
            } catch (IOException ex) {
                shouldRun = false;
                Logger.getGlobal().log(Level.SEVERE, PREFIX + "{0}", ex.getMessage());
            }
        }
    }
}
