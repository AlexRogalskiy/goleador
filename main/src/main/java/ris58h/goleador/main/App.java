package ris58h.goleador.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public class App {
    public static void main(String[] args) {
        Function<String, Optional<String>> appProperties = appProperties(args.length == 1 ? args[0] : null);

        YoutubeAccess youtubeAccess = new YoutubeAccess(appProperties.apply("youtube.apiKey").get());
        DataAccess dataAccess = new DataAccess(
                appProperties.apply("datasource.url").get(),
                appProperties.apply("datasource.username").get(),
                appProperties.apply("datasource.password").get()
        );
        Producer producer = new Producer(youtubeAccess, dataAccess);
        appProperties.apply("producer.checkChannelsDelay").map(Long::parseLong).ifPresent(producer::setCheckChannelsDelay);
        appProperties.apply("producer.maxVideoDuration").map(Long::parseLong).ifPresent(producer::setMaxVideoDuration);
        appProperties.apply("producer.channelCheckInterval").map(Long::parseLong).ifPresent(producer::setChannelCheckInterval);
        appProperties.apply("producer.newChannelGap").map(Long::parseLong).ifPresent(producer::setNewChannelGap);
        appProperties.apply("producer.checkDefinitionDelay").map(Long::parseLong).ifPresent(producer::setCheckDefinitionDelay);
        appProperties.apply("producer.definitionGap").map(Long::parseLong).ifPresent(producer::setDefinitionGap);
        YoutubeCommenter youtubeCommenter = new YoutubeCommenter(
                appProperties.apply("google.app.clientId").get(),
                appProperties.apply("google.app.clientSecret").get(),
                appProperties.apply("google.app.refreshToken").get()
        );
        Commenter commenter = new Commenter(dataAccess, youtubeCommenter);
        appProperties.apply("commenter.delay").map(Long::parseLong).ifPresent(commenter::setDelay);
        try {
            dataAccess.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        producer.start();
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
        return key -> Optional.ofNullable(properties.getProperty(key));
    }
}
