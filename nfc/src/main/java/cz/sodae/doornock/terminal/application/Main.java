package cz.sodae.doornock.terminal.application;


public class Main {

    public static void main(String[] args) throws Exception {
        Application application = new Application();
        application.configFile("config.yaml");
        application.start();
        application.commandLine();
    }

}
