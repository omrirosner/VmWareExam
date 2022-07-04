package personal.wmware.exam.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "common")
@Data
public class CommonConfig {
    private String catalogPrefix;
}
