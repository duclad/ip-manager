package ro.duclad.ipmngr.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ro.duclad.ipmngr.services.exceptions.PoolNotFoundException;

@ControllerAdvice
public class RestApiExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {PoolNotFoundException.class})
    protected ResponseEntity handleNotFoundErrors(RuntimeException e, WebRequest request){
        return handleExceptionInternal(e,e.getCause().getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
