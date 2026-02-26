package com.wordiam.openelifba.domain.port

import com.wordiam.openelifba.domain.score.ScoreEntry
import com.wordiam.openelifba.domain.user.UserId

interface ScoreboardFetcher {
    fun fetchTopScores(limit: Int): List<ScoreEntry>

    fun fetchUserRank(userId: UserId): ScoreEntry?
}
