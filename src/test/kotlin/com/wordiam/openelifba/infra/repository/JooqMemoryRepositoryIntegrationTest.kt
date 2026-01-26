package com.wordiam.openelifba.infra.repository

import com.github.database.rider.core.api.dataset.DataSet
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.user.UserId
import com.wordiam.openelifba.infra.repository.config.JooqTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.UUID

@JooqTest
class JooqMemoryRepositoryIntegrationTest {
    @Autowired
    private lateinit var memoryRepository: JooqMemoryRepository

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should fetch memory statistics for user and category`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))

        // When
        val stats = memoryRepository.fetchMemoryStatistics(userId, categoryId)

        // Then
        assertThat(stats.reviewedExerciseCount).isEqualTo(2) // Two exercises have memory records for this user
        assertThat(stats.correctCount).isEqualTo(8) // 5 + 3
        assertThat(stats.incorrectCount).isEqualTo(3) // 1 + 2
        assertThat(stats.averageResponseTime.toMillis()).isEqualTo(2850) // (2500 + 3200) / 2
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should return zero statistics for user with no memory records`() {
        // Given
        val userId = UserId(UUID.fromString("999e8400-e29b-41d4-a716-446655440999"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))

        // When
        val stats = memoryRepository.fetchMemoryStatistics(userId, categoryId)

        // Then
        assertThat(stats.reviewedExerciseCount).isEqualTo(0)
        assertThat(stats.correctCount).isEqualTo(0)
        assertThat(stats.incorrectCount).isEqualTo(0)
        assertThat(stats.averageResponseTime).isEqualTo(Duration.ZERO)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should fetch due exercise count for user`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))

        // When - using a future date to make sure exercises are not due
        val futureDueCount = memoryRepository.fetchDueExerciseCount(userId, categoryId)

        // Then - no exercises should be due in the future
        assertThat(futureDueCount).isEqualTo(0)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml"])
    fun `should return zero due exercises when no memory records exist`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))

        // When
        val dueCount = memoryRepository.fetchDueExerciseCount(userId, categoryId)

        // Then
        assertThat(dueCount).isEqualTo(0)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should return statistics for different user`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440002"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))

        // When
        val stats = memoryRepository.fetchMemoryStatistics(userId, categoryId)

        // Then
        assertThat(stats.reviewedExerciseCount).isEqualTo(1) // Only one exercise has memory record for this user
        assertThat(stats.correctCount).isEqualTo(7)
        assertThat(stats.incorrectCount).isEqualTo(0)
        assertThat(stats.averageResponseTime.toMillis()).isEqualTo(1800)
    }
}
