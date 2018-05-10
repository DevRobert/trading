package trading.api;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import trading.api.account.FaultResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ClientException.class)
    protected ResponseEntity<Object> handleClientException(ClientException exception, WebRequest request) {
        FaultResponse faultResponse = new FaultResponse();
        faultResponse.setMessage(exception.getMessage());
        return this.handleExceptionInternal(exception, faultResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGenericException(Exception exception, WebRequest request) {
        // TODO write test

        FaultResponse faultResponse = new FaultResponse();

        String message = "Internal server error";

        if(exception.getMessage() != null) {
            message += ": " + exception.getMessage();
        }
        else {
            message += ": " + exception.toString();
        }

        faultResponse.setMessage(message);

        exception.printStackTrace();

        return this.handleExceptionInternal(exception, faultResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
