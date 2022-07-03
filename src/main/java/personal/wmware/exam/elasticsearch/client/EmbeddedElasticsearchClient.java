package personal.wmware.exam.elasticsearch.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig.IndexSettings;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

import java.util.Collections;
import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmbeddedElasticsearchClient implements ElasticsearchClient {
    private final Client client;
    private final ObjectMapper mapper;
    private final EmbeddedElasticConfig config;

    public <T> void insertDocument(T object, String index, String type, String id) throws JsonProcessingException {
        String content = mapper.writeValueAsString(object);
        Map<CharSequence, CharSequence> document = Map.of(id, content);
        this.client.index(new IndexRequest(index).type(type).source(content));
    }

    public void createIndex(String name) {
        // I Know this is weird and ugly but the embedded elastic library has a bug
        // with index creation that i've found too late into starting this
        RestTemplate restTemplate = new RestTemplate();

        String uri = "http://localhost:9200";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new BasicHttpEntity();
        restTemplate.put(String.format("%s/%s", uri, name), null);
    }

//    public boolean checkOwner() {
////        this.embeddedElasticl
//    }

    public IndexSettings getIndicesSettings(String name) {
        return config.getIndexSettings().get(name);
    }

}
