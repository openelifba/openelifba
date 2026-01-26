package com.wordiam.openelifba.domain.memory

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.ExerciseId
import com.wordiam.openelifba.domain.time.Clock
import com.wordiam.openelifba.domain.user.UserId
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

data class Memory(
    val id: UUID,
    val exerciseId: ExerciseId,
    val categoryId: CategoryId,
    val userId: UserId,
    val nextReviewAt: LocalDateTime,
    val lastReviewedAt: LocalDateTime? = null,
    val interval: Duration,
    val streak: Int,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val responseTime: Duration,
    val totalReviewTimeMillis: Long = 0,
    val easeFactor: Double = DEFAULT_EASE_FACTOR,
    val updatedAt: LocalDateTime,
) {
    companion object {
        private val INITIAL_INTERVAL_MINUTES = Duration.ofHours(1)
        private const val DEFAULT_EASE_FACTOR = 2.5
        private const val MIN_EASE_FACTOR = 1.3
        private const val MAX_EASE_FACTOR = 3.5

        // Response time thresholds for quality assessment
        private val FAST_RESPONSE_THRESHOLD = Duration.ofSeconds(3)
        private val SLOW_RESPONSE_THRESHOLD = Duration.ofSeconds(10)

        // Ease factor adjustments based on performance
        private const val EASE_FACTOR_BOOST_FAST = 0.15
        private const val EASE_FACTOR_BOOST_NORMAL = 0.10
        private const val EASE_FACTOR_BOOST_SLOW = 0.05
        private const val EASE_FACTOR_PENALTY = -0.20

        fun create(
            exerciseId: ExerciseId,
            categoryId: CategoryId,
            userId: UserId,
            success: Boolean,
            responseTime: Duration,
            clock: Clock,
        ): Memory {
            val now = clock.now()
            return Memory(
                id = UUID.randomUUID(),
                exerciseId = exerciseId,
                categoryId = categoryId,
                userId = userId,
                nextReviewAt = now.plus(INITIAL_INTERVAL_MINUTES),
                interval = INITIAL_INTERVAL_MINUTES,
                streak = if (success) 1 else 0,
                correctCount = if (success) 1 else 0,
                incorrectCount = if (success) 0 else 1,
                responseTime = responseTime,
                totalReviewTimeMillis = responseTime.toMillis(),
                updatedAt = now,
            )
        }
    }

    fun updateWithResult(
        success: Boolean,
        responseTime: Duration,
        clock: Clock,
    ): Memory {
        val now = clock.now()
        val newStreak = if (success) streak + 1 else 0
        val newEaseFactor = calculateNewEaseFactor(success, responseTime)
        val newInterval = calculateNewInterval(success, newEaseFactor)

        return copy(
            nextReviewAt = now.plus(newInterval),
            lastReviewedAt = now,
            interval = newInterval,
            streak = newStreak,
            correctCount = if (success) correctCount + 1 else correctCount,
            incorrectCount = if (success) incorrectCount else incorrectCount + 1,
            responseTime = responseTime,
            totalReviewTimeMillis = totalReviewTimeMillis + responseTime.toMillis(),
            easeFactor = newEaseFactor,
            updatedAt = now,
        )
    }

    private fun calculateNewEaseFactor(
        success: Boolean,
        responseTime: Duration,
    ): Double {
        val adjustment =
            if (success) {
                // Positive adjustment based on response speed
                when {
                    responseTime < FAST_RESPONSE_THRESHOLD -> EASE_FACTOR_BOOST_FAST // Very well known
                    responseTime < SLOW_RESPONSE_THRESHOLD -> EASE_FACTOR_BOOST_NORMAL // Well known
                    else -> EASE_FACTOR_BOOST_SLOW // Known but took time
                }
            } else {
                // Penalty for incorrect answer
                EASE_FACTOR_PENALTY
            }

        val newFactor = easeFactor + adjustment
        return newFactor.coerceIn(MIN_EASE_FACTOR, MAX_EASE_FACTOR)
    }

    private fun calculateNewInterval(
        success: Boolean,
        newEaseFactor: Double,
    ): Duration =
        if (success) {
            // Apply ease factor to current interval for personalized growth
            val multipliedInterval = interval.toMillis() * newEaseFactor
            Duration.ofMillis(multipliedInterval.toLong())
        } else {
            // Reset to initial interval on failure
            INITIAL_INTERVAL_MINUTES
        }
}
