package com.wordiam.openelifba.domain.port

import com.wordiam.openelifba.domain.memory.Memory

interface ExerciseMemoryUpserter {
    fun upsert(memory: Memory)
}
