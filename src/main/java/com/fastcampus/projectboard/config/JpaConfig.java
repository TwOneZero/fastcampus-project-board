package com.fastcampus.projectboard.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

// TODO : JPA Auditing 공부

@EnableJpaAuditing
@Configuration
public class JpaConfig {
    // Auditing 할 때 생성자 이름을 넣어주기 위한 필드 설정
    @Bean
    public AuditorAware<String> auditorAware() {
        // TODO : lambda 와 Optional 정리
        return () -> Optional.of("twonezero"); // TODO : 스프링 시큐리티로 인증 기능을 붙일 때 변경해야 함.

    }
}
