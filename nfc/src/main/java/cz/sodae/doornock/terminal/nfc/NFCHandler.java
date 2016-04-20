package cz.sodae.doornock.terminal.nfc;

import java.io.IOException;

public interface NFCHandler {
    /**
     * Handle contacted device and do authentication
     *
     * @return if is handled return true
     */
    boolean handle(NFCCommunication e) throws IOException;
}
