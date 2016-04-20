package cz.sodae.doornock.terminal.door;


/**
 * Non blocking lock request, it's mean door is opened async by request and openFor is non blocking
 * <p/>
 * Explain:
 * situation:
 * 1) openFor(500ms) : open for 500ms and then closed
 * 2) openFor(500ms); sleep(100ms); openFor(200ms) : closed after first expand after 500ms: (max(500 - 100, 200) or max(500, 100 + 200))
 * 3) openFor(500ms); sleep(1s); openFor(500ms) : open for 500ms, closed for 500ms; open for 500ms
 */
public class NonBlockingDoorControl implements DoorControl {
    private Thread doorThread;

    private Countdowner countdowner;

    public NonBlockingDoorControl(DoorResource doorResource) {
        countdowner = new Countdowner();
        doorThread = new Thread(new Controller(doorResource, countdowner));
        doorThread.start();
    }

    /**
     * Non blocking
     *
     * @param openingTime time how long will be lock unlocked in milliseconds
     */
    public synchronized void openFor(int openingTime) {
        countdowner.expandTime(openingTime);
    }

    /**
     * Memory which countdown time to have still open the door
     */
    class Countdowner {
        /**
         * How long it will be opened after last modified
         */
        long howLong = 0;

        /**
         * Last time modified in milliseconds
         */
        long at;

        /**
         * Add time (now+time=earliest time to close)
         *
         * @param time in millisconds
         */
        public synchronized void expandTime(int time) {
            howLong = remaining() + time;
            at = System.currentTimeMillis();
            this.notify();
            log("NOTIFY!:" + time);
        }


        private synchronized long remaining() {
            return at == 0 ? 0 : howLong + (at - System.currentTimeMillis());
        }


        public synchronized long fetchSleepTimeOrWait() {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
            return fetchSleepTime();
        }


        public synchronized long fetchSleepTime() {
            long l = Math.max(0, remaining());
            log("FETCH!:" + l);
            howLong = 0;
            at = 0;
            return l;
        }

    }


    private class Controller implements Runnable {
        private Countdowner countdowner;

        private DoorResource doorResource;


        public Controller(DoorResource doorResource, Countdowner countdowner) {
            this.doorResource = doorResource;
            this.countdowner = countdowner;
        }

        public void run() {
            doorResource.init();

            try {
                for (; ; ) {
                    long sleep = countdowner.fetchSleepTimeOrWait();
                    do {
                        log("Sleep:" + sleep);
                        if (sleep == 0) {
                            break;
                        }
                        log("OPEN");
                        doorResource.open();
                        Thread.sleep(sleep);
                        sleep = countdowner.fetchSleepTime();
                    } while (sleep > 0);
                    log("CLOSE");
                    doorResource.close();
                }
            } catch (InterruptedException e) {
                doorResource.release();
            }
        }

    }


    private void log(String message) {
        System.out.print(new java.util.Date());
        System.out.println(": " + message);
    }

}
