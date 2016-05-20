package cz.sodae.doornock.terminal.httpApi;

import cz.sodae.doornock.terminal.configuration.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServerConfiguration implements Configuration {

    private ApiServerSender apiServerSender;

    private ConfigDef loaded;

    public ServerConfiguration(ApiServerSender apiServerSender) {
        this.apiServerSender = apiServerSender;
    }

    public ConfigDef ask() throws LoadFailedException {
        try {
            ConfigDef def = new ConfigDef();

            JSONObject jsonObject = apiServerSender.secureGet("/api/v1/node/log-in-node");

            jsonObject = jsonObject.getJSONObject("data");

            SiteDef site = new SiteDef();
            site.setGuid(jsonObject.getJSONObject("site").getString("guid"));
            def.setSite(site);

            if (jsonObject.has("nfc")) {
                NfcDef nfcDef = new NfcDef();
                nfcDef.setAid(jsonObject.getJSONObject("nfc").getString("aid"));
                def.setNfc(nfcDef);
            }

            ArrayList<DoorDef> doorList = new ArrayList<DoorDef>();

            for (Object d : jsonObject.getJSONArray("doors")) {
                JSONObject r = (JSONObject) d;
                DoorDef doorDef = new DoorDef();
                doorDef.setId(r.getString("id"));
                doorDef.setGpio(r.getInt("gpio"));
                doorDef.setType(r.getString("type"));
                doorDef.setCloseIsZero(r.getBoolean("closeIsZero"));
                doorDef.setGpioIsOutput(r.getBoolean("gpioOutput"));
                doorList.add(doorDef);
            }

            def.setDoors(doorList);

            return loaded = def;

        } catch (Exception e) {
            e.printStackTrace();
            throw new LoadFailedException();
        }
    }

    public ConfigDef getConfig() {
        return loaded;
    }

    public class LoadFailedException extends Exception
    {

    }
}
