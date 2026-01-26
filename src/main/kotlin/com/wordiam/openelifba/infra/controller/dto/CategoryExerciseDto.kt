package com.wordiam.openelifba.infra.controller.dto

import com.wordiam.openelifba.domain.exercise.ExerciseWithStreak
import java.util.UUID

data class CategoryExerciseDto(
    val id: UUID,
    val value: String,
    val audioUrl: String,
    val strikeCount: Int,
) {
    companion object {
        fun from(exerciseWithStreak: ExerciseWithStreak): CategoryExerciseDto =
            CategoryExerciseDto(
                id = exerciseWithStreak.exercise.id.value,
                value = exerciseWithStreak.exercise.value,
                audioUrl = exerciseWithStreak.exercise.audioUrl,
                strikeCount = exerciseWithStreak.streak,
            )
    }
}
