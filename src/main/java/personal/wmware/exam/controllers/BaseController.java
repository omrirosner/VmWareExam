package personal.wmware.exam.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import personal.wmware.exam.common.ActionResponse;

import java.util.Objects;

@RestController
@Service
@Log4j2
public abstract class BaseController {
    @ExceptionHandler(BindException.class)
    private ResponseEntity<ActionResponse> handleInvalidRequest(BindException ex) {
        FieldError fieldError = ex.getFieldError();
        String message = String.format("%s %s", Objects.requireNonNull(fieldError).getField(), fieldError.getDefaultMessage());
        ActionResponse actionResponse = new ActionResponse(message, false);
        log.error("an error has occurred", ex);
        return new ResponseEntity<>(actionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    private ResponseEntity<ActionResponse> handleMissingHeader(MissingRequestHeaderException ex) {
        ActionResponse actionResponse = new ActionResponse(ex.getHeaderName() + " header is missing", false);
        log.error("an error has occurred", ex);
        return new ResponseEntity<>(actionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ActionResponse> handleUnexpectedError(Exception ex) {
        ActionResponse actionResponse = new ActionResponse("an unexpected error has occurred. Check the logs for more details", false);
        log.error("an error has occurred", ex);
        return new ResponseEntity<>(actionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
