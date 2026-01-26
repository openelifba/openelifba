package com.wordiam.openelifba.infra.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class RequestLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestWrapper = ContentCachingRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)

        val startTime = System.currentTimeMillis()

        try {
            filterChain.doFilter(requestWrapper, responseWrapper)
        } finally {
            val duration = System.currentTimeMillis() - startTime
            val requestBody = String(requestWrapper.contentAsByteArray)
            val responseBody = String(responseWrapper.contentAsByteArray)

            log.info(
                "method={} uri={} status={} duration={}ms req_body={} res_body={}",
                request.method,
                request.requestURI,
                response.status,
                duration,
                preview(requestBody),
                preview(responseBody)
            )
            
            responseWrapper.copyBodyToResponse()
        }
    }
    
    private fun preview(content: String, maxLength: Int = 100): String {
        if (content.isBlank()) return ""
        val normalized = content.replace(Regex("\\s+"), " ")
        return if (normalized.length > maxLength) "${normalized.take(maxLength)}..." else normalized
    }
}
