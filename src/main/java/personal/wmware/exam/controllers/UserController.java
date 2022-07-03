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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import personal.wmware.exam.common.ActionResponse;
import personal.wmware.exam.elasticsearch.client.ElasticsearchClient;
import personal.wmware.exam.elasticsearch.embedded.EmbeddedElasticConfig.IndexSettings;
import personal.wmware.exam.users.CreateStoreUserRequest;
import personal.wmware.exam.users.customer.UserDocument;

import javax.validation.Valid;
import java.util.Objects;
import java.util.UUID;

@RestController
@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController extends BaseController{
    private final ElasticsearchClient elasticsearchClient;

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse createUser(@Valid @RequestBody CreateStoreUserRequest request) throws JsonProcessingException {
        String userType = request.getUserType().name().toLowerCase();
        String createdId = this.insertNewUser(userType, request.getUsername(), request.getPassword());
        return new ActionResponse(String.format("created %s with id %s", userType, createdId), true);
    }

    private String insertNewUser(String type, String username, String password) throws JsonProcessingException {
        IndexSettings indicesSettings = elasticsearchClient.getIndicesSettings(type);
        String id = UUID.randomUUID().toString();
        this.elasticsearchClient.insertDocument(
                new UserDocument(id, username, password),
                indicesSettings.getName(), indicesSettings.getType(),
                id);
        return id;
    }
}
