package cz.sodae.doornock.terminal.application;

/**
 * Service is started or stopped depends on application state
 * Signals to service which will be started after start, start has to be non-blocking!
 */
public interface Service {
    void start(Application application);

    void stop();
}
