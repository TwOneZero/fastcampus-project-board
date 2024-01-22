package com.fastcampus.projectboard.config;


import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// TODO : JPA Auditing 공부

@EnableJpaAuditing
@Configuration
public class JpaConfig {
    // Auditing 할 때 생성자 이름을 넣어주기 위한 필드 설정
    @Bean
    public AuditorAware<String> auditorAware() {
        // TODO : lambda 와 Optional 정리
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(BoardPrincipal.class::cast)
                .map(BoardPrincipal::getUsername);

    }
}
