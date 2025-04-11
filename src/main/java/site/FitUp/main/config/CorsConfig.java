package site.FitUp.main.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import site.FitUp.main.util.EnvironmentUtil;

@Configuration
public class CorsConfig {
    private final EnvironmentUtil envUtil;

    public CorsConfig(EnvironmentUtil envUtil) {
        this.envUtil = envUtil;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

//        config.addAllowedOriginPattern("*");
        if (envUtil.isProd()) {
            config.addAllowedOrigin("https://www.fitup.space");
        } else if (envUtil.isDev()) {
            config.addAllowedOrigin("http://localhost:3000");
            config.addAllowedOrigin("https://dev.fitup.space");
        }
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
