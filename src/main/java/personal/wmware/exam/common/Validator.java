package personal.wmware.exam.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import personal.wmware.exam.common.config.Consts;
import personal.wmware.exam.elasticsearch.client.ElasticsearchClient;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig;
import personal.wmware.exam.users.UserType;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Validator {
    private final ElasticsearchClient elasticsearchClient;
    private final EmbeddedElasticConfig elasticConfig;

    public boolean authenticate(String userId, String password, UserType type) throws InterruptedException, ExecutionException, TimeoutException {
        String index_settings = "nothing";
        if (type.equals(UserType.OWNER)) {
            index_settings = Consts.OWNERS_SETTINGS;
        } else if (type.equals(UserType.CUSTOMER)) {
            index_settings = Consts.CUSTOMER_SETTINGS;
        }
        SearchHits hits = this.elasticsearchClient
                .searchByField(elasticConfig.getIndexSettings().get(index_settings).getName(),
                        Map.of("id", userId, "password", password)).getHits();

        return hits.getTotalHits().value > 0;
    }
}
