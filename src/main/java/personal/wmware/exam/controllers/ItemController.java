package personal.wmware.exam.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import personal.wmware.exam.common.Utils;
import personal.wmware.exam.common.Validator;
import personal.wmware.exam.common.responses.ActionResponse;
import personal.wmware.exam.elasticsearch.client.ElasticsearchClient;
import personal.wmware.exam.items.CreateItemRequest;
import personal.wmware.exam.items.ItemModel;
import personal.wmware.exam.users.UserType;

import javax.validation.Valid;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController extends BaseController {
    private final ElasticsearchClient elasticsearchClient;
    private final Validator validator;
    private final Utils utils;


    @PostMapping(value = "/item", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse createItem(@Valid @RequestBody CreateItemRequest request, @RequestHeader("userId") String userId, @RequestHeader("password") String password) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        if (this.validator.authenticate(userId, password, UserType.OWNER)) {
            String id = UUID.randomUUID().toString();
            this.insertNewItem(request.getCatalog(), request.getItemModel(), id);
            return new ActionResponse(String.format("created item %s", id), true);
        } else {
            return new ActionResponse("wrong id or password", false);
        }
    }

    @PutMapping(value = "/item/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse updateItem(@Valid @RequestBody CreateItemRequest request, @RequestHeader("userId") String userId, @RequestHeader("password") String password, @PathVariable String itemId) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        if (this.validator.authenticate(userId, password, UserType.OWNER)) {
            this.insertNewItem(request.getCatalog(), request.getItemModel(), itemId);
            return new ActionResponse(String.format("updated item %s", itemId), true);
        } else {
            return new ActionResponse("wrong id or password", false);
        }
    }

    @DeleteMapping(value = "/item/{catalog}/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse deleteCatalog(@RequestHeader("userId") String userId, @RequestHeader("password") String password, @PathVariable String catalog, @PathVariable String itemId) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        if (this.validator.authenticate(userId, password, UserType.OWNER)) {
            boolean success = this.deleteItem(catalog, itemId);
            if (success) {
                return new ActionResponse("deleted item", true);
            } else {
                return new ActionResponse("item or catalog not found", false);
            }
        } else {
            return new ActionResponse("wrong id or password", false);

        }
    }

    private void insertNewItem(String catalog, ItemModel item, String id) throws JsonProcessingException {
        item.setId(id);
        this.elasticsearchClient.insertDocument(
                item,
                utils.getCatalogIndex(catalog), "item",
                id);
    }


    private boolean deleteItem(String catalog, String item) {
        String indexName = utils.getCatalogIndex(catalog);
        try {
            this.elasticsearchClient.deleteDocument(indexName, item);
            return true;
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            log.error("failed to delete index", e);
        }
        return false;
    }
}
