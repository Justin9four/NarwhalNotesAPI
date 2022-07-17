package com.projectfawkes.api.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig {
    @Configuration
    @Order(1)
    class UserSecurityConfig : WebSecurityConfigurerAdapter() {
        @Autowired
        val authenticationProvider: AuthenticationProviderUser? = null

        @Autowired
        val authenticationEntryPoint: AuthenticationExceptionEntryPoint? = null

        override fun configure(auth: AuthenticationManagerBuilder) {
            auth.authenticationProvider(authenticationProvider)
        }

        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            http
                .csrf().disable()
                .requestMatchers()
                .antMatchers("/api/admin**", "/api/admin/**", "/api/users**", "/api/users/**")
                .and()
                .exceptionHandling()
                .accessDeniedHandler(AccessDeniedExceptionHandler())
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .addFilterBefore(FilterSession(), UsernamePasswordAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers("/api/admin**", "/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/users**", "/api/users/**").hasAnyRole("ADMIN", "USER")
        }
    }

    @Configuration
    @Order(2)
    class ServiceAccountSecurityConfig : WebSecurityConfigurerAdapter() {
        @Autowired
        val authenticationProvider: AuthenticationProviderServiceAccount? = null

        override fun configure(auth: AuthenticationManagerBuilder) {
            auth.authenticationProvider(authenticationProvider)
        }

        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            http
                .requestMatchers()
                .antMatchers("/api/authenticate**", "/api/register**", "/api/checkToken**", "/api/signOut**")
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .addFilterBefore(FilterServiceAccount(), UsernamePasswordAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers("/api/**").hasRole("SERVICE_ACCOUNT")
        }
    }

    @Configuration
    @Order(3)
    class AnonymousSecurityConfig : WebSecurityConfigurerAdapter() {

        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            http
                .requestMatchers()
                .antMatchers("/api**", "/api/**", "/error")
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
        }
    }
}