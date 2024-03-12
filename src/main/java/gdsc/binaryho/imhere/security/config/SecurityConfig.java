package gdsc.binaryho.imhere.security.config;


import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.security.filter.JwtAuthorizationFilter;
import gdsc.binaryho.imhere.security.jwt.TokenPropertyHolder;
import gdsc.binaryho.imhere.security.jwt.TokenService;
import gdsc.binaryho.imhere.security.oauth.CustomOAuth2SuccessHandler;
import gdsc.binaryho.imhere.security.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final MemberRepository memberRepository;

    private final CorsFilter corsFilter;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    private final TokenService tokenService;
    private final TokenPropertyHolder tokenPropertyHolder;

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

            .oauth2Login(configurer -> {
                    configurer.userInfoEndpoint(
                        endpoint -> endpoint.userService(customOAuth2UserService));
                    configurer.successHandler(customOAuth2SuccessHandler);
                    configurer.failureHandler(setStatusUnauthorized());
                }
            )

            .authorizeRequests()
            .antMatchers("/login/**", "/logout", "/member/**",
                "/swagger*/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
            .permitAll()

            .antMatchers("/api/admin/**")
            .access("hasRole('ROLE_ADMIN')")

            .antMatchers("/api/lecture/**", "/api/enrollment/**", "/api/attendance/**")
            .access("hasAnyRole('ROLE_ADMIN', 'ROLE_LECTURER', 'ROLE_STUDENT')")

            .anyRequest().authenticated();

        http.addFilterBefore(new JwtAuthorizationFilter(
                authenticationManager(authenticationConfiguration),
                tokenService, memberRepository, tokenPropertyHolder),
            BasicAuthenticationFilter.class);

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

    private AuthenticationFailureHandler setStatusUnauthorized() {
        int unauthorized = HttpStatus.UNAUTHORIZED.value();
        return (request, response, exception) -> response.setStatus(unauthorized);
    }
}
