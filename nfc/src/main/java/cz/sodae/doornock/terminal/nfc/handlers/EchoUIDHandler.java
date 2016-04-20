package cz.sodae.doornock.terminal.nfc.handlers;

import cz.sodae.doornock.terminal.nfc.NFCCommunication;
import cz.sodae.doornock.terminal.nfc.NFCHandler;

import java.io.IOException;

public class EchoUIDHandler implements NFCHandler {
    public boolean handle(NFCCommunication e) throws IOException {

        if (e.getDeviceUID() == null) {
            System.out.println("Detected device no UID");
        } else {
            System.out.println("Detected: " + e.getDeviceUID().formatString());
        }
        return false;
    }
}
