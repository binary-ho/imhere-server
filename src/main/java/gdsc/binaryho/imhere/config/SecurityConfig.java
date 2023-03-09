package gdsc.binaryho.imhere.config;

import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsFilter corsFilter;

    public SecurityConfig(CorsFilter corsFilter) {
        this.corsFilter = corsFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .addFilter(corsFilter)
            .formLogin().disable()
            .httpBasic().disable()

            .authorizeRequests()
            .antMatchers("/api/v1/admin/**")
            .access("hasRole('ROLE_ADMIN')")

            .antMatchers("/api/v1/lectures/**", "/api/v1/enrollment/**")
            .access("hasAnyRole('ROLE_ADMIN', 'ROLE_LECTURER')")

            .antMatchers("/api/v1/students/**")
            .access("hasAnyRole('ROLE_ADMIN', 'ROLE_LECTURER', 'ROLE_STUDENT')")
            .anyRequest().authenticated();

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
// ADMIN, LECTURER, STUDENT
// "/api/v1/admin/{admin_id}/member/{member_id}.state" 권한 변경 -> 어드민

// "/api/v1/enrollment/{lecture_id}" -> 학생 등록 (어드민, 강사)
// "/api/v1/lectures" -> 강의 가져오기, 만들기 (어드민, 강사)
// "/api/v1/lectures/{lecture_id}/state" -> 강의 상태 변경 (어드민, 강사)

// "/api/v1/students/{student_id}/open-lectures" -> 자신이 수강중인 수업 중에 OPEN 상태 수업 가져오기 (어드민, 강사, 학생)
// "/api/v1/students/{student_id}/lectures" -> 자신이 수강중인 수업 전부 가져오기 (어드민, 강사, 학생)
// "/api/v1/students/{student_id}/attendance/{lecture_id}" -> 출석 참여 (어드민, 강사, 학생)
