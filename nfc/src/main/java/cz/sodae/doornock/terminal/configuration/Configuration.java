package cz.sodae.doornock.terminal.configuration;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class Configuration {

    /**
     * Load configuration from YAML file, could be bundled for development
     *
     * @param filename
     * @throws FileNotFoundException
     */
    public static ConfigDef loadConfig(String filename) throws FileNotFoundException {
        File file;
        file = new File(filename);
        if (!file.exists()) {
            System.out.println("Load bundled config.yaml");

            URL url = Configuration.class.getClassLoader().getResource("config.yaml");
            file = new File(url.getFile());
        }

        InputStream input = new FileInputStream(file);
        return (ConfigDef) parser().load(input);
    }

    private static Yaml parser() {
        Constructor constructor = new Constructor(ConfigDef.class);//Car.class is root
        TypeDescription carDescription = new TypeDescription(DoorDef.class);
        carDescription.putListPropertyType("doors", DoorDef.class);
        constructor.addTypeDescription(carDescription);
        return new Yaml(constructor);
    }

}
