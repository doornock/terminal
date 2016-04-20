package cz.sodae.doornock.terminal.application.security;


import cz.sodae.doornock.terminal.application.devices.Device;
import cz.sodae.doornock.terminal.application.devices.DeviceRepository;
import cz.sodae.doornock.terminal.utils.Bytes;
import cz.sodae.doornock.terminal.utils.RSA;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class RSAAuthenticator implements Authenticator {

    DeviceRepository repository;

    public RSAAuthenticator(DeviceRepository repository) {
        this.repository = repository;
    }

    /**
     * Generates random bytes
     *
     * @param deviceId
     * @return RSAChallenge
     */
    public RSAChallenge generateChallenge(String deviceId) {

        byte[] randomBytes = Bytes.generateRandomBytes(255);
        return new RSAChallenge(deviceId, randomBytes);
    }

    /**
     * Verify sign
     * {@inheritDoc}
     */
    public Result authenticate(Challenge challenge) {

        try {
            Device device = this.repository.getByDeviceId(challenge.getDeviceId());

            if (device == null) {
                return new Result();
            }

            boolean verified = RSA.verify(
                    challenge.getRequest(), // origin text
                    device.getPublicRSAKey(),
                    challenge.getResponse() // signed text
            );

            return new Result(verified, device);

        } catch (InvalidKeyException e) {
            System.out.println("Device has registered invalid key: " + e.getMessage());
            e.printStackTrace();
        } catch (SignatureException e) {
            System.out.println("Signature is invalid: " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Encryption is not present!", e);
        }
        return new Result();
    }

    public class RSAChallenge implements Challenge {
        private byte[] request;

        private byte[] response;

        private String deviceId;

        public RSAChallenge(String deviceId, byte[] request) {
            this.deviceId = deviceId;
            this.request = request;
        }

        /**
         * {@inheritDoc}
         */
        public byte[] getRequest() {
            return request;
        }

        /**
         * {@inheritDoc}
         */
        public byte[] getResponse() {
            return response;
        }

        /**
         * {@inheritDoc}
         */
        public String getDeviceId() {
            return deviceId;
        }

        /**
         * {@inheritDoc}
         */
        public void setResponse(byte[] response) {
            this.response = response;
        }
    }

    public class Result implements Authenticator.Result {

        private boolean success;

        private Device device;

        public Result() {
            this.success = false;
            this.device = null;
        }

        public Result(boolean success, Device device) {
            this.success = success;
            this.device = device;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isSuccessful() {
            return success;
        }

        /**
         * {@inheritDoc}
         */
        public Device getDevice() {
            return device;
        }
    }
}
