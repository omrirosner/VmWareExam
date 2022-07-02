package personal.wmware.exam.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import personal.wmware.exam.common.ActionResponse;
import personal.wmware.exam.users.CreateStoreUserRequest;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@Service
@Log4j2
public class UserController {

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponse createUser(@Valid @RequestBody CreateStoreUserRequest request) {
        return new ActionResponse("hello", true);
    }

    @ExceptionHandler(BindException.class)
    private ResponseEntity<ActionResponse> handleInvalidRequest(BindException ex) {
        FieldError fieldError = ex.getFieldError();
        String message = String.format("%s %s", Objects.requireNonNull(fieldError).getField(), fieldError.getDefaultMessage());
        ActionResponse actionResponse = new ActionResponse(message, false);
        log.error(ex);
        return new ResponseEntity<>(actionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ActionResponse> handleUnexpectedError(Exception ex) {
        ActionResponse actionResponse = new ActionResponse("an unexpected error has accursed. Check the logs for more details", false);
        log.error(ex);
        return new ResponseEntity<>(actionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
