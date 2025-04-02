package org.edu_app.config;

import org.edu_app.Main;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String PASSWORD_PEPPER = "8X7nM4pL9qR2tV3zW5yA6bC1dE0fG";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PepperedPasswordEncoder(PASSWORD_PEPPER);
    }

    public static void printBCryptPassword(String rawPassword) {
        // Apply pepper before encoding
        String pepperedPassword = rawPassword + PASSWORD_PEPPER;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(pepperedPassword);

        Main.getLogger().info("Original password: " + rawPassword);
        Main.getLogger().info("BCrypt encrypted: " + encodedPassword);
    }

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
    public UserDetailsService userDetailsService() {
        return Main.getDbManager()
                .createUserDetailsService();
    }

    // Custom password encoder that applies the pepper
    public static class PepperedPasswordEncoder implements PasswordEncoder {
        private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();
        private final String pepper;

        public PepperedPasswordEncoder(String pepper) {
            this.pepper = pepper;
        }

        @Override
        public String encode(CharSequence rawPassword) {
            // Add the pepper before encoding
            String pepperedPassword = rawPassword + pepper;
            return delegate.encode(pepperedPassword);
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            // Add the pepper before checking
            String pepperedPassword = rawPassword + pepper;
            return delegate.matches(pepperedPassword, encodedPassword);
        }
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