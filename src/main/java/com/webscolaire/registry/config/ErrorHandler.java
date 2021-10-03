package com.webscolaire.registry.config;

import com.webscolaire.registry.exception.ValidationException;
import com.webscolaire.registry.exception.WsRegisterError;
import com.webscolaire.registry.exception.WsRegisterException;
import com.webscolaire.registry.handler.error.BadRequestAlertException;
import com.webscolaire.registry.validator.ValidationErrorDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.webscolaire.registry.exception.WsRegisterError.WS_002;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@ControllerAdvice
@Order(-2)
public class ErrorHandler extends AbstractErrorWebExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    public ErrorHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext) {
        super(errorAttributes, resources, applicationContext);
        setMessageWriters(singletonList(new EncoderHttpMessageWriter<>(new Jackson2JsonEncoder())));
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable throwable = getError(request);
        BadRequestAlertException problem = handleError(throwable);
        return ServerResponse.status(problem.statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(singletonMap("error", problem)));
    }

    public BadRequestAlertException handleError(Throwable throwable) {
        log.error("error", throwable);
        if (throwable instanceof ValidationException) {
            return handleValidationException((ValidationException) throwable);
        } else if (throwable instanceof WsRegisterException) {
            return handleWsRegisterException((WsRegisterException) throwable);
        }  else if ((throwable instanceof ServerWebInputException && ((ServerWebInputException) throwable).getRootCause() instanceof DateTimeParseException) ) {
            return handleDateTimeParseException();
        }
        return new BadRequestAlertException(throwable.getMessage(), "unknownBadRequest");
    }

    private BadRequestAlertException handleWsRegisterException(WsRegisterException exception) {
        WsRegisterError error = exception.error;
        log.error("{}", error);
        return new BadRequestAlertException(error.getCode(), error.getCode());
    }

    private BadRequestAlertException handleValidationException(ValidationException exception) {
        List<ValidationErrorDescriptor> errors = exception.errors;

        Map<String, Object> params = new HashMap<>();
        errors.stream().forEach(validationErrorDescriptor
                -> params.put(validationErrorDescriptor.field, validationErrorDescriptor.message));
        log.error("invalidBeans : {}", params);
        return new BadRequestAlertException(exception.error.getDescription(), "invalidBean", params);
    }

    private BadRequestAlertException handleDateTimeParseException() {
        log.error("invalid field dateOfBirth");
        return new BadRequestAlertException(WS_002.getCode(), WS_002.getCode(), null);
    }

}
