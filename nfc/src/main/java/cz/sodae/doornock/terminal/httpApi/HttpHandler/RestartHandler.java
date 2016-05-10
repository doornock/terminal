package cz.sodae.doornock.terminal.httpApi.HttpHandler;

import cz.sodae.doornock.terminal.application.Application;
import cz.sodae.doornock.terminal.application.signal.OpenDoor;
import cz.sodae.doornock.terminal.httpApi.ApiKeyValidator;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class RestartHandler extends HttpHandler {

    private ApiKeyValidator apiKeyValidator;

    private Application app;

    public RestartHandler(ApiKeyValidator apiKeyValidator, Application app) {
        this.apiKeyValidator = apiKeyValidator;
        this.app = app;
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

        app.restart();

        JSONObject obj = new JSONObject();
        obj.put("status", "OK");
        Helpers.sendJson(response, obj);
    }
}
