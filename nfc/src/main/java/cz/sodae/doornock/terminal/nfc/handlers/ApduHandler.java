package cz.sodae.doornock.terminal.nfc.handlers;

import cz.sodae.doornock.terminal.application.security.Authenticator;
import cz.sodae.doornock.terminal.application.signal.AccessSignal;
import cz.sodae.doornock.terminal.nfc.NFCCommunication;
import cz.sodae.doornock.terminal.nfc.NFCHandler;
import cz.sodae.doornock.terminal.utils.Bytes;

import java.io.IOException;
import java.util.Arrays;


/**
 * RSA apdu handling
 */
public class ApduHandler implements NFCHandler {

    String siteGuid;
    String AID = "F0394148148111";

    Authenticator authenticator;

    AccessSignal accessSignal;

    public ApduHandler(String siteGuid, Authenticator authenticator, AccessSignal accessSignal) {
        this.siteGuid = siteGuid;
        this.authenticator = authenticator;
        this.accessSignal = accessSignal;
    }


    /**
     * Application ID for devices to recognize application
     * http://stackoverflow.com/a/27620724
     *
     * @param AID Application ID
     */
    public void setAID(String AID) {
        this.AID = AID;
    }


    /**
     * Network GUID
     *
     * @param siteGuid
     */
    public void setSiteGuid(String siteGuid) {
        this.siteGuid = siteGuid;
    }

    public boolean handle(NFCCommunication communication) throws IOException {
        NFCCommunication.APDUResponse lastResponse;

        lastResponse = communication.sendCommand(ApduSelectByteCommand());
        if (!Arrays.equals(lastResponse.sw(), A_OKAY)) return false; // not found, another AID?

        System.out.print("New device on NFC reader");

        lastResponse = communication.sendCommand(getGUIDSite(siteGuid.getBytes()));
        if (!Arrays.equals(lastResponse.sw(), A_OKAY)) {
            System.err.println("GUID not accepted SW: " + Bytes.bytesToHexString(lastResponse.sw()));
            return true; // handled, network not found
        }

        String deviceId = new String(lastResponse.response());

        Authenticator.Challenge challenge = authenticator.generateChallenge(deviceId);

        lastResponse = communication.sendCommand(getCheckSignCommand(challenge.getRequest()));
        if (!Arrays.equals(lastResponse.sw(), A_OKAY)) {
            System.err.println("String was not accepted SW: " + Bytes.bytesToHexString(lastResponse.sw()));
            return true; // handled, network not found
        }

        challenge.setResponse(lastResponse.response());

        Authenticator.Result result = this.authenticator.authenticate(challenge);

        if (result.isSuccessful()) {
            System.out.println("Device " + result.getDevice().getDeviceId() + " is authenticated");
            accessSignal.onSuccess(result.getDevice());
        } else {
            System.out.println("Device is not accepted");
            accessSignal.onFailure(result.getDevice());
        }

        return true;
    }


    private static final byte[] A_OKAY = {
            (byte) 0x90,  // SW1	Status byte 1 - Command processing status
            (byte) 0x00   // SW2	Status byte 2 - Command processing qualifier
    };

    private static final byte[] A_ERROR_INVALID_AUTH = {
            (byte) 0x98,  // SW1	Status byte 1 - Command processing status
            (byte) 0x04   // SW2	Status byte 2 - Command processing qualifier
    };

    private static final byte[] A_ERROR_INVALID_LC = {
            (byte) 0x67,  // SW1	Status byte 1 - Command processing status
            (byte) 0x00   // SW2	Status byte 2 - Command processing qualifier
    };

    private byte[] ApduSelectByteCommand() {
        byte[] header = {
                (byte) 0x00, // CLA	- Class - Class of instruction
                (byte) 0xA4, // INS	- Instruction - Instruction code
                (byte) 0x04, // P1	- Parameter 1 - Instruction parameter 1
                (byte) 0x00, // P2	- Parameter 2 - Instruction parameter 2
                (byte) 0x07, // Lc field	- Number of bytes present in the data field of the command
        };
        byte[] tail = {
                (byte) 0x00  // Le field	- Maximum number of bytes expected in the data field of the response to the command
        };
        byte[] aid = Bytes.parseHexStringToBytes(AID);

        //(byte)0xF0, (byte)0x39, (byte)0x41, (byte)0x48, (byte)0x14, (byte)0x81, (byte)0x11, // NDEF Tag Application name

        return Bytes.concatenate(header, aid, tail);
    }

    public static byte[] getCheckSignCommand(byte[] s) {
        byte[] header = {
                (byte) 0xD0, // CLA - http://www.cardwerk.com/smartcards/smartcard_standard_ISO7816-4_5_basic_organizations.aspx#table8
                (byte) 0x02,
                (byte) 0x00,
                (byte) 0x00,
                (byte) s.length
        };

        byte[] tail = {
                (byte) 0x08
        };

        return Bytes.concatenate(header, s, tail);
    }

    public static byte[] getGUIDSite(byte[] s) {
        byte[] header = {
                (byte) 0xD0, // CLA - http://www.cardwerk.com/smartcards/smartcard_standard_ISO7816-4_5_basic_organizations.aspx#table8
                (byte) 0x03,
                (byte) 0x00,
                (byte) 0x00,
                (byte) s.length
        };

        byte[] tail = {
                (byte) 0x08
        };

        return Bytes.concatenate(header, s, tail);
    }

}
