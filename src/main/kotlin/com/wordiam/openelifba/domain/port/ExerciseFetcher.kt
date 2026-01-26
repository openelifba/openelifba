package com.wordiam.openelifba.domain.port

import com.wordiam.openelifba.domain.category.CategoryId

interface ExerciseFetcher {
    fun fetchTotalExerciseCount(categoryId: CategoryId): Int
}
