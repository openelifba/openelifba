package com.wordiam.openelifba.domain.score

import com.wordiam.openelifba.domain.user.UserId

data class ScoreEntry(
    val userId: UserId,
    val score: Long,
    val rank: Long,
)
