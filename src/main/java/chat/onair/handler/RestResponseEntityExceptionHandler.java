package chat.onair.handler;

import chat.onair.response.Error;
import chat.onair.response.Response;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ RepositoryConstraintViolationException.class })
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {

        RepositoryConstraintViolationException exception = (RepositoryConstraintViolationException) ex;

        String errors = "";
        List<FieldError> fieldErrorList = exception.getErrors().getFieldErrors();

        System.out.println(fieldErrorList.size());

        for(int i = 0; i < fieldErrorList.size()-1; ++i){
            errors += fieldErrorList.get(i).getDefaultMessage() + ", ";
        }
        errors += fieldErrorList.get(fieldErrorList.size()-1).getDefaultMessage();

        return new ResponseEntity<>(new Error(errors),
                                    new HttpHeaders(),
                                    HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(  HttpMessageNotReadableException ex,
                                                                    HttpHeaders headers,
                                                                    HttpStatus status,
                                                                    WebRequest request){

        return new ResponseEntity<>(new Error(Response.NoSuchStatus),
                                    new HttpHeaders(),
                                    HttpStatus.BAD_REQUEST);
    }
}
