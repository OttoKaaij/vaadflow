package it.algos.vaadflow.modules.address;

import com.vaadin.flow.spring.annotation.SpringComponent;
import it.algos.vaadflow.annotation.*;
import it.algos.vaadflow.backend.entity.AEntity;
import it.algos.vaadflow.enumeration.EACompanyRequired;
import it.algos.vaadflow.enumeration.EAFieldType;
import lombok.*;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import static it.algos.vaadflow.application.FlowCost.TAG_ADD;

/**
 * Project vaadflow <br>
 * Created by Algos <br>
 * User: Gac <br>
 * Fix date: 22-set-2018 21.34.26 <br>
 * <p>
 * Estende la entity astratta AEntity che contiene la key property ObjectId <br>
 * <p>
 * Annotated with @SpringComponent (obbligatorio) <br>
 * Annotated with @Document (facoltativo) per avere un nome della collection (DB Mongo) diverso dal nome della Entity <br>
 * Annotated with @TypeAlias (facoltativo) to replace the fully qualified class name with a different value. <br>
 * Annotated with @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) (obbligatorio) <br>
 * Annotated with @Data (Lombok) for automatic use of Getter and Setter <br>
 * Annotated with @NoArgsConstructor (Lombok) for JavaBean specifications <br>
 * Annotated with @AllArgsConstructor (Lombok) per usare il costruttore completo nel Service <br>
 * Annotated with @Builder (Lombok) con un metodo specifico, per usare quello standard nella (eventuale) sottoclasse <br>
 * Annotated with @Builder (Lombok) lets you automatically produce the code required to have your class
 * be instantiable with code such as: Person.builder().name("Adam Savage").city("San Francisco").build(); <br>
 * Annotated with @EqualsAndHashCode (Lombok) per l'uguaglianza di due istanze della classe <br>
 * Annotated with @Qualifier (obbligatorio) per permettere a Spring di istanziare la sottoclasse specifica <br>
 * Annotated with @AIEntity (facoltativo Algos) per alcuni parametri generali del modulo <br>
 * Annotated with @AIList (facoltativo Algos) per le colonne automatiche della Lista  <br>
 * Annotated with @AIForm (facoltativo Algos) per i fields automatici del Dialog e del Form <br>
 * Annotated with @AIScript (facoltativo Algos) per controllare la ri-creazione di questo file dal Wizard <br>
 * <p>
 * Inserisce SEMPRE la versione di serializzazione <br>
 * Le singole property sono pubbliche in modo da poterne leggere il valore tramite 'reflection' <br>
 * Le singole property sono annotate con @AIColumn (facoltativo Algos) per il tipo di Column nella Grid <br>
 * Le singole property sono annotate con @AIField (obbligatorio Algos) per il tipo di Field nel Dialog e nel Form <br>
 * Le singole property sono annotate con @Field("xxx") (facoltativo)
 * -which gives a name to the key to be used to store the field inside the document.
 * -The property name (i.e. 'descrizione') would be used as the field key if this annotation was not included.
 * -Remember that field keys are repeated for every document so using a smaller key name will reduce the required space.
 */
@SpringComponent
@Document(collection = "address")
@TypeAlias("address")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderAddress")
@EqualsAndHashCode(callSuper = false)
@Qualifier(TAG_ADD)
@AIEntity(company = EACompanyRequired.nonUsata)
@AIList(fields = {"indirizzo", "localita", "cap"})
@AIForm(fields = {"indirizzo", "localita", "cap"})
@AIScript(sovrascrivibile = false)
public class Address extends AEntity {


    /**
     * versione della classe per la serializzazione
     */
    private final static long serialVersionUID = 1L;


    /**
     * indirizzo: via, nome e numero (obbligatoria, non unica)
     */
    @NotNull(message = "L'indirizzo è obbligatorio")
    @Size(min = 2, max = 50)
    @Field("ind")
    @AIField(type = EAFieldType.text, required = true, focus = true, widthEM = 12)
    @AIColumn(width = 210)
    public String indirizzo;


    /**
     * località (obbligatoria, non unica)
     */
    @NotNull(message = "La località è obbligatoria")
    @Size(min = 2, max = 50)
    @Field("loc")
    @AIField(type = EAFieldType.text, firstCapital = true, widthEM = 24)
    @AIColumn(width = 370)
    public String localita;


    /**
     * codice di avviamento postale (facoltativo, non unica)
     */
    @Size(min = 5, max = 5, message = "Il codice postale deve essere di 5 cifre")
    @Field("cap")
    @AIField(type = EAFieldType.text, widthEM = 5)
    @AIColumn(width = 370)
    public String cap;


    /**
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        String value = "";
        String spazio = " ";
        String sep = " - ";

        value += indirizzo;
        value += (localita != null && !localita.equals("")) ? sep + localita : "";
        value += spazio + cap;

        return value.trim();
    }// end of method

}// end of entity class