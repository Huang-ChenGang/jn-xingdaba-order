package com.jn.xingdaba.order.infrastructure.handler;

import com.jn.core.api.ServerResponse;
import com.jn.core.exception.JNException;
import com.jn.xingdaba.order.infrastructure.exception.OrderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.BAD_REQUEST;
import static com.jn.xingdaba.order.infrastructure.exception.OrderSystemError.ORDER_SYSTEM_ERROR;

@Slf4j
@RestControllerAdvice
public class OrderExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex,
                ServerResponse.error(BAD_REQUEST, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()),
                headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex,
                ServerResponse.error(BAD_REQUEST, "请求参数不能为空"),
                headers, status, request);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({
            OrderException.class
    })
    public ServerResponse<Void> handleLogicError(JNException exception) {
        log.error("resource system logic error", exception);
        return ServerResponse.error(exception.getJNError());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            IllegalStateException.class,
            IllegalArgumentException.class
    })
    public ServerResponse<Void> handleSystemError(RuntimeException exception) {
        log.error("order system error", exception);
        return ServerResponse.error(ORDER_SYSTEM_ERROR);
    }
}
