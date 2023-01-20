package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

//    /**
//     * In-Memory Credential
//     * Authentication Provider 는 InMemoryUserDetailsManager 사용.
//     * 이건 DB가 아직 없을때 빠르게 프로토타입으로 만들기 적합하다.
//     */
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        // 임의의 3 유저를 하드코딩해서 메모리에 올려두겠다.
//        UserDetails user1 = User.withUsername("user1")
//                .password(passwordEncoder().encode("user1Pass"))
//                .roles("USER")
//                .build();
//        UserDetails user2 = User.withUsername("user2")
//                .password(passwordEncoder().encode("user2Pass"))
//                .roles("USER")
//                .build();
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder().encode("adminPass"))
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user1, user2, admin);
//    }

    /**
     * WebSecurityConfigurerAdaptor deprecated 되어서
     * WebSecurityCustomizer bean 등록해서 configure() 구현한다.
     */
//    @Bean
//    public WebSecurityCustomizer configure(AuthenticationManagerBuilder auth) {
//
//    }


    /**
     * JDBC based Authentication
     */
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        UserDetails user = User.withUsername("user")
                .password("password")
                .roles("USER")
                .build();

        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.createUser(user);

        return users;
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
     * 규칙.
     * 순서대로 실행되다가 true 가 반환되면 return 한다.
     *
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                    .antMatchers("/home*")      // /home 하위 url에 대해서는
                        .hasRole("USER")                    // USER 역할과 일치하는지 확인한다.

                    .antMatchers("/admin*")     // /admin 하위 url에 대해서는
                        .hasRole("ADMIN")                   // ADMIN 역할과 일히차는지 확인한다.

                    .antMatchers("/", "/index", "/h2-console/**", "/api/**") // /index 페이지는 ( /login url 은 디폴트 )
                        .permitAll()                        // 모든 사용자의 접근을 허용한다.

                    .anyRequest()                           // 모든 request에 대해서는
                        .authenticated()                    // 역할과 상관없이 인증된 경우라면 허용한다.
                .and()
                    .headers()
                    .addHeaderWriter(
                            new XFrameOptionsHeaderWriter(
                                    new WhiteListedAllowFromStrategy(List.of("localhost"))
                            )
                    )
                    .frameOptions().sameOrigin()
                .and()
                    .formLogin()
//                    .loginPage("/login.html")   // spring boot 기본제공 ui 사용할것
                    .defaultSuccessUrl("/home", true)
                // todo
//                    .failureUrl("/login.html?error=true")
//                    .failureHandler(authenticationFailureHandler())
                .and()
                    .logout()
                .and()
                    .exceptionHandling().accessDeniedPage("/403");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
