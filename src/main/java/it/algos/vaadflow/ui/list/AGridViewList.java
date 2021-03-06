package it.algos.vaadflow.ui.list;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.data.selection.SingleSelectionEvent;
import it.algos.vaadflow.backend.entity.AEntity;
import it.algos.vaadflow.enumeration.EAOperation;
import it.algos.vaadflow.presenter.IAPresenter;
import it.algos.vaadflow.ui.dialog.IADialog;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.List;

/**
 * Project vaadflow
 * Created by Algos
 * User: gac
 * Date: Mon, 20-May-2019
 * Time: 08:24
 * <p>
 * Sottoclasse di servizio per gestire la Grid di AViewList in una classe 'dedicata' <br>
 * Alleggerisce la 'lettura' della classe principale <br>
 * Le property sono regolarmente disponibili in AViewList ed in tutte le sue sottoclassi <br>
 * Costruisce e regola la Grid <br>
 * Nelle sottoclassi concrete la Grid può essere modificata. <br>
 */
@Slf4j
public abstract class AGridViewList extends ALayoutViewList {

    /**
     * Costruttore <br>
     *
     * @param presenter per gestire la business logic del package
     * @param dialog    per visualizzare i fields
     */
    public AGridViewList(IAPresenter presenter, IADialog dialog) {
        super(presenter, dialog);
    }// end of Spring constructor


    /**
     * Crea il corpo centrale della view
     * Componente grafico obbligatorio
     * Alcune regolazioni vengono (eventualmente) lette da mongo e (eventualmente) sovrascritte nella sottoclasse
     * Costruisce la Grid con le colonne. Gli items vengono caricati in updateView()
     * Facoltativo (presente di default) il bottone Edit (flag da mongo eventualmente sovrascritto)
     */
    protected void creaGrid() {
        List<String> gridPropertyNamesList = null;
        FlexLayout layout = new FlexLayout();

        //--Costruisce una lista di nomi delle properties della Grid
        gridPropertyNamesList = getGridPropertyNamesList();

        if (usaGridPaginata) {
            grid = creaGridPaginata();
        } else {
            if (entityClazz != null && AEntity.class.isAssignableFrom(entityClazz)) {
                try { // prova ad eseguire il codice
                    //--Costruisce la Grid SENZA creare automaticamente le colonne
                    //--Si possono così inserire colonne manuali prima e dopo di quelle automatiche
                    grid = new Grid(entityClazz, false);
                } catch (Exception unErrore) { // intercetta l'errore
                    log.error(unErrore.toString());
                    return;
                }// fine del blocco try-catch
            } else {
                grid = new Grid();
            }// end of if/else cycle
        }// end of if/else cycle

        //--Apre il dialog di detail
        //--Eventuale inserimento (se previsto nelle preferenze) del bottone Edit come prima colonna
        this.addDetailDialog();

        //--Eventuali colonne calcolate aggiunte PRIMA di quelle automatiche
        this.addSpecificColumnsBefore();

        //--Colonne normali aggiunte nel metodo sovrascritto dalla sottoclasse specifica (se PaginatedGrid)
        //--Colonne normali aggiunte in automatico (se Grid normale)
        if (usaGridPaginata) {
            addColumnsGridPaginata();
        } else {
            addColumnsGrid(gridPropertyNamesList);
        }// end of if/else cycle

        //--Eventuali colonne calcolate aggiunte DOPO quelle automatiche
        this.addSpecificColumnsAfter();

        //--Regolazioni finali sulla grid e sulle colonne
        this.fixLayout();

        //questo funzione per Grid
//        layout.add(grid);
//        this.add(layout);
//        layout.setFlexGrow(1, grid);
//        this.setFlexGrow(1, layout);

        //questo funzione per gridPaginated
        gridHolder.add(grid);
        gridHolder.setFlexGrow(1, grid);

        grid.addSelectionListener(new SelectionListener<Grid<AEntity>, AEntity>() {
            @Override
            public void selectionChange(SelectionEvent<Grid<AEntity>, AEntity> selectionEvent) {
                boolean enabled = selectionEvent != null && selectionEvent.getAllSelectedItems().size() > 0;
                sincroBottoniMenu(enabled);
            }// end of inner method
        });//end of lambda expressions and anonymous inner class

        fixGridHeader();
    }// end of method

    /**
     * Crea la GridPaginata specifica della sottoclasse <br>
     * Sovrascritto <br>
     */
    protected Grid creaGridPaginata() {
        return null;
    }// end of method

    /**
     * Costruisce una lista di nomi delle properties <br>
     * 1) Cerca nell'annotation @AIList della Entity e usa quella lista (con o senza ID) <br>
     * 2) Utilizza tutte le properties della Entity (properties della classe e superclasse) <br>
     * 3) Sovrascrive il metodo getGridPropertyNamesList() nella sottoclasse specifica di xxxService <br>
     * Un eventuale modifica dell'ordine di presentazione delle colonne viene regolata nel metodo sovrascritto <br>
     */
    protected List<String> getGridPropertyNamesList() {
        List<String> gridPropertyNamesList = service != null ? service.getGridPropertyNamesList(context) : null;

        return gridPropertyNamesList;
    }// end of method


