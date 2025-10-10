package club.rentstuff;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOriginPatterns(
                "http://localhost:8090","http://localhost:5173",
                "http://local.rentstuff.com:8090","http://local.rentstuff.com:5173",
                "https://rentstuff.club", "https://www.rentstuff.club")
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("Content-Type", "Authorization","Access-Control-Allow-Origin")
        .allowCredentials(true)
        .maxAge(10);
    }
}
