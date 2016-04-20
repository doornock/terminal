package cz.sodae.doornock.terminal.httpApi.HttpHandler;


import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class NotFoundHandler extends HttpHandler {
    public void service(Request request, Response response) throws Exception {
        response.setStatus(404, "Not found");
        response.setContentType("application/json");
        response.getWriter().write("{error:\"Bad path\"}");
    }
}

