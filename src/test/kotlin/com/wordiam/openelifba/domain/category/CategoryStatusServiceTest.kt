package com.wordiam.openelifba.domain.category

import com.wordiam.openelifba.domain.memory.MemoryStatistic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration

class CategoryStatusServiceTest {

    private val service = CategoryStatusService()

    @Test
    fun `should return NOT_STARTED when reviewed exercise count is 0`() {
        val status = service.getStatus(
            totalExerciseCount = 10,
            dueExerciseCount = 10,
            memoryStats = createMemoryStats(reviewedCount = 0)
        )
        assertEquals(CategoryStatus.NOT_STARTED, status)
    }

    @Test
    fun `should return COMPLETED when all exercises reviewed with excellent stats`() {
        val status = service.getStatus(
            totalExerciseCount = 10,
            dueExerciseCount = 0,
            memoryStats = createMemoryStats(
                reviewedCount = 10,
                averageResponseTime = Duration.ofSeconds(2),
                accuracyPercent = 95.0
            )
        )
        assertEquals(CategoryStatus.COMPLETED, status)
    }

    @Test
    fun `should return PASSIVE when all reviewed but stats not excellent and nothing due`() {
        val status = service.getStatus(
            totalExerciseCount = 10,
            dueExerciseCount = 0,
            memoryStats = createMemoryStats(
                reviewedCount = 10,
                averageResponseTime = Duration.ofSeconds(15), // Slow
                accuracyPercent = 80.0
            )
        )
        assertEquals(CategoryStatus.PASSIVE, status)
    }

    @Test
    fun `should return ACTIVE when exercises are due`() {
        val status = service.getStatus(
            totalExerciseCount = 10,
            dueExerciseCount = 5,
            memoryStats = createMemoryStats(
                reviewedCount = 10,
                averageResponseTime = Duration.ofSeconds(15),
                accuracyPercent = 80.0
            )
        )
        assertEquals(CategoryStatus.ACTIVE, status)
    }

    @Test
    fun `should return ACTIVE when not all exercises reviewed`() {
        val status = service.getStatus(
            totalExerciseCount = 10,
            dueExerciseCount = 0,
            memoryStats = createMemoryStats(
                reviewedCount = 5,
                averageResponseTime = Duration.ofSeconds(2),
                accuracyPercent = 95.0
            )
        )
        assertEquals(CategoryStatus.ACTIVE, status)
    }

    private fun createMemoryStats(
        reviewedCount: Int,
        averageResponseTime: Duration = Duration.ofSeconds(5),
        accuracyPercent: Double = 100.0
    ) = MemoryStatistic(
        reviewedExerciseCount = reviewedCount,
        averageResponseTime = averageResponseTime,
        accuracyPercent = accuracyPercent
    )
}
