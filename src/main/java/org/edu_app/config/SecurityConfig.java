package org.edu_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/css/stylesLogin.css", "/js/scriptLogin.js").permitAll()
                .antMatchers("/login").permitAll() // Allow open access to certain pages
                .anyRequest().authenticated() // All other pages require authentication
            .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
            .and()
            .logout()
                .permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() { // Consider using the actual DB
        return username -> {
            if ("user".equals(username)) {
                return User.withUsername("user").password("{noop}password").roles("USER").build();
            } else if ("admin@eduapp.com".equals(username)) {
                return User.withUsername("admin").password("{noop}admin").roles("ADMIN").build();
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        };
    }
}

/*
package org.edu_app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/login").permitAll() // Allow open access to certain pages
                .anyRequest().authenticated() // All other pages require authentication
            .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
            .and()
            .logout()
                .permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() { // Consider using the actual DB
        return email -> {
            if ("admin@eduapp.com".equals(email)) {
                return User.withUsername("Admin").password("{noop}admin").roles("ADMIN").build();
            } else if ("john.doe@eduapp.com".equals(email)) {
                return User.withUsername("John").password("{noop}password").roles("USER").build();
            } else if ("alice.johnson@eduapp.com".equals(email)) {
                return User.withUsername("Alice").password("{noop}password").roles("USER").build();
            } else {
                throw new UsernameNotFoundException("No user with such email found!");
            }
        };
    }
}*/