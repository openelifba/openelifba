package com.wordiam.openelifba.domain.category

import com.wordiam.openelifba.domain.memory.MemoryStatistic
import java.time.Duration

class CategoryStatusService {
    fun getStatus(
        totalExerciseCount: Int,
        dueExerciseCount: Int,
        memoryStats: MemoryStatistic,
    ): CategoryStatus =
        when {
            memoryStats.reviewedExerciseCount == 0 -> CategoryStatus.NOT_STARTED
            totalExerciseCount == memoryStats.reviewedExerciseCount &&
                memoryStats.averageResponseTime <= EXCELLENT_SPEED_THRESHOLD &&
                memoryStats.accuracyPercent >= EXCELLENT_ACCURACY_THRESHOLD -> CategoryStatus.COMPLETED

            dueExerciseCount == 0 && memoryStats.reviewedExerciseCount == totalExerciseCount -> CategoryStatus.PASSIVE
            else -> CategoryStatus.ACTIVE
        }

    private companion object {
        val EXCELLENT_SPEED_THRESHOLD: Duration = Duration.ofSeconds(10) // 10 seconds or less is considered very good
        const val EXCELLENT_ACCURACY_THRESHOLD = 90.0 // 90% or higher is considered very good
    }
}
