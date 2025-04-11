package site.FitUp.main.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EnvironmentUtil {
    private final Environment env;

    public EnvironmentUtil(Environment env) {
        this.env = env;
    }

    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    public boolean isDev() {
        return Arrays.asList(env.getActiveProfiles()).contains("dev");
    }
}