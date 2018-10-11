package ris58h.goleador.processor;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public interface Parameters extends Function<String, Optional<String>> {

    static Parameters fromInputStream(InputStream inputStream) throws Exception {
        Properties properties = new Properties();
        properties.load(inputStream);
        return (key) -> Optional.ofNullable(properties.getProperty(key));
    }

    static Parameters subParameters(Parameters target, String base) {
        return (key) -> target.apply(base + "." + key);
    }

    static Parameters empty() {
        return (key) -> Optional.empty();
    }

    static Parameters combine(Parameters first, Parameters second) {
        return (key) -> {
            Optional<String> val = first.apply(key);
            return val.isPresent() ? val : second.apply(key);
        };
    }
}
