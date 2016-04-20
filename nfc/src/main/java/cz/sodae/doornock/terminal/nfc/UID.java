package cz.sodae.doornock.terminal.nfc;

import cz.sodae.doornock.terminal.utils.Bytes;


/**
 * NFCID3
 */
public class UID {
    /**
     * UID id bytes
     */
    private byte[] definition;

    /**
     * @return UID in bytes
     */
    public byte[] getDefinition() {
        return definition;
    }


    /**
     * @return Human readable UID in hex format
     */
    public String formatString() {
        return Bytes.bytesToHexString(definition);
    }


    /**
     * UID is not persistent for device and is random generated
     * It's hardwired in Android and PN53x
     *
     * @return bool
     * @url http://www.libnfc.org/httpApi/nfc-emulate-uid_8c.html
     */
    private boolean isEmulatedUID() {
        return definition[0] == 0x08 && definition.length == 4;
    }


    public static UID createByString(String content) {
        UID self = new UID();
        self.definition = Bytes.parseHexStringToBytes(content);
        return self;
    }


    public static UID createByBytes(byte[] content) {
        UID self = new UID();
        self.definition = content;
        return self;
    }

}
