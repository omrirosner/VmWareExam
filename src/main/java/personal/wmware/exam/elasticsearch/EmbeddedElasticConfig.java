package personal.wmware.exam.elasticsearch;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class EmbeddedElasticConfig {
    private Map<String, IndexSettings> indexSettings;

    @Data
    public static class IndexSettings {
        private String name;
        private String type;
        private String mappingFile;
    }
}
