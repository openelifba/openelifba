package com.wordiam.openelifba.infra.repository

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.port.ExerciseFetcher
import com.wordiam.openelifba.jooq.generated.Tables.EXERCISE
import org.springframework.stereotype.Repository

@Repository
class JooqExerciseRepository(
    private val jooq: org.jooq.DSLContext,
) : ExerciseFetcher {
    override fun fetchTotalExerciseCount(categoryId: CategoryId): Int =
        jooq
            .selectCount()
            .from(EXERCISE)
            .where(EXERCISE.CATEGORY_ID.eq(categoryId.value))
            .fetchOneInto(Int::class.java) ?: 0
}
