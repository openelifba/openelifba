package com.wordiam.openelifba.infra.repository

import com.github.database.rider.core.api.dataset.DataSet
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.infra.repository.config.JooqTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

@JooqTest
class JooqExerciseRepositoryIntegrationTest {
    @Autowired
    private lateinit var exerciseRepository: JooqExerciseRepository

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml"])
    fun `should return correct exercise count for existing category`() {
        // Given
        val lettersCategoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))

        // When
        val count = exerciseRepository.fetchTotalExerciseCount(lettersCategoryId)

        // Then
        assertThat(count).isEqualTo(3)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml"])
    fun `should return correct exercise count for numbers category`() {
        // Given
        val numbersCategoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"))

        // When
        val count = exerciseRepository.fetchTotalExerciseCount(numbersCategoryId)

        // Then
        assertThat(count).isEqualTo(2)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml"])
    fun `should return zero for category with no exercises`() {
        // Given
        val wordsCategoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"))

        // When
        val count = exerciseRepository.fetchTotalExerciseCount(wordsCategoryId)

        // Then
        assertThat(count).isEqualTo(0)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml"])
    fun `should return zero for non-existing category`() {
        // Given
        val nonExistingCategoryId = CategoryId(UUID.fromString("999e8400-e29b-41d4-a716-446655440999"))

        // When
        val count = exerciseRepository.fetchTotalExerciseCount(nonExistingCategoryId)

        // Then
        assertThat(count).isEqualTo(0)
    }
}
