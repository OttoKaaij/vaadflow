package it.algos.vaadflow.ui;

import com.flowingcode.addons.applayout.AppLayout;
import com.flowingcode.addons.applayout.menu.MenuItem;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.ui.LoadMode;
import it.algos.vaadflow.application.AContext;
import it.algos.vaadflow.application.FlowCost;
import it.algos.vaadflow.application.StaticContextAccessor;
import it.algos.vaadflow.backend.login.ALogin;
import it.algos.vaadflow.modules.company.Company;
import it.algos.vaadflow.modules.role.EARole;
import it.algos.vaadflow.modules.role.EARoleType;
import it.algos.vaadflow.modules.utente.Utente;
import it.algos.vaadflow.modules.utente.UtenteService;
import it.algos.vaadflow.service.AAnnotationService;
import it.algos.vaadflow.service.AMongoService;
import it.algos.vaadflow.service.AReflectionService;
import it.algos.vaadflow.service.ATextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.*;

import static it.algos.vaadflow.application.FlowCost.*;

/**
 * Gestore dei menu. Unico nell'applicazione (almeno finche non riesco a farne girare un altro)
 * <p>
 * Not annotated with @SpringComponent (sbagliato) perché usa la @Route di VaadinFlow <br>
 * Annotated with @HtmlImport (obbligatorio) per usare il CSS
 * Annotated with @Push (obbligatorio)
 * Annotated with @PageTitle (facoltativo)
 */
@SuppressWarnings("serial")
@HtmlImport(value = "styles/shared-styles.html", loadMode = LoadMode.INLINE)
@Push
@PageTitle(value = MainLayout.SITE_TITLE)
@Slf4j
public class MainLayout extends VerticalLayout implements RouterLayout, PageConfigurator {


    public static final String SITE_TITLE = "World Cup 2018 Stats";

    public final static String KEY_MAPPA_CRONO = "crono";

    private ALogin login;

    private AAnnotationService annotation = AAnnotationService.getInstance();

    private AReflectionService reflection = AReflectionService.getInstance();

    private ATextService text = ATextService.getInstance();

    private UtenteService utenteService;


    //    public MainLayout(UtenteService utenteService, ALogin login) {
    public MainLayout() {
//        this.utenteService = utenteService;
//        this.login = login;
        setMargin(false);
        setSpacing(false);
        setPadding(false);

        fixLoginAndContext();
        creaAllMenu();
    }// end of method


    protected void creaAllMenu() {
        final AppLayout appLayout = new AppLayout(null, createAvatarComponent(), FlowCost.LAYOUT_TITLE);
        ArrayList<MenuItem> listaMenu = null;
        MenuItem menu = null;
        Map<String, List<Class>> mappaClassi = creaMappa(FlowCost.MENU_CLAZZ_LIST);

        if (mappaClassi != null && mappaClassi.size() > 0) {
            listaMenu = new ArrayList<>();

            //--crea i menu del developer, inserendoli come sub-menu
            if (login != null && login.isDeveloper()) {
                this.addDev(listaMenu, mappaClassi);
            }// end of if cycle

            //--crea i menu di admin, inserendoli come sub-menu
            if (login != null && login.isAdmin()) {
                this.addAdmin(listaMenu, mappaClassi);
            }// end of if cycle

            //--crea gli altri menu, esclusi quelli del developer e di admin che sono già inseriti
            this.addUser(listaMenu, mappaClassi);

            //--crea il logout
            listaMenu.add(creaMenuLogout());

            //--aggiunge i menu
            appLayout.setMenuItems(listaMenu.toArray(new MenuItem[listaMenu.size()]));

            //--crea la barra di bottoni, in alto a destra
            appLayout.setToolbarIconButtons(new MenuItem("Logout", "exit-to-app", () -> UI.getCurrent().getPage().executeJavaScript("location.assign('logout')")));

            this.add(appLayout);
        }// end of if cycle
    }// end of method


