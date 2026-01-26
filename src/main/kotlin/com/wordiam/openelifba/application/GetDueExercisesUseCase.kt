package com.wordiam.openelifba.application

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.ExerciseWithStreak
import com.wordiam.openelifba.domain.port.ExerciseMemoryFetcher
import com.wordiam.openelifba.domain.time.Clock
import com.wordiam.openelifba.domain.user.UserId
import org.springframework.stereotype.Component

@Component
class GetDueExercisesUseCase(
    private val exerciseMemoryFetcher: ExerciseMemoryFetcher,
    private val clock: Clock,
) {
    fun execute(
        userId: UserId,
        categoryId: CategoryId,
    ): List<ExerciseWithStreak> = exerciseMemoryFetcher.fetchDueExercises(userId, categoryId, clock.now())
}
