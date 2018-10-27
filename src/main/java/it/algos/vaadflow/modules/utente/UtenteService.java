package it.algos.vaadflow.modules.utente;

import it.algos.vaadflow.annotation.AIScript;
import it.algos.vaadflow.application.FlowCost;
import it.algos.vaadflow.backend.entity.AEntity;
import it.algos.vaadflow.modules.company.Company;
import it.algos.vaadflow.modules.company.CompanyService;
import it.algos.vaadflow.modules.company.EACompany;
import it.algos.vaadflow.modules.role.EARole;
import it.algos.vaadflow.modules.role.Role;
import it.algos.vaadflow.modules.role.RoleService;
import it.algos.vaadflow.service.AService;
import it.algos.vaadflow.ui.dialog.AViewDialog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static it.algos.vaadflow.application.FlowCost.TAG_UTE;

/**
 * Project vaadflow <br>
 * Created by Algos <br>
 * User: Gac <br>
 * Fix date: 26-ott-2018 9.59.58 <br>
 * <br>
 * Business class. Layer di collegamento per la Repository. <br>
 * <br>
 * Annotated with @Service (obbligatorio, se si usa la catena @Autowired di SpringBoot) <br>
 * NOT annotated with @SpringComponent (inutile, esiste già @Service) <br>
 * Annotated with @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) (obbligatorio) <br>
 * NOT annotated with @VaadinSessionScope (sbagliato, perché SpringBoot va in loop iniziale) <br>
 * Annotated with @Qualifier (obbligatorio) per permettere a Spring di istanziare la classe specifica <br>
 * Annotated with @@Slf4j (facoltativo) per i logs automatici <br>
 * Annotated with @AIScript (facoltativo Algos) per controllare la ri-creazione di questo file dal Wizard <br>
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Qualifier(TAG_UTE)
@Slf4j
@AIScript(sovrascrivibile = false)
public class UtenteService extends AService {

    private final static String SUFFIX = "123";

    /**
     * versione della classe per la serializzazione
     */
    private final static long serialVersionUID = 1L;

    /**
     * Service iniettato da Spring (@Scope = 'singleton'). Unica per tutta l'applicazione. Usata come libreria.
     */
    @Autowired
    public RoleService roleService;

    /**
     * Service iniettato da Spring (@Scope = 'singleton'). Unica per tutta l'applicazione. Usata come libreria.
     */
    @Autowired
    public CompanyService companyService;

    /**
     * Istanza (@Scope = 'singleton') inietta da Spring <br>
     */
