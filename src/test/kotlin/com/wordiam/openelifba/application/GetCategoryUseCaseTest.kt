package com.wordiam.openelifba.application

import com.wordiam.openelifba.domain.category.Category
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.category.CategoryStatus
import com.wordiam.openelifba.domain.category.CategoryStatusService
import com.wordiam.openelifba.domain.memory.MemoryStatistic
import com.wordiam.openelifba.domain.port.CategoryFetcher
import com.wordiam.openelifba.domain.port.ExerciseFetcher
import com.wordiam.openelifba.domain.port.MemoryStatsFetcher
import com.wordiam.openelifba.domain.user.UserId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.UUID

class GetCategoryUseCaseTest {
    private lateinit var categoryFetcher: CategoryFetcher
    private lateinit var exerciseFetcher: ExerciseFetcher
    private lateinit var memoryStatsFetcher: MemoryStatsFetcher
    private lateinit var categoryStatusService: CategoryStatusService
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase

    private val testUserId = UserId(UUID.randomUUID())
    private val testCategoryId = CategoryId(UUID.randomUUID())

    @BeforeEach
    fun setUp() {
        categoryFetcher = mockk()
        exerciseFetcher = mockk()
        memoryStatsFetcher = mockk()
        categoryStatusService = mockk()
        getCategoriesUseCase =
            GetCategoriesUseCase(
                categoryFetcher,
                exerciseFetcher,
                memoryStatsFetcher,
                categoryStatusService,
            )
    }

    @Test
    fun `should return categories with statistics when all data is available`() {
        // Given
        val category =
            Category(
                id = testCategoryId,
                name = "Test Category",
                statistic = null,
            )
        val totalExerciseCount = 10
        val dueExerciseCount = 3
        val memoryStats =
            MemoryStatistic(
                reviewedExerciseCount = 7,
                averageResponseTime = Duration.ofSeconds(2),
                correctCount = 6,
                incorrectCount = 1,
            )
        val status = CategoryStatus.ACTIVE

        every { categoryFetcher.fetchCategories() } returns listOf(category)
        every { exerciseFetcher.fetchTotalExerciseCount(testCategoryId) } returns totalExerciseCount
        every { memoryStatsFetcher.fetchMemoryStatistics(testUserId, testCategoryId) } returns memoryStats
        every { memoryStatsFetcher.fetchDueExerciseCount(testUserId, testCategoryId) } returns dueExerciseCount
        every {
            categoryStatusService.getStatus(
                totalExerciseCount = totalExerciseCount,
                dueExerciseCount = dueExerciseCount,
                memoryStats = memoryStats,
            )
        } returns status

        // When
        val result = getCategoriesUseCase.execute(testUserId)

        // Then
        assertEquals(1, result.size)
        val resultCategory = result[0]
        assertEquals(testCategoryId, resultCategory.id)
        assertEquals("Test Category", resultCategory.name)

        val statistic = resultCategory.statistic!!
        assertEquals(totalExerciseCount, statistic.totalExerciseCount)
        assertEquals(7, statistic.reviewedExerciseCount)
        assertEquals(Duration.ofSeconds(2), statistic.averageSpeed)
        assertEquals(85.71, statistic.accuracyPercent, 0.01) // 6/7 * 100
        assertEquals(dueExerciseCount, statistic.dueExerciseCount)
        assertEquals(status, statistic.status)

        // Verify all dependencies were called
        verify { categoryFetcher.fetchCategories() }
        verify { exerciseFetcher.fetchTotalExerciseCount(testCategoryId) }
        verify { memoryStatsFetcher.fetchMemoryStatistics(testUserId, testCategoryId) }
        verify { memoryStatsFetcher.fetchDueExerciseCount(testUserId, testCategoryId) }
        verify {
            categoryStatusService.getStatus(
                totalExerciseCount = totalExerciseCount,
                dueExerciseCount = dueExerciseCount,
                memoryStats = memoryStats,
            )
        }
    }

