package com.wordiam.openelifba.infra.controller

import com.wordiam.openelifba.application.GetCategoriesUseCase
import com.wordiam.openelifba.domain.user.UserId
import com.wordiam.openelifba.infra.controller.dto.CategoryDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Operations related to exercise categories")
class CategoryController(
    private val getCategoriesUseCase: GetCategoriesUseCase,
) {
    @Operation(summary = "Get all categories", description = "Retrieves a list of all exercise categories with user-specific statistics")
    @GetMapping
    fun getCategories(
        @RequestHeader("x-user-id") userId: UUID,
    ): List<CategoryDto> {
        val categories = getCategoriesUseCase.execute(UserId(userId))
        return categories.map { CategoryDto.from(it) }
    }
}
