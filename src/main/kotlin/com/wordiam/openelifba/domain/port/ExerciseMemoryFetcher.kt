package com.wordiam.openelifba.domain.port

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.ExerciseWithStreak
import com.wordiam.openelifba.domain.user.UserId
import java.time.LocalDateTime

interface ExerciseMemoryFetcher {
    fun fetchDueExercises(
        userId: UserId,
        categoryId: CategoryId,
        now: LocalDateTime,
    ): List<ExerciseWithStreak>
}