    /**
     * Eventuali colonne calcolate aggiunte PRIMA di quelle automatiche
     * Sovrascritto
     */
    protected void addSpecificColumnsBefore() {
    }// end of method


    /**
     * Aggiunge in automatico le colonne previste in gridPropertyNamesList <br>
     * Funziona SOLO con la Grid normale <br>
     */
    protected void addColumnsGrid(List<String> gridPropertyNamesList) {
        if (gridPropertyNamesList != null) {
            for (String propertyName : gridPropertyNamesList) {
                column.create(grid, entityClazz, propertyName);
            }// end of for cycle
        }// end of if cycle
    }// end of method


    /**
     * Aggiunge le colonne alla PaginatedGrid <br>
     * Sovrascritto (obbligatorio) <br>
     */
    protected void addColumnsGridPaginata() {
    }// end of method


    /**
     * Eventuali colonne calcolate aggiunte DOPO quelle automatiche
     * Sovrascritto
     */
    protected void addSpecificColumnsAfter() {
    }// end of method


    /**
     * Eventuali aggiustamenti finali al layout
     * Regolazioni finali sulla grid e sulle colonne
     * Sovrascritto
     */
    protected void fixLayout() {
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setWidth("60em");
        grid.setHeightByRows(true);
        grid.addClassName("pippoz");
        grid.getElement().setAttribute("theme", "row-dividers");
    }// end of method


    /**
     * Apre il dialog di detail <br>
     * Eventuale inserimento (se previsto nelle preferenze) del bottone Edit come prima colonna <br>
     */
    protected void addDetailDialog() {
        //--Flag di preferenza per aprire il dialog di detail con un bottone Edit. Normalmente true.
        if (usaBottoneEdit) {
            ComponentRenderer renderer = new ComponentRenderer<>(this::createEditButton);
            Grid.Column colonna = grid.addColumn(renderer);
            colonna.setWidth("6em");
            colonna.setFlexGrow(0);
        } else {
            EAOperation operation = isEntityModificabile ? EAOperation.edit : EAOperation.showOnly;
            grid.addSelectionListener(evento -> apreDialogo((SingleSelectionEvent) evento, operation));
        }// end of if/else cycle
    }// end of method


    protected Button createEditButton(AEntity entityBean) {
        Button edit = new Button(testoBottoneEdit, event -> dialog.open(entityBean, EAOperation.edit, context));
        edit.setIcon(new Icon("lumo", "edit"));
        edit.addClassName("review__edit");
        edit.getElement().setAttribute("theme", "tertiary");
        return edit;
    }// end of method


    protected void apreDialogo(SingleSelectionEvent evento, EAOperation operation) {
        if (evento != null && evento.getOldValue() != evento.getValue()) {
            if (evento.getValue().getClass().getName().equals(entityClazz.getName())) {
                if (usaRouteFormView && text.isValid(routeNameFormEdit)) {
                    AEntity entity = (AEntity) evento.getValue();
                    routeVerso(routeNameFormEdit, entity);
                } else {
                    dialog.open((AEntity) evento.getValue(), operation, context);
                }// end of if/else cycle
            }// end of if cycle
        }// end of if cycle
    }// end of method


    protected void sincroBottoniMenu(boolean enabled) {
    }// end of method


    /**
     * Eventuale header text
     */
    protected void fixGridHeader() {
        try { // prova ad eseguire il codice
            HeaderRow topRow = grid.prependHeaderRow();
            Grid.Column[] matrix = array.getColumnArray(grid);
            HeaderRow.HeaderCell informationCell = topRow.join(matrix);
            headerGridHolder = new Label("x");
            informationCell.setComponent(headerGridHolder);
        } catch (Exception unErrore) { // intercetta l'errore
            log.error(unErrore.toString());
        }// fine del blocco try-catch
    }// end of method


    /**
     * Header text
     */
    protected String getGridHeaderText() {
        int numRecCollezione = items.size();
        String filtro = text.format(items.size());
        String totale = text.format(numRecCollezione);
        String testo = entityClazz != null ? entityClazz.getSimpleName() + " - " : "";

        switch (numRecCollezione) {
            case 0:
                testo += "Al momento non ci sono elementi in questa collezione";
                break;
            case 1:
                testo += "Collezione con un solo elemento";
                break;
            default:
                if (isPagination) {
                    testo += "Collezione di " + limit + " elementi su " + totale + " totali. ";
                } else {
                    testo += "Collezione di " + totale + " elementi";
                }// end of if/else cycle
                break;
        } // end of switch statement

        return testo;
    }// end of method


    public void updateView() {
        updateItems();

        if (items != null) {
            try { // prova ad eseguire il codice
                grid.deselectAll();
                grid.setItems(items);
                headerGridHolder.setText(getGridHeaderText());
            } catch (Exception unErrore) { // intercetta l'errore
                log.error(unErrore.toString());
            }// fine del blocco try-catch
        }// end of if cycle

        creaAlertLayout();
    }// end of method


    protected void updateItems() {
        if (isPagination) {
            items = service != null ? service.findAll(offset, limit) : null;
        } else {
            items = service != null ? service.findAll() : null;
        }// end of if/else cycle
    }// end of method


}// end of class
