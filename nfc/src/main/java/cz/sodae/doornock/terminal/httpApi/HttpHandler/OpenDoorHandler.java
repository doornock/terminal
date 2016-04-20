package cz.sodae.doornock.terminal.httpApi.HttpHandler;

import cz.sodae.doornock.terminal.application.signal.OpenDoor;
import cz.sodae.doornock.terminal.httpApi.ApiKeyValidator;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenDoorHandler extends HttpHandler {

    private ApiKeyValidator apiKeyValidator;

    private OpenDoor openDoorSignal;

    public OpenDoorHandler(ApiKeyValidator apiKeyValidator, OpenDoor openDoorSignal) {
        this.apiKeyValidator = apiKeyValidator;
        this.openDoorSignal = openDoorSignal;
    }

    @Override
    public void service(Request request, Response response) throws Exception {
        String input = Helpers.readInput(request);

        String key = request.getHeader("X-API-Auth-V1");
        if (key == null) {
            Helpers.responseError(response, 401, "Auth key forget");
            return;
        }
        if (!apiKeyValidator.verify(key, input)) {
            Helpers.responseError(response, 403, "Verification failed");
            return;
        }

        JSONObject jsonInput;
        try {
            jsonInput = new JSONObject(input);
        } catch (JSONException e) {
            Helpers.responseError(response, 400, "Invalid json");
            return;
        }

        if (!jsonInput.has("door_id") || jsonInput.get("door_id").equals("")) {
            Helpers.responseError(response, 400, "Missing door_id parameter or it's empty");
            return;
        }

        if (jsonInput.has("opening_time") && jsonInput.optInt("opening_time", -1) < 1) {
            Helpers.responseError(response, 400, "Parameter opening_time have to be positive int and bigger then 0");
            return;
        }

        try {
            if (jsonInput.has("opening_time")) {
                this.openDoorSignal.openDoor(jsonInput.getString("door_id"), jsonInput.getInt("opening_time"));
            } else {
                this.openDoorSignal.openDoor(jsonInput.getString("door_id"));
            }
        } catch (OpenDoor.DoorNotFoundException e) {
            Helpers.responseError(response, 404, "Door id not found");
            return;
        }

        JSONObject obj = new JSONObject();
        obj.put("status", "OK");
        Helpers.sendJson(response, obj);
    }
}
