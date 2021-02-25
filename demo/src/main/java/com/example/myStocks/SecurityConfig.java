package com.example.myStocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomOidcUserService customOidcUserService;

    @Override
    public void configure(HttpSecurity http) throws Exception {

    	http
        .authorizeRequests(a -> a
            .antMatchers("/", "/error", "/webjars/**").permitAll()
            .anyRequest().authenticated()
        )
        .logout(l -> l
                .logoutSuccessUrl("/").permitAll()
            )
        .csrf(c -> c
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
        .exceptionHandling(e -> e
            .authenticationEntryPoint((AuthenticationEntryPoint) new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        )
        .oauth2Login().userInfoEndpoint()
        .oidcUserService(customOidcUserService);
    }
}