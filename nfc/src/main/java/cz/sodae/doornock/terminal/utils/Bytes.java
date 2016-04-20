package cz.sodae.doornock.terminal.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;

final public class Bytes {


    public Bytes() throws Exception {
        throw new Exception("Utils.Bytes is static class");
    }

    static final SecureRandom numberGenerator = new SecureRandom();


    /**
     * Concatenate bytes
     * http://stackoverflow.com/a/12141556
     */
    public static byte[] concatenate(byte[]... arrays) {
        // Determine the length of the result array
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }

        // create the result array
        byte[] result = new byte[totalLength];

        // copy the source arrays into the result array
        int currentIndex = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }

        return result;
    }


    /**
     * Java bytes converts to string with HEX written bytes
     *
     * @param bytes java bytes
     * @return encoded bytes to string (00-FF)*
     */
    public static String bytesToHexString(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }


    /**
     * String with HEX written bytes converts to java bytes
     *
     * @param string hex (00-FF) bytes string
     * @return decoded bytes from string
     */
    public static byte[] parseHexStringToBytes(String string) {
        return DatatypeConverter.parseHexBinary(string);
    }


    /**
     * Generates random bytes array
     *
     * @param size how big it be
     * @return random bytes
     */
    public static byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        numberGenerator.nextBytes(bytes);
        return bytes;
    }

}
