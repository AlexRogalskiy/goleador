package ris58h.goleador.producer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class App {

    public static void main(String[] args) {
        Function<String, Optional<String>> appProperties = appProperties(args.length == 1 ? args[0] : null);

        YoutubeAccess youtubeAccess = new YoutubeAccess(appProperties.apply("youtube.apiKey").get());
        DataAccess dataAccess = new DataAccess(
                appProperties.apply("datasource.url").get(),
                appProperties.apply("datasource.username").get(),
                appProperties.apply("datasource.password").get());
        Producer producer = new Producer(youtubeAccess, dataAccess);
        appProperties.apply("producer.delay").map(Long::parseLong).ifPresent(producer::setDelay);
        appProperties.apply("producer.maxVideoDuration").map(Long::parseLong).ifPresent(producer::setMaxVideoDuration);
        appProperties.apply("producer.channelCheckInterval").map(Long::parseLong).ifPresent(producer::setChannelCheckInterval);
        appProperties.apply("producer.newChannelGap").map(Long::parseLong).ifPresent(producer::setNewChannelGap);
        try {
            dataAccess.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        producer.start();
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
