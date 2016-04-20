package cz.sodae.doornock.terminal.application.devices;

public interface DeviceRepository {
    /**
     * Method search device in repository and return them, otherwise return null
     *
     * @param id registered deviceId
     * @return found device
     */
    Device getByDeviceId(String id);
}
