package com.wordiam.openelifba.infra.repository

import com.wordiam.openelifba.domain.port.ScoreboardFetcher
import com.wordiam.openelifba.domain.score.ScoreEntry
import com.wordiam.openelifba.domain.user.UserId
import com.wordiam.openelifba.jooq.generated.Tables.MEMORY
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
class JooqScoreboardRepository(
    private val dsl: DSLContext,
) : ScoreboardFetcher {
    override fun fetchTopScores(limit: Int): List<ScoreEntry> {
        val score = DSL.sum(MEMORY.CORRECT_COUNT).`as`("score")
        val rank = DSL.rank().over().orderBy(DSL.sum(MEMORY.CORRECT_COUNT).desc()).`as`("rank")

        return dsl
            .select(MEMORY.USER_ID, score, rank)
            .from(MEMORY)
            .groupBy(MEMORY.USER_ID)
            .orderBy(score.desc())
            .limit(limit)
            .fetch { record ->
                ScoreEntry(
                    userId = UserId(record.get(MEMORY.USER_ID)),
                    score = record.get(score)?.toLong() ?: 0L,
                    rank = record.get(rank)?.toLong() ?: 0L,
                )
            }
    }

    override fun fetchUserRank(userId: UserId): ScoreEntry? {
        val userIdAlias = MEMORY.USER_ID.`as`("userId")
        val scoreAlias = DSL.sum(MEMORY.CORRECT_COUNT).`as`("score")
        val rankAlias = DSL.rank().over().orderBy(DSL.sum(MEMORY.CORRECT_COUNT).desc()).`as`("rank")

        val ranked =
            dsl
                .select(userIdAlias, scoreAlias, rankAlias)
                .from(MEMORY)
                .groupBy(MEMORY.USER_ID)
                .asTable("ranked")

        val userIdField = ranked.field("userId", UUID::class.java) ?: error("userId field not found in ranked subquery")
        val scoreField =
            ranked.field("score", BigDecimal::class.java) ?: error("score field not found in ranked subquery")
        val rankField = ranked.field("rank", Long::class.java) ?: error("rank field not found in ranked subquery")

        return dsl
            .select(userIdField, scoreField, rankField)
            .from(ranked)
            .where(userIdField.eq(userId.value))
            .fetchOne { record ->
                ScoreEntry(
                    userId = UserId(record.get(userIdField)),
                    score = record.get(scoreField)?.toLong() ?: 0L,
                    rank = record.get(rankField) ?: 0L,
                )
            }
    }
}
