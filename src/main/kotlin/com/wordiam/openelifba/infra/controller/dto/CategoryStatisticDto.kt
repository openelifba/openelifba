package com.wordiam.openelifba.infra.controller.dto

import com.wordiam.openelifba.domain.category.CategoryStatistic

data class CategoryStatisticDto(
    val totalExerciseCount: Int,
    val reviewedExerciseCount: Int,
    val dueExerciseCount: Int,
    val averageSpeedSec: Double,
    val accuracyPercent: Double,
    val status: CategoryStatusDto,
) {
    companion object {
        fun from(statistic: CategoryStatistic): CategoryStatisticDto =
            CategoryStatisticDto(
                totalExerciseCount = statistic.totalExerciseCount,
                reviewedExerciseCount = statistic.reviewedExerciseCount,
                dueExerciseCount = statistic.dueExerciseCount,
                averageSpeedSec = statistic.averageSpeed.toMillis() / 1000.0,
                accuracyPercent = statistic.accuracyPercent,
                status = CategoryStatusDto.from(statistic.status),
            )
    }
}
