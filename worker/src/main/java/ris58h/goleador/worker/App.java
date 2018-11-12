package ris58h.goleador.worker;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public class App {

    public static void main(String[] args) {
        Function<String, Optional<String>> appProperties = appProperties(args.length == 1 ? args[0] : null);

        Worker worker = new Worker(
                appProperties.apply("rabbitmq.uri").get()
        );
        try {
            worker.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            worker.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Function<String, Optional<String>> appProperties(String propertiesPath) {
        Properties properties = new Properties();
        if (propertiesPath != null) {
            try {
                properties.load(new FileInputStream(propertiesPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return key -> Optional.ofNullable(properties.getProperty(key));
    }
}
