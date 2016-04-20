package cz.sodae.doornock.terminal.door;

/**
 * Testing door resource if you have not a real door resource or would you like test on local device
 */
public class EchoDoorResource implements DoorResource {
    /**
     * Identification this object in printed output
     */
    private String identification;

    public EchoDoorResource(String identification) {
        this.identification = identification;
    }

    public EchoDoorResource() {
        this.identification = "";
    }

    public void init() {
        System.out.println("Door " + identification + " inited");
    }

    public void open() {
        System.out.println("Door " + identification + " opened");
    }

    public void close() {
        System.out.print("Door " + identification + " closed");
    }

    public void release() {
        System.out.println("Door " + identification + " released");
    }
}
