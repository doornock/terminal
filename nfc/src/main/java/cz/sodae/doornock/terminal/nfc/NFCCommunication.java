package cz.sodae.doornock.terminal.nfc;

import java.io.IOException;

/**
 * @see https://en.wikipedia.org/wiki/Smart_card_application_protocol_data_unit
 */
public interface NFCCommunication {

    /**
     * Contain UID if it is 14443A
     */
    UID getDeviceUID();

    /**
     * Sends APDU command and wait to response
     */
    APDUResponse sendCommand(byte[] command) throws IOException;

    interface APDUResponse {
        /**
         * Command processing status
         */
        byte[] sw();

        /**
         * First command processing status
         */
        byte sw1();

        /**
         * Second command processing status
         */
        byte sw2();

        /**
         * Return response bytes with SW bytes
         */
        byte[] bytes();

        /**
         * Return only response bytes without SW bytes
         */
        byte[] response();

        /**
         * Length of response
         */
        long length();

        /**
         * Length of response with SW bytes
         */
        long lengthWithSW();
    }

}


