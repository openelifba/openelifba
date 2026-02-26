package com.wordiam.openelifba.infra.controller

import com.wordiam.openelifba.application.GetScoreboardUseCase
import com.wordiam.openelifba.domain.user.UserId
import com.wordiam.openelifba.infra.controller.dto.ScoreboardDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/scoreboard")
@Tag(name = "Scoreboard", description = "Operations related to user scores and rankings")
class ScoreboardController(
    private val getScoreboardUseCase: GetScoreboardUseCase,
) {
    @Operation(
        summary = "Get scoreboard",
        description = "Retrieves the top-scoring users and the requesting user's own rank",
    )
    @GetMapping
    fun getScoreboard(
        @RequestHeader("x-user-id") userId: UUID,
        @RequestParam(defaultValue = "10") limit: Int,
    ): ScoreboardDto = ScoreboardDto.from(getScoreboardUseCase.execute(UserId(userId), limit))
}
