package com.wordiam.openelifba.infra.controller

import com.wordiam.openelifba.application.UpdateMemoryUseCase
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.ExerciseId
import com.wordiam.openelifba.domain.user.UserId
import com.wordiam.openelifba.infra.controller.dto.UpdateMemoryDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.util.UUID

@RestController
@RequestMapping("/api/memory")
@Tag(name = "Memory", description = "Operations related to spaced repetition memory tracking")
class MemoryController(
    private val updateMemoryUseCase: UpdateMemoryUseCase,
) {
    @Operation(
        summary = "Update memory",
        description = "Records a review attempt for an exercise and updates SRS parameters",
    )
    @PostMapping
    fun updateMemory(
        @RequestHeader("x-user-id") userId: UUID,
        @Valid @RequestBody request: UpdateMemoryDto,
    ) {
        updateMemoryUseCase.execute(
            UpdateMemoryUseCase.UpdateMemoryRequest(
                userId = UserId(userId),
                categoryId = CategoryId(request.categoryId),
                exerciseId = ExerciseId(request.exerciseId),
                success = request.success,
                responseTime = Duration.ofMillis(request.responseTimeMillis),
            ),
        )
    }
}
