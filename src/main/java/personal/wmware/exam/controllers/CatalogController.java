package personal.wmware.exam.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import personal.wmware.exam.catalogs.CreateCatalogRequest;
import personal.wmware.exam.common.ActionResponse;
import personal.wmware.exam.common.CommonConfig;
import personal.wmware.exam.elasticsearch.client.ElasticsearchClient;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CatalogController extends BaseController {
    private final ElasticsearchClient elasticsearchClient;
    private final CommonConfig config;

    @PostMapping(value = "/catalog/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse createCatalog(@RequestHeader("userId") String userId, @RequestHeader("password") String password, @Valid @RequestBody CreateCatalogRequest request) throws JsonProcessingException {
        String catalogIndex = this.insertNewCatalog(request.getCatalogName());
        return new ActionResponse(String.format("created index %s", catalogIndex), true);
    }

    private String insertNewCatalog(String name) {
        String indexName = String.format("%s%s", config.getCatalogPrefix(), name);
        this.elasticsearchClient.createIndex(indexName);
        return indexName;
    }


}
