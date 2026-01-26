package com.wordiam.openelifba.infra.time

import com.wordiam.openelifba.domain.time.Clock
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SystemClock : Clock {
    override fun now(): LocalDateTime = LocalDateTime.now()
}
