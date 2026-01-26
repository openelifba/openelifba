package com.wordiam.openelifba.infra.repository

import com.github.database.rider.core.api.dataset.DataSet
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.infra.repository.config.JooqTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

@JooqTest
class JooqCategoryRepositoryIntegrationTest {
    @Autowired
    private lateinit var categoryRepository: JooqCategoryRepository

    @Test
    @DataSet("datasets/categories.yml")
    fun `should fetch categories ordered by rank`() {
        // When
        val categories = categoryRepository.fetchCategories()

        // Then
        assertThat(categories).hasSize(3)
        assertThat(categories[0].name).isEqualTo("Letters")
        assertThat(categories[1].name).isEqualTo("Numbers")
        assertThat(categories[2].name).isEqualTo("Words")
    }

    @Test
    @DataSet("datasets/categories.yml")
    fun `should return correct category details`() {
        // When
        val categories = categoryRepository.fetchCategories()

        // Then
        val firstCategory = categories[0]
        assertThat(firstCategory.id).isEqualTo(CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001")))
        assertThat(firstCategory.name).isEqualTo("Letters")
    }
}
