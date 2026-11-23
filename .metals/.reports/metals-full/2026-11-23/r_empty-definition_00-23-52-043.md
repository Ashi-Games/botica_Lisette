error id: file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/config/SecurityConfig.java:_empty_/`<any>`#requestMatchers#permitAll#requestMatchers#hasRole#requestMatchers#hasAnyRole#anyRequest#authenticated#
file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/config/SecurityConfig.java
empty definition using pc, found symbol in pc: _empty_/`<any>`#requestMatchers#permitAll#requestMatchers#hasRole#requestMatchers#hasAnyRole#anyRequest#authenticated#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1197
uri: file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/config/SecurityConfig.java
text:
```scala
package com.botica.lisette.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/error", "/css/**", "/js/**", "/img/**",
                                 "/actuator/health").permitAll()
                .requestMatchers("/dashboard", "/alertas", "/predicciones/**", "/ingresos/**")
                        .hasRole("ADMIN")
                .requestMatchers("/inventario", "/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")
                .anyRequest().authentica@@ted()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .failureUrl("/login?error")
                .successHandler((request, response, auth) -> {
                    boolean admin = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    response.sendRedirect(request.getContextPath() + (admin ? "/dashboard" : "/ventas"));
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            );

        return http.build();
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/`<any>`#requestMatchers#permitAll#requestMatchers#hasRole#requestMatchers#hasAnyRole#anyRequest#authenticated#