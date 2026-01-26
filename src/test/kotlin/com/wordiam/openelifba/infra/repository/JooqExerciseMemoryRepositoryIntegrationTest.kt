package com.wordiam.openelifba.infra.repository

import com.github.database.rider.core.api.dataset.DataSet
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.ExerciseId
import com.wordiam.openelifba.domain.memory.Memory
import com.wordiam.openelifba.domain.user.UserId
import com.wordiam.openelifba.infra.repository.config.JooqTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@JooqTest
class JooqExerciseMemoryRepositoryIntegrationTest {
    @Autowired
    private lateinit var exerciseMemoryRepository: JooqExerciseMemoryRepository

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should fetch due exercises for user and category`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
        val futureDate = LocalDateTime.of(2025, 12, 31, 23, 59)

        // When
        val dueExercises = exerciseMemoryRepository.fetchDueExercises(userId, categoryId, futureDate)

        // Then
        assertThat(dueExercises).hasSize(3) // Should return all exercises in the category

        val exerciseIds =
            dueExercises.map {
                it.exercise.id.value
                    .toString()
            }
        assertThat(exerciseIds).contains(
            "660e8400-e29b-41d4-a716-446655440001",
            "660e8400-e29b-41d4-a716-446655440002",
            "660e8400-e29b-41d4-a716-446655440003",
        )
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should fetch exercises without memory records as due`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
        val pastDate = LocalDateTime.of(2024, 1, 1, 0, 0)

        // When
        val dueExercises = exerciseMemoryRepository.fetchDueExercises(userId, categoryId, pastDate)

        // Then
        // Should include exercises without memory records (third exercise)
        assertThat(dueExercises).hasSize(1)
        assertThat(
            dueExercises[0]
                .exercise.id.value
                .toString(),
        ).isEqualTo("660e8400-e29b-41d4-a716-446655440003")
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should find existing exercise memory`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
        val exerciseId = ExerciseId(UUID.fromString("660e8400-e29b-41d4-a716-446655440001"))

        // When
        val exerciseMemory = exerciseMemoryRepository.findExerciseMemory(userId, categoryId, exerciseId)

        // Then
        assertThat(exerciseMemory).isNotNull
        assertThat(exerciseMemory!!.id.toString()).isEqualTo("770e8400-e29b-41d4-a716-446655440001")
        assertThat(exerciseMemory.correctCount).isEqualTo(5)
        assertThat(exerciseMemory.incorrectCount).isEqualTo(1)
        assertThat(exerciseMemory.streak).isEqualTo(3)
        assertThat(exerciseMemory.interval.toMinutes()).isEqualTo(1440)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should return null for non-existing exercise memory`() {
        // Given
        val userId = UserId(UUID.fromString("999e8400-e29b-41d4-a716-446655440999"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
        val exerciseId = ExerciseId(UUID.fromString("660e8400-e29b-41d4-a716-446655440001"))

        // When
        val exerciseMemory = exerciseMemoryRepository.findExerciseMemory(userId, categoryId, exerciseId)

        // Then
        assertThat(exerciseMemory).isNull()
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml"])
    fun `should upsert new exercise memory`() {
        // Given
        val newMemory =
            Memory(
                id = UUID.fromString("770e8400-e29b-41d4-a716-446655440999"),
                exerciseId = ExerciseId(UUID.fromString("660e8400-e29b-41d4-a716-446655440001")),
                categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001")),
                userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440999")),
                nextReviewAt = LocalDateTime.of(2025, 2, 1, 10, 0),
                lastReviewedAt = LocalDateTime.of(2025, 1, 26, 15, 30),
                interval = Duration.ofMinutes(720),
                streak = 2,
                correctCount = 3,
                incorrectCount = 1,
                responseTime = Duration.ofSeconds(3),
                totalReviewTimeMillis = 9000L, // 3 seconds * 3 correct attempts
                easeFactor = 2.7,
                updatedAt = LocalDateTime.of(2025, 1, 26, 15, 30),
            )

        // When
        exerciseMemoryRepository.upsert(newMemory)

        // Then
        val saved =
            exerciseMemoryRepository.findExerciseMemory(
                newMemory.userId,
                newMemory.categoryId,
                newMemory.exerciseId,
            )

        assertThat(saved).isNotNull
        assertThat(saved!!.correctCount).isEqualTo(3)
        assertThat(saved.incorrectCount).isEqualTo(1)
        assertThat(saved.streak).isEqualTo(2)
        assertThat(saved.interval.toMinutes()).isEqualTo(720)
        assertThat(saved.totalReviewTimeMillis).isEqualTo(9000L)
        assertThat(saved.easeFactor).isEqualTo(2.7)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should update existing exercise memory on upsert`() {
        // Given
        val existingMemory =
            exerciseMemoryRepository.findExerciseMemory(
                UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001")),
                CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001")),
                ExerciseId(UUID.fromString("660e8400-e29b-41d4-a716-446655440001")),
            )!!

        val updatedMemory =
            existingMemory.copy(
                correctCount = 10,
                incorrectCount = 2,
                streak = 5,
                interval = Duration.ofMinutes(2880),
                totalReviewTimeMillis = existingMemory.totalReviewTimeMillis + 5000L, // Add 5 seconds
                updatedAt = LocalDateTime.of(2025, 1, 27, 10, 0),
            )

        // When
        exerciseMemoryRepository.upsert(updatedMemory)

        // Then
        val saved =
            exerciseMemoryRepository.findExerciseMemory(
                updatedMemory.userId,
                updatedMemory.categoryId,
                updatedMemory.exerciseId,
            )

        assertThat(saved).isNotNull
        assertThat(saved!!.correctCount).isEqualTo(10)
        assertThat(saved.incorrectCount).isEqualTo(2)
        assertThat(saved.streak).isEqualTo(5)
        assertThat(saved.interval.toMinutes()).isEqualTo(2880)
        assertThat(saved.totalReviewTimeMillis).isEqualTo(existingMemory.totalReviewTimeMillis + 5000L)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml"])
    fun `should return empty list when no exercises exist for category`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"))
        val now = LocalDateTime.now()

        // When
        val dueExercises = exerciseMemoryRepository.fetchDueExercises(userId, categoryId, now)

        // Then
        assertThat(dueExercises).isEmpty()
    }
}
