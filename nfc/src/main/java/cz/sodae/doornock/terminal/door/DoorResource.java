package cz.sodae.doornock.terminal.door;

public interface DoorResource {

    /**
     * Is called in thread where will be called open and close before open and close
     * Set up door to close!
     */
    void init();

    /**
     * Is called door lock is unlocked
     */
    void open();

    /**
     * Is called when door will be again locked
     */
    void close();

    /**
     * Release door resource and lock door
     */
    void release();

}
