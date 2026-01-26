package com.wordiam.openelifba.infra.controller

import com.wordiam.openelifba.domain.exception.DomainException
import com.wordiam.openelifba.infra.controller.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.warn("Domain exception occurred: {}", ex.message)
        
        val status = when (ex) {
            is DomainException.CategoryNotFoundException,
            is DomainException.ExerciseNotFoundException -> HttpStatus.NOT_FOUND
            is DomainException.InvalidOperationException -> HttpStatus.BAD_REQUEST
        }
        
        val errorResponse = ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message ?: "Domain error",
            path = request.getDescription(false).replace("uri=", "")
        )
        
        return ResponseEntity(errorResponse, status)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val errorResponse = ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = "An unexpected error occurred",
            path = request.getDescription(false).replace("uri=", "")
        )
        
        return ResponseEntity(errorResponse, status)
    }
}
