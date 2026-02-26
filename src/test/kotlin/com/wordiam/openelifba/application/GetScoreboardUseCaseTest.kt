package com.wordiam.openelifba.application

import com.wordiam.openelifba.domain.port.ScoreboardFetcher
import com.wordiam.openelifba.domain.score.ScoreEntry
import com.wordiam.openelifba.domain.user.UserId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class GetScoreboardUseCaseTest {
    private lateinit var scoreboardFetcher: ScoreboardFetcher
    private lateinit var getScoreboardUseCase: GetScoreboardUseCase

    private val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))

    @BeforeEach
    fun setUp() {
        scoreboardFetcher = mockk()
        getScoreboardUseCase = GetScoreboardUseCase(scoreboardFetcher)
    }

    @Test
    fun `should return top scores and user rank`() {
        // Given
        val topScores =
            listOf(
                ScoreEntry(userId = userId, score = 150L, rank = 1L),
                ScoreEntry(userId = UserId(UUID.randomUUID()), score = 120L, rank = 2L),
            )
        val myRank = ScoreEntry(userId = userId, score = 150L, rank = 1L)

        every { scoreboardFetcher.fetchTopScores(10) } returns topScores
        every { scoreboardFetcher.fetchUserRank(userId) } returns myRank

        // When
        val result = getScoreboardUseCase.execute(userId)

        // Then
        assertEquals(2, result.topScores.size)
        assertEquals(150L, result.topScores[0].score)
        assertEquals(1L, result.topScores[0].rank)
        assertEquals(myRank, result.myRank)

        verify { scoreboardFetcher.fetchTopScores(10) }
        verify { scoreboardFetcher.fetchUserRank(userId) }
    }

    @Test
    fun `should return null myRank when user has no memory records`() {
        // Given
        every { scoreboardFetcher.fetchTopScores(10) } returns emptyList()
        every { scoreboardFetcher.fetchUserRank(userId) } returns null

        // When
        val result = getScoreboardUseCase.execute(userId)

        // Then
        assertEquals(0, result.topScores.size)
        assertNull(result.myRank)
    }

    @Test
    fun `should respect custom limit`() {
        // Given
        val topScores =
            listOf(
                ScoreEntry(userId = userId, score = 150L, rank = 1L),
            )

        every { scoreboardFetcher.fetchTopScores(1) } returns topScores
        every { scoreboardFetcher.fetchUserRank(userId) } returns topScores[0]

        // When
        val result = getScoreboardUseCase.execute(userId, limit = 1)

        // Then
        assertEquals(1, result.topScores.size)
        verify { scoreboardFetcher.fetchTopScores(1) }
    }
}
