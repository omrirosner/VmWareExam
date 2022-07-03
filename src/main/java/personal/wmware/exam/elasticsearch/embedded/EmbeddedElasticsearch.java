package personal.wmware.exam.elasticsearch.embedded;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.transport.Netty4Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static java.lang.ClassLoader.getSystemResourceAsStream;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class EmbeddedElasticsearch {
    private static class PluginConfigurableNode extends Node {

        protected PluginConfigurableNode(Settings settings, Collection<Class<? extends Plugin>> classpathPlugins) {
            super(InternalSettingsPreparer.prepareEnvironment(settings, new HashMap<>(), null, null), classpathPlugins, false);
        }
    }

    @Bean
    public Client client() throws NodeValidationException {
        Settings settings = Settings.builder()
                .put("http.cors.enabled", "true")
                .put("node.name", "es-node")
                .put("path.data", "target/elasticsearch/data")
                .put("transport.type", "netty4")
                .put("path.home", "target/elasticsearch")
                .put("http.type", "netty4")
                .build();

        Node node = new PluginConfigurableNode(settings, Collections.singleton(Netty4Plugin.class));
        node.start();
        return node.client();
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        HttpHost[] hosts = Arrays.stream(new String[]{"localhost:9200"})
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);

        return new RestHighLevelClient(RestClient.builder(hosts));
    }


}
