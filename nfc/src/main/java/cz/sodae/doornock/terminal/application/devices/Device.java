package cz.sodae.doornock.terminal.application.devices;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Container with information about device
 */
public class Device {
    /**
     * Device id
     */
    private String deviceId;

    /**
     * UID of device
     */
    private cz.sodae.doornock.terminal.nfc.UID UID;

    /**
     * Public key of device
     */
    private PublicKey publicRSAKey;

    /**
     * List door id where device has access
     */
    private List<Door> actualAccessDoor;

    public Device(String deviceId) {
        this.actualAccessDoor = new LinkedList<Door>();
        this.deviceId = deviceId;
    }

    /**
     * Device id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * @return uid of device
     */
    public cz.sodae.doornock.terminal.nfc.UID getUID() {
        return UID;
    }

    /**
     * @return public RSA key of device
     */
    public PublicKey getPublicRSAKey() {
        return publicRSAKey;
    }

    /**
     * List of doors id where has access
     *
     * @return doors
     */
    public ArrayList<Door> getAccessToDoor() {
        return new ArrayList<Door>(actualAccessDoor);
    }

    /**
     * Add door where has device access
     *
     * @param doorId door id
     */
    public void addAccessToDoor(String doorId, int openingTime) {
        this.actualAccessDoor.add(new Door(doorId, openingTime));
    }

    /**
     * Set UID of device
     *
     * @param UID
     */
    public void setUID(cz.sodae.doornock.terminal.nfc.UID UID) {
        this.UID = UID;
    }

    /**
     * Set public RSA key
     *
     * @param publicRSAKey RSA key
     */
    public void setPublicRSAKey(PublicKey publicRSAKey) {
        this.publicRSAKey = publicRSAKey;
    }

    public class Door {
        private String id;
        private int openingTime;

        public Door(String id, int openingTime) {
            this.id = id;
            this.openingTime = openingTime;
        }

        public String getId() {
            return id;
        }

        public int getOpeningTime() {
            return openingTime;
        }
    }

}
