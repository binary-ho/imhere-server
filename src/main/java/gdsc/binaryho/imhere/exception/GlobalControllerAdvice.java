package gdsc.binaryho.imhere.exception;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ImhereException.class)
    public ResponseEntity<ErrorResponse> imhereExceptionHandler(HttpServletRequest request, ImhereException error) {

        log.info("[ImhereException] Method : {}, RequestURI : {}, Exception : {}, Message : {}",
            request::getMethod, request::getRequestURI,
            () -> error.getClass().getSimpleName(), () -> error.getErrorInfo().getMessage());
        return ResponseEntity
            .status(error.getErrorInfo().getHttpStatus())
            .body(new ErrorResponse(error.getErrorInfo().getCode(), error.getErrorInfo().getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(HttpServletRequest request, RuntimeException error) {

        log.info("[RuntimeException] Method : {}, RequestURI : {}, Exception : {}, Message : {}",
            request::getMethod, request::getRequestURI,
            () -> error.getClass().getSimpleName(), error::getMessage);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(HttpStatus.NO_CONTENT.value(), error.getMessage()));
    }
}
