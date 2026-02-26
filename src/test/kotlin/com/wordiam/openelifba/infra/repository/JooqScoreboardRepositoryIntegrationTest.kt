package com.wordiam.openelifba.infra.repository

import com.github.database.rider.core.api.dataset.DataSet
import com.wordiam.openelifba.domain.user.UserId
import com.wordiam.openelifba.infra.repository.config.JooqTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

@JooqTest
class JooqScoreboardRepositoryIntegrationTest {
    @Autowired
    private lateinit var scoreboardRepository: JooqScoreboardRepository

    // memory.yml data:
    // user 001: correct_count = 5 + 3 = 8  -> rank 1
    // user 002: correct_count = 7           -> rank 2

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should return users ranked by score descending`() {
        // When
        val result = scoreboardRepository.fetchTopScores(10)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0].score).isEqualTo(8L)
        assertThat(result[0].rank).isEqualTo(1L)
        assertThat(result[0].userId.value).isEqualTo(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))

        assertThat(result[1].score).isEqualTo(7L)
        assertThat(result[1].rank).isEqualTo(2L)
        assertThat(result[1].userId.value).isEqualTo(UUID.fromString("880e8400-e29b-41d4-a716-446655440002"))
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should respect limit parameter`() {
        // When
        val result = scoreboardRepository.fetchTopScores(1)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].score).isEqualTo(8L)
        assertThat(result[0].rank).isEqualTo(1L)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should return correct rank for known user`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))

        // When
        val result = scoreboardRepository.fetchUserRank(userId)

        // Then
        assertThat(result).isNotNull
        assertThat(result!!.score).isEqualTo(8L)
        assertThat(result.rank).isEqualTo(1L)
        assertThat(result.userId).isEqualTo(userId)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should return rank 2 for second user`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440002"))

        // When
        val result = scoreboardRepository.fetchUserRank(userId)

        // Then
        assertThat(result).isNotNull
        assertThat(result!!.score).isEqualTo(7L)
        assertThat(result.rank).isEqualTo(2L)
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml", "datasets/memory.yml"])
    fun `should return null for user with no memory records`() {
        // Given
        val userId = UserId(UUID.fromString("999e8400-e29b-41d4-a716-446655440999"))

        // When
        val result = scoreboardRepository.fetchUserRank(userId)

        // Then
        assertThat(result).isNull()
    }

    @Test
    @DataSet(value = ["datasets/categories.yml", "datasets/exercises.yml"])
    fun `should return empty list when no memory records exist`() {
        // When
        val result = scoreboardRepository.fetchTopScores(10)

        // Then
        assertThat(result).isEmpty()
    }
}
