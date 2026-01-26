package com.wordiam.openelifba.application

import com.wordiam.openelifba.domain.category.Category
import com.wordiam.openelifba.domain.category.CategoryStatistic
import com.wordiam.openelifba.domain.category.CategoryStatus
import com.wordiam.openelifba.domain.category.CategoryStatusService
import com.wordiam.openelifba.domain.port.CategoryFetcher
import com.wordiam.openelifba.domain.port.ExerciseFetcher
import com.wordiam.openelifba.domain.port.MemoryStatsFetcher
import com.wordiam.openelifba.domain.user.UserId
import org.springframework.stereotype.Component

@Component
class GetCategoriesUseCase(
    private val categoryFetcher: CategoryFetcher,
    private val exerciseFetcher: ExerciseFetcher,
    private val memoryStatsFetcher: MemoryStatsFetcher,
    private val categoryStatusService: CategoryStatusService,
) {
    fun execute(userId: UserId): List<Category> {
        val categories = categoryFetcher.fetchCategories()
        val enriched =
            categories.map { category ->
                val totalExerciseCount = exerciseFetcher.fetchTotalExerciseCount(category.id)
                val memoryStats = memoryStatsFetcher.fetchMemoryStatistics(userId, category.id)
                val dueExerciseCount = memoryStatsFetcher.fetchDueExerciseCount(userId, category.id)
                val status =
                    categoryStatusService.getStatus(
                        totalExerciseCount = totalExerciseCount,
                        dueExerciseCount = dueExerciseCount,
                        memoryStats = memoryStats,
                    )
                category.copy(
                    statistic =
                        CategoryStatistic(
                            totalExerciseCount = totalExerciseCount,
                            reviewedExerciseCount = memoryStats.reviewedExerciseCount,
                            averageSpeed = memoryStats.averageResponseTime,
                            accuracyPercent = memoryStats.accuracyPercent,
                            dueExerciseCount = dueExerciseCount,
                            status = status,
                        ),
                )
            }

        // Fallback: if no category is ACTIVE, mark the first NOT_STARTED category as ACTIVE (if any)
        if (enriched.none { it.statistic?.status == CategoryStatus.ACTIVE }) {
            val idx = enriched.indexOfFirst { it.statistic?.status == CategoryStatus.NOT_STARTED }
            if (idx >= 0) {
                return enriched.mapIndexed { i, cat ->
                    if (i == idx) {
                        cat.copy(
                            statistic = cat.statistic?.copy(status = CategoryStatus.ACTIVE),
                        )
                    } else {
                        cat
                    }
                }
            }
        }

        return enriched
    }
}
