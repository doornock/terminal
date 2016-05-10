package cz.sodae.doornock.terminal.httpApi;

import cz.sodae.doornock.terminal.application.devices.DeviceRepository;
import cz.sodae.doornock.terminal.configuration.HttpApiDef;

public class HttpApiFactory {
    private HttpApiDef definition;


    public HttpApiFactory(HttpApiDef definition) {
        this.definition = definition;
    }


    private ApiServerSender createApiSender() {
        return new ApiServerSender(
                definition.getUrl(),
                definition.getNodeId(),
                definition.getApiKey()
        );
    }


    private ApiKeyValidator createValidator() {
        return new Hmac256ApiValidator(this.definition.getApiKey());
    }


    public DeviceRepository createDeviceRepository() {
        return new HttpDeviceRepository(createApiSender());
    }

    public ServerConfiguration createServerConfiguration() {
        return new ServerConfiguration(createApiSender());
    }


    public HttpApi createService() {
        return new HttpApi(this.createValidator(), definition.getPort());
    }

}
