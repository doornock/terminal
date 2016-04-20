package cz.sodae.doornock.terminal.application.security;

import cz.sodae.doornock.terminal.application.devices.Device;

/**
 * Challenge authenticator
 * It works as challenge factory for certain device and then verify challenge
 */
public interface Authenticator {

    /**
     * Generate new challenge to authentication
     */
    Challenge generateChallenge(String deviceId);

    /**
     * Valuation response on challenge
     *
     * @return challenge with result and device object, when device is not found, result is negative without device object
     */
    Result authenticate(Challenge challenge);

    interface Challenge {

        /**
         * Device id, who is challenged
         */
        String getDeviceId();

        /**
         * Input to challenge
         */
        byte[] getRequest();

        /**
         * Device sets data as response to challenge
         */
        void setResponse(byte[] response);

        /**
         * Return data of response
         */
        byte[] getResponse();
    }

    interface Result {

        /**
         * Was response successful answered?
         */
        boolean isSuccessful();

        /**
         * Return device
         */
        Device getDevice();

    }

}
