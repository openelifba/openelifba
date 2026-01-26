package com.wordiam.openelifba.infra.controller

import com.ninjasquad.springmockk.MockkBean
import com.wordiam.openelifba.application.UpdateMemoryUseCase
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = ["spring.flyway.enabled=false"])
class MemoryControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var updateMemoryUseCase: UpdateMemoryUseCase

    @Test
    fun `should update memory when valid request is provided`() {
        // Given
        val userId = UUID.fromString("880e8400-e29b-41d4-a716-446655440001")
        val categoryId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
        val exerciseId = UUID.fromString("660e8400-e29b-41d4-a716-446655440001")
        val requestSlot = slot<UpdateMemoryUseCase.UpdateMemoryRequest>()

        every { updateMemoryUseCase.execute(capture(requestSlot)) } returns Unit

        // When & Then
        mockMvc
            .perform(
                post("/api/memory")
                    .header("x-user-id", userId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "categoryId": "$categoryId",
                        "exerciseId": "$exerciseId",
                        "success": true,
                        "responseTimeMillis": 3000
                    }
                """,
                    ),
            ).andExpect(status().isOk)

        // Verify the use case was called with correct parameters
        verify { updateMemoryUseCase.execute(any()) }
        val capturedRequest = requestSlot.captured
        assert(capturedRequest.userId.value == userId)
        assert(capturedRequest.categoryId.value == categoryId)
        assert(capturedRequest.exerciseId.value == exerciseId)
        assert(capturedRequest.success == true)
        assert(capturedRequest.responseTime == Duration.ofMillis(3000))
    }

    @Test
    fun `should update memory when response was incorrect`() {
        // Given
        val userId = UUID.fromString("880e8400-e29b-41d4-a716-446655440001")
        val categoryId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
        val exerciseId = UUID.fromString("660e8400-e29b-41d4-a716-446655440001")
        val requestSlot = slot<UpdateMemoryUseCase.UpdateMemoryRequest>()

        every { updateMemoryUseCase.execute(capture(requestSlot)) } returns Unit

        // When & Then
        mockMvc
            .perform(
                post("/api/memory")
                    .header("x-user-id", userId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "categoryId": "$categoryId",
                        "exerciseId": "$exerciseId",
                        "success": false,
                        "responseTimeMillis": 8000
                    }
                """,
                    ),
            ).andExpect(status().isOk)

        // Verify the use case was called with correct parameters
        verify { updateMemoryUseCase.execute(any()) }
        val capturedRequest = requestSlot.captured
        assert(capturedRequest.userId.value == userId)
        assert(capturedRequest.categoryId.value == categoryId)
        assert(capturedRequest.exerciseId.value == exerciseId)
        assert(capturedRequest.success == false)
        assert(capturedRequest.responseTime == Duration.ofMillis(8000))
    }

    @Test
    fun `should return 400 when user id header is missing`() {
        // When & Then
        mockMvc
            .perform(
                post("/api/memory")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "categoryId": "550e8400-e29b-41d4-a716-446655440001",
                        "exerciseId": "660e8400-e29b-41d4-a716-446655440001",
                        "success": true,
                        "responseTimeMillis": 3000
                    }
                """,
                    ),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when user id header is invalid UUID`() {
        // When & Then
        mockMvc
            .perform(
                post("/api/memory")
                    .header("x-user-id", "invalid-uuid")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "categoryId": "550e8400-e29b-41d4-a716-446655440001",
                        "exerciseId": "660e8400-e29b-41d4-a716-446655440001",
                        "success": true,
                        "responseTimeMillis": 3000
                    }
                """,
                    ),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when request body is missing`() {
        // Given
        val userId = UUID.fromString("880e8400-e29b-41d4-a716-446655440001")

        // When & Then
        mockMvc
            .perform(
                post("/api/memory")
                    .header("x-user-id", userId.toString())
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when request body has invalid JSON`() {
        // Given
        val userId = UUID.fromString("880e8400-e29b-41d4-a716-446655440001")

        // When & Then
        mockMvc
            .perform(
                post("/api/memory")
                    .header("x-user-id", userId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ invalid json }"),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when categoryId is invalid UUID`() {
        // Given
        val userId = UUID.fromString("880e8400-e29b-41d4-a716-446655440001")

        // When & Then
        mockMvc
            .perform(
                post("/api/memory")
                    .header("x-user-id", userId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "categoryId": "invalid-uuid",
                        "exerciseId": "660e8400-e29b-41d4-a716-446655440001",
                        "success": true,
                        "responseTimeMillis": 3000
                    }
                """,
                    ),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when exerciseId is invalid UUID`() {
        // Given
        val userId = UUID.fromString("880e8400-e29b-41d4-a716-446655440001")

        // When & Then
        mockMvc
            .perform(
                post("/api/memory")
                    .header("x-user-id", userId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "categoryId": "550e8400-e29b-41d4-a716-446655440001",
                        "exerciseId": "invalid-uuid",
                        "success": true,
                        "responseTimeMillis": 3000
                    }
                """,
                    ),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should handle zero response time correctly`() {
        // Given
        val userId = UUID.fromString("880e8400-e29b-41d4-a716-446655440001")
        val categoryId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
        val exerciseId = UUID.fromString("660e8400-e29b-41d4-a716-446655440001")
        val requestSlot = slot<UpdateMemoryUseCase.UpdateMemoryRequest>()

        every { updateMemoryUseCase.execute(capture(requestSlot)) } returns Unit

        // When & Then
        mockMvc
            .perform(
                post("/api/memory")
                    .header("x-user-id", userId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "categoryId": "$categoryId",
                        "exerciseId": "$exerciseId",
                        "success": true,
                        "responseTimeMillis": 0
                    }
                """,
                    ),
            ).andExpect(status().isOk)

        // Verify the use case was called with correct parameters
        verify { updateMemoryUseCase.execute(any()) }
        val capturedRequest = requestSlot.captured
        assert(capturedRequest.responseTime == Duration.ZERO)
    }

    @Test
    fun `should handle large response time correctly`() {
        // Given
        val userId = UUID.fromString("880e8400-e29b-41d4-a716-446655440001")
        val categoryId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
        val exerciseId = UUID.fromString("660e8400-e29b-41d4-a716-446655440001")
        val requestSlot = slot<UpdateMemoryUseCase.UpdateMemoryRequest>()

        every { updateMemoryUseCase.execute(capture(requestSlot)) } returns Unit

        // When & Then
        mockMvc
            .perform(
                post("/api/memory")
                    .header("x-user-id", userId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "categoryId": "$categoryId",
                        "exerciseId": "$exerciseId",
                        "success": false,
                        "responseTimeMillis": 120000
                    }
                """,
                    ),
            ).andExpect(status().isOk)

        // Verify the use case was called with correct parameters
        verify { updateMemoryUseCase.execute(any()) }
        val capturedRequest = requestSlot.captured
        assert(capturedRequest.responseTime == Duration.ofMillis(120000))
    }
}
