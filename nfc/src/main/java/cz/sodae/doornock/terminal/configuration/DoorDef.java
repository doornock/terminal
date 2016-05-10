package cz.sodae.doornock.terminal.configuration;

public class DoorDef {

    private String id;

    private String type;

    private Integer gpio;

    private Boolean closeIsZero;

    private Boolean gpioIsOutput;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getGpio() {
        return gpio;
    }

    public void setGpio(Integer gpio) {
        this.gpio = gpio;
    }

    public Boolean getCloseIsZero() {
        return closeIsZero;
    }

    public void setCloseIsZero(Boolean closeIsZero) {
        this.closeIsZero = closeIsZero;
    }

    public Boolean getGpioIsOutput() {
        return gpioIsOutput;
    }

    public void setGpioIsOutput(Boolean gpioIsOutput) {
        this.gpioIsOutput = gpioIsOutput;
    }
}
