package com.wordiam.openelifba.application

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.Exercise
import com.wordiam.openelifba.domain.exercise.ExerciseId
import com.wordiam.openelifba.domain.exercise.ExerciseWithStreak
import com.wordiam.openelifba.domain.port.ExerciseMemoryFetcher
import com.wordiam.openelifba.domain.time.Clock
import com.wordiam.openelifba.domain.user.UserId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class GetCategoryExerciseUseCaseTest {
    private lateinit var exerciseMemoryFetcher: ExerciseMemoryFetcher
    private lateinit var clock: Clock
    private lateinit var getDueExercisesUseCase: GetDueExercisesUseCase

    private val testUserId = UserId(UUID.randomUUID())
    private val testCategoryId = CategoryId(UUID.randomUUID())
    private val testExerciseId = ExerciseId(UUID.randomUUID())
    private val testCurrentTime = LocalDateTime.of(2024, 1, 15, 10, 0)

    @BeforeEach
    fun setUp() {
        exerciseMemoryFetcher = mockk()
        clock = mockk()
        getDueExercisesUseCase =
            GetDueExercisesUseCase(
                exerciseMemoryFetcher,
                clock,
            )
    }

    @Test
    fun `should return due exercises for user and category`() {
        // Given
        val exercise1 =
            Exercise(
                id = testExerciseId,
                value = "Test Exercise 1",
                audioUrl = "https://example.com/audio1.mp3",
            )
        val exercise2 =
            Exercise(
                id = ExerciseId(UUID.randomUUID()),
                value = "Test Exercise 2",
                audioUrl = "https://example.com/audio2.mp3",
            )
        val expectedExercises =
            listOf(
                ExerciseWithStreak(exercise1, 3),
                ExerciseWithStreak(exercise2, 0),
            )

        every { clock.now() } returns testCurrentTime
        every {
            exerciseMemoryFetcher.fetchDueExercises(testUserId, testCategoryId, testCurrentTime)
        } returns expectedExercises

        // When
        val result = getDueExercisesUseCase.execute(testUserId, testCategoryId)

        // Then
        assertEquals(2, result.size)
        assertEquals(exercise1, result[0].exercise)
        assertEquals(3, result[0].streak)
        assertEquals(exercise2, result[1].exercise)
        assertEquals(0, result[1].streak)

        verify { clock.now() }
        verify { exerciseMemoryFetcher.fetchDueExercises(testUserId, testCategoryId, testCurrentTime) }
    }

    @Test
    fun `should return empty list when no exercises are due`() {
        // Given
        every { clock.now() } returns testCurrentTime
        every {
            exerciseMemoryFetcher.fetchDueExercises(testUserId, testCategoryId, testCurrentTime)
        } returns emptyList()

        // When
        val result = getDueExercisesUseCase.execute(testUserId, testCategoryId)

        // Then
        assertTrue(result.isEmpty())

        verify { clock.now() }
        verify { exerciseMemoryFetcher.fetchDueExercises(testUserId, testCategoryId, testCurrentTime) }
    }

    @Test
    fun `should pass correct parameters to exerciseMemoryFetcher`() {
        // Given
        val differentUserId = UserId(UUID.randomUUID())
        val differentCategoryId = CategoryId(UUID.randomUUID())
        val differentTime = LocalDateTime.of(2024, 2, 20, 15, 30)

        every { clock.now() } returns differentTime
        every {
            exerciseMemoryFetcher.fetchDueExercises(differentUserId, differentCategoryId, differentTime)
        } returns emptyList()

        // When
        getDueExercisesUseCase.execute(differentUserId, differentCategoryId)

        // Then
        verify { clock.now() }
        verify { exerciseMemoryFetcher.fetchDueExercises(differentUserId, differentCategoryId, differentTime) }
    }

    @Test
    fun `should handle single exercise correctly`() {
        // Given
        val singleExercise =
            Exercise(
                id = testExerciseId,
                value = "Single Exercise",
                audioUrl = "https://example.com/single.mp3",
            )

        every { clock.now() } returns testCurrentTime
        every {
            exerciseMemoryFetcher.fetchDueExercises(testUserId, testCategoryId, testCurrentTime)
        } returns listOf(ExerciseWithStreak(singleExercise, 5))

        // When
        val result = getDueExercisesUseCase.execute(testUserId, testCategoryId)

        // Then
        assertEquals(1, result.size)
        assertEquals(singleExercise, result[0].exercise)
        assertEquals(5, result[0].streak)
        assertEquals("Single Exercise", result[0].exercise.value)
        assertEquals("https://example.com/single.mp3", result[0].exercise.audioUrl)
    }

    @Test
    fun `should call clock every time execute is called`() {
        // Given
        val time1 = LocalDateTime.of(2024, 1, 1, 10, 0)
        val time2 = LocalDateTime.of(2024, 1, 1, 11, 0)

        every { clock.now() } returnsMany listOf(time1, time2)
        every { exerciseMemoryFetcher.fetchDueExercises(any(), any(), any()) } returns emptyList()

        // When
        getDueExercisesUseCase.execute(testUserId, testCategoryId)
        getDueExercisesUseCase.execute(testUserId, testCategoryId)

        // Then
        verify(exactly = 2) { clock.now() }
        verify { exerciseMemoryFetcher.fetchDueExercises(testUserId, testCategoryId, time1) }
        verify { exerciseMemoryFetcher.fetchDueExercises(testUserId, testCategoryId, time2) }
    }
}
