package cz.sodae.doornock.terminal.door;

public interface DoorControl {
    /**
     * Open door for opening time
     *
     * @param openingTime time how long will be lock unlocked in milliseconds
     */
    void openFor(int openingTime);

    /**
     * Program releasing this controller
     */
    void release();

}