//    @Autowired
//    private SecurityConfiguration securityConfiguration;

    /**
     * La repository viene iniettata dal costruttore e passata al costruttore della superclasse, <br>
     * Spring costruisce una implementazione concreta dell'interfaccia MongoRepository (prevista dal @Qualifier) <br>
     * Qui si una una interfaccia locale (col casting nel costruttore) per usare i metodi specifici <br>
     */
    private UtenteRepository repository;


    /**
     * Costruttore @Autowired <br>
     * Si usa un @Qualifier(), per avere la sottoclasse specifica <br>
     * Si usa una costante statica, per essere sicuri di scrivere sempre uguali i riferimenti <br>
     * Regola nella superclasse il modello-dati specifico <br>
     *
     * @param repository per la persistenza dei dati
     */
    @Autowired
    public UtenteService(@Qualifier(TAG_UTE) MongoRepository repository) {
        super(repository);
        super.entityClass = Utente.class;
        this.repository = (UtenteRepository) repository;
    }// end of Spring constructor


    /**
     * Crea una entity solo se non esisteva <br>
     *
     * @param eaUtente: enumeration di dati iniziali di prova
     *
     * @return true se la entity è stata creata
     */
    public boolean creaIfNotExist(EAUtente eaUtente) {
        boolean creata = false;

        if (isMancaByKeyUnica(eaUtente.userName)) {//@todo migliorare
            AEntity entity = save(newEntity(eaUtente));
            creata = entity != null;
        }// end of if cycle

        return creata;
    }// end of method


    /**
     * Creazione in memoria di una nuova entity che NON viene salvata <br>
     * Eventuali regolazioni iniziali delle property <br>
     * Senza properties per compatibilità con la superclasse <br>
     *
     * @return la nuova entity appena creata (non salvata)
     */
    public Utente newEntity() {
        return newEntity("", "", (List<Role>) null, "", false);
    }// end of method


    /**
     * Creazione in memoria di una nuova entity che NON viene salvata <br>
     * Eventuali regolazioni iniziali delle property <br>
     * Usa una enumeration di dati iniziali di prova <br>
     *
     * @param eaUtente: enumeration di dati iniziali di prova
     *
     * @return la nuova entity appena creata (non salvata)
     */
    public Utente newEntity(EAUtente eaUtente) {
        Utente entity;
        String userName;
        String passwordInChiaro;
        EARole ruolo;
        List<Role> ruoli;
        String mail;
        EACompany eaCompany;

        userName = eaUtente.getUserName();
        passwordInChiaro = eaUtente.getPasswordInChiaro();
        ruolo = eaUtente.getRuolo();
        ruoli = roleService.getRoles(ruolo);
        mail = eaUtente.getMail();
        entity = newEntity(userName, passwordInChiaro, ruoli, mail);

        eaCompany = eaUtente.getCompany();
        if (eaCompany != null) {
            entity.company = (Company) companyService.findById(eaCompany.getCode());
        }// end of if cycle

        return entity;
    }// end of method


    /**
     * Creazione in memoria di una nuova entity che NON viene salvata <br>
     * Eventuali regolazioni iniziali delle property <br>
     * Properties obbligatorie <br>
     * <p>
     * @param userName         userName o nickName (obbligatorio, unico)
     * @param passwordInChiaro password in chiaro (obbligatoria, non unica)
     *                         con inserimento automatico (prima del 'save') se è nulla
     * @param ruoli            Ruoli attribuiti a questo utente (lista di valori obbligatoria)
     *                         con inserimento del solo ruolo 'user' (prima del 'save') se la lista è nulla
     *                         lista modificabile solo da developer ed admin
     * @param mail             posta elettronica (facoltativo)
     *
     * @return la nuova entity appena creata (non salvata)
     */
    public Utente newEntity(String userName, String passwordInChiaro, List<Role> ruoli, String mail) {
        return newEntity(userName, passwordInChiaro, ruoli, mail, false);
    }// end of method


    /**
     * Creazione in memoria di una nuova entity che NON viene salvata <br>
     * Eventuali regolazioni iniziali delle property <br>
     * All properties <br>
     * <p>
     * @param userName         userName o nickName (obbligatorio, unico)
     * @param passwordInChiaro password in chiaro (obbligatoria, non unica)
     *                         con inserimento automatico (prima del 'save') se è nulla
     * @param ruoli            Ruoli attribuiti a questo utente (lista di valori obbligatoria)
     *                         con inserimento del solo ruolo 'user' (prima del 'save') se la lista è nulla
     *                         lista modificabile solo da developer ed admin
     * @param mail             posta elettronica (facoltativo)
     * @param locked           flag locked (facoltativo, di default false)
     *
     * @return la nuova entity appena creata (non salvata)
     */
    public Utente newEntity(String userName, String passwordInChiaro, List<Role> ruoli, String mail, boolean locked) {
        Utente entity = Utente.builderUtente()
                .userName(text.isValid(userName) ? userName : null)
                .passwordInChiaro(text.isValid(passwordInChiaro) ? passwordInChiaro : null)
                .ruoli(ruoli != null ? ruoli : roleService.getUserRole())
                .mail(text.isValid(mail) ? mail : null)
                .locked(locked)
                .build();
        entity.id = userName;

        return (Utente) super.addCompany(entity);
    }// end of method


    /**
     * Operazioni eseguite PRIMA del save <br>
     * Regolazioni automatiche di property <br>
     *
     * @param entityBean da regolare prima del save
     * @param operation  del dialogo (NEW, EDIT)
     *
     * @return the modified entity
     */
    @Override
    public AEntity beforeSave(AEntity entityBean, AViewDialog.Operation operation) {
        Utente entity = (Utente) super.beforeSave(entityBean, operation);

        if (text.isEmpty(entity.userName)) {
            entity.id = FlowCost.STOP_SAVE;
            log.error("userName è vuoto in UtenteService.beforeSave()");
        }// end of if cycle

        if (text.isEmpty(entity.passwordInChiaro)) {
            entity.passwordInChiaro = entity.userName + SUFFIX;
        }// end of if cycle

        if (entity.ruoli == null) {
            entity.ruoli = roleService.getUserRole();
        }// end of if cycle

        return entity;
    }// end of method


    /**
     * Creazione di alcuni dati demo iniziali <br>
     * Viene invocato alla creazione del programma e dal bottone Reset della lista (solo per il developer) <br>
     * La collezione viene svuotata <br>
     * I dati possono essere presi da una Enumeration o creati direttamemte <br>
     * Deve essere sovrascritto - Invocare PRIMA il metodo della superclasse
     *
     * @return numero di elementi creato
     */
    @Override
    public int reset() {
        int numRec = super.reset();

        for (EAUtente eaUtente : EAUtente.values()) {
            numRec = creaIfNotExist(eaUtente) ? numRec + 1 : numRec;
        }// end of for cycle

        return numRec;
    }// end of method


    public boolean isUser(Utente utente) {
        for (Role role : utente.ruoli) {
            if (role.code.equals(roleService.getUser().code)) {
                return true;
            }// end of if cycle
        }// end of for cycle

        return false;
    }// end of method


    public boolean isAdmin(Utente utente) {
        for (Role role : utente.ruoli) {
            if (role.code.equals(roleService.getAdmin().code)) {
                return true;
            }// end of if cycle
        }// end of for cycle

        return false;
    }// end of method


    public boolean isDev(Utente utente) {
        for (Role role : utente.ruoli) {
            if (role.code.equals(roleService.getDeveloper().code)) {
                return true;
            }// end of if cycle
        }// end of for cycle

        return false;
    }// end of method

}// end of class