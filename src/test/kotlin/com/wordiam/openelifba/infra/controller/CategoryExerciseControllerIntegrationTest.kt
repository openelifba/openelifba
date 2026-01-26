package com.wordiam.openelifba.infra.controller

import com.ninjasquad.springmockk.MockkBean
import com.wordiam.openelifba.application.GetDueExercisesUseCase
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.Exercise
import com.wordiam.openelifba.domain.exercise.ExerciseId
import com.wordiam.openelifba.domain.exercise.ExerciseWithStreak
import com.wordiam.openelifba.domain.user.UserId
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = ["spring.flyway.enabled=false"])
class CategoryExerciseControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var getDueExercisesUseCase: GetDueExercisesUseCase

    @Test
    fun `should return due exercises for valid category and user`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))

        val mockExercises =
            listOf(
                ExerciseWithStreak(
                    Exercise(
                        id = ExerciseId(UUID.fromString("660e8400-e29b-41d4-a716-446655440001")),
                        value = "Alif",
                        audioUrl = "/audio/alif.mp3",
                    ),
                    streak = 3,
                ),
                ExerciseWithStreak(
                    Exercise(
                        id = ExerciseId(UUID.fromString("660e8400-e29b-41d4-a716-446655440002")),
                        value = "Ba",
                        audioUrl = "/audio/ba.mp3",
                    ),
                    streak = 0,
                ),
            )

        every { getDueExercisesUseCase.execute(userId, categoryId) } returns mockExercises

        // When & Then
        mockMvc
            .perform(
                get("/api/categories/${categoryId.value}/exercises/due")
                    .header("x-user-id", userId.value.toString()),
            ).andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(
                content().json(
                    """
                [
                    {
                        "id": "660e8400-e29b-41d4-a716-446655440001",
                        "value": "Alif",
                        "audioUrl": "/audio/alif.mp3",
                        "strikeCount": 3
                    },
                    {
                        "id": "660e8400-e29b-41d4-a716-446655440002",
                        "value": "Ba",
                        "audioUrl": "/audio/ba.mp3",
                        "strikeCount": 0
                    }
                ]
            """,
                    true,
                ),
            )
    }

    @Test
    fun `should return empty list when no due exercises exist`() {
        // Given
        val userId = UserId(UUID.fromString("880e8400-e29b-41d4-a716-446655440001"))
        val categoryId = CategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))

        every { getDueExercisesUseCase.execute(userId, categoryId) } returns emptyList()

        // When & Then
        mockMvc
            .perform(
                get("/api/categories/${categoryId.value}/exercises/due")
                    .header("x-user-id", userId.value.toString()),
            ).andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json("[]", true))
    }

    @Test
    fun `should return 400 when user id header is missing`() {
        // Given
        val categoryId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")

        // When & Then
        mockMvc
            .perform(get("/api/categories/$categoryId/exercises/due"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when user id header is invalid UUID`() {
        // Given
        val categoryId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")

        // When & Then
        mockMvc
            .perform(
                get("/api/categories/$categoryId/exercises/due")
                    .header("x-user-id", "invalid-uuid"),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when category id is invalid UUID`() {
        // Given
        val userId = UUID.fromString("880e8400-e29b-41d4-a716-446655440001")

        // When & Then
        mockMvc
            .perform(
                get("/api/categories/invalid-uuid/exercises/due")
                    .header("x-user-id", userId.toString()),
            ).andExpect(status().isBadRequest)
    }
}
