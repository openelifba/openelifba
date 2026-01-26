package com.wordiam.openelifba.infra.repository.config

import com.wordiam.openelifba.infra.config.TestClockConfiguration
import com.wordiam.openelifba.infra.config.TestcontainersConfiguration
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration::class, TestClockConfiguration::class)
@ActiveProfiles("test")
@WithDataSet
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
annotation class JooqTest
