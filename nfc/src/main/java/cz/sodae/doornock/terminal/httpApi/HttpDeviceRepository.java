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

    ApiSender apiSender = new ApiSender();

    private String apiUrl;

    private String nodeId;

    private String apiKey;

    public HttpDeviceRepository(String apiUrl, String nodeId, String apiKey) {
        this.apiUrl = apiUrl.replaceAll("/+$", "");
        this.nodeId = nodeId;
        this.apiKey = apiKey;
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
            StringBuilder sb = new StringBuilder();
            sb.append(this.apiUrl)
                    .append("/api/v1/node/device-permission?device_id=")
                    .append(URLEncoder.encode(deviceId, "UTF-8"));

            JSONObject json = apiSender.get(sb.toString(), this.nodeId, this.apiKey);

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
