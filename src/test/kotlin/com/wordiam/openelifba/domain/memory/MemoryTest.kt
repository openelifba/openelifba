package com.wordiam.openelifba.domain.memory

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.ExerciseId
import com.wordiam.openelifba.domain.time.Clock
import com.wordiam.openelifba.domain.user.UserId
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class MemoryTest {

    private val clock = mockk<Clock>()
    private val now = LocalDateTime.parse("2024-01-01T10:00:00")
    
    init {
        every { clock.now() } returns now
    }

    private val exerciseId = ExerciseId(UUID.randomUUID())
    private val categoryId = CategoryId(UUID.randomUUID())
    private val userId = UserId(UUID.randomUUID())

    @Test
    fun `create should initialize memory with default values`() {
        val memory = Memory.create(
            exerciseId = exerciseId,
            categoryId = categoryId,
            userId = userId,
            success = true,
            responseTime = Duration.ofSeconds(5),
            clock = clock
        )

        assertEquals(exerciseId, memory.exerciseId)
        assertEquals(categoryId, memory.categoryId)
        assertEquals(userId, memory.userId)
        assertEquals(1, memory.streak)
        assertEquals(1, memory.correctCount)
        assertEquals(0, memory.incorrectCount)
        assertEquals(Duration.ofHours(1), memory.interval)
        assertEquals(now.plusHours(1), memory.nextReviewAt)
    }

    @Test
    fun `updateWithResult should increase ease factor and interval on fast success`() {
        val initialMemory = Memory.create(
            exerciseId = exerciseId,
            categoryId = categoryId,
            userId = userId,
            success = true,
            responseTime = Duration.ofSeconds(5),
            clock = clock
        )

        // Fast response (< 3s)
        val updatedMemory = initialMemory.updateWithResult(
            success = true,
            responseTime = Duration.ofSeconds(2),
            clock = clock
        )

        assertTrue(updatedMemory.easeFactor > initialMemory.easeFactor)
        assertTrue(updatedMemory.interval > initialMemory.interval)
        assertEquals(2, updatedMemory.streak)
    }

    @Test
    fun `updateWithResult should decrease ease factor and reset interval on failure`() {
        val initialMemory = Memory.create(
            exerciseId = exerciseId,
            categoryId = categoryId,
            userId = userId,
            success = true,
            responseTime = Duration.ofSeconds(5),
            clock = clock
        )

        val updatedMemory = initialMemory.updateWithResult(
            success = false,
            responseTime = Duration.ofSeconds(5),
            clock = clock
        )

        assertTrue(updatedMemory.easeFactor < initialMemory.easeFactor)
        assertEquals(Duration.ofHours(1), updatedMemory.interval) // Resets to initial
        assertEquals(0, updatedMemory.streak)
    }

    @Test
    fun `updateWithResult should simply increase streak and stats on normal success`() {
        val initialMemory = Memory.create(
            exerciseId = exerciseId,
            categoryId = categoryId,
            userId = userId,
            success = true,
            responseTime = Duration.ofSeconds(5),
            clock = clock
        )

        val updatedMemory = initialMemory.updateWithResult(
            success = true,
            responseTime = Duration.ofSeconds(5), // Normal speed (3-10s)
            clock = clock
        )

        assertEquals(2, updatedMemory.streak)
        assertEquals(2, updatedMemory.correctCount)
        // Interval calculation check roughly (oldInterval * newEaseFactor)
        // newEaseFactor for normal is +0.10. 2.5 + 0.1 = 2.6
        // 1 hour * 2.6 = 2.6 hours = 156 minutes
        assertEquals(Duration.ofMinutes(156), updatedMemory.interval)
    }
}
