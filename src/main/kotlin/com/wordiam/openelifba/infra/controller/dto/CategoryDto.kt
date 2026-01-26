package com.wordiam.openelifba.infra.controller.dto

import com.wordiam.openelifba.domain.category.Category
import java.util.UUID

data class CategoryDto(
    val id: UUID,
    val name: String,
    val statistic: CategoryStatisticDto,
) {
    companion object {
        fun from(category: Category): CategoryDto =
            CategoryDto(
                id = category.id.value,
                name = category.name,
                statistic =
                    CategoryStatisticDto.from(
                        category.statistic ?: throw IllegalArgumentException("Category statistic cannot be null"),
                    ),
            )
    }
}
