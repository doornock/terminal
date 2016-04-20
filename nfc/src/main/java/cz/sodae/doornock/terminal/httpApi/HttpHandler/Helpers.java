package cz.sodae.doornock.terminal.httpApi.HttpHandler;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Helpers {

    public static String readInput(Request request) throws IOException {
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String read;
        while ((read = reader.readLine()) != null) {
            builder.append(read);
        }
        reader.close();

        return builder.toString();
    }

    public static void responseError(Response response, int httpCode, String message) throws IOException {

        JSONObject obj = new JSONObject();
        obj.put("status", "error");
        obj.put("message", message);

        sendJsonResponse(response, httpCode, obj);
    }

    public static void sendJson(Response response, JSONObject obj) throws IOException {
        sendJsonResponse(response, 200, obj);
    }

    public static void sendJsonResponse(Response response, int httpCode, JSONObject obj) throws IOException {
        response.setContentType("text/json");
        response.setCharacterEncoding("utf-8");
        sendResponse(response, httpCode, obj.toString());
    }

    public static void sendResponse(Response response, int httpCode, String string) throws IOException {

        response.setStatus(httpCode);
        response.setContentLength(string.length());
        response.getWriter().write(string);
    }

}
