package com.wordiam.openelifba.domain.memory

import java.time.Duration

data class MemoryStatistic(
    val reviewedExerciseCount: Int = 0,
    val averageResponseTime: Duration = Duration.ZERO,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
) {
    companion object {
        private const val PERCENTAGE_MULTIPLIER = 100.0
    }

    val accuracyPercent: Double
        get() =
            if (correctCount + incorrectCount > 0) {
                (correctCount.toDouble() / (correctCount + incorrectCount)) * PERCENTAGE_MULTIPLIER
            } else {
                0.0
            }
}
