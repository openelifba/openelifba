package com.wordiam.openelifba.application

import com.wordiam.openelifba.application.UpdateMemoryUseCase.UpdateMemoryRequest
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.ExerciseId
import com.wordiam.openelifba.domain.memory.Memory
import com.wordiam.openelifba.domain.port.ExerciseMemoryFinder
import com.wordiam.openelifba.domain.port.ExerciseMemoryUpserter
import com.wordiam.openelifba.domain.time.Clock
import com.wordiam.openelifba.domain.user.UserId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class UpdateExerciseMemoryUseCaseTest {
    private lateinit var exerciseMemoryFinder: ExerciseMemoryFinder
    private lateinit var exerciseMemoryUpserter: ExerciseMemoryUpserter
    private lateinit var clock: Clock
    private lateinit var updateMemoryUseCase: UpdateMemoryUseCase

    private val testUserId = UserId(UUID.randomUUID())
    private val testCategoryId = CategoryId(UUID.randomUUID())
    private val testExerciseId = ExerciseId(UUID.randomUUID())
    private val testResponseTime = Duration.ofSeconds(3)

    @BeforeEach
    fun setUp() {
        exerciseMemoryFinder = mockk()
        exerciseMemoryUpserter = mockk(relaxed = true)
        clock = mockk()
        every { clock.now() } returns LocalDateTime.now()
        updateMemoryUseCase =
            UpdateMemoryUseCase(
                exerciseMemoryFinder,
                exerciseMemoryUpserter,
                clock,
            )
    }

    @Test
    fun `should create new exercise memory when no existing memory found`() {
        // Given
        val request =
            UpdateMemoryRequest(
                userId = testUserId,
                categoryId = testCategoryId,
                exerciseId = testExerciseId,
                success = true,
                responseTime = testResponseTime,
            )

        every {
            exerciseMemoryFinder.findExerciseMemory(testUserId, testCategoryId, testExerciseId)
        } returns null

        // When
        updateMemoryUseCase.execute(request)

        // Then
        verify { exerciseMemoryFinder.findExerciseMemory(testUserId, testCategoryId, testExerciseId) }
        verify {
            exerciseMemoryUpserter.upsert(
                match { memory ->
                    memory.exerciseId == testExerciseId &&
                        memory.categoryId == testCategoryId &&
                        memory.userId == testUserId &&
                        memory.correctCount == 1 &&
                        memory.incorrectCount == 0 &&
                        memory.streak == 1 &&
                        memory.responseTime == testResponseTime
                },
            )
        }
    }

    @Test
    fun `should create new exercise memory with failure when success is false`() {
        // Given
        val request =
            UpdateMemoryRequest(
                userId = testUserId,
                categoryId = testCategoryId,
                exerciseId = testExerciseId,
                success = false,
                responseTime = testResponseTime,
            )

        every {
            exerciseMemoryFinder.findExerciseMemory(testUserId, testCategoryId, testExerciseId)
        } returns null

        // When
        updateMemoryUseCase.execute(request)

        // Then
        verify {
            exerciseMemoryUpserter.upsert(
                match { memory ->
                    memory.exerciseId == testExerciseId &&
                        memory.categoryId == testCategoryId &&
                        memory.userId == testUserId &&
                        memory.correctCount == 0 &&
                        memory.incorrectCount == 1 &&
                        memory.streak == 0 &&
                        memory.responseTime == testResponseTime
                },
            )
        }
    }

    @Test
    fun `should update existing exercise memory with success`() {
        // Given
        val now = LocalDateTime.now()
        val existingMemory =
            Memory(
                id = UUID.randomUUID(),
                exerciseId = testExerciseId,
                categoryId = testCategoryId,
                userId = testUserId,
                nextReviewAt = now.plusMinutes(10),
                lastReviewedAt = now.minusMinutes(30),
                interval = Duration.ofMinutes(10),
                streak = 2,
                correctCount = 3,
                incorrectCount = 1,
                responseTime = Duration.ofSeconds(2),
                easeFactor = 2.5,
                updatedAt = now,
            )

        val request =
            UpdateMemoryRequest(
                userId = testUserId,
                categoryId = testCategoryId,
                exerciseId = testExerciseId,
                success = true,
                responseTime = testResponseTime,
            )

        every {
            exerciseMemoryFinder.findExerciseMemory(testUserId, testCategoryId, testExerciseId)
        } returns existingMemory

        // When
        updateMemoryUseCase.execute(request)

        // Then
        verify { exerciseMemoryFinder.findExerciseMemory(testUserId, testCategoryId, testExerciseId) }
        verify {
            exerciseMemoryUpserter.upsert(
                match { memory ->
                    memory.exerciseId == testExerciseId &&
                        memory.correctCount == 4 &&
                        // incremented from 3
                        memory.incorrectCount == 1 &&
                        // unchanged
                        memory.streak == 3 &&
                        // incremented from 2
                        memory.responseTime == testResponseTime &&
                        // 10 * 2.6 (ease factor 2.5 + 0.1 for normal speed)
                        memory.interval == Duration.ofMinutes(26) &&
                        memory.easeFactor == 2.6 // increased from 2.5 due to correct answer
                },
            )
        }
    }

    @Test
    fun `should update existing exercise memory with failure`() {
        // Given
        val now = LocalDateTime.now()
        val existingMemory =
            Memory(
                id = UUID.randomUUID(),
                exerciseId = testExerciseId,
                categoryId = testCategoryId,
                userId = testUserId,
                nextReviewAt = now.plusMinutes(20),
                lastReviewedAt = now.minusMinutes(60),
                interval = Duration.ofMinutes(20),
                streak = 3,
                correctCount = 5,
                incorrectCount = 2,
                responseTime = Duration.ofSeconds(1),
                easeFactor = 2.8,
                updatedAt = now,
            )

        val request =
            UpdateMemoryRequest(
                userId = testUserId,
                categoryId = testCategoryId,
                exerciseId = testExerciseId,
                success = false,
                responseTime = testResponseTime,
            )

        every {
            exerciseMemoryFinder.findExerciseMemory(testUserId, testCategoryId, testExerciseId)
        } returns existingMemory

        // When
        updateMemoryUseCase.execute(request)

        // Then
        verify {
            exerciseMemoryUpserter.upsert(
                match { memory ->
                    memory.exerciseId == testExerciseId &&
                        memory.correctCount == 5 &&
                        // unchanged
                        memory.incorrectCount == 3 &&
                        // incremented from 2
                        memory.streak == 0 &&
                        // reset to 0
                        memory.responseTime == testResponseTime &&
                        memory.interval == Duration.ofHours(1) &&
                        // reset to initial interval (1 hour)
                        // decreased from 2.8 by 0.2 (with floating point tolerance)
                        kotlin.math.abs(memory.easeFactor - 2.6) < 0.001
                },
            )
        }
    }

    @Test
    fun `should handle different response times correctly`() {
        // Given
        val customResponseTime = Duration.ofMillis(1500)
        val request =
            UpdateMemoryRequest(
                userId = testUserId,
                categoryId = testCategoryId,
                exerciseId = testExerciseId,
                success = true,
                responseTime = customResponseTime,
            )

        every {
            exerciseMemoryFinder.findExerciseMemory(testUserId, testCategoryId, testExerciseId)
        } returns null

        // When
        updateMemoryUseCase.execute(request)

        // Then
        verify {
            exerciseMemoryUpserter.upsert(
                match { memory ->
                    memory.responseTime == customResponseTime
                },
            )
        }
    }

    @Test
    fun `should handle different user and category combinations`() {
        // Given
        val differentUserId = UserId(UUID.randomUUID())
        val differentCategoryId = CategoryId(UUID.randomUUID())
        val differentExerciseId = ExerciseId(UUID.randomUUID())

        val request =
            UpdateMemoryRequest(
                userId = differentUserId,
                categoryId = differentCategoryId,
                exerciseId = differentExerciseId,
                success = true,
                responseTime = testResponseTime,
            )

        every {
            exerciseMemoryFinder.findExerciseMemory(differentUserId, differentCategoryId, differentExerciseId)
        } returns null

        // When
        updateMemoryUseCase.execute(request)

        // Then
        verify { exerciseMemoryFinder.findExerciseMemory(differentUserId, differentCategoryId, differentExerciseId) }
        verify {
            exerciseMemoryUpserter.upsert(
                match { memory ->
                    memory.userId == differentUserId &&
                        memory.categoryId == differentCategoryId &&
                        memory.exerciseId == differentExerciseId
                },
            )
        }
    }

    @Test
    fun `UpdateExerciseMemoryRequest should be created with correct values`() {
        // Given & When
        val request =
            UpdateMemoryRequest(
                userId = testUserId,
                categoryId = testCategoryId,
                exerciseId = testExerciseId,
                success = true,
                responseTime = testResponseTime,
            )

        // Then
        assertEquals(testUserId, request.userId)
        assertEquals(testCategoryId, request.categoryId)
        assertEquals(testExerciseId, request.exerciseId)
        assertTrue(request.success)
        assertEquals(testResponseTime, request.responseTime)
    }
}
