package com.wordiam.openelifba.infra.controller

import com.wordiam.openelifba.domain.exception.DomainException
import com.wordiam.openelifba.infra.controller.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(
        ex: DomainException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Domain exception occurred: {}", ex.message)

        val status =
            when (ex) {
                is DomainException.CategoryNotFoundException,
                is DomainException.ExerciseNotFoundException,
                -> HttpStatus.NOT_FOUND
                is DomainException.InvalidOperationException -> HttpStatus.BAD_REQUEST
            }

        val errorResponse =
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = ex.message ?: "Domain error",
                path = request.getDescription(false).replace("uri=", ""),
            )

        return ResponseEntity(errorResponse, status)
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingRequestHeader(
        ex: MissingRequestHeaderException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Missing request header: {}", ex.headerName)

        val status = HttpStatus.BAD_REQUEST
        val errorResponse =
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = "Missing required header: ${ex.headerName}",
                path = request.getDescription(false).replace("uri=", ""),
            )

        return ResponseEntity(errorResponse, status)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid argument type: {}", ex.message)

        val status = HttpStatus.BAD_REQUEST
        val errorResponse =
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = "Invalid value for parameter: ${ex.name}",
                path = request.getDescription(false).replace("uri=", ""),
            )

        return ResponseEntity(errorResponse, status)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid request body: {}", ex.message)

        val status = HttpStatus.BAD_REQUEST
        val errorResponse =
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = "Invalid or missing request body",
                path = request.getDescription(false).replace("uri=", ""),
            )

        return ResponseEntity(errorResponse, status)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)

        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val errorResponse =
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = "An unexpected error occurred",
                path = request.getDescription(false).replace("uri=", ""),
            )

        return ResponseEntity(errorResponse, status)
    }
}
