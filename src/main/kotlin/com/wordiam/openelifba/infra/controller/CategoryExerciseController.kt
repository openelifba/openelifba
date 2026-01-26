package com.wordiam.openelifba.infra.controller

import com.wordiam.openelifba.application.GetDueExercisesUseCase
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.user.UserId
import com.wordiam.openelifba.infra.controller.dto.CategoryExerciseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Exercises", description = "Operations related to exercises within categories")
class CategoryExerciseController(
    private val getDueExercisesUseCase: GetDueExercisesUseCase,
) {
    @Operation(summary = "Get due exercises", description = "Retrieves exercises that are due for review in a specific category")
    @GetMapping("/{categoryId}/exercises/due")
    fun getDueExercises(
        @RequestHeader("x-user-id") userId: UUID,
        @PathVariable categoryId: UUID,
    ): List<CategoryExerciseDto> =
        getDueExercisesUseCase
            .execute(UserId(userId), CategoryId(categoryId))
            .map { CategoryExerciseDto.from(it) }
}
