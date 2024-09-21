package de.hsrm.mi.web.projekt.services.benutzer;

import de.hsrm.mi.web.projekt.entities.benutzer.Benutzer;
import de.hsrm.mi.web.projekt.entities.benutzer.BenutzerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BenutzerUserDetailsService implements UserDetailsService {

    private BenutzerRepository benutzerRepository;

    public BenutzerUserDetailsService(BenutzerRepository benutzerRepository) {
        this.benutzerRepository = benutzerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Schritt 1: notwendige Daten zu Login-Name 'username' besorgen, z.B. aus Datenbank
        Benutzer user = benutzerRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (user.getLikeThings().contains("MACHT")) {
            // Schritt 2: Spring 'User'-Objekt mit relevanten Daten für 'username' zurückgeben
            return User.withUsername(user.getEmail())
                    .password(user.getPassword()) // falls in DB encoded gespeichert
                    .roles("CHEF")
                    .build();
        } else {
            return User.withUsername(user.getEmail())
                    .password(user.getPassword()) // falls in DB encoded gespeichert
                    .roles("USER")
                    .build();
        }
    }

}
