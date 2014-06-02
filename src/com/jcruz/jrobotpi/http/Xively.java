/*
 * 
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
    APIKEY("8KOJdwNubAa5FS4GhWqzBPB6p4GcEHexTAsGn2ui3FFbCzVM"),
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
