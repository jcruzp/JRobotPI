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
package com.jcruz.jrobotpi.test;

import com.jcruz.jrobotpi.i2c.driver.SC16IS750Device;
import com.jcruz.jrobotpi.log.LoggingHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author jcruz
 */
public class SC16IS750Test extends MIDlet {

    private LoggingHandler loggerHandler = LoggingHandler.getInstance();

    @Override
    public void startApp() {
        loggerHandler.start();
        Logger.getGlobal().setLevel(Level.ALL);

        Logger.getGlobal().log(Level.INFO, "************************************");
        try {
            SC16IS750Device sc = new SC16IS750Device();
            sc.write("STest It 1\r\n");
            
            sc.write("STest It 2\r\n");
            
            sc.write("STest It 3\r\n");
            
            sc.write("STest It 4\r\n");
            
            sc.write("STest It 5\r\n");
            
            sc.write("STest It 6\r\n");
            
            sc.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void destroyApp(boolean unconditional) {
    }
}
