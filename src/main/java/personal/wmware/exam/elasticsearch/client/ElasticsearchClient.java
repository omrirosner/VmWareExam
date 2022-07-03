package personal.wmware.exam.elasticsearch.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.action.search.SearchResponse;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig.IndexSettings;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ElasticsearchClient {
    <T> void insertDocument(T object, String index, String type, String id) throws JsonProcessingException;

    IndexSettings getIndicesSettings(String name);

    public SearchResponse searchByField(String index, Map<String, String> query) throws InterruptedException, ExecutionException, TimeoutException;

    public <T> void createIndex(String name) throws ExecutionException, InterruptedException;

    public boolean deleteIndex(String name) throws ExecutionException, InterruptedException;

    public void deleteDocument(String index, String id) throws InterruptedException, ExecutionException, TimeoutException;

}
