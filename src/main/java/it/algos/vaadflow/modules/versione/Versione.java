package it.algos.vaadflow.modules.versione;

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
import java.time.LocalDateTime;

import static it.algos.vaadflow.application.FlowCost.TAG_VER;

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
@Document(collection = "versione")
@TypeAlias("versione")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "builderVersione")
@EqualsAndHashCode(callSuper = false)
@Qualifier(TAG_VER)
@AIEntity(company = EACompanyRequired.nonUsata)
@AIList(fields = {"id", "titolo", "nome", "timestamp"})
@AIForm(fields = {"id", "titolo","nome", "timestamp"})
@AIScript(sovrascrivibile = false)
public class Versione extends AEntity {


    /**
     * versione della classe per la serializzazione
     */
    private final static long serialVersionUID = 1L;


    /**
     * titolo della versione (obbligatorio, non unico) <br>
     */
    @NotNull
    @Indexed()
    @Size(min = 3)
    @Field("tit")
    @AIField(type = EAFieldType.text, required = true, focus = true, widthEM = 12)
    @AIColumn(widthPX = 1)
    public String titolo;

    /**
     * nome descrittivo della versione (obbligatorio, unico) <br>
     */
    @NotNull(message = "La descrizione è obbligatoria")
    @Size(min = 2, max = 50)
    @Field("nome")
    @AIField(type = EAFieldType.text, firstCapital = true, widthEM = 24)
    @AIColumn(widthPX = 12)
    public String nome;

    /**
     * momento in cui si effettua la modifica della versione (obbligatorio, non unica) <br>
     * inserito automaticamente prima del persist <br>
     */
    @NotNull(message = "Il tempo è obbligatorio")
    @Size(min = 2, max = 50)
    @Field("time")
    @AIField(type = EAFieldType.localdatetime, firstCapital = true, widthEM = 24)
    @AIColumn(widthPX = 5)
    public LocalDateTime timestamp;

    /**
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return id;
    }// end of method


}// end of entity class