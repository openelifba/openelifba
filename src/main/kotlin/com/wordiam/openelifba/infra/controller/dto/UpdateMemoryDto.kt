package com.wordiam.openelifba.infra.controller.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.util.UUID

data class UpdateMemoryDto(
    @field:NotNull
    val categoryId: UUID,
    @field:NotNull
    val exerciseId: UUID,
    @field:NotNull
    val success: Boolean,
    @field:Positive
    val responseTimeMillis: Long,
)
