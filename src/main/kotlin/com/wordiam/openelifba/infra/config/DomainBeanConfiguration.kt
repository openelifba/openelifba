package com.wordiam.openelifba.infra.config

import com.wordiam.openelifba.domain.category.CategoryStatusService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainBeanConfiguration {
    @Bean
    fun categoryStatusService(): CategoryStatusService = CategoryStatusService()
}
