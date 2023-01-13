package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * In-Memory Credential
     * Authentication Provider 는 InMemoryUserDetailsManager 사용.
     * 이건 DB가 아직 없을때 빠르게 프로토타입으로 만들기 적합하다.
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // 임의의 3 유저를 하드코딩해서 메모리에 올려두겠다.
        UserDetails user1 = User.withUsername("user1")
                .password(passwordEncoder().encode("user1Pass"))
                .roles("USER")
                .build();
        UserDetails user2 = User.withUsername("user2")
                .password(passwordEncoder().encode("user2Pass"))
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder().encode("adminPass"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user1, user2, admin);
    }

    /**
     * 요청에 대해 인증할때 사용하는 설정
     *
     * /login 요청에 대해서는 모든 사용자가 접근하게 만들기 위해 anonymous 로 오픈할것이다.
     * /admin 요청에 대해서는 ADMIN 역할만 접근하도록 제한할 것이다.
     *
     * anyMatchers() >> 순서가 중요.
     * 더 제한적인 규칙을 먼저 오게 하고, 나중에 갈수록 일반적이고 루즈한 규칙들을 오게 해야한다.
     *
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                    .antMatchers("/home*").hasRole("USER")
                    .antMatchers("/admin*").hasRole("ADMIN")
                    .antMatchers("/", "/index").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
//                    .loginPage("/login.html")   // spring boot 기본제공 ui 사용할것
//                    .loginProcessingUrl("/perform_login")
                    .defaultSuccessUrl("/home", true)
                // todo
//                    .failureUrl("/login.html?error=true")
//                    .failureHandler(authenticationFailureHandler())
                .and()
                    .logout()
//                .logoutUrl("/perform_logout")
//                    .invalidateHttpSession(true)
//                    .deleteCookies("JSESSIONID")
                .and()
                    .exceptionHandling().accessDeniedPage("/403");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public LoginFailureHandlerImpl authenticationFailureHandler() {
//        return new LoginFailureHandlerImpl();
//    }
}
