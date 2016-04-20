package cz.sodae.doornock.terminal.nfc.handlers;

import cz.sodae.doornock.terminal.nfc.NFCCommunication;
import cz.sodae.doornock.terminal.nfc.NFCHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * Handler which contains multiple handler
 * Signal will be called to each handler in list (order as was added) until one returns true
 */
public class MultipleHandler implements NFCHandler {

    private List<NFCHandler> handlers = new LinkedList<NFCHandler>();

    /**
     * Add handler to list
     *
     * @param handler handling signal about new device
     */
    public synchronized void addHandler(NFCHandler handler) {
        this.handlers.add(handler);
    }

    public boolean handle(NFCCommunication e) throws IOException {
        for (NFCHandler handler : handlers) {
            if (handler.handle(e)) {
                return true;
            }
        }
        return false;
    }


    /**
     * It is used to clear handlers - etc. reload new instances
     */
    public synchronized void clear() {
        handlers.clear();
    }
}