    private Map<String, List<Class>> creaMappa(List<Class> listaClassiMenu) {
        Map<String, List<Class>> mappa = new HashMap<>();
        ArrayList<Class> devClazzList = new ArrayList<>();
        ArrayList<Class> cronoDevClazzList = new ArrayList<>();
        ArrayList<Class> adminClazzList = new ArrayList<>();
        ArrayList<Class> utenteClazzList = new ArrayList<>();
        EARoleType type = null;
        List<String> cronoList = Arrays.asList(new String[]{"secolo", "anno", "mese", "giorno"});
        String menuName;

        for (Class viewClazz : listaClassiMenu) {
            type = annotation.getViewRoleType(viewClazz);

            switch (type) {
                case developer:
                    menuName = annotation.getViewName(viewClazz);
                    if (cronoList.contains(menuName)) {
                        cronoDevClazzList.add(viewClazz);
                    } else {
                        devClazzList.add(viewClazz);
                    }// end of if/else cycle
                    break;
                case admin:
                    adminClazzList.add(viewClazz);
                    break;
                case user:
                    utenteClazzList.add(viewClazz);
                    break;
                default:
                    utenteClazzList.add(viewClazz);
                    log.warn("Switch - caso non definito");
                    break;
            } // end of switch statement
        }// end of for cycle

        mappa.put(EARole.developer.toString(), devClazzList);
        mappa.put(EARole.admin.toString(), adminClazzList);
        mappa.put(KEY_MAPPA_CRONO, cronoDevClazzList);
        mappa.put(EARole.user.toString(), utenteClazzList);

        return mappa;
    }// end of method


    /**
     * Crea i menu visibili solo per il developer <br>
     * Li incapsula come sub-menu <br>
     */
    private void addDev(ArrayList<MenuItem> listaMenu, Map<String, List<Class>> mappaClassi) {
        MenuItem developerItem;
        MenuItem[] matrice;
        ArrayList<MenuItem> listaSubMenuDev = new ArrayList<>();
        MenuItem menu;
        List<Class> devClazzList = null;

        if (mappaClassi.get(EARole.developer.toString()) != null) {
            devClazzList = mappaClassi.get(EARole.developer.toString());
        }// end of if cycle

        if (devClazzList != null && devClazzList.size() > 0) {
            for (Class viewClazz : devClazzList) {
                menu = creaMenu(viewClazz);
                listaSubMenuDev.add(menu);
            }// end of for cycle
        }// end of if cycle

        //--crea i menu del developer, inserendoli come sub-menu
        this.addCronoDev(listaSubMenuDev, mappaClassi);

        matrice = listaSubMenuDev.toArray(new MenuItem[listaSubMenuDev.size()]);
        developerItem = new MenuItem("Developer", "build", matrice);
        listaMenu.add(developerItem);
    }// end of method


    /**
     * Crea i menu crono, visibili solo per il developer <br>
     * Li incapsula come sub-menu <br>
     */
    private void addCronoDev(ArrayList<MenuItem> listaMenuDev, Map<String, List<Class>> mappaClassi) {
        MenuItem developerItem;
        MenuItem[] matrice;
        ArrayList<MenuItem> listaSubMenuCronoDev = new ArrayList<>();
        MenuItem menu;
        List<Class> devCronoClazzList = null;

        if (mappaClassi.get(KEY_MAPPA_CRONO) != null) {
            devCronoClazzList = mappaClassi.get(KEY_MAPPA_CRONO);
        }// end of if cycle

        if (devCronoClazzList != null && devCronoClazzList.size() > 0) {
            for (Class viewClazz : devCronoClazzList) {
                menu = creaMenu(viewClazz);
                listaSubMenuCronoDev.add(menu);
            }// end of for cycle
        }// end of if cycle

        if (devCronoClazzList != null && devCronoClazzList.size() > 0) {
            matrice = listaSubMenuCronoDev.toArray(new MenuItem[listaSubMenuCronoDev.size()]);
            developerItem = new MenuItem("Crono", "build", matrice);
            listaMenuDev.add(developerItem);
        }// end of if cycle
    }// end of method


    /**
     * Crea i menu visibili solo per l'admin ed per il developer <br>
     * Li incapsula come sub-menu <br>
     */
    private void addAdmin(ArrayList<MenuItem> listaMenu, Map<String, List<Class>> mappaClassi) {
        MenuItem adminItem;
        MenuItem[] matrice;
        ArrayList<MenuItem> listaSubMenuDev = new ArrayList<>();
        MenuItem menu;
        List<Class> adminClazzList = null;

        if (mappaClassi.get(EARole.admin.toString()) != null) {
            adminClazzList = mappaClassi.get(EARole.admin.toString());
        }// end of if cycle

        if (adminClazzList != null && adminClazzList.size() > 0) {
            for (Class viewClazz : adminClazzList) {
                menu = creaMenu(viewClazz);
                listaSubMenuDev.add(menu);
            }// end of for cycle
        }// end of if cycle

        matrice = listaSubMenuDev.toArray(new MenuItem[listaSubMenuDev.size()]);
        adminItem = new MenuItem("Admin", "apps", matrice);
        listaMenu.add(adminItem);
    }// end of method


