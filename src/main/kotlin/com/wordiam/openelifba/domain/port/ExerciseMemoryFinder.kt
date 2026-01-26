package com.wordiam.openelifba.domain.port

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.ExerciseId
import com.wordiam.openelifba.domain.memory.Memory
import com.wordiam.openelifba.domain.user.UserId

interface ExerciseMemoryFinder {
    fun findExerciseMemory(
        userId: UserId,
        categoryId: CategoryId,
        exerciseId: ExerciseId,
    ): Memory?
}
