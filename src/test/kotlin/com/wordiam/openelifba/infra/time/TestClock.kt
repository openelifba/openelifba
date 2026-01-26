package com.wordiam.openelifba.infra.time

import com.wordiam.openelifba.domain.time.Clock
import java.time.LocalDateTime

class TestClock(
    private var currentTime: LocalDateTime = LocalDateTime.now(),
) : Clock {
    override fun now(): LocalDateTime = currentTime

    fun setTime(time: LocalDateTime) {
        currentTime = time
    }

    fun advanceBy(
        hours: Long = 0,
        minutes: Long = 0,
        seconds: Long = 0,
    ) {
        currentTime = currentTime.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds)
    }
}
