package personal.wmware.exam.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import personal.wmware.exam.common.ActionResponse;
import personal.wmware.exam.common.CommonConfig;
import personal.wmware.exam.elasticsearch.client.ElasticsearchClient;
import personal.wmware.exam.items.ItemModel;
import personal.wmware.exam.users.UserType;
import personal.wmware.exam.users.customer.CreditCard;
import personal.wmware.exam.users.customer.PaymentMethods;
import personal.wmware.exam.users.customer.UserDocument;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentMethodsController extends BaseController {
    private final ElasticsearchClient elasticsearchClient;
    private final Validator validator;
    private final ObjectMapper mapper;
    private final CommonConfig config;

    @PutMapping(value = "/payment/creditCard", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse updateItem(@Valid @RequestBody CreditCard request, @Valid @RequestHeader("userId") String userId, @RequestHeader("password") String password) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        if (this.validator.authenticate(userId, password, UserType.CUSTOMER)) {
            this.updateCreditCard(request, userId);
            return new ActionResponse(String.format("updated user %s with credit card", userId), true);
        } else {
            return new ActionResponse("wrong username or password", false);

        }
    }

    @PostMapping(value = "/buy/{catalog}/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse buyItem(@Valid @RequestHeader("userId") String userId, @RequestHeader("password") String password, @PathVariable String catalog, @PathVariable String itemId) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        if (!this.validator.authenticate(userId, password, UserType.CUSTOMER)) {
            return new ActionResponse("wrong username or password", false);
        }
        if (!this.checkIfUserHasPaymentMethod(userId)) {
            return new ActionResponse("this user has no payment method", false);
        }
        if (!this.checkIfItemIsInStock(catalog, itemId)) {
            return new ActionResponse("this item is out of stock", false);
        }
        this.updateItemStock(catalog, itemId);
        return new ActionResponse("item was bought and removed 1 from stock", true);

    }

    private void updateCreditCard(CreditCard creditCard, String userId) throws JsonProcessingException {
        PaymentMethods paymentMethods = new PaymentMethods(creditCard);
        this.elasticsearchClient.updateJsonField(
                paymentMethods,
                "customers", userId);
    }

    private void updateItemStock(String catalog, String itemId) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        ItemModel item = this.queryItem(catalog, itemId);
        item.setStock(item.getStock() - 1);
        this.elasticsearchClient.insertDocument(item, String.format("%s%s", this.config.getCatalogPrefix(), catalog), "item", itemId);
    }


    private boolean checkIfUserHasPaymentMethod(String userId) throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {
        SearchHit[] hits = this.elasticsearchClient.searchByField(
                "customers", Map.of("id", userId)).getHits().getHits();
        UserDocument userDocument = mapper.readValue(hits[0].getSourceAsString(), UserDocument.class);
        return !Objects.isNull(userDocument.getCreditCard());
    }

    private boolean checkIfItemIsInStock(String catalog, String itemId) throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {
        ItemModel item = this.queryItem(catalog, itemId);
        return item.getStock() > 0;
    }

    private ItemModel queryItem(String catalog, String itemId) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        String hit = this.elasticsearchClient.getById(String.format("%s%s", this.config.getCatalogPrefix(), catalog), itemId).getSourceAsString();
        return mapper.readValue(hit, ItemModel.class);
    }
}
