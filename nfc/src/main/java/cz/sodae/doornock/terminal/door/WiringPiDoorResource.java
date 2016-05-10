package cz.sodae.doornock.terminal.door;

import java.io.IOException;

public class WiringPiDoorResource implements DoorResource {
    int gpioPin;

    boolean closeIsZero;

    boolean isOutput;

    /**
     * @param gpioPin by wiringPi annotation, supports: 0,2,3,4 and 5
     */
    public WiringPiDoorResource(int gpioPin, boolean closeIsZero, boolean isOutput) throws UnsupportedPinException {
        if (!(gpioPin == 0 || gpioPin >= 2 && gpioPin <= 5)) {
            throw new UnsupportedPinException(gpioPin);
        }
        this.gpioPin = gpioPin;
        this.closeIsZero = closeIsZero;
        this.isOutput = isOutput;
    }

    public void init() {
        try {
            String type = isOutput ? "output" : "input";
            Runtime.getRuntime().exec("gpio mode " + gpioPin + " " + type).waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        close();
    }

    public void open() {
        writeState(closeIsZero);
    }

    public void close() {
        writeState(!closeIsZero);
    }

    private void writeState(boolean state) {
        try {
            Runtime.getRuntime().exec("gpio write " + gpioPin + " " + (state ? "1" : "0"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void release() {

    }

    public class UnsupportedPinException extends Exception {
        public UnsupportedPinException(int i) {
            super("Unsupported pin " + i + ". Supported pin is only 0 and 2 to 5 by WiringPi");
        }
    }


}
