package com.wordiam.openelifba.infra.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("OpenElifba API")
                    .description("API for OpenElifba Vocabulary Learning Application")
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .name("OpenElifba Team")
                            .url("https://github.com/wordiam/openelifba")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
    }
}
