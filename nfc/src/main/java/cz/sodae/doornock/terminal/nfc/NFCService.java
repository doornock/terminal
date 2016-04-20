package cz.sodae.doornock.terminal.nfc;

import cz.sodae.doornock.terminal.application.Application;
import cz.sodae.doornock.terminal.application.Service;
import cz.sodae.doornock.terminal.application.security.RSAAuthenticator;
import cz.sodae.doornock.terminal.nfc.handlers.ApduHandler;
import cz.sodae.doornock.terminal.nfc.handlers.EchoUIDHandler;
import cz.sodae.doornock.terminal.nfc.handlers.MultipleHandler;
import cz.sodae.doornock.terminal.nfc.libnfc.NFCController;

public class NFCService implements Service {
    private String AID;

    private NFCController controller;

    public NFCService() {
        this.controller = new NFCController();
    }

    public NFCService(String AID) {
        this();
        this.AID = AID;
    }

    public void start(Application application) {
        ApduHandler apduHandler = new ApduHandler(
                application.getGUID(),
                new RSAAuthenticator(application.getDeviceRepository()),
                application.getAccessSignal()
        );

        if (AID != null) {
            apduHandler.setAID(AID);
        }

        MultipleHandler handlers = new MultipleHandler();
        handlers.addHandler(new EchoUIDHandler());
        handlers.addHandler(apduHandler);

        controller.setHandler(handlers);
        controller.start();
    }

    public void stop() {
        controller.stop();
    }
}
