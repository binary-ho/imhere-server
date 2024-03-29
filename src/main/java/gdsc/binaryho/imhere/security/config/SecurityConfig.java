package gdsc.binaryho.imhere.security.config;


import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.security.filter.JwtAuthenticationFilter;
import gdsc.binaryho.imhere.security.filter.JwtAuthorizationFilter;
import gdsc.binaryho.imhere.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final MemberRepository memberRepository;

    private final CorsFilter corsFilter;

    private final TokenService tokenService;

    @Value("${actuator.username}")
    private String ACTUATOR_USERNAME;

    @Value("${actuator.password}")
    private String ACTUATOR_PASSWORD;

    @Value("${actuator.role}")
    private String ACTUATOR_ROLE;

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
    @Order(0)
    public SecurityFilterChain filterChainForActuator(HttpSecurity http) throws Exception {
        http
            .requestMatchers()
            .antMatchers("/system/actuator/**")

            .and()
            .httpBasic()

            .and()
            .userDetailsService(getActuatorUserDetailsService())

            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
            .formLogin().disable()
            .httpBasic().disable()

            .authorizeRequests()

            .antMatchers("/login", "/logout", "/member/**",
                "/swagger*/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
            .permitAll()

            .antMatchers("/api/admin/**")
            .access("hasRole('ROLE_ADMIN')")

            .antMatchers("/api/lecture/**", "/api/enrollment/**", "/api/attendance/**")
            .access("hasAnyRole('ROLE_ADMIN', 'ROLE_LECTURER', 'ROLE_STUDENT')")

            .anyRequest().authenticated();

        http.addFilterBefore(new JwtAuthenticationFilter(
            authenticationManager(authenticationConfiguration), tokenService), UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(new JwtAuthorizationFilter(
            authenticationManager(authenticationConfiguration), tokenService, memberRepository), BasicAuthenticationFilter.class);

        return http.build();
    }

    private UserDetailsService getActuatorUserDetailsService() {
        String encodedPassword = passwordEncoder().encode(ACTUATOR_PASSWORD);
        UserDetails userDetails = User.withUsername(ACTUATOR_USERNAME)
            .password(encodedPassword)
            .roles(ACTUATOR_ROLE)
            .build();

        return new InMemoryUserDetailsManager(userDetails);
    }
}
