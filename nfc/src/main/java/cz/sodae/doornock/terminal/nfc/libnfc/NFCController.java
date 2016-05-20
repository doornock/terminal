package cz.sodae.doornock.terminal.nfc.libnfc;

import cz.sodae.doornock.terminal.nfc.NFCCommunication;
import cz.sodae.doornock.terminal.nfc.NFCHandler;
import cz.sodae.doornock.terminal.nfc.UID;
import cz.sodae.doornock.terminal.nfc.handlers.MultipleHandler;
import cz.sodae.hardware.nfc.NFCISO14443ATarget;

import java.io.IOException;
import java.util.Arrays;


/**
 * NFC controller which running as thread
 * <p/>
 * Do NOT create two instances if NFC controller running!
 */
public class NFCController {

    private NFCHandler handler;

    private Thread thread;

    public NFCController() {
        this.handler = new MultipleHandler(); // empty
    }

    public void setHandler(NFCHandler handler) {
        this.handler = handler;
    }

    private boolean isRunning = true;

    static {
        try {
            System.loadLibrary("hardware_nfc");
            System.out.println("NFC library loaded");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Unable load NFC library");
            e.printStackTrace();
        }
    }


    public synchronized void start() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(new Process());
            thread.start();
        }
        isRunning = true;
    }

    public synchronized void stop() {
        isRunning = false;
    }

    private class Process implements Runnable {
        private cz.sodae.hardware.nfc.NFCController device;

        public void run() {
            System.out.print("Run!");
            while (isRunning) {

                try {
                    if (device == null) {
                        device = new cz.sodae.hardware.nfc.NFCController();
                    }
                    Communication communication;

                    NFCISO14443ATarget target = device.selectISO14443A();

                    byte[] UIDBytes = target.getUid();
                    if (!isRunning) {
                        break; // because this is thread, and device cannot be stopped while searching target
                    }

                    if (!target.isTransmissionProtocolCompliant()) {
                        System.out.println("Device does not support ISO 14443-4");
                    }

                    if (UIDBytes.length > 0) {
                        communication = new Communication(device, UID.createByBytes(UIDBytes));
                    } else {
                        communication = new Communication(device);
                    }

                    if (!handler.handle(communication)) {
                        System.err.println("Device did NOT handled");
                    }

                } catch (IOException e) {
                    System.err.println("NFC device error:" + e.getMessage());
                    if (device != null) {
                        device.close();
                        device.destruct();
                        device = null;
                    }
                }
            }
            if (device != null) {
                device.close();
                device.destruct();
                device = null;
            }
        }

    }


    class Communication implements NFCCommunication {
        private UID uid;

        cz.sodae.hardware.nfc.NFCController device;

        public Communication(cz.sodae.hardware.nfc.NFCController device) {
            this.device = device;
        }

        public Communication(cz.sodae.hardware.nfc.NFCController device, UID uid) {
            this(device);
            this.uid = uid;
        }

        public UID getDeviceUID() {
            return uid;
        }

        public NFCCommunication.APDUResponse sendCommand(byte[] command) throws IOException {
            return new Response(this.device.sendAPDU(command));
        }

        class Response implements NFCCommunication.APDUResponse {
            byte[] response;
            byte[] origin;

            byte sw1;

            byte sw2;

            public Response(byte[] response) {
                this.origin = response;
                this.response = Arrays.copyOfRange(response, 0, response.length - 2);

                this.sw1 = response[response.length - 2];
                this.sw2 = response[response.length - 1];
            }

            public byte[] sw() {
                return new byte[]{sw1(), sw2()};
            }

            public byte sw1() {
                return this.sw1;
            }

            public byte sw2() {
                return this.sw2;
            }

            public byte[] bytes() {
                return origin;
            }

            public byte[] response() {
                return response;
            }

            public long length() {
                return response.length;
            }

            public long lengthWithSW() {
                return origin.length;
            }
        }

    }


}
