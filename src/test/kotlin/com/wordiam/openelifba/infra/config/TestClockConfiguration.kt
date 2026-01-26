package com.wordiam.openelifba.infra.config

import com.wordiam.openelifba.domain.time.Clock
import com.wordiam.openelifba.infra.time.TestClock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import java.time.LocalDateTime

@TestConfiguration
class TestClockConfiguration {
    @Bean
    @Primary
    fun testClock(): Clock = TestClock(LocalDateTime.of(2025, 1, 1, 10, 0, 0))
}
