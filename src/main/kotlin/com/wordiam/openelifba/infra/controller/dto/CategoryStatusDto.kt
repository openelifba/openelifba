package com.wordiam.openelifba.infra.controller.dto

import com.wordiam.openelifba.domain.category.CategoryStatus

enum class CategoryStatusDto {
    NOT_STARTED,
    PASSIVE,
    ACTIVE,
    COMPLETED,
    ;

    companion object {
        fun from(status: CategoryStatus): CategoryStatusDto =
            when (status) {
                CategoryStatus.NOT_STARTED -> NOT_STARTED
                CategoryStatus.PASSIVE -> PASSIVE
                CategoryStatus.ACTIVE -> ACTIVE
                CategoryStatus.COMPLETED -> COMPLETED
            }
    }
}