    /**
     * Crea i menu visibili a tutti <br>
     */
    private void addUser(ArrayList<MenuItem> listaMenu, Map<String, List<Class>> mappaClassi) {
        MenuItem menu;
        List<Class> userClazzList = null;

        if (mappaClassi.get(EARole.user.toString()) != null) {
            userClazzList = mappaClassi.get(EARole.user.toString());
        }// end of if cycle

        if (userClazzList != null && userClazzList.size() > 0) {
            for (Class viewClazz : userClazzList) {
                menu = creaMenu(viewClazz);
                listaMenu.add(menu);
            }// end of for cycle
        }// end of if cycle
    }// end of method


    /**
     * Crea l'immagine (se esiste) dell'utente collegato <br>
     */
    private Component createAvatarComponent() {
        Div container = new Div();
        H5 userName;
        container.getElement().setAttribute("style", "text-align: center;");
        Image i = new Image("/frontend/images/avatar.png", "avatar");
        i.getElement().setAttribute("style", "width: 60px; margin-top:10px");

        if (login != null && login.getUtente() != null) {
            container.add(i, new H5(login.getUtente().userName));
        } else {
            container.add(i);
        }// end of if/else cycle

        return container;
    }// end of method


    /**
     * Crea il singolo item di menu <br>
     */
    protected MenuItem creaMenu(Class<? extends AViewList> viewClazz) {
        String linkRoute;
        String menuName;
        String icon;

        linkRoute = annotation.getQualifierName(viewClazz);
        menuName = annotation.getViewName(viewClazz);
        menuName = text.primaMaiuscola(menuName);
        icon = reflection.getIronIcon(viewClazz);

        return new MenuItem(menuName, icon, () -> UI.getCurrent().navigate(linkRoute));
    }// end of method


    /**
     * Crea (in fondo) il menu item per il logout <br>
     */
    protected MenuItem creaMenuLogout() {
        MenuItem menuItem = null;
        String menuName = "Logout";

        menuItem = new MenuItem(menuName, "exit-to-app", () -> UI.getCurrent().getPage().executeJavaScript("location.assign('logout')"));
        return menuItem;
    }// end of method


    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }// end of method


    @Override
    public void configurePage(final InitialPageSettings settings) {
        settings.addMetaTag("viewport", "width=device-width, initial-scale=1.0");
        settings.addLink("shortcut icon", "/frontend/images/favicons/favicon-96x96.png");
        settings.addLink("manifest", "/manifest.json");
        settings.addFavIcon("icon", "/frontend/images/favicons/favicon-96x96.png", "96x96");
    }// end of method


    /**
     * Crea il login ed il context <br>
     * Controlla che non esista già il context nella vaadSession
     * (non è chiaro se passa prima da MainLayout o da AViewList) <br>
     * <p>
     * Recupera l'user dall'attributo della sessione HttpSession al termine della security <br>
     * Crea il login <br>
     * Crea il context <br>
     * Inserisce il context come attributo nella vaadSession <br>
     */
    private void fixLoginAndContext() {
        AContext context;
        String uniqueUserName = "";
        Utente utente;
        Company company = null;
        VaadinSession vaadSession = UI.getCurrent().getSession();
        login = StaticContextAccessor.getBean(ALogin.class);

        context = (AContext) vaadSession.getAttribute(KEY_CONTEXT);
        if (context == null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession httpSession = attr.getRequest().getSession(true);
            SecurityContext securityContext = (SecurityContext) httpSession.getAttribute(KEY_SECURITY_CONTEXT);
            User springUser = (User) securityContext.getAuthentication().getPrincipal();
            uniqueUserName = springUser.getUsername();
            AMongoService mongo = StaticContextAccessor.getBean(AMongoService.class);
            utente = (Utente) mongo.findByProperty(Utente.class, "userName", uniqueUserName);

            login.setUtente(utente);
            company = utente.company;
            context = new AContext(login, company);
            vaadSession.setAttribute(KEY_CONTEXT, context);
        }// end of if cycle
    }// end of method

}// end of class