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

import com.jcruz.jrobotpi.log.LoggingHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 *
 * JRobotPI Java Firmware v2.0.2
 * State Machine to control Robot.
 * Use I2C bus for all sensors data and send data to Arduino Due with a I2C controller
 * to read all wii remote buttons and activate DC Motors.
 * @author jcruz
 */
public class JRobotPI extends MIDlet {
    
    private LoggingHandler loggerHandler = LoggingHandler.getInstance();
    
    private Processor processor = null;

    /**
     * Start App Midlet
     */
    @Override
    public void startApp() {
        try {
            loggerHandler.start();
            Logger.getGlobal().setLevel(Level.INFO);
            
            Logger.getGlobal().log(Level.INFO, "************************************");
            Logger.getGlobal().log(Level.INFO, "*     Starting JRobotPI v2.0.3...  *");
            Logger.getGlobal().log(Level.INFO, "************************************");

            //TODO Convert to Thread
            processor = new Processor();
            processor.Start();
            
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING, ex.getLocalizedMessage());
        }
    }

    /**
     *
     */
    @Override
    public void pauseApp() {
        
        System.out.println("End program 2...");
    }

    @Override
    public void destroyApp(boolean unconditional){
        System.out.println("End program...");
        if (processor != null) {
            processor.Stop();
        }
        
        if (loggerHandler != null) {
            loggerHandler.stop();
        } 
        
    }
}
