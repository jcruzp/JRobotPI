/*
 * JRobotPI Java Firmaware v2.0.1
 * State Machine to control Robot.
 * Use I2C bus for all sensors data and send data to Arduino Due with a I2C controller
 * to read all wii remote buttons and activate DC Motors.
 */
package com.jcruz.jrobotpi;

import com.jcruz.jrobotpi.log.LoggingHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author jcruz
 */
public class JRobotPI extends MIDlet {

    private LoggingHandler loggerHandler = LoggingHandler.getInstance();
    
    private MenuWiiRemote menu=null;
    /**
     * Start App Midlet
     */
    @Override
    public void startApp() {
        try {
            loggerHandler.start();

            Logger.getGlobal().log(Level.INFO, "************************************");
            Logger.getGlobal().log(Level.INFO, "*     Starting JRobotPI v2.0.1...  *");
            Logger.getGlobal().log(Level.INFO, "************************************");

            //TODO Convert to Thread
            menu=new MenuWiiRemote();
            menu.Start();
            
        } catch (IOException ex) {
             Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        }
    }
    

    /**
     *
     * @param unconditional
     */
    @Override
    public void destroyApp(boolean unconditional) {
        if (menu!=null) menu.Stop();
        
        if (loggerHandler != null) {
            loggerHandler.stop();
        }

    }
}
