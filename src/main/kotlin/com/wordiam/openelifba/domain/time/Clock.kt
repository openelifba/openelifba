package com.wordiam.openelifba.domain.time

import java.time.LocalDateTime

interface Clock {
    fun now(): LocalDateTime
}
