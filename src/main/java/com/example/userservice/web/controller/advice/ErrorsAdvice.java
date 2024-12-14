package com.example.userservice.web.controller.advice;

import com.example.userservice.web.controller.exception.BadRequestException;
import com.example.userservice.web.controller.exception.ClientNotFoundException;
import com.example.userservice.web.controller.exception.DuplicateException;
import com.example.userservice.web.controller.exception.ForbiddenException;
import com.example.userservice.web.controller.exception.InternalServerException;
import com.example.userservice.web.controller.exception.NotFoundException;
import com.example.userservice.web.controller.exception.UnauthorizedException;
import com.example.userservice.web.controller.exception.UnprocessableEntityException;
import com.example.userservice.web.controller.exception.ViolationBlockingPeriodException;
import com.example.userservice.web.dto.errors.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorsAdvice {

    @ApiResponse(
            responseCode = "400",
            description = "Не были переданы обязательные входные параметры или во входных параметрах были переданы невалидные данные",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ExceptionHandler({BadRequestException.class, MissingServletRequestParameterException.class,
            ValidationException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(Exception e) {
        ErrorResponseDTO badRequestResponse = new ErrorResponseDTO("Bad Request", e.getMessage());

        return new ResponseEntity<>(badRequestResponse, HttpStatus.BAD_REQUEST);
    }

    @ApiResponse(
            responseCode = "400",
            description = "Во входных параметрах были переданы невалидные данные",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        ErrorResponseDTO badRequestResponse = new ErrorResponseDTO("Bad Request", msg);

        return new ResponseEntity<>(badRequestResponse, HttpStatus.BAD_REQUEST);
    }

    @ApiResponse(
            responseCode = "405",
            description = "Метод запроса не поддерживается",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponseDTO> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        ErrorResponseDTO methodNotSupportedResponse = new ErrorResponseDTO("Method Not Allowed", e.getMessage());

        return new ResponseEntity<>(methodNotSupportedResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ApiResponse(
            responseCode = "500",
            description = "В случае возникновения ошибки на сервере",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalServerException(HttpMessageNotWritableException e) {
        ErrorResponseDTO internalServerResponse = new ErrorResponseDTO("Internal Server", e.getMessage());

        return new ResponseEntity<>(internalServerResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ViolationBlockingPeriodException.class)
    public ResponseEntity<ErrorResponseDTO> handleViolationBlockingPeriodException(Exception e) {
        ErrorResponseDTO violationBlockingPeriodResponse = new ErrorResponseDTO("Forbidden", e.getMessage());

        return new ResponseEntity<>(violationBlockingPeriodResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(NotFoundException e) {
        ErrorResponseDTO internalServerResponse = new ErrorResponseDTO("Not found", e.getMessage());

        return new ResponseEntity<>(internalServerResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateException(DuplicateException e) {
        ErrorResponseDTO internalServerResponse = new ErrorResponseDTO("Duplicate", e.getMessage());

        return new ResponseEntity<>(internalServerResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnprocessableEntityException(UnprocessableEntityException e) {
        ErrorResponseDTO internalServerResponse = new ErrorResponseDTO("Unprocessable Entity", e.getMessage());

        return new ResponseEntity<>(internalServerResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbiddenException(ForbiddenException e) {
        ErrorResponseDTO internalServerResponse = new ErrorResponseDTO(
                "Forbidden " + e.getRemainingSeconds(),
                e.getMessage()
        );

        return new ResponseEntity<>(internalServerResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponseDTO> internalServerException(ForbiddenException e) {
        ErrorResponseDTO internalServerResponse = new ErrorResponseDTO("Internal Server Error", e.getMessage());

        return new ResponseEntity<>(internalServerResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(UnauthorizedException e) {
        ErrorResponseDTO internalServerResponse = new ErrorResponseDTO("Unautorized", e.getMessage());

        return new ResponseEntity<>(internalServerResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(ClientNotFoundException e) {
        ErrorResponseDTO internalServerResponse = new ErrorResponseDTO("Not found", e.getMessage());

        return new ResponseEntity<>(internalServerResponse, HttpStatus.NOT_FOUND);
    }
}