package cz.sodae.doornock.terminal.httpApi;

public interface ApiKeyValidator {

    /**
     * Verify income message by key (could be sign)
     *
     * @param key     income key
     * @param message income message
     * @return key is verified
     */
    boolean verify(String key, String message);

}
