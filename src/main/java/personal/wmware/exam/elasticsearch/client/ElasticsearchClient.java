package personal.wmware.exam.elasticsearch.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig.IndexSettings;

public interface ElasticsearchClient {
    <T> void insertDocument(T object, String index, String type, String id) throws JsonProcessingException;

    IndexSettings getIndicesSettings(String name);

    public <T> void createIndex(String name);
}
