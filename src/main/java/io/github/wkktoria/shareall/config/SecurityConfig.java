package io.github.wkktoria.shareall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(AbstractHttpConfigurer::disable);
        http.httpBasic(httpBasicConfigurer -> httpBasicConfigurer
                .authenticationEntryPoint(new BasicAuthenticationEntryPoint()));

        http.authorizeHttpRequests((authentication) -> authentication
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/1.0/login")).authenticated()
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/api/1.0/users/{id:[0-9]+}")).authenticated()
                .anyRequest().permitAll()
        );

        http.sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
