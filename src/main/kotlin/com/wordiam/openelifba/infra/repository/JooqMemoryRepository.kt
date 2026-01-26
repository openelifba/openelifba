package com.wordiam.openelifba.infra.repository

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.memory.MemoryStatistic
import com.wordiam.openelifba.domain.port.MemoryStatsFetcher
import com.wordiam.openelifba.domain.time.Clock
import com.wordiam.openelifba.domain.user.UserId
import com.wordiam.openelifba.jooq.generated.Tables.EXERCISE
import com.wordiam.openelifba.jooq.generated.Tables.MEMORY
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class JooqMemoryRepository(
    private val jooq: DSLContext,
    private val clock: Clock,
) : MemoryStatsFetcher {
    override fun fetchMemoryStatistics(
        userId: UserId,
        categoryId: CategoryId,
    ): MemoryStatistic {
        val e = EXERCISE.`as`("e")
        val m = MEMORY.`as`("m")

        return jooq
            .select(
                DSL.countDistinct(m.EXERCISE_ID).`as`("reviewedExerciseCount"),
                DSL.avg(m.RESPONSE_TIME_MILLIS).`as`("averageResponseTimeMillis"),
                DSL.sum(m.CORRECT_COUNT).`as`("correctCount"),
                DSL.sum(m.INCORRECT_COUNT).`as`("incorrectCount"),
            ).from(e)
            .leftJoin(m)
            .on(
                e.ID
                    .eq(m.EXERCISE_ID)
                    .and(m.USER_ID.eq(userId.value))
                    .and(m.CATEGORY_ID.eq(categoryId.value)),
            ).where(e.CATEGORY_ID.eq(categoryId.value))
            .fetchOneInto(ExerciseMemoryStatisticRecord::class.java)
            ?.toDomainObject() ?: MemoryStatistic()
    }

    override fun fetchDueExerciseCount(
        userId: UserId,
        categoryId: CategoryId,
    ): Int {
        val m = MEMORY.`as`("m")

        val dueExerciseCount =
            jooq
                .select(
                    DSL.countDistinct(m.EXERCISE_ID),
                ).from(m)
                .where(
                    m.USER_ID
                        .eq(userId.value)
                        .and(m.CATEGORY_ID.eq(categoryId.value))
                        .and(m.NEXT_REVIEW_AT.le(clock.now())),
                ).fetchOne(0, Int::class.java) ?: 0

        return dueExerciseCount
    }

    data class ExerciseMemoryStatisticRecord(
        val reviewedExerciseCount: Int = 0,
        val averageResponseTimeMillis: Long = 0,
        val correctCount: Int = 0,
        val incorrectCount: Int = 0,
    )

    private fun ExerciseMemoryStatisticRecord.toDomainObject(): MemoryStatistic =
        MemoryStatistic(
            reviewedExerciseCount = reviewedExerciseCount,
            averageResponseTime = Duration.ofMillis(averageResponseTimeMillis),
            correctCount = correctCount,
            incorrectCount = incorrectCount,
        )
}
