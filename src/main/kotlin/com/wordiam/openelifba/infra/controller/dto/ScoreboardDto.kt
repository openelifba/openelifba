package com.wordiam.openelifba.infra.controller.dto

import com.wordiam.openelifba.application.GetScoreboardUseCase
import com.wordiam.openelifba.domain.score.ScoreEntry
import java.util.UUID

data class ScoreEntryDto(
    val userId: UUID,
    val score: Long,
    val rank: Long,
) {
    companion object {
        fun from(entry: ScoreEntry): ScoreEntryDto =
            ScoreEntryDto(
                userId = entry.userId.value,
                score = entry.score,
                rank = entry.rank,
            )
    }
}

data class ScoreboardDto(
    val topScores: List<ScoreEntryDto>,
    val myRank: ScoreEntryDto?,
) {
    companion object {
        fun from(scoreboard: GetScoreboardUseCase.Scoreboard): ScoreboardDto =
            ScoreboardDto(
                topScores = scoreboard.topScores.map { ScoreEntryDto.from(it) },
                myRank = scoreboard.myRank?.let { ScoreEntryDto.from(it) },
            )
    }
}
