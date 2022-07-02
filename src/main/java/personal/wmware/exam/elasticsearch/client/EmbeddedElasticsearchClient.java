package personal.wmware.exam.elasticsearch.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig.IndexSettings;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmbeddedElasticsearchClient implements ElasticsearchClient {
    private final EmbeddedElastic embeddedElastic;
    private final ObjectMapper mapper;
    private final EmbeddedElasticConfig config;

    public <T> void insertDocument(T object, String index, String type, String id) throws JsonProcessingException {
        String content = mapper.writeValueAsString(object);
        Map<CharSequence, CharSequence> document = Map.of(id, content);
        this.embeddedElastic.index(index, type, document);
    }

    public IndexSettings getIndicesSettings(String name) {
        return config.getIndexSettings().get(name);
    }

}
