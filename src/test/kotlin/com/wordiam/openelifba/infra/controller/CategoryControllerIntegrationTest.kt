package com.wordiam.openelifba.infra.controller

import com.ninjasquad.springmockk.MockkBean
import com.wordiam.openelifba.application.GetCategoriesUseCase
import com.wordiam.openelifba.domain.category.Category
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.category.CategoryStatistic
import com.wordiam.openelifba.domain.category.CategoryStatus
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
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@WebMvcTest(CategoryController::class)
class CategoryControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var clock: Clock

    @MockkBean
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase

    @Test
    fun `should return categories with statistics when valid user id provided`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))
        setupClock()
        val mockCategories = createActiveCategoriesWithStatistics()
        every { getCategoriesUseCase.execute(userId) } returns mockCategories

        // When & Then
        performGetCategoriesRequest(userId)
            .andExpectActiveCategoriesResponse()
    }

    @Test
    fun `should return empty statistics for user with no memory records`() {
        // Given
        val userId = UserId(UUID.fromString("990e8400-e29b-41d4-a716-446655440999"))
        setupClock()
        val mockCategories = createEmptyStatisticsCategories()
        every { getCategoriesUseCase.execute(userId) } returns mockCategories

        // When & Then
        performGetCategoriesRequest(userId)
            .andExpectEmptyStatisticsResponse()
    }

    @Test
    fun `should return 400 when user id header is missing`() {
        // When & Then
        mockMvc
            .perform(get("/api/categories"))
            .andExpect(status().isBadRequest)
    }

    private fun setupClock() {
        every { clock.now() } returns LocalDateTime.parse("2025-01-18T00:00:00")
    }

    private fun createActiveCategoriesWithStatistics(): List<Category> =
        listOf(
            Category(
                id = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001")),
                name = "Letters",
                statistic =
                    CategoryStatistic(
                        totalExerciseCount = 3,
                        reviewedExerciseCount = 2,
                        dueExerciseCount = 1,
                        averageSpeed = Duration.ofMillis(2500),
                        accuracyPercent = 83.33,
                        status = CategoryStatus.ACTIVE,
                    ),
            ),
            Category(
                id = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440002")),
                name = "Numbers",
                statistic =
                    CategoryStatistic(
                        totalExerciseCount = 2,
                        reviewedExerciseCount = 0,
                        dueExerciseCount = 2,
                        averageSpeed = Duration.ZERO,
                        accuracyPercent = 0.0,
                        status = CategoryStatus.NOT_STARTED,
                    ),
            ),
            Category(
                id = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440003")),
                name = "Words",
                statistic =
                    CategoryStatistic(
                        totalExerciseCount = 0,
                        reviewedExerciseCount = 0,
                        dueExerciseCount = 0,
                        averageSpeed = Duration.ZERO,
                        accuracyPercent = 0.0,
                        status = CategoryStatus.NOT_STARTED,
                    ),
            ),
        )

    private fun createEmptyStatisticsCategories(): List<Category> =
        listOf(
            Category(
                id = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001")),
                name = "Letters",
                statistic =
                    CategoryStatistic(
                        totalExerciseCount = 3,
                        reviewedExerciseCount = 0,
                        dueExerciseCount = 3,
                        averageSpeed = Duration.ZERO,
                        accuracyPercent = 0.0,
                        status = CategoryStatus.NOT_STARTED,
                    ),
            ),
            Category(
                id = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440002")),
                name = "Numbers",
                statistic =
                    CategoryStatistic(
                        totalExerciseCount = 2,
                        reviewedExerciseCount = 0,
                        dueExerciseCount = 2,
                        averageSpeed = Duration.ZERO,
                        accuracyPercent = 0.0,
                        status = CategoryStatus.NOT_STARTED,
                    ),
            ),
            Category(
                id = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440003")),
                name = "Words",
                statistic =
                    CategoryStatistic(
                        totalExerciseCount = 0,
                        reviewedExerciseCount = 0,
                        dueExerciseCount = 0,
                        averageSpeed = Duration.ZERO,
                        accuracyPercent = 0.0,
                        status = CategoryStatus.NOT_STARTED,
                    ),
            ),
        )

    private fun performGetCategoriesRequest(userId: UserId) =
        mockMvc.perform(
            get("/api/categories")
                .header("x-user-id", userId.value),
        )

    private fun org.springframework.test.web.servlet.ResultActions.andExpectActiveCategoriesResponse() =
        this
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(
                content().json(
                    """
                [
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440001",
                        "name": "Letters",
                        "statistic": {
                            "totalExerciseCount": 3,
                            "reviewedExerciseCount": 2,
                            "dueExerciseCount": 1,
                            "averageSpeedSec": 2.5,
                            "accuracyPercent": 83.33,
                            "status": "ACTIVE"
                        }
                    },
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440002",
                        "name": "Numbers",
                        "statistic": {
                            "totalExerciseCount": 2,
                            "reviewedExerciseCount": 0,
                            "dueExerciseCount": 2,
                            "averageSpeedSec": 0.0,
                            "accuracyPercent": 0.0,
                            "status": "NOT_STARTED"
                        }
                    },
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440003",
                        "name": "Words",
                        "statistic": {
                            "totalExerciseCount": 0,
                            "reviewedExerciseCount": 0,
                            "dueExerciseCount": 0,
                            "averageSpeedSec": 0.0,
                            "accuracyPercent": 0.0,
                            "status": "NOT_STARTED"
                        }
                    }
                ]
            """,
                    false,
                ),
            )

    private fun org.springframework.test.web.servlet.ResultActions.andExpectEmptyStatisticsResponse() =
        this
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(
                content().json(
                    """
                [
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440001",
                        "name": "Letters",
                        "statistic": {
                            "totalExerciseCount": 3,
                            "reviewedExerciseCount": 0,
                            "dueExerciseCount": 3,
                            "averageSpeedSec": 0.0,
                            "accuracyPercent": 0.0,
                            "status": "NOT_STARTED"
                        }
                    },
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440002",
                        "name": "Numbers",
                        "statistic": {
                            "totalExerciseCount": 2,
                            "reviewedExerciseCount": 0,
                            "dueExerciseCount": 2,
                            "averageSpeedSec": 0.0,
                            "accuracyPercent": 0.0,
                            "status": "NOT_STARTED"
                        }
                    },
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440003",
                        "name": "Words",
                        "statistic": {
                            "totalExerciseCount": 0,
                            "reviewedExerciseCount": 0,
                            "dueExerciseCount": 0,
                            "averageSpeedSec": 0.0,
                            "accuracyPercent": 0.0,
                            "status": "NOT_STARTED"
                        }
                    }
                ]
            """,
                    false,
                ),
            )
}
