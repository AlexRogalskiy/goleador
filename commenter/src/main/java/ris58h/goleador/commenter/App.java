package ris58h.goleador.commenter;

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
        YoutubeAccess youtubeAccess = new YoutubeAccess(
                appProperties.apply("google.app.clientId").get(),
                appProperties.apply("google.app.clientSecret").get(),
                appProperties.apply("google.app.refreshToken").get());
        Commenter commenter = new Commenter(dataAccess, youtubeAccess);
        appProperties.apply("commenter.delay").map(Long::parseLong).ifPresent(commenter::setDelay);
        try {
            dataAccess.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        commenter.start();
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
        return key -> {
            String property = properties.getProperty(key);
            if (property == null) {
                property = System.getProperty(key);
            }
            return Optional.ofNullable(property);
        };
    }
}
