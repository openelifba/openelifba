package com.wordiam.openelifba.application

import com.wordiam.openelifba.domain.port.ScoreboardFetcher
import com.wordiam.openelifba.domain.score.ScoreEntry
import com.wordiam.openelifba.domain.user.UserId
import org.springframework.stereotype.Component

@Component
class GetScoreboardUseCase(
    private val scoreboardFetcher: ScoreboardFetcher,
) {
    fun execute(
        userId: UserId,
        limit: Int = DEFAULT_LIMIT,
    ): Scoreboard {
        val topScores = scoreboardFetcher.fetchTopScores(limit)
        val myRank = scoreboardFetcher.fetchUserRank(userId)
        return Scoreboard(topScores = topScores, myRank = myRank)
    }

    data class Scoreboard(
        val topScores: List<ScoreEntry>,
        val myRank: ScoreEntry?,
    )

    companion object {
        private const val DEFAULT_LIMIT = 10
    }
}
