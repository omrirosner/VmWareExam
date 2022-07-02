package personal.wmware.exam.elasticsearch.embedded;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig.IndexSettings;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static java.lang.ClassLoader.getSystemResourceAsStream;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmbeddedElasticsearch {
    private final EmbeddedElasticConfig configuration;

    @Bean
    public EmbeddedElastic embbeddedElasticsearch() throws IOException, InterruptedException {
        EmbeddedElastic.Builder builder = EmbeddedElastic.builder()
                .withElasticVersion("5.0.0")
                .withSetting(PopularProperties.TRANSPORT_TCP_PORT, 9350)
                .withSetting(PopularProperties.CLUSTER_NAME, "local_cluster")
                .withSetting("path.data", "target/elasticsearch");

        for (IndexSettings settings : configuration.getIndexSettings().values()) {
            builder = this.addIndex(builder, settings.getName(), settings.getType(), settings.getMappingFile());
        }

        return builder.build().start();
    }

    private EmbeddedElastic.Builder addIndex(EmbeddedElastic.Builder builder, String name, String type, String pathToMapping) throws IOException {
        InputStream inputStream = getSystemResourceAsStream(pathToMapping);
        try {
            return builder.withIndex(name, pl.allegro.tech.embeddedelasticsearch.IndexSettings.builder()
                    .withType(type, Objects.requireNonNull(inputStream))
                    .build());
        } catch (IOException e) {
            log.error(String.format("failed reading mapping %s for index %s", pathToMapping, name));
            throw e;
        }
    }
}
