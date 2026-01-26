package com.wordiam.openelifba.domain.category

import java.time.Duration

data class CategoryStatistic(
    val totalExerciseCount: Int,
    val reviewedExerciseCount: Int,
    val dueExerciseCount: Int,
    val averageSpeed: Duration,
    val accuracyPercent: Double,
    val status: CategoryStatus,
)
