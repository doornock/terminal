package cz.sodae.doornock.terminal.httpApi;


import cz.sodae.doornock.terminal.application.devices.Device;
import cz.sodae.doornock.terminal.application.devices.DeviceRepository;
import cz.sodae.doornock.terminal.utils.RSA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

class HttpDeviceRepository implements DeviceRepository {

    private ApiServerSender apiServerSender;

    public HttpDeviceRepository(ApiServerSender apiServerSender) {
        this.apiServerSender = apiServerSender;
    }

    public Device getByDeviceId(String id) {
        return ask(id);
    }

    private PublicKey stringToPublicKey(String key) {
        try {
            return RSA.getPublicKey(DatatypeConverter.parseBase64Binary(key));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected Device ask(String deviceId) {
        try {
            JSONObject json = apiServerSender.secureGet(
                    "/api/v1/node/device-permission?device_id=" + URLEncoder.encode(deviceId, "UTF-8")
            );

            json = json.getJSONObject("data");

            PublicKey pk = this.stringToPublicKey(json.getString("public_key"));
            if (pk == null) return null;

            Device device = new Device(deviceId);
            device.setPublicRSAKey(pk);

            JSONArray doors = json.getJSONArray("door_with_access");
            for (int i = 0; i < doors.length(); i++) {
                device.addAccessToDoor(
                        doors.getJSONObject(i).getString("id"),
                        doors.getJSONObject(i).getInt("opening_time")
                );
            }

            return device;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ApiSender.ApiException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
