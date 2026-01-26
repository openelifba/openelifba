package com.wordiam.openelifba.application

import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.exercise.ExerciseId
import com.wordiam.openelifba.domain.memory.Memory
import com.wordiam.openelifba.domain.port.ExerciseMemoryFinder
import com.wordiam.openelifba.domain.port.ExerciseMemoryUpserter
import com.wordiam.openelifba.domain.time.Clock
import com.wordiam.openelifba.domain.user.UserId
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class UpdateMemoryUseCase(
    private val exerciseMemoryFinder: ExerciseMemoryFinder,
    private val exerciseMemoryUpserter: ExerciseMemoryUpserter,
    private val clock: Clock,
) {
    fun execute(updateMemoryRequest: UpdateMemoryRequest) {
        val existingMemory =
            exerciseMemoryFinder.findExerciseMemory(
                userId = updateMemoryRequest.userId,
                categoryId = updateMemoryRequest.categoryId,
                exerciseId = updateMemoryRequest.exerciseId,
            )

        val memory =
            existingMemory?.updateWithResult(
                updateMemoryRequest.success,
                updateMemoryRequest.responseTime,
                clock,
            ) ?: Memory.create(
                exerciseId = updateMemoryRequest.exerciseId,
                categoryId = updateMemoryRequest.categoryId,
                userId = updateMemoryRequest.userId,
                success = updateMemoryRequest.success,
                responseTime = updateMemoryRequest.responseTime,
                clock = clock,
            )

        exerciseMemoryUpserter.upsert(memory)
    }

    class UpdateMemoryRequest(
        val userId: UserId,
        val categoryId: CategoryId,
        val exerciseId: ExerciseId,
        val success: Boolean,
        val responseTime: Duration,
    )
}
