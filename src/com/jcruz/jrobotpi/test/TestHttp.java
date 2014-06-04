/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jcruz.jrobotpi.test;

import com.jcruz.jrobotpi.http.driver.XivelyDevice;
import com.oracle.json.Json;
import com.oracle.json.JsonBuilderFactory;
import com.oracle.json.JsonObject;
import com.oracle.json.JsonWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author jcruz
 */
public class TestHttp extends MIDlet {

    @Override
    public void startApp() {
        XivelyDevice xdev = new XivelyDevice();
        xdev.updateValue("PIR_Sensor", "1");

        System.out.println(xdev.getFeedInfo().toString());
        System.out.println(xdev.getFeedInfo().getString("title"));

    }

    @Override
    public void destroyApp(boolean unconditional) {
    }
}
