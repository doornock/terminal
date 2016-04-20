package cz.sodae.doornock.terminal.application;


import cz.sodae.doornock.terminal.configuration.ConfigDef;
import cz.sodae.doornock.terminal.configuration.Configuration;
import cz.sodae.doornock.terminal.configuration.DoorDef;
import cz.sodae.doornock.terminal.door.DoorFactory;
import cz.sodae.doornock.terminal.httpApi.HttpApiFactory;
import cz.sodae.doornock.terminal.nfc.NFCFactory;

public class Main {

    public static void main(String[] args) throws Exception {

        ConfigDef config;
        try {
            config = Configuration.loadConfig("config.yaml");
        } catch (Exception e) {
            System.err.println("Config: Loading YAML config FAILED");
            e.printStackTrace();
            return;
        }

        Application application = new Application(config.getSite().getGuid());

        if (config.getHttpApi() != null) {
            HttpApiFactory httpApiFactory = new HttpApiFactory(config.getHttpApi());
            application.setDeviceRepository(httpApiFactory.createDeviceRepository());
            application.addService(httpApiFactory.createService());
        } else {
            System.err.println("Config: api config missing");
            return;
        }

        DoorFactory doorFactory = new DoorFactory();
        for (DoorDef doorDef : config.getDoors()) {
            application.addDoor(doorDef.getId(), doorFactory.createDoorResource(doorDef));
        }

        if (config.getNfc() != null) {
            NFCFactory NFCFactory = new NFCFactory(config.getNfc());
            application.addService(NFCFactory.createService());
        }

        try {
            application.run();
        } catch (Exception e) {
            e.printStackTrace();
            application.shutdown();
        }

    }

}
