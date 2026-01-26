package com.wordiam.openelifba.domain.exception

import java.util.UUID

sealed class DomainException(message: String) : RuntimeException(message) {
    class CategoryNotFoundException(id: UUID) : DomainException("Category not found: $id")
    class ExerciseNotFoundException(id: UUID) : DomainException("Exercise not found: $id")
    class InvalidOperationException(reason: String) : DomainException(reason)
}
