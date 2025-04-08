package org.edu_app.config;

import org.edu_app.utils.InitDBManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private static final String PASSWORD_PEPPER = "8X7nM4pL9qR2tV3zW5yA6bC1dE0fG";

    @Autowired
    private InitDBManager dbManager;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PepperedPasswordEncoder(PASSWORD_PEPPER);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return dbManager.createUserDetailsService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/stylesLogin.css", "/js/scriptLogin.js", "/login").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
            )
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.ALWAYS)
            );

        return http.build();
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
            String pepperedPassword = rawPassword + pepper;
            return delegate.encode(pepperedPassword);
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            String pepperedPassword = rawPassword + pepper;
            return delegate.matches(pepperedPassword, encodedPassword);
        }
    }

    public static void printBCryptPassword(String rawPassword) {
        String pepperedPassword = rawPassword + PASSWORD_PEPPER;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(pepperedPassword);

        System.out.println("Original password: " + rawPassword);
        System.out.println("BCrypt encrypted: " + encodedPassword);
    }
}
