package personal.wmware.exam.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import personal.wmware.exam.catalogs.CatalogRequest;
import personal.wmware.exam.common.ActionResponse;
import personal.wmware.exam.common.CommonConfig;
import personal.wmware.exam.elasticsearch.client.ElasticsearchClient;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CatalogController extends BaseController {
    private final ElasticsearchClient elasticsearchClient;
    private final CommonConfig config;
    private final Validator validator;

    @PostMapping(value = "/catalog/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse createCatalog(@RequestHeader("userId") String userId, @RequestHeader("password") String password, @Valid @RequestBody CatalogRequest request) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        if (this.validator.checkForOwner(userId, password)) {
            String catalogIndex = this.insertNewCatalog(request.getCatalogName());
            return new ActionResponse(String.format("created index %s", catalogIndex), true);
        } else {
            return new ActionResponse("wrong id or password", false);
        }
    }

    @DeleteMapping(value = "/catalog/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse deleteCatalog(@RequestHeader("userId") String userId, @RequestHeader("password") String password, @Valid @RequestBody CatalogRequest request) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        if (this.validator.checkForOwner(userId, password)) {
            boolean success = this.deleteCatalog(request.getCatalogName());
            if (success) {
                return new ActionResponse("deleted catalog", true);
            } else {
                return new ActionResponse("catalog not found", false);
            }
        } else {
            return new ActionResponse("wrong id or password", false);

        }
    }

    private String insertNewCatalog(String name) {
        String indexName = String.format("%s%s", config.getCatalogPrefix(), name);
        try {
            this.elasticsearchClient.createIndex(indexName);
        } catch (ExecutionException | InterruptedException e) {
            log.error("failed to create index", e);
        }
        return indexName;
    }

    private boolean deleteCatalog(String name) {
        String indexName = String.format("%s%s", config.getCatalogPrefix(), name);
        try {
            return this.elasticsearchClient.deleteIndex(indexName);
        } catch (ExecutionException | InterruptedException e) {
            log.error("failed to delete index", e);
        }
        return false;
    }


}
