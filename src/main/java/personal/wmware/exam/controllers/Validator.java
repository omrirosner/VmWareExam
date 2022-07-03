package personal.wmware.exam.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import personal.wmware.exam.common.CommonConfig;
import personal.wmware.exam.common.Consts;
import personal.wmware.exam.elasticsearch.client.ElasticsearchClient;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Validator {
    private final ElasticsearchClient elasticsearchClient;
    private final EmbeddedElasticConfig elasticConfig;

    public boolean checkForOwner(String ownerId, String password) throws InterruptedException, ExecutionException, TimeoutException {
        SearchHits hits = this.elasticsearchClient
                .searchByField(elasticConfig.getIndexSettings().get(Consts.OWNERS_SETTINGS).getName(),
                        Map.of("id", ownerId, "password", password)).getHits();

        return hits.getTotalHits().value > 0;
    }
}