    @Test
    fun `should handle multiple categories correctly`() {
        // Given
        val category1 = Category(CategoryId(UUID.randomUUID()), "Category 1", null)
        val category2 = Category(CategoryId(UUID.randomUUID()), "Category 2", null)
        val categories = listOf(category1, category2)

        every { categoryFetcher.fetchCategories() } returns categories
        every { exerciseFetcher.fetchTotalExerciseCount(any()) } returns 5
        every { memoryStatsFetcher.fetchMemoryStatistics(any(), any()) } returns MemoryStatistic()
        every { memoryStatsFetcher.fetchDueExerciseCount(any(), any()) } returns 0
        every { categoryStatusService.getStatus(any(), any(), any()) } returns CategoryStatus.NOT_STARTED

        // When
        val result = getCategoriesUseCase.execute(testUserId)

        // Then
        assertEquals(2, result.size)
        assertEquals("Category 1", result[0].name)
        assertEquals("Category 2", result[1].name)

        // Fallback should activate the first NOT_STARTED category
        assertEquals(CategoryStatus.ACTIVE, result[0].statistic!!.status)
        assertEquals(CategoryStatus.NOT_STARTED, result[1].statistic!!.status)

        // Verify calls for each category
        verify { exerciseFetcher.fetchTotalExerciseCount(category1.id) }
        verify { exerciseFetcher.fetchTotalExerciseCount(category2.id) }
        verify { memoryStatsFetcher.fetchMemoryStatistics(testUserId, category1.id) }
        verify { memoryStatsFetcher.fetchMemoryStatistics(testUserId, category2.id) }
    }

    @Test
    fun `should return empty list when no categories exist`() {
        // Given
        every { categoryFetcher.fetchCategories() } returns emptyList()

        // When
        val result = getCategoriesUseCase.execute(testUserId)

        // Then
        assertTrue(result.isEmpty())
        verify { categoryFetcher.fetchCategories() }
        verify(exactly = 0) { exerciseFetcher.fetchTotalExerciseCount(any()) }
        verify(exactly = 0) { memoryStatsFetcher.fetchMemoryStatistics(any(), any()) }
    }

    @Test
    fun `should handle zero statistics correctly`() {
        // Given
        val category = Category(testCategoryId, "Test Category", null)
        val emptyMemoryStats =
            MemoryStatistic(
                reviewedExerciseCount = 0,
                averageResponseTime = Duration.ZERO,
                correctCount = 0,
                incorrectCount = 0,
            )

        every { categoryFetcher.fetchCategories() } returns listOf(category)
        every { exerciseFetcher.fetchTotalExerciseCount(testCategoryId) } returns 0
        every { memoryStatsFetcher.fetchMemoryStatistics(testUserId, testCategoryId) } returns emptyMemoryStats
        every { memoryStatsFetcher.fetchDueExerciseCount(testUserId, testCategoryId) } returns 0
        every { categoryStatusService.getStatus(any(), any(), any()) } returns CategoryStatus.NOT_STARTED

        // When
        val result = getCategoriesUseCase.execute(testUserId)

        // Then
        assertEquals(1, result.size)
        val statistic = result[0].statistic!!
        assertEquals(0, statistic.totalExerciseCount)
        assertEquals(0, statistic.reviewedExerciseCount)
        assertEquals(Duration.ZERO, statistic.averageSpeed)
        assertEquals(0.0, statistic.accuracyPercent)
        assertEquals(0, statistic.dueExerciseCount)
        // Fallback should mark single NOT_STARTED as ACTIVE
        assertEquals(CategoryStatus.ACTIVE, statistic.status)

        // Verify dependencies
        verify { categoryFetcher.fetchCategories() }
        verify { exerciseFetcher.fetchTotalExerciseCount(testCategoryId) }
        verify { memoryStatsFetcher.fetchMemoryStatistics(testUserId, testCategoryId) }
        verify { memoryStatsFetcher.fetchDueExerciseCount(testUserId, testCategoryId) }
        verify { categoryStatusService.getStatus(any(), any(), any()) }
    }

    @Test
    fun `should activate first NOT_STARTED when none are ACTIVE`() {
        // Given
        val cat1 = Category(CategoryId(UUID.randomUUID()), "A", null)
        val cat2 = Category(CategoryId(UUID.randomUUID()), "B", null)
        every { categoryFetcher.fetchCategories() } returns listOf(cat1, cat2)
        every { exerciseFetcher.fetchTotalExerciseCount(any()) } returns 0
        every { memoryStatsFetcher.fetchMemoryStatistics(any(), any()) } returns MemoryStatistic()
        every { memoryStatsFetcher.fetchDueExerciseCount(any(), any()) } returns 0
        every { categoryStatusService.getStatus(any(), any(), any()) } returns CategoryStatus.NOT_STARTED

        // When
        val result = getCategoriesUseCase.execute(testUserId)

        // Then
        assertEquals(CategoryStatus.ACTIVE, result[0].statistic!!.status)
        assertEquals(CategoryStatus.NOT_STARTED, result[1].statistic!!.status)
    }
}
