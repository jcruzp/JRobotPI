/*
 * 
 */
package com.jcruz.jrobotpi.http.driver;

import com.jcruz.jrobotpi.http.Xively;
import com.oracle.json.Json;
import com.oracle.json.JsonBuilderFactory;
import com.oracle.json.JsonObject;
import com.oracle.json.JsonReader;
import com.oracle.json.JsonWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * Connect to Xively site and update or get values from created channels
 *
 * @author Jose Cuz
 */
public class XivelyDevice {

    private HttpConnection hc = null;
    private OutputStream os = null;

    /**
     * For the channel identify by id update its current_value
     *
     * @param id channel id
     * @param value current value for it id
     * @return http response code after PUT request, 200 = OK
     */
    public synchronized int updateValue(String id, String value) {
        int rc = -1;
        try {
            hc = (HttpConnection) Connector.open(Xively.FEEDURL.value);
            hc.setRequestMethod(HttpConnection.PUT);
            hc.setRequestProperty(Xively.APIKEY_ID.value, Xively.APIKEY.value);
            os = hc.openOutputStream();

            JsonBuilderFactory factory = Json.createBuilderFactory(null);
            JsonObject jsonvalue = factory.createObjectBuilder()
                    .add("title", Xively.TITLE.value)
                    .add("version", Xively.VERSION.value)
                    .add("datastreams", factory.createArrayBuilder()
                            .add(factory.createObjectBuilder()
                                    .add(Xively.ID.value, id)
                                    .add(Xively.CURRENT_VALUE.value, value)))
                    .build();

            try (JsonWriter jsonWriter = Json.createWriter(os)) {
                jsonWriter.writeObject(jsonvalue);
            }

            rc = hc.getResponseCode();

        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                }
            }
            if (hc != null) {
                try {
                    hc.close();
                } catch (IOException ex) {
                }
            }
        }

        return rc;

    }

    /**
     *
     * @return JsonObject with response from feed with channel info
     */
    public JsonObject getFeedInfo() {
        InputStream is = null;
        JsonObject json = null;
        try {
            hc = (HttpConnection) Connector.open(Xively.FEEDURL.value);
            hc.setRequestMethod(HttpConnection.GET);
            hc.setRequestProperty(Xively.APIKEY_ID.value, Xively.APIKEY.value);
            is = hc.openInputStream();

            int rc = hc.getResponseCode();

            try (JsonReader jsonReader = Json.createReader(is)) {
                json = jsonReader.readObject();
            }

        } catch (IOException ex) {
            Logger.getGlobal().log(Level.WARNING,ex.getLocalizedMessage());
        } finally {

            if (hc != null) {
                try {
                    hc.close();
                } catch (IOException ex) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }

        }
        return json;
    }

}
