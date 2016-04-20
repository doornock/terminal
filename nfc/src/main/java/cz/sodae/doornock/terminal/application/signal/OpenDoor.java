package cz.sodae.doornock.terminal.application.signal;

/**
 * Signal to open door, if door not found throws DoorNotFoundException
 */
public interface OpenDoor {

    void openDoor(String doorId, int openingTime) throws DoorNotFoundException;

    void openDoor(String doorId) throws DoorNotFoundException;

    class DoorNotFoundException extends Exception {

    }

}
