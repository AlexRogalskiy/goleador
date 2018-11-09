package ris58h.goleador.worker;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public class App {

    public static void main(String[] args) {
        Function<String, Optional<String>> appProperties = appProperties(args.length == 1 ? args[0] : null);

        DataAccess dataAccess = new DataAccess(
                appProperties.apply("datasource.url").get(),
                appProperties.apply("datasource.username").get(),
                appProperties.apply("datasource.password").get());
        Worker worker = new Worker(dataAccess);
        appProperties.apply("worker.delay").map(Long::parseLong).ifPresent(worker::setDelay);
        try {
            dataAccess.init();
            worker.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        worker.start();
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
