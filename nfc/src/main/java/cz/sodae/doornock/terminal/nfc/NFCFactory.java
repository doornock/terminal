package cz.sodae.doornock.terminal.nfc;

import cz.sodae.doornock.terminal.configuration.NfcDef;

public class NFCFactory {
    private NfcDef def;

    public NFCFactory(NfcDef def) {
        this.def = def;
    }

    public NFCService createService() {
        return new NFCService(def.getAid());
    }
}
