package com.wordiam.openelifba.infra.repository

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.Exercise
import com.wordiam.openelifba.domain.exercise.ExerciseId
import com.wordiam.openelifba.domain.exercise.ExerciseWithStreak
import com.wordiam.openelifba.domain.memory.Memory
import com.wordiam.openelifba.domain.port.ExerciseMemoryFetcher
import com.wordiam.openelifba.domain.port.ExerciseMemoryFinder
import com.wordiam.openelifba.domain.port.ExerciseMemoryUpserter
import com.wordiam.openelifba.domain.user.UserId
import com.wordiam.openelifba.jooq.generated.Tables.EXERCISE
import com.wordiam.openelifba.jooq.generated.Tables.MEMORY
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime

@Repository
class JooqExerciseMemoryRepository(
    private val dsl: DSLContext,
) : ExerciseMemoryFetcher,
    ExerciseMemoryUpserter,
    ExerciseMemoryFinder {
    override fun fetchDueExercises(
        userId: UserId,
        categoryId: CategoryId,
        now: LocalDateTime,
    ): List<ExerciseWithStreak> =
        dsl
            .select(
                EXERCISE.ID,
                EXERCISE.VALUE,
                EXERCISE.AUDIO_URL,
                MEMORY.STREAK,
            ).from(EXERCISE)
            .leftJoin(MEMORY)
            .on(
                EXERCISE.ID
                    .eq(MEMORY.EXERCISE_ID)
                    .and(MEMORY.USER_ID.eq(userId.value)),
            ).where(EXERCISE.CATEGORY_ID.eq(categoryId.value))
            .and(
                MEMORY.NEXT_REVIEW_AT.isNull
                    .or(MEMORY.NEXT_REVIEW_AT.lessThan(now)),
            ).orderBy(MEMORY.INCORRECT_COUNT.desc(), MEMORY.STREAK.asc())
            .fetch { record ->
                ExerciseWithStreak(
                    exercise =
                        Exercise(
                            id = ExerciseId(record.get(EXERCISE.ID)),
                            value = record.get(EXERCISE.VALUE),
                            audioUrl = record.get(EXERCISE.AUDIO_URL),
                        ),
                    streak = record.get(MEMORY.STREAK) ?: 0,
                )
            }

    override fun findExerciseMemory(
        userId: UserId,
        categoryId: CategoryId,
        exerciseId: ExerciseId,
    ): Memory? {
        val record =
            dsl
                .select()
                .from(MEMORY)
                .where(
                    MEMORY.USER_ID
                        .eq(userId.value)
                        .and(MEMORY.EXERCISE_ID.eq(exerciseId.value))
                        .and(MEMORY.CATEGORY_ID.eq(categoryId.value)),
                ).fetchOne() ?: return null

        return Memory(
            id = record.get(MEMORY.ID),
            exerciseId = ExerciseId(record.get(MEMORY.EXERCISE_ID)),
            categoryId = CategoryId(record.get(MEMORY.CATEGORY_ID)),
            userId = UserId(record.get(MEMORY.USER_ID)),
            nextReviewAt = record.get(MEMORY.NEXT_REVIEW_AT),
            lastReviewedAt = record.get(MEMORY.LAST_REVIEWED_AT),
            interval = Duration.ofSeconds(record.get(MEMORY.INTERVAL_SECOND)),
            streak = record.get(MEMORY.STREAK),
            correctCount = record.get(MEMORY.CORRECT_COUNT),
            incorrectCount = record.get(MEMORY.INCORRECT_COUNT),
            responseTime = Duration.ofMillis(record.get(MEMORY.RESPONSE_TIME_MILLIS)),
            totalReviewTimeMillis = record.get(MEMORY.TOTAL_REVIEW_TIME_MILLIS) ?: 0L,
            easeFactor = record.get(MEMORY.EASE_FACTOR),
            updatedAt = record.get(MEMORY.UPDATED_AT),
        )
    }

    override fun upsert(memory: Memory) {
        dsl
            .insertInto(
                MEMORY,
                MEMORY.ID,
                MEMORY.EXERCISE_ID,
                MEMORY.CATEGORY_ID,
                MEMORY.USER_ID,
                MEMORY.NEXT_REVIEW_AT,
                MEMORY.INTERVAL_SECOND,
                MEMORY.RESPONSE_TIME_MILLIS,
                MEMORY.TOTAL_REVIEW_TIME_MILLIS,
                MEMORY.STREAK,
                MEMORY.CORRECT_COUNT,
                MEMORY.INCORRECT_COUNT,
                MEMORY.EASE_FACTOR,
                MEMORY.UPDATED_AT,
                MEMORY.LAST_REVIEWED_AT,
            ).values(
                memory.id,
                memory.exerciseId.value,
                memory.categoryId.value,
                memory.userId.value,
                memory.nextReviewAt,
                memory.interval.toSeconds(),
                memory.responseTime.toMillis(),
                memory.totalReviewTimeMillis,
                memory.streak,
                memory.correctCount,
                memory.incorrectCount,
                memory.easeFactor,
                memory.updatedAt,
                memory.lastReviewedAt,
            ).onConflict(MEMORY.EXERCISE_ID, MEMORY.CATEGORY_ID, MEMORY.USER_ID)
            .doUpdate()
            .set(MEMORY.UPDATED_AT, memory.updatedAt)
            .set(MEMORY.NEXT_REVIEW_AT, memory.nextReviewAt)
            .set(MEMORY.INTERVAL_SECOND, memory.interval.toSeconds())
            .set(MEMORY.STREAK, memory.streak)
            .set(MEMORY.LAST_REVIEWED_AT, memory.lastReviewedAt)
            .set(MEMORY.CORRECT_COUNT, memory.correctCount)
            .set(MEMORY.INCORRECT_COUNT, memory.incorrectCount)
            .set(MEMORY.RESPONSE_TIME_MILLIS, memory.responseTime.toMillis())
            .set(MEMORY.TOTAL_REVIEW_TIME_MILLIS, memory.totalReviewTimeMillis)
            .set(MEMORY.EASE_FACTOR, memory.easeFactor)
            .execute()
    }
}
