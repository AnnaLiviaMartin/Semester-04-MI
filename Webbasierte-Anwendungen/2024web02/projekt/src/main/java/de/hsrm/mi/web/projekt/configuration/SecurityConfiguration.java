package de.hsrm.mi.web.projekt.configuration;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration @EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() { // @Bean -> Encoder woanders per @Autowired abrufbar
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /*
    So kann man hardcoded Benutzer erstellen und sich dann mit diesen anmelden:
    @Bean
    public UserDetailsService userDetailsService() {
        User.UserBuilder userbuilder = User.withDefaultPasswordEncoder(); // Klartext-Passwort codiert speichern

        // hardcoded user anlegen
        UserDetails user1 = userbuilder.username("joendhard@diebiffels.de").password("password").roles("USER").build();
        UserDetails user2 = userbuilder.username("joghurta@diebiffels.de").password("password").roles("CHEF").build();
        UserDetails user3 = userbuilder.username("Supervisor").password("Supervisor").roles("CHEF", "USER").build();

        return new InMemoryUserDetailsManager(user1, user2, user3);
    }*/

    @Bean
    SecurityFilterChain filterChainApp(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(toH2Console()).permitAll()
                        .requestMatchers("/admin/benutzer", "/admin/tour").permitAll()
                        .requestMatchers("/admin/ort/*").hasRole("CHEF")
                        .anyRequest().authenticated())
                .formLogin(o -> o.defaultSuccessUrl("/admin"))
                .csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/admin/benutzer/*/hx/feld/*"))
                .headers(hdrs -> hdrs.frameOptions(fo -> fo.sameOrigin()));
        return http.build();
    }

}
