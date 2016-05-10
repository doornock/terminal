package cz.sodae.doornock.terminal.httpApi;

import cz.sodae.doornock.terminal.application.Application;
import cz.sodae.doornock.terminal.application.Service;
import cz.sodae.doornock.terminal.application.signal.OpenDoor;
import cz.sodae.doornock.terminal.httpApi.HttpHandler.HelloHandler;
import cz.sodae.doornock.terminal.httpApi.HttpHandler.NotFoundHandler;
import cz.sodae.doornock.terminal.httpApi.HttpHandler.OpenDoorHandler;
import cz.sodae.doornock.terminal.httpApi.HttpHandler.RestartHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;

import java.io.IOException;

public class HttpApi implements Service {
    private int port;

    private HttpServer server;

    private ApiKeyValidator apiKeyValidator;

    public HttpApi(ApiKeyValidator apiKeyValidator, int port) {
        this.apiKeyValidator = apiKeyValidator;
        this.port = port;
    }

    /**
     * Run API
     *
     * @param application
     */
    public void start(final Application application) {
        try {
            server = new HttpServer();
            NetworkListener nl = new NetworkListener("api-listener", "0.0.0.0", this.port);
            server.addListener(nl);
            server.getServerConfiguration().addHttpHandler(new NotFoundHandler());
            server.getServerConfiguration().addHttpHandler(new HelloHandler(), "hello");
            server.getServerConfiguration().addHttpHandler(new OpenDoorHandler(apiKeyValidator, application.getDoorOpenSignal()), "/open-door");
            server.getServerConfiguration().addHttpHandler(new RestartHandler(apiKeyValidator, application), "/restart");

            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops http server
     */
    public void stop() {
        if (server != null && server.isStarted()) {
            server.shutdown();
        }
    }

}
