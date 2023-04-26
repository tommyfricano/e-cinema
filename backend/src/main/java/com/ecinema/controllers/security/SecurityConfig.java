package com.ecinema.controllers.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    public void configure(AuthenticationManagerBuilder builder) throws Exception{
        builder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/resources/static/**",
                        "/bookMovie/**", "/search", "/descriptions/**", "/static/**",
                        "/login", "/forgotPassword", "/Cinema", "/resetPassword**",
                        "/resetPassword?token=", "/confirmRegistration?token=",
                        "/confirmRegistration", "/css/**", "/js/**", "/Cinema.html",
                        "/registration", "/registration_attempt","/registration-error")
                .permitAll()
                .requestMatchers("/user/**").hasAuthority("CUSTOMER")
                .requestMatchers("/**").hasAuthority("ADMIN")
                .and()
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error=true")
                        .usernameParameter("email").passwordParameter("password")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .permitAll()
                ).logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
                        .deleteCookies("JSESSIONID")
//                        .logoutSuccessHandler(logoutSuccessHandler())
                );
        return http.build();
    }

}
