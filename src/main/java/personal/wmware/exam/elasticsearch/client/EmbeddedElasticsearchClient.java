package personal.wmware.exam.elasticsearch.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.lucene.queryparser.surround.query.AndQuery;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig.IndexSettings;

import javax.annotation.Nullable;
import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static personal.wmware.exam.common.Consts.OWNERS_SETTINGS;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmbeddedElasticsearchClient implements ElasticsearchClient {
    private final Client client;
    private final ObjectMapper mapper;
    private final EmbeddedElasticConfig config;

    public <T> void insertDocument(T object, String index, String type, String id) throws JsonProcessingException {
        String content = mapper.writeValueAsString(object);
        this.client.index(new IndexRequest(index).id(id).type(type).source(content, XContentType.JSON));
    }

    public <T> void updateJsonField(T object, String index, String id) throws JsonProcessingException {
        String content = mapper.writeValueAsString(object);
        this.client.update(new UpdateRequest(index, id).doc(content, XContentType.JSON));
    }

    public void createIndex(String name) throws ExecutionException, InterruptedException {
        this.client.admin().indices().create(new CreateIndexRequest(name)).get();
    }

    public boolean deleteIndex(String name) throws ExecutionException, InterruptedException {
        IndicesAdminClient indicesAdminClient = this.client.admin().indices();
        if (indicesAdminClient.exists(new IndicesExistsRequest(name)).get().isExists()) {
            indicesAdminClient.delete(new DeleteIndexRequest(name)).get();
            return true;
        }
        return false;
    }

    public void deleteDocument(String index, String id) throws InterruptedException, ExecutionException, TimeoutException {
        this.client.delete(new DeleteRequest(index, id)).get(5, TimeUnit.SECONDS);
    }

    public SearchResponse searchByField(String index, Map<String, Object> query) throws InterruptedException, ExecutionException, TimeoutException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
        }
        SearchRequest searchRequest = new SearchRequest(index)
                .source(new SearchSourceBuilder()
                        .query(boolQueryBuilder));
        return this.client.search(searchRequest).get(5, TimeUnit.SECONDS);
    }

    public IndexSettings getIndicesSettings(String name) {
        return config.getIndexSettings().get(name);
    }

}
