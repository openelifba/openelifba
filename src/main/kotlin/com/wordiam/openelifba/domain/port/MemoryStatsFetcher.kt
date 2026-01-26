package com.wordiam.openelifba.domain.port

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.memory.MemoryStatistic
import com.wordiam.openelifba.domain.user.UserId

interface MemoryStatsFetcher {
    fun fetchMemoryStatistics(
        userId: UserId,
        categoryId: CategoryId,
    ): MemoryStatistic

    fun fetchDueExerciseCount(
        userId: UserId,
        categoryId: CategoryId,
    ): Int
}
