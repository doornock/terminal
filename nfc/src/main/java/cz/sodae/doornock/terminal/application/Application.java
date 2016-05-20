package cz.sodae.doornock.terminal.application;


import cz.sodae.doornock.terminal.application.devices.DeviceRepository;
import cz.sodae.doornock.terminal.application.signal.AccessSignal;
import cz.sodae.doornock.terminal.application.signal.OpenDoor;
import cz.sodae.doornock.terminal.configuration.ConfigDef;
import cz.sodae.doornock.terminal.configuration.DoorDef;
import cz.sodae.doornock.terminal.configuration.HttpApiDef;
import cz.sodae.doornock.terminal.configuration.YamlStaticConfiguration;
import cz.sodae.doornock.terminal.door.DoorFactory;
import cz.sodae.doornock.terminal.door.NonBlockingDoorControl;
import cz.sodae.doornock.terminal.httpApi.HttpApi;
import cz.sodae.doornock.terminal.httpApi.HttpApiFactory;
import cz.sodae.doornock.terminal.httpApi.ServerConfiguration;
import cz.sodae.doornock.terminal.nfc.NFCFactory;
import cz.sodae.doornock.terminal.nfc.NFCService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Application {
    private ConfigDef configDef;

    private String configFile;


    private NFCService nfcService;


    /**
     * Devices repository
     */
    private DeviceRepository deviceRepository;

    /**
     * Doors
     */
    private DoorsContainer doorsContainer = new DoorsContainer();

    /**
     * Services which is started after basic configuration and shutdowned
     */
    private ArrayList<Service> services = new ArrayList<Service>();

    /**
     * GUID of site to recognize
     *
     * @return GUID
     */
    public String getGUID() {
        return configDef.getSite().getGuid();
    }

    /**
     * Application service to open door
     */
    public OpenDoor getDoorOpenSignal() {
        return doorsContainer;
    }


    /**
     * Application service to handle authentication
     */
    public AccessSignal getAccessSignal() {
        return doorsContainer;
    }


    public DeviceRepository getDeviceRepository() {
        return deviceRepository;
    }


    public Application() {
        this.nfcService = new NFCService();
    }

    public void configFile(String filename) throws LoadConfigException, InterruptedException {
        this.configFile = filename;
    }


    public void commandLine() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        do {
            try {
                System.out.print("$: ");
                String line = in.readLine();
                if (line.equals("stop")) {
                    this.stop();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);

    }

    public void restart()
    {
        stop();
        start();
    }

    public void stop() {
        doorsContainer.clear();
        for (Service service : this.services) {
            service.stop();
        }
    }


    public void start() {
        try {
            ConfigDef config;
            try {
                YamlStaticConfiguration loader = new YamlStaticConfiguration();
                config = loader.loadConfig(configFile != null ? configFile : "config.yaml");
            } catch (Exception e) {
                throw new LoadConfigException(e);
            }

            if (config.getHttpApi() == null) {
                throw new LoadConfigException("config.yaml: unable load httpApi");
            }

            HttpApiFactory httpApiFactory = new HttpApiFactory(config.getHttpApi());

            if (config.getSite() == null) {
                System.out.println("Loading config from server over HTTP API");

                HttpApiDef apiDef = config.getHttpApi();
                config = null;
                ServerConfiguration configuration = httpApiFactory.createServerConfiguration();
                do {
                    try {
                        config = configuration.ask();
                    } catch (ServerConfiguration.LoadFailedException e) {
                        System.err.println("Unable load configuration from server, 2 seconds wait and retry");
                        Thread.sleep(2000);
                    }
                } while (config == null);
                config.setHttpApi(apiDef);
            }
            configDef = config;

            deviceRepository = httpApiFactory.createDeviceRepository();
            HttpApi apiServer = httpApiFactory.createService();

            apiServer.start(this);
            services.add(apiServer);

            DoorFactory doorFactory = new DoorFactory();
            for (DoorDef doorDef : configDef.getDoors()) {
                doorsContainer.addDoor(
                        doorDef.getId(),
                        new NonBlockingDoorControl(
                                doorFactory.createDoorResource(doorDef)
                        )
                );
            }

            services.remove(nfcService);
            if (configDef.getNfc() != null) {
                nfcService.config(configDef.getNfc());
                nfcService.start(this);
                services.add(nfcService);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public class LoadConfigException extends Exception {
        public LoadConfigException(String message) {
            super(message);
        }

        public LoadConfigException(Throwable cause) {
            super(cause);
        }
    }

}
