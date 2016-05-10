package cz.sodae.doornock.terminal.door;

import cz.sodae.doornock.terminal.configuration.DoorDef;

public class DoorFactory {

    public DoorResource createDoorResource(DoorDef doorDef) throws Exception {
        if (doorDef.getType().equals("gpio")) {
            return new WiringPiDoorResource(doorDef.getGpio(), doorDef.getCloseIsZero(), doorDef.getGpioIsOutput());
        } else if (doorDef.getType().equals("echo")) {
            return new EchoDoorResource(doorDef.getId());
        }
        throw new Exception("Unknown door type");
    }

}
