package it.algos.vaadflow.modules.giorno;

import it.algos.vaadflow.annotation.AIScript;
import it.algos.vaadflow.backend.entity.AEntity;
import it.algos.vaadflow.modules.mese.Mese;
import it.algos.vaadflow.modules.mese.MeseService;
import it.algos.vaadflow.service.ADateService;
import it.algos.vaadflow.service.AService;
import it.algos.vaadflow.ui.dialog.AViewDialog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static it.algos.vaadflow.application.FlowCost.*;

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
@Qualifier(TAG_GIO)
@Slf4j
@AIScript(sovrascrivibile = false)
public class GiornoService extends AService {


    /**
     * versione della classe per la serializzazione
     */
    private final static long serialVersionUID = 1L;
    /**
     * La repository viene iniettata dal costruttore e passata al costruttore della superclasse, <br>
     * Spring costruisce una implementazione concreta dell'interfaccia MongoRepository (prevista dal @Qualifier) <br>
     * Qui si una una interfaccia locale (col casting nel costruttore) per usare i metodi specifici <br>
     */
    public GiornoRepository repository;
    /**
     * Istanza (@Scope = 'singleton') inietta da Spring <br>
     */
    @Autowired
    private ADateService dateService;
    /**
     * Istanza (@Scope = 'singleton') inietta da Spring <br>
     */
    @Autowired
    private MeseService meseService;


    /**
     * Costruttore @Autowired <br>
     * Si usa un @Qualifier(), per avere la sottoclasse specifica <br>
     * Si usa una costante statica, per essere sicuri di scrivere sempre uguali i riferimenti <br>
     * Regola il modello-dati specifico e lo passa al costruttore della superclasse <br>
     *
     * @param repository per la persistenza dei dati
     */
    @Autowired
    public GiornoService(@Qualifier(TAG_GIO) MongoRepository repository) {
        super(repository);
        super.entityClass = Giorno.class;
        this.repository = (GiornoRepository) repository;
    }// end of Spring constructor

    /**
     * Crea una entity e la registra <br>
     *
     * @param mese        di riferimento (obbligatorio)
     * @param ordinamento (obbligatorio, unico)
     * @param titolo      (obbligatorio, unico)
     *
     * @return la entity appena creata
     */
    public Giorno crea(Mese mese, int ordinamento, String titolo) {
        return (Giorno) save(newEntity(mese, ordinamento, titolo));
    }// end of method

    /**
     * Creazione in memoria di una nuova entity che NON viene salvata <br>
     * Eventuali regolazioni iniziali delle property <br>
     * Senza properties per compatibilità con la superclasse <br>
     *
     * @return la nuova entity appena creata (non salvata)
     */
    public Giorno newEntity() {
        return newEntity((Mese) null, 0, "");
    }// end of method


    /**
     * Creazione in memoria di una nuova entity che NON viene salvata <br>
     * Eventuali regolazioni iniziali delle property <br>
     * All properties <br>
     * Utilizza, eventualmente, la newEntity() della superclasse, per le property della superclasse <br>
     *
     * @param mese   di riferimento (obbligatorio)
     * @param ordine (obbligatorio, unico)
     * @param titolo (obbligatorio, unico)
     *
     * @return la nuova entity appena creata (non salvata)
     */
    public Giorno newEntity(Mese mese, int ordine, String titolo) {
        Giorno entity = findByKeyUnica(titolo);

        if (entity == null) {
            entity = Giorno.builderGiorno()
                    .mese(mese)
                    .ordine(ordine > 0 ? ordine : getNewOrdine())
                    .titolo(titolo)
                    .build();
            entity.id = titolo;
        }// end of if cycle

        return entity;
    }// end of method


//    /**
//     * Property unica (se esiste).
//     */
//    public String getPropertyUnica(AEntity entityBean) {
//        return text.isValid(((Giorno) entityBean).getTitolo()) ? ((Giorno) entityBean).getTitolo() : "";
//    }// end of method


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
        Giorno entity = (Giorno) super.beforeSave(entityBean, operation);

        if (entity.getMese() == null || entity.ordine == 0 || text.isEmpty(entity.titolo)) {
            entity = null;
            log.error("entity incompleta in GiornoService.beforeSave()");
        }// end of if cycle

        return entity;
    }// end of method


    /**
     * Recupera una istanza della Entity usando la query della property specifica (obbligatoria ed unica) <br>
     *
     * @param titolo (obbligatorio, unico)
     *
     * @return istanza della Entity, null se non trovata
     */
    public Giorno findByKeyUnica(String titolo) {
        return repository.findByTitolo(titolo);
    }// end of method


//
//    /**
//     * Returns all entities of the type <br>
//     * <p>
//     * Ordinate secondo l'ordinamento previsto
//     *
//     * @param sort ordinamento previsto
//     *
//     * @return all ordered entities
//     */
//    @Override
//    protected List<? extends AEntity> findAll(Sort sort) {
//        return super.findAll(sort);
//    }// end of method
//

    /**
     * Controlla l'esistenza di una Entity usando la query della property specifica (obbligatoria ed unica) <br>
     *
     * @param titolo (obbligatorio, unico)
     *
     * @return true se trovata
     */
    public boolean isEsiste(String titolo) {
        return findByKeyUnica(titolo) != null;
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
        int ordine = 0;
        Giorno entity;
        List<HashMap> lista;
        String titolo;
        int bisestile;
        Mese meseEntity;

        //costruisce i 366 records
        lista = dateService.getAllGiorni();
        for (HashMap mappaGiorno : lista) {
            titolo = (String) mappaGiorno.get(KEY_MAPPA_GIORNI_TITOLO);
            bisestile = (int) mappaGiorno.get(KEY_MAPPA_GIORNI_BISESTILE);
            meseEntity = meseService.findByKeyUnica((String) mappaGiorno.get(KEY_MAPPA_GIORNI_MESE_TESTO));
            crea(meseEntity, ordine, titolo);
//            entity = Giorno.builderGiorno()
//                    .mese(meseEntity)
//                    .ordine(ordine)
//                    .titolo(titolo)
//                    .build();
//            entity.id = entity.titolo;
//            mongoService.insert(entity, Giorno.class);
            numRec++;
        }// end of for cycle

        return numRec;
    }// end of method

}// end of class