package it.algos.vaadflow.modules.utente;

import com.vaadin.flow.spring.annotation.SpringComponent;
import it.algos.vaadflow.annotation.*;
import it.algos.vaadflow.backend.entity.AEntity;
import it.algos.vaadflow.enumeration.EACompanyRequired;
import it.algos.vaadflow.enumeration.EAFieldType;
import it.algos.vaadflow.modules.role.EARole;
import it.algos.vaadflow.modules.role.Role;
import it.algos.vaadflow.modules.role.RoleService;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static it.algos.vaadflow.application.FlowCost.TAG_UTE;

/**
 * Project vaadflow <br>
 * Created by Algos <br>
 * User: Gac <br>
 * Fix date: 20-ott-2018 18.52.54 <br>
 * <p>
 * Estende la entity astratta AEntity che contiene la key property ObjectId <br>
 * <p>
 * Not annotated with @SpringComponent (inutile).  <br>
 * Not annotated with @Scope (inutile). Le istanze 'prototype' vengono generate da xxxService.newEntity() <br>
 * Not annotated with @Qualifier (inutile) <br>
 * Annotated with @Document (facoltativo) per avere un nome della collection (DB Mongo) diverso dal nome della Entity <br>
 * Annotated with @TypeAlias (facoltativo) to replace the fully qualified class name with a different value. <br>
 * Annotated with @Data (Lombok) for automatic use of Getter and Setter <br>
 * Annotated with @NoArgsConstructor (Lombok) for JavaBean specifications <br>
 * Annotated with @AllArgsConstructor (Lombok) per usare il costruttore completo nel Service <br>
 * Annotated with @Builder (Lombok) con un metodo specifico, per usare quello standard nella (eventuale) sottoclasse <br>
 * - lets you automatically produce the code required to have your class be instantiable with code such as:
 * - Person.builder().name("Adam Savage").city("San Francisco").build(); <br>
 * Annotated with @EqualsAndHashCode (Lombok) per l'uguaglianza di due istanze della classe <br>
 * Annotated with @AIEntity (facoltativo Algos) per alcuni parametri generali del modulo <br>
 * Annotated with @AIList (facoltativo Algos) per le colonne automatiche della Grid nella lista <br>
 * Annotated with @AIForm (facoltativo Algos) per i fields automatici nel dialogo del Form <br>
 * Annotated with @AIScript (facoltativo Algos) per controllare la ri-creazione di questo file dal Wizard <br>
 * <p>
 * Inserisce SEMPRE la versione di serializzazione <br>
 * Le singole property sono pubbliche in modo da poterne leggere il valore tramite 'reflection' <br>
 * Le singole property sono annotate con @AIColumn (facoltativo Algos) per il tipo di Column nella Grid <br>
 * Le singole property sono annotate con @AIField (obbligatorio Algos) per il tipo di fields nel dialogo del Form <br>
 * Le singole property sono annotate con @Field("xxx") (facoltativo)
 * -which gives a name to the key to be used to store the field inside the document.
 * -The property name (i.e. 'descrizione') would be used as the field key if this annotation was not included.
 * -Remember that field keys are repeated for every document so using a smaller key name will reduce the required space.
 */
/**
 * Vengono usate SOLO le property indispensabili per la gestione della security <br>
 * Altre property, anche generiche, vanno nella sottoclasse anagrafica Person <br>
 */
@Entity
@Document(collection = "utente")
@TypeAlias("utente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderUtente")
@EqualsAndHashCode(callSuper = false)
@AIEntity(company = EACompanyRequired.facoltativa)
@AIList(fields = {"userName", "passwordInChiaro", "locked", "mail"})
@AIForm(fields = {"userName", "ruoli", "passwordInChiaro", "locked", "mail"})
@AIScript(sovrascrivibile = false)
public class Utente extends AEntity {


    /**
     * versione della classe per la serializzazione
     */
    private final static long serialVersionUID = 1L;


    /**
     * userName o nickName (obbligatorio, unico)
     */
    @NotNull(message = "UserName, anche detto nickName, non può essere lasciato vuoto")
    @Field("user")
    @AIField(type = EAFieldType.text)
    @AIColumn(name = "user")
    public String userName;


    /**
     * password in chiaro (obbligatoria, non unica)
     * con inserimento automatico (prima del 'save') se è nulla
     */
    @Field("pass")
    @AIField(type = EAFieldType.text)
    @AIColumn(name = "pass")
    public String passwordInChiaro;


    /**
     * flag locked (facoltativo, di default false)
     */
    @Field("lock")
    @AIField(type = EAFieldType.checkbox)
    @AIColumn(name = "lock")
    public boolean locked;


    /**
     * Ruoli attribuiti a questo utente (lista di valori obbligatoria)
     * con inserimento del solo ruolo 'user' (prima del 'save') se la lista è nulla
     * lista modificabile solo da developer ed admin
     * Siccome sono 'embedded' in utente, non serve @OneToMany() o @ManyToOne()
     */
    @Field("role")
    @AIField(type = EAFieldType.noone, required = true, clazz = RoleService.class)
    @AIColumn(name = "ruolo", width = 200)
    public List<Role> ruoli;


    /**
     * posta elettronica (facoltativo)
     */
    @Field("mail")
    @AIField(type = EAFieldType.email, widthEM = 24)
    @AIColumn(width = 350, name = "eMail")
    public String mail;


//    public boolean isUser() {
//        return service.isUser(this);
//    }// end of method
//
//    public boolean isAdmin() {
//        return service.isAdmin(this);
//    }// end of method
//
//    public boolean isDev() {
//        return service.isDev(this);
//    }// end of method

    public boolean isAdmin() {
        for (Role role : this.ruoli) {
            if (role.code.equals(EARole.admin.toString())) {
                return true;
            }// end of if cycle
        }// end of for cycle

        return false;
    }// end of method

    public boolean isDev( ) {
        for (Role role : this.ruoli) {
            if (role.code.equals(EARole.developer.toString())) {
                return true;
            }// end of if cycle
        }// end of for cycle

        return false;
    }// end of method


    /**
     * GrantedAuthority
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> listAuthority = new ArrayList<>();
        List<Role> ruoli = this.ruoli;
        GrantedAuthority authority;

        if (ruoli!=null&&ruoli.size()>0) {
            for (Role ruolo : ruoli) {
                authority = new SimpleGrantedAuthority(ruolo.code);
                listAuthority.add(authority);
            }// end of for cycle
        }// end of if cycle

        return listAuthority;
    }// end of method

}// end of entity class