package it.algos.vaadtest.modules.prova;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import it.algos.vaadflow.annotation.AIScript;
import it.algos.vaadflow.enumeration.EAColor;
import it.algos.vaadflow.presenter.IAPresenter;
import it.algos.vaadflow.ui.dialog.ADialog;
import it.algos.vaadflow.ui.dialog.AViewDialog;
import it.algos.vaadflow.ui.fields.ATextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;

import static it.algos.vaadtest.application.TestCost.TAG_PRO;

/**
 * Project vaadtest <br>
 * Created by Algos
 * User: Gac
 * Fix date: 20-ott-2018 18.52.31 <br>
 * <p>
 * Estende la classe astratta AViewDialog per visualizzare i fields <br>
 * <p>
 * Not annotated with @SpringView (sbagliato) perché usa la @Route di VaadinFlow <br>
 * Annotated with @SpringComponent (obbligatorio) <br>
 * Annotated with @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) (obbligatorio) <br>
 * Annotated with @Qualifier (obbligatorio) per permettere a Spring di istanziare la classe specifica <br>
 * Annotated with @Slf4j (facoltativo) per i logs automatici <br>
 * Annotated with @AIScript (facoltativo Algos) per controllare la ri-creazione di questo file dal Wizard <br>
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Qualifier(TAG_PRO)
@Slf4j
@AIScript(sovrascrivibile = true)
public class ProvaViewDialog extends AViewDialog<Prova> {

    @Autowired
    ApplicationContext appContext;


    /**
     * Costruttore @Autowired <br>
     * Si usa un @Qualifier(), per avere la sottoclasse specifica <br>
     * Si usa una costante statica, per essere sicuri di scrivere sempre uguali i riferimenti <br>
     *
     * @param presenter per gestire la business logic del package
     */
    @Autowired
    public ProvaViewDialog(@Qualifier(TAG_PRO) IAPresenter presenter) {
        super(presenter);
    }// end of Spring constructor


//    @Override
//    protected void initButtonBar() {
//        super.initButtonBar();
//    }


    /**
     * Le preferenze specifiche, eventualmente sovrascritte nella sottoclasse
     * Può essere sovrascritto, per aggiungere informazioni
     * Invocare PRIMA il metodo della superclasse
     */
    @Override
    protected void fixPreferenzeSpecifiche() {
        super.fixPreferenzeSpecifiche();
        super.usaFormDueColonne = false;
    }// end of method


    /**
     * Costruisce eventuali fields specifici (costruiti non come standard type)
     * Aggiunge i fields specifici al binder
     * Aggiunge i fields specifici alla fieldMap
     * Sovrascritto nella sottoclasse
     */
    @Override
    protected void addSpecificAlgosFields() {
        super.addSpecificAlgosFields();
        AbstractField propertyField = null;
        propertyField = new ATextField("Alfa");
        ((ATextField) propertyField).setWidth("2em");

        if (propertyField != null) {
            fieldMap.put("alfa", propertyField);
        }// end of if cycle
    }// end of method


    /**
     * Eventuali aggiustamenti finali al layout
     * Aggiunge eventuali altri componenti direttamente al layout grafico (senza binder e senza fieldMap)
     * Sovrascritto nella sottoclasse
     */
    @Override
    protected void fixLayout() {
//        Object beta = StaticContextAccessor.getBean(ADialog.class);
        getFormLayout().add(new Label("Prova"));
        getFormLayout().add(new Button("Click", event -> paperino()));
        getFormLayout().add(new Button("ClickSecondo", event -> topolino()));

        Component comp = creaCombo();
        getFormLayout().add(comp);

        addComboColor();
    }// end of method


    public Component creaCombo() {

        MultiselectComboBox<String> multiselectComboBox = new MultiselectComboBox();
        multiselectComboBox.setLabel("Select items");
        multiselectComboBox.setPlaceholder("Choose...");
        multiselectComboBox.setItems("Item 1", "Item 2", "Item 3", "Item 4");

        return multiselectComboBox;
    }// end of method


    /**
     * Costruisce un comboBox di colori, costruendo una classe ad hoc con testo e bottone-colore.
     */
    private void addComboColor() {
//        Component comp = new AColor("maroon");
//        ComboBox<AColor> combo = new ComboBox<>();

        Select<AColor> select = new Select<>();
        select.setLabel("Seleziona un colore");

        ArrayList<AColor> items = new ArrayList<>();
        for (EAColor color : EAColor.values()) {
            items.add(new AColor(color.getTag()));
        }// end of for cycle

        select.setItems(items);
        select.setRenderer(new ComponentRenderer<>(color -> {
            Div div = new Div();
            div.getStyle().set("text-align", "left");
            div.setText((color.getName()));

            FlexLayout wrapper = new FlexLayout();
            wrapper.setFlexGrow(1, div);
            wrapper.add(color.getColore(), new Label("&nbsp;&nbsp;&nbsp;"), div);
            return wrapper;
        }));

        select.setWidth("8em");
        getFormLayout().add(select);
    }// end of method


    /**
     * Recupera il field dal nome
     *
     * @param publicFieldName
     */
    @Override
    protected AbstractField getField(String publicFieldName) {
        return super.getField(publicFieldName);
    }// end of method


    public void pippo() {
        int a = 87;
    }// end of method


    public void pluto() {
        int a = 87;
    }// end of method


    public void paperino() {
        ADialog dialog = appContext.getBean(ADialog.class, "Alfetta");
        dialog.open("Pippoz", this::pippo, this::pluto);
    }// end of method


    public void topolino() {
        ADialog dialog = appContext.getBean(ADialog.class, "Burletta");
        VerticalLayout vert = new VerticalLayout(new Label("PrimaRiga"), new Button("Ok"), new Label("Terza riga"));
        dialog.open(vert, this::pippo, this::pluto);
    }// end of method


    public class AColor extends Div {

        private Button colore;

        private String name;


        /**
         * Costruttore completo con parametri.
         *
         * @param name descrizione del parametro
         */
        public AColor(String name) {
            this.name = name;
            this.colore = new Button();
            colore.getElement().getStyle().set("background-color", name);

            this.add(colore);
            this.add(name);
        }// fine del metodo costruttore completo


        public Button getColore() {
            return colore;
        }


        public String getName() {
            return name;
        }

    }// end of class

}// end of class