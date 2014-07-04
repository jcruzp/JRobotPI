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
package com.jcruz.jrobotpi.http;

/**
 * Info to send data to my account at Xively site
 *
 * @author Jose Cruz
 */
public enum Xively {

    /**
     * Xively Feed Url for Device
     */
    FEEDURL("https://api.xively.com/v2/feeds/918735601"),
    /**
     * Api Key to update and read values
     */
    APIKEY_ID("X-ApiKey"),
    /**
     * Api key value for let read values
     */
    APIKEY("Z0rDP12O5ospqm9t0konGJntqytY7WG9OY6bhchGNXU0Y48i"),
    /**
     * Title header from response GET
     */
    TITLE("JRobotPI"),
    /**
     * Version header from response GET
     */
    VERSION("1.0.0"),
    /**
     * Key to identify a channel
     */
    ID("id"),
    /**
     * Current value for the channel
     */
    CURRENT_VALUE("current_value");

    /**
     * Get value from enum
     */
    public String value;

    private Xively(String value) {
        this.value = value;
    }

}
