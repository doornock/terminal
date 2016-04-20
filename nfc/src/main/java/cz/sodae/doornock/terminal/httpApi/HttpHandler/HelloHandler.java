package cz.sodae.doornock.terminal.httpApi.HttpHandler;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HelloHandler extends HttpHandler {
    public void service(Request request, Response response) throws Exception {


        final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        final String output = "Hello, now is " + format.format(new Date(System.currentTimeMillis())) + request.getRequestURI();
        response.setContentType("text/plain");
        response.setContentLength(output.length());
        response.getWriter().write(output);
    }
}
