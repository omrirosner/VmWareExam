package personal.wmware.exam.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import personal.wmware.exam.common.ActionResponse;
import personal.wmware.exam.common.CommonConfig;
import personal.wmware.exam.elasticsearch.client.ElasticsearchClient;
import personal.wmware.exam.users.UserType;
import personal.wmware.exam.users.customer.CreditCard;
import personal.wmware.exam.users.customer.PaymentMethods;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentMethodsController extends BaseController {
    private final ElasticsearchClient elasticsearchClient;
    private final Validator validator;

    @PutMapping(value = "/payment/creditCard", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse updateItem(@Valid @RequestBody CreditCard request, @Valid @RequestHeader("userId") String userId, @RequestHeader("password") String password) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        if (userId == null || password == null) {
            return new ActionResponse("must pass userId and password headers", false);
        }
        if (this.validator.authenticate(userId, password, UserType.CUSTOMER)) {
            this.updateCreditCard(request, userId);
            return new ActionResponse(String.format("updated user %s with credit card", userId), true);
        } else {
            return new ActionResponse("wrong username or password", false);

        }
    }


    private void updateCreditCard(CreditCard creditCard, String userId) throws JsonProcessingException {
        PaymentMethods paymentMethods = new PaymentMethods(creditCard);
        this.elasticsearchClient.updateJsonField(
                paymentMethods,
                "customers", userId);
    }
}
