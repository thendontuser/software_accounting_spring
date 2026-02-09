package ru.thendont.software_accounting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.thendont.software_accounting.util.UserRoles;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**"
                )
                .permitAll()
                .requestMatchers(
                    "/",
                    "/auth/**",
                    "/visitor/**"
                ).permitAll()
                .requestMatchers("/admin/**", "/pending-users/**").hasRole(UserRoles.ADMIN)
                .requestMatchers("/teacher/**").hasRole(UserRoles.TEACHER)
                .requestMatchers("/accountant/**").hasRole(UserRoles.ACCOUNTANT)
                .requestMatchers("/manager/**").hasRole(UserRoles.MANAGER)
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                // Разрешаем DELETE запросы без CSRF
                .ignoringRequestMatchers("/admin/delete/**", "/pending-users/reject-user/**")
                //.ignoringRequestMatchers("/api/**") // если у вас REST API
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/auth/login/success", true)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecret")
                .tokenValiditySeconds(86400)
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}