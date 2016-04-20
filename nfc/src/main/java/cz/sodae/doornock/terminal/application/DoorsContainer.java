package cz.sodae.doornock.terminal.application;

import cz.sodae.doornock.terminal.application.devices.Device;
import cz.sodae.doornock.terminal.application.signal.AccessSignal;
import cz.sodae.doornock.terminal.application.signal.OpenDoor;
import cz.sodae.doornock.terminal.door.DoorControl;

import java.util.Map;
import java.util.TreeMap;

class DoorsContainer implements OpenDoor, AccessSignal {

    private Map<String, DoorControl> doors = new TreeMap<String, DoorControl>();

    public void addDoor(String doorId, DoorControl control) {
        this.doors.put(doorId, control);
    }


    public void openDoor(String doorId, int openingTime) throws OpenDoor.DoorNotFoundException {
        if (!doors.containsKey(doorId)) {
            throw new OpenDoor.DoorNotFoundException();
        }
        doors.get(doorId).openFor(openingTime);
    }

    public void openDoor(String doorId) throws OpenDoor.DoorNotFoundException {
        if (!doors.containsKey(doorId)) {
            throw new OpenDoor.DoorNotFoundException();
        }
        doors.get(doorId).openFor(3000); // some default time @todo configurable?
    }

    public void onSuccess(Device device) {
        for (Device.Door door : device.getAccessToDoor()) {
            if (doors.containsKey(door.getId())) {
                doors.get(door.getId()).openFor(door.getOpeningTime());
            }
        }
    }

    public void onFailure(Device device) {

    }
}
