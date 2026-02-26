package com.wordiam.openelifba.infra.controller

import com.ninjasquad.springmockk.MockkBean
import com.wordiam.openelifba.application.GetScoreboardUseCase
import com.wordiam.openelifba.domain.score.ScoreEntry
import com.wordiam.openelifba.domain.time.Clock
import com.wordiam.openelifba.domain.user.UserId
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(ScoreboardController::class)
class ScoreboardControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var clock: Clock

    @MockkBean
    private lateinit var getScoreboardUseCase: GetScoreboardUseCase

    private val user1Id = UUID.fromString("880e8400-e29b-41d4-a716-446655440001")
    private val user2Id = UUID.fromString("880e8400-e29b-41d4-a716-446655440002")

    @Test
    fun `should return scoreboard with top scores and user rank`() {
        // Given
        val userId = UserId(user1Id)
        val topScores =
            listOf(
                ScoreEntry(userId = UserId(user1Id), score = 8L, rank = 1L),
                ScoreEntry(userId = UserId(user2Id), score = 7L, rank = 2L),
            )
        val myRank = ScoreEntry(userId = UserId(user1Id), score = 8L, rank = 1L)

        every { getScoreboardUseCase.execute(userId, 10) } returns
            GetScoreboardUseCase.Scoreboard(topScores = topScores, myRank = myRank)

        // When & Then
        mockMvc
            .perform(
                get("/api/scoreboard")
                    .header("x-user-id", user1Id),
            ).andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(
                content().json(
                    """
                    {
                        "topScores": [
                            { "userId": "$user1Id", "score": 8, "rank": 1 },
                            { "userId": "$user2Id", "score": 7, "rank": 2 }
                        ],
                        "myRank": { "userId": "$user1Id", "score": 8, "rank": 1 }
                    }
                    """,
                    true,
                ),
            )
    }

    @Test
    fun `should return null myRank when user has no scores`() {
        // Given
        val userId = UserId(user1Id)
        every { getScoreboardUseCase.execute(userId, 10) } returns
            GetScoreboardUseCase.Scoreboard(topScores = emptyList(), myRank = null)

        // When & Then
        mockMvc
            .perform(
                get("/api/scoreboard")
                    .header("x-user-id", user1Id),
            ).andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                        "topScores": [],
                        "myRank": null
                    }
                    """,
                    true,
                ),
            )
    }

    @Test
    fun `should return 400 when x-user-id header is missing`() {
        // When & Then
        mockMvc
            .perform(get("/api/scoreboard"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should respect custom limit parameter`() {
        // Given
        val userId = UserId(user1Id)
        val topScores = listOf(ScoreEntry(userId = UserId(user1Id), score = 8L, rank = 1L))

        every { getScoreboardUseCase.execute(userId, 1) } returns
            GetScoreboardUseCase.Scoreboard(topScores = topScores, myRank = topScores[0])

        // When & Then
        mockMvc
            .perform(
                get("/api/scoreboard")
                    .header("x-user-id", user1Id)
                    .param("limit", "1"),
            ).andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                        "topScores": [
                            { "userId": "$user1Id", "score": 8, "rank": 1 }
                        ]
                    }
                    """,
                    false,
                ),
            )
    }
}
