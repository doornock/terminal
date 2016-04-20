package cz.sodae.doornock.terminal.httpApi;

import cz.sodae.doornock.terminal.application.Application;
import cz.sodae.doornock.terminal.application.Service;
import cz.sodae.doornock.terminal.application.signal.OpenDoor;
import cz.sodae.doornock.terminal.httpApi.HttpHandler.HelloHandler;
import cz.sodae.doornock.terminal.httpApi.HttpHandler.NotFoundHandler;
import cz.sodae.doornock.terminal.httpApi.HttpHandler.OpenDoorHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;

import java.io.IOException;

class HttpApi implements Service {
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
            startHttpServer(application.getDoorOpenSignal());
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

    /**
     * Start http server on background
     *
     * @param openDoorSignal callable signal to open door
     * @throws IOException
     */
    private void startHttpServer(OpenDoor openDoorSignal) throws IOException {
        server = new HttpServer();
        NetworkListener nl = new NetworkListener("api-listener", "0.0.0.0", this.port);
        server.addListener(nl);
        server.getServerConfiguration().addHttpHandler(new NotFoundHandler());
        server.getServerConfiguration().addHttpHandler(new HelloHandler(), "hello");
        server.getServerConfiguration().addHttpHandler(new OpenDoorHandler(apiKeyValidator, openDoorSignal), "/open-door");

        server.start();
    }

}
