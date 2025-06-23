package com.rfid.platform.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionModel handleRuntimeException(HttpServletRequest request, Exception ex) {
        return getExceptionModel(HttpStatus.INTERNAL_SERVER_ERROR, request, ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionModel handleException(HttpServletRequest request, Exception ex) {
        return getExceptionModel(HttpStatus.INTERNAL_SERVER_ERROR, request, ex);
    }


    private ExceptionModel getExceptionModel(HttpStatus httpStatus, HttpServletRequest request, Exception ex) {
        String message = ex.getMessage();
        ExceptionModel exceptionModel = new ExceptionModel();
        exceptionModel.setErrorCode(String.valueOf(httpStatus.value()));
        exceptionModel.setErrorMessage(message);
        exceptionModel.setUrl(request.getRequestURL() == null ? "" : request.getRequestURL().toString());
        return exceptionModel;

    }

}