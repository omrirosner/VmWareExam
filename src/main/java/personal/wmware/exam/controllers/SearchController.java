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
import org.springframework.web.bind.annotation.*;
import personal.wmware.exam.catalogs.CatalogFetchRequest;
import personal.wmware.exam.catalogs.CatalogSearchRequest;
import personal.wmware.exam.common.Utils;
import personal.wmware.exam.common.Validator;
import personal.wmware.exam.common.config.CommonConfig;
import personal.wmware.exam.common.responses.ActionResponse;
import personal.wmware.exam.common.responses.ItemSearchResponse;
import personal.wmware.exam.elasticsearch.client.ElasticsearchClient;
import personal.wmware.exam.items.BuyItemRequest;
import personal.wmware.exam.items.ItemModel;
import personal.wmware.exam.users.UserType;
import personal.wmware.exam.users.customer.UserDocument;

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
    private final Utils utils;
    private final ObjectMapper mapper;
    private final Validator validator;


    @GetMapping(value = "/catalog/{catalogName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemSearchResponse searchCatalog(@Valid @RequestBody CatalogSearchRequest request, @PathVariable String catalogName) throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {
        CatalogFetchRequest fetchRequest = CatalogFetchRequest
                .builder()
                .description(request.getDescription())
                .name(request.getName()).build();
        SearchResponse searchResponse = this.elasticsearchClient.searchByField(utils.getCatalogIndex(catalogName),
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

    @PostMapping(value = "/buy/{catalog}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse buyItem(@Valid @RequestHeader("userId") String userId, @RequestHeader("password") String password, @PathVariable String catalog, @Valid @RequestBody BuyItemRequest request) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        if (!this.validator.authenticate(userId, password, UserType.CUSTOMER)) {
            return new ActionResponse("wrong username or password", false);
        }
        if (!this.checkIfUserHasPaymentMethod(userId)) {
            return new ActionResponse("this user has no payment method", false);
        }
        ItemModel item = queryItem(catalog, request.getItemId());
        if (item.getStock() < request.getAmount()) {
            return new ActionResponse("this item is out of stock", false);
        }
        this.updateItemStock(item, catalog, request.getAmount());
        item.setPriceAfterDiscount(request.getAmount());
        return new ActionResponse(String.format("bought %s %s(s) with price %s$", request.getAmount(), item.getName(), item.getPriceAfterDiscount()), true);

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

    private void updateItemStock(ItemModel item, String catalog, int amount) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        item.setStock(item.getStock() - amount);
        this.elasticsearchClient.insertDocument(item, this.utils.getCatalogIndex(catalog), "item", item.getId());
    }

    private boolean checkIfUserHasPaymentMethod(String userId) throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {
        SearchHit[] hits = this.elasticsearchClient.searchByField(
                "customers", Map.of("id", userId)).getHits().getHits();
        UserDocument userDocument = mapper.readValue(hits[0].getSourceAsString(), UserDocument.class);
        return !Objects.isNull(userDocument.getCreditCard());
    }

    private ItemModel queryItem(String catalog, String itemId) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        String hit = this.elasticsearchClient.getById(this.utils.getCatalogIndex(catalog), itemId).getSourceAsString();
        return mapper.readValue(hit, ItemModel.class);
    }
}
