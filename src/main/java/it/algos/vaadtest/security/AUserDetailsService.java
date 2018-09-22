package it.algos.vaadtest.security;

import it.algos.vaadflow.application.FlowCost;
import it.algos.vaadflow.backend.login.ALogin;
import it.algos.vaadflow.modules.role.RoleService;
import it.algos.vaadflow.modules.utente.Utente;
import it.algos.vaadflow.modules.utente.UtenteService;
import it.algos.vaadflow.service.ABootService;
import it.algos.vaadtest.modules.prova.ProvaViewList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Implements the {@link UserDetailsService}.
 * <p>
 * This implementation searches for {@link Utente} entities by the e-mail address
 * supplied in the login screen.
 */
@Service
@Primary
public class AUserDetailsService implements UserDetailsService {

    private final ALogin login;
    private final UtenteService utenteService;
    private final RoleService roleService;

    public PasswordEncoder passwordEncoder;

    /**
     * Istanza (@Scope = 'singleton') inietta da Spring <br>
     */
    @Autowired
    protected ABootService boot;

    @Autowired
    public AUserDetailsService(ALogin login, UtenteService utenteService, RoleService roleService) {
        this.login = login;
        this.utenteService = utenteService;
        this.roleService = roleService;
    }// end of Spring constructor

    /**
     * Recovers the {@link Utente} from the database using the e-mail address supplied
     * in the login screen. If the user is found, returns a
     * {@link org.springframework.security.core.userdetails.User}.
     *
     * @param username User's e-mail address
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String passwordHash = "";
        Collection<? extends GrantedAuthority> authorities;
        Utente utente = utenteService.findByUserName(username);

        if (utenteService.isDev(utente)) {
            boot.creaRouteStandardDeveloper();
        } else {
            if (utenteService.isAdmin(utente)) {
                boot.creaRouteStandardAdmin();
            }// end of if/else cycle
        }// end of if/else cycle
        //--menu specifici
        FlowCost.MENU_CLAZZ_LIST.add(ProvaViewList.class);

        if (null == utente) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            passwordHash = passwordEncoder.encode(utente.getPasswordInChiaro());
            authorities = roleService.getAuthorities(utente);
            return new User(utente.getUserName(), passwordHash, authorities);
        }// end of if/else cycle

    }// end of method

}// end of class