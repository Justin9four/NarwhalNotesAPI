package com.projectfawkes.api.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.session.ReactiveMapSessionRepository
import org.springframework.session.ReactiveSessionRepository
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession
import java.util.concurrent.ConcurrentHashMap

@Configuration
@EnableSpringWebSession
class SessionConfig {
    @Bean
    fun reactiveSessionRepository(): ReactiveSessionRepository<*> {
        return ReactiveMapSessionRepository(ConcurrentHashMap())
    }
}

@Configuration
@EnableRedisWebSession
class RedisConfig {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory()
    }
}