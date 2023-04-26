package gdsc.binaryho.imhere.config;


import gdsc.binaryho.imhere.config.jwt.JwtAuthenticationFilter;
import gdsc.binaryho.imhere.config.jwt.JwtAuthorizationFilter;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

// 필터 추가, url 허용
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final MemberRepository memberRepository;

    private final CorsFilter corsFilter;

    private final TokenService tokenService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
            .formLogin().disable()
            .httpBasic().disable()

            .authorizeRequests()

            .antMatchers("/login", "/logout", "/member/**", "/swagger*/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
            .permitAll()

            .antMatchers("/api/v1/admin/**")
            .access("hasRole('ROLE_ADMIN')")

            .antMatchers("/api/v1/lectures/**", "/api/v1/enrollment/**", "/api/v1/attendance/**")
            .access("hasAnyRole('ROLE_ADMIN', 'ROLE_LECTURER', 'ROLE_STUDENT')")

            .anyRequest().authenticated();

        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), tokenService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtAuthorizationFilter(authenticationManager(authenticationConfiguration), tokenService, memberRepository), BasicAuthenticationFilter.class);

        return http.build();
    }
}
