package cz.sodae.doornock.terminal.httpApi;


import cz.sodae.doornock.terminal.utils.Hmac256;

class Hmac256ApiValidator implements ApiKeyValidator {

    private String apiKey;

    public Hmac256ApiValidator(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean verify(String authKey, String message) {
        try {
            String[] parts = authKey.split(" ");
            if (parts.length != 2) {
                return false;
            }
            int time = Integer.decode(parts[0]);
            long now = (System.currentTimeMillis() / 1000L);
            if (!((now - 10) < time && (now + 10) > time)) { // -+ 10s due to desynchronized time
                return false;
            }
            String calc = Hmac256.calculate(apiKey, parts[0] + "|" + message);
            return calc.equals(parts[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
