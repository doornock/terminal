package cz.sodae.doornock.terminal.door;

import com.pi4j.io.gpio.*;

/**
 * @deprecated do not use them, it's example how could be used Pi4J, but this library is unstable
 */
public class Pi4JDoorResouce implements DoorResource {
    int gpioPin;

    protected GpioPinDigitalOutput output;

    protected Pin pin;

    protected GpioController gpio;


    /**
     * @param gpioPin by wiringPi annotation, supports: 0,2,3,4 and 5
     */
    public Pi4JDoorResouce(int gpioPin) throws UnsupportedPinException {
        this.gpioPin = gpioPin;
        if (gpioPin == 0 || gpioPin >= 2 && gpioPin <= 5) {
            throw new UnsupportedPinException(gpioPin);
        }
    }

    public void init() {

        gpio = GpioFactory.getInstance();
        output = gpio.provisionDigitalOutputPin(pin, "Relay ", PinState.HIGH);

        if (gpioPin == 0) pin = RaspiPin.GPIO_00;
        else if (gpioPin == 2) pin = RaspiPin.GPIO_02;
        else if (gpioPin == 3) pin = RaspiPin.GPIO_03;
        else if (gpioPin == 4) pin = RaspiPin.GPIO_04;
        else if (gpioPin == 5) pin = RaspiPin.GPIO_05;
    }

    public void open() {
        output.low();
        output.setState(PinState.LOW); // open
    }

    public void close() {
        output.high();
        output.setState(PinState.HIGH); // close
    }

    public void release() {

    }

    public class UnsupportedPinException extends Exception {
        public UnsupportedPinException(int i) {
            super("Unsupported pin " + i + ". Supported pin is only 1 to 5 by WiringPi");
        }
    }


}
