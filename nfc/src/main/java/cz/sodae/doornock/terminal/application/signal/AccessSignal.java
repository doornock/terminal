package cz.sodae.doornock.terminal.application.signal;

import cz.sodae.doornock.terminal.application.devices.Device;


/**
 * Signal is called after user was challenged
 */
public interface AccessSignal {

    /**
     * When user(device) is successfully verified
     *
     * @param device
     */
    void onSuccess(Device device);

    /**
     * When device authentication failed, or it is unknown device
     *
     * @param device is null when unknown device
     */
    void onFailure(Device device);

}
