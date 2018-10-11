package ris58h.goleador.processor;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public interface Props extends Function<String, Optional<String>> {

    static Props fromInputStream(InputStream inputStream) throws Exception {
        Properties properties = new Properties();
        properties.load(inputStream);
        return (key) -> Optional.ofNullable(properties.getProperty(key));
    }

    static Props subProps(Props target, String base) {
        return (key) -> target.apply(base + "." + key);
    }
}
