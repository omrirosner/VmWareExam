package personal.wmware.exam.elasticsearch.embedded;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.node.NodeValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static java.lang.ClassLoader.getSystemResourceAsStream;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class ElasticsearchPreparer {
    private final Client client;
    private final EmbeddedElasticConfig configuration;


    @PostConstruct
    private void setUpData() throws IOException, NodeValidationException, ExecutionException, InterruptedException {
        for (EmbeddedElasticConfig.IndexSettings settings : configuration.getIndexSettings().values()) {
            this.addIndex(settings.getName(), settings.getType(), settings.getMappingFile());
        }
        this.addElasticsearchTemplates("templates/catalog_template.json");
    }

    private void addIndex(String name, String type, String pathToMapping) throws IOException, ExecutionException, InterruptedException {
        InputStream inputStream = getSystemResourceAsStream(String.format("mappings/%s", pathToMapping));
        if (!this.client.admin().indices().exists(new IndicesExistsRequest(name)).get().isExists()) {
            this.client.admin().indices().create(new CreateIndexRequest(name).mapping(type, IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8), XContentType.JSON)).get();
        }
    }

    private void addElasticsearchTemplates(String pathToTemplate) throws IOException {
        InputStream inputStream = getSystemResourceAsStream(pathToTemplate);
        this.client.admin().indices().putTemplate(new PutIndexTemplateRequest(IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8)));
    }
}
