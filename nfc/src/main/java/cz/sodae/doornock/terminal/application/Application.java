package cz.sodae.doornock.terminal.application;

import cz.sodae.doornock.terminal.application.devices.DeviceRepository;
import cz.sodae.doornock.terminal.application.signal.AccessSignal;
import cz.sodae.doornock.terminal.application.signal.OpenDoor;
import cz.sodae.doornock.terminal.door.DoorResource;
import cz.sodae.doornock.terminal.door.NonBlockingDoorControl;

import java.util.ArrayList;

public class Application {
    /**
     * GUID of site
     */
    private String guid;

    /**
     * Devices repository
     */
    private DeviceRepository deviceRepository;

    /**
     * Doors
     */
    private DoorsContainer doorsContainer = new DoorsContainer();

    /**
     * Services which is started after basic configuration and shutdowned
     */
    private ArrayList<Service> services = new ArrayList<Service>();

    public Application(String guid) {
        this.setGUID(guid);
    }

    /**************************
     * Site information
     ****************************/


    public void setGUID(String guid) {
        this.guid = guid;
    }

    /**
     * GUID of site to recognize
     *
     * @return GUID
     */
    public String getGUID() {
        return guid;
    }


    /************************** Device Repository ****************************/


    /**
     * Device repository
     */
    public void setDeviceRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }


    public DeviceRepository getDeviceRepository() {
        return deviceRepository;
    }


    /************************** Doors ****************************/


    /**
     * Add door to application
     */
    public void addDoor(String doorId, DoorResource resource) {
        this.doorsContainer.addDoor(doorId, new NonBlockingDoorControl(resource));
    }


    /**
     * Application service to open door
     */
    public OpenDoor getDoorOpenSignal() {
        return doorsContainer;
    }


    /**
     * Application service to handle authentication
     */
    public AccessSignal getAccessSignal() {
        return doorsContainer;
    }


    /************************** Services ****************************/


    /**
     * Add background service
     *
     * @param service
     */
    public void addService(Service service) {
        this.services.add(service);
    }


    /**
     * Run background services, which can etc. open door
     */
    public void run() {
        for (Service service : this.services) {
            service.start(this);
        }
    }


    /**
     * Stops all services on background
     */
    public void shutdown() {
        for (Service service : this.services) {
            service.stop();
        }
    }

}
