package personal.wmware.exam.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import personal.wmware.exam.catalogs.CatalogFetchRequest;
import personal.wmware.exam.catalogs.CatalogSearchRequest;
import personal.wmware.exam.common.CommonConfig;
import personal.wmware.exam.common.ItemSearchResponse;
import personal.wmware.exam.elasticsearch.client.ElasticsearchClient;
import personal.wmware.exam.items.ItemModel;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RestController
@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SearchController extends BaseController {
    private final ElasticsearchClient elasticsearchClient;
    private final CommonConfig config;
    private final ObjectMapper mapper;

    @GetMapping(value = "/catalog/{catalogName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemSearchResponse searchCatalog(@Valid @RequestBody CatalogSearchRequest request, @PathVariable String catalogName) throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {
        CatalogFetchRequest fetchRequest = CatalogFetchRequest
                .builder()
                .description(request.getDescription())
                .name(request.getName()).build();
        SearchResponse searchResponse = this.elasticsearchClient.searchByField(String.format("%s%s", this.config.getCatalogPrefix(), catalogName),
                mapper.convertValue(fetchRequest, new TypeReference<Map<String, Object>>() {
                }));

        SearchHit[] hits = searchResponse.getHits().getHits();
        List<ItemModel> results = new ArrayList<>();
        for (SearchHit hit : hits) {
            ItemModel itemModel = mapper.readValue(hit.getSourceAsString(), ItemModel.class);
            results.add(itemModel);
        }
        results = this.filterByPrice(results, request.getMaxPrice(), request.getMinPrice());
        if (request.isMustBeInStock()) {
            results = this.filterIsInStock(results);
        }
        return new ItemSearchResponse(results);
    }

    private List<ItemModel> filterByPrice(List<ItemModel> items, Integer max, Integer min) {
        return items.stream()
                .filter(item -> item.getPrice() < Objects.requireNonNullElse(max, Integer.MAX_VALUE)
                        && item.getPrice() > Objects.requireNonNullElse(min, Integer.MIN_VALUE))
                .collect(Collectors.toList());
    }

    private List<ItemModel> filterIsInStock(List<ItemModel> items) {
        return items.stream().filter(item -> item.getStock() > 0).collect(Collectors.toList());
    }
}
