package it.algos.vaadflow.application;

import com.vaadin.flow.router.RouterLayout;
import it.algos.vaadflow.ui.MainLayout;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FlowCost {

    public final static String DEVELOPER_COMPANY = "Algos® ";

    public final static String TAG_LOG = "log";

    public final static String TAG_LOGIN = "alogin";

    public final static String TAG_TYP = "logtype";

    public final static String TAG_UTE = "utente";

    public final static String TAG_VER = "versione";

    public final static String TAG_SEC = "secolo";

    public final static String TAG_MES = "mese";

    public final static String TAG_ANN = "anno";

    public final static String TAG_GIO = "giorno";

    public static final Locale APP_LOCALE = Locale.US;

    public final static boolean DEBUG = false;

    public static final String VUOTA = "";

    public static final String SPAZIO = " ";

    public final static String A_CAPO = "\n";

    public final static String ASTERISCO = "*";

    public final static Class<? extends RouterLayout> LAYOUT = MainLayout.class;

    public final static String LAYOUT_NAME = "MainLayout";

    public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes";

    public final static int FLASH = 2000;

    public final static String TAG_HOME = "home";

    public final static String TAG_WIZ = "wizard";

    public final static String TAG_DEV = "developer";

    public final static String TAG_ADD = "address";

    public final static String TAG_PER = "person";

    public final static String TAG_COM = "company";

    public final static String TAG_ROL = "role";

    public final static String TAG_PRE = "preferenza";

    public static final String PAGE_ROOT = "";

    public static final String PAGE_WIZARD = "wizard";

    public static final String PAGE_ADDRESS = "address";

    public static final String PAGE_ADDRESS_EDIT = "address/edit";

    public static final String PAGE_STOREFRONT = "storefront";

    public static final String PAGE_STOREFRONT_EDIT = "storefront/edit";

    public static final String PAGE_COMPANY = "company";

    public static final String PAGE_USERS = "users";

    public static final String PAGE_PRODUCTS = "products";

    public static final String PAGE_LOGOUT = "logout";

    public static final String PAGE_NOTFOUND = "404";

    public static final String PAGE_DEFAULT = PAGE_COMPANY;

    public static final String PAGE_ACCESS_DENIED = "access-denied";

    public static final String ICON_WIZARD = "magic";

    public static final String ICON_STOREFRONT = "edit";

    public static final String ICON_DASHBOARD = "clock";

    public static final String ICON_USERS = "user";

    public static final String ICON_PRODUCTS = "calendar";

    public static final String ICON_LOGOUT = "exit";

    public static final String TITLE_WIZARD = "Wizard";

    public static final String TITLE_STOREFRONT = "Storefront";

    public static final String TITLE_DASHBOARD = "Dashboard";

    public static final String TITLE_USERS = "Users";

    public static final String TITLE_PRODUCTS = "Products";

    public static final String TITLE_LOGOUT = "Logout";

    public static final String TITLE_NOT_FOUND = "Page was not found";

    public static final String TITLE_ACCESS_DENIED = "Access denied";

    public static final String[] ORDER_SORT_FIELDS = {"dueDate", "dueTime", "id"};

    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    public final static String FLAG_TEXT_SEARCH = "textSearch";

    public final static String FLAG_TEXT_NEW = "textNew";

    public final static String BOT_ACCETTA = "Accetta";

    public final static String BOT_CONFERMA = "Conferma";

    public final static String BOT_ANNULLA = "Annulla";

    public final static String BOT_BACK = "Back";

    public final static String BOT_CREATE = "New";

    public final static String BOT_DELETE = "Elimina";

    public final static String BOT_EDIT = "Edit";

    public final static String BOT_SHOW = "Show";

    public final static String BOT_IMAGE = "immagine";

    public final static String BOT_IMPORT = "import";

    public final static String BOT_LINK_ACCETTA = "linkaccetta";

    public final static String BOT_LINK_REGISTRA = "linkregistra";

    public final static String BOT_REGISTRA = "registra";

    public final static String BOT_REVERT = "revert";

    public final static String BOT_SEARCH = "ricerca";

    public final static String BOT_SHOW_ALL = "tutto";

    public final static String BOT_LINK = "botlink";

    public final static String BOT_CHOOSER = "apri";

    public final static String BUTTON_NORMAL_WIDTH = "8em";

    public final static String BUTTON_ICON_WIDTH = "3em";

    public final static String PROPERTY_ID = "id";

    public final static String PROPERTY_COMPANY = "company2";

    public final static String PROPERTY_SERIAL = "serialVersionUID";

    public final static String PROPERTY_ORDINE = "ordine";

    public final static String PROPERTY_NOTE = "note";

    public final static String PROPERTY_CREAZIONE = "creazione";

    public final static String PROPERTY_MODIFICA = "modifica";

    public final static String COMPANY_CODE = "code";

    public final static String COMPANY_UNICO = "codeCompanyUnico";

    //--bottoni della scheda/form/dialog
    public final static String REGISTRA = "Save";

    public final static String ANNULLA = "Back";

    public final static String CANCELLA = "Delete";

    public final static String DELETE = "Delete";

    //log type
    public final static String LOG_SETUP = "logSetup";

    public final static String LOG_NEW = "logNew";

    public final static String LOG_EDIT = "logEdit";

    public final static String LOG_DELETE = "logDelete";

    public final static String LOG_DEBUG = "logDebug";

    public final static String LOG_INFO = "logInfo";

    public final static String LOG_WARN = "logWarn";

    public final static String LOG_ERROR = "logError";

    public final static String LOG_IMPORT = "logImport";

    // generali
    public final static String USA_DEBUG = "usaDebug";

    public final static String USA_LOG_DEBUG = "usaLogDebug";

    public final static String USA_SECURITY = "usaSecurity";

    public final static String USA_CHECK_BOX = "usaCheckbox";

    public final static String USA_COMPANY = "usaCompany";

    public final static String USA_LOG_MAIL = "usaLogMail";

    public final static String MAIL_FROM = "mailFrom";

    public final static String MAIL_TO = "mailTo";

    public final static String MAX_RIGHE_GRID = "maxRigheGrid";

    public final static String MONGO_PAGE_LIMIT = "mongoPageLimit";

    public final static String USA_MENU = "usaMenu";

    // moduli visibili
    public final static String SHOW_COMPANY = "showCompany";

    public final static String SHOW_PREFERENZA = "showPreferenza";

    public final static String SHOW_WIZARD = "showWizard";

    public final static String SHOW_DEVELOPER = "showDeveloper";

    public final static String SHOW_ADDRESS = "showAddress";

    public final static String SHOW_PERSON = "showPerson";

    public final static String SHOW_ROLE = "showRole";

    public final static String SHOW_VERSION = "showVersion";

    public final static String SHOW_USER = "showUser";

    public final static String SHOW_LOG = "showLog";

    public final static String SHOW_LOGTYPE = "showLogType";

    public final static String SHOW_SECOLO = "showSecolo";

    public final static String SHOW_ANNO = "showAnno";

    public final static String SHOW_MESE = "showMese";

    public final static String SHOW_GIORNO = "showGiorno";

    public final static String SHOW_ACCOUNT_ON_MENU = "showAccount";
    public final static String SOGLIA_PAGINATION = "sogliaPagination";

    public final static String LOAD_UTENTI = "loadUtenti";

    public final static String STOP_SAVE = "nonRegistrare";

    //--chiavi mappa costruzione giorni
    public final static String PRIMO_GIORNO_MESE = "1º";

    public final static String KEY_MAPPA_GIORNI_MESE_NUMERO = "meseNumero";

    public final static String KEY_MAPPA_GIORNI_MESE_TESTO = "meseTesto";

    public final static String KEY_MAPPA_GIORNI_NORMALE = "normale";

    public final static String KEY_MAPPA_GIORNI_BISESTILE = "bisestile";

    public final static String KEY_MAPPA_GIORNI_NOME = "nome";

    public final static String KEY_MAPPA_GIORNI_TITOLO = "titolo";

    public final static String KEY_MAPPA_GIORNI_MESE_MESE = "meseMese";

    public final static String KEY_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";

    public final static String KEY_LOGGED_USER = "loggedUser";

    public final static String KEY_UNIQUE_USER_NAME = "uniqueUserName";

    public final static String KEY_CONTEXT = "context";

    //--date
    public final static Locale LOCALE = Locale.ITALIAN;

    public final static String TAG_SEARCH = "search";

    private final static String[] esclusiAll = {PROPERTY_SERIAL, PROPERTY_CREAZIONE, PROPERTY_MODIFICA};

    public final static List<String> ESCLUSI_ALL = Arrays.asList(esclusiAll);

    private final static String[] esclusiList = {PROPERTY_ID, PROPERTY_SERIAL, PROPERTY_COMPANY, PROPERTY_NOTE, PROPERTY_CREAZIONE, PROPERTY_MODIFICA};

    public final static List<String> ESCLUSI_LIST = Arrays.asList(esclusiList);

    private final static String[] esclusiForm = {PROPERTY_ID, PROPERTY_SERIAL, PROPERTY_COMPANY, PROPERTY_CREAZIONE, PROPERTY_MODIFICA};

    public final static List<String> ESCLUSI_FORM = Arrays.asList(esclusiForm);

    private final static String[] esclusiMatrice = {PROPERTY_SERIAL, PROPERTY_ID, PROPERTY_COMPANY};

    public final static List<String> ESCLUSI = Arrays.asList(esclusiMatrice);

    private final static String[] companyMatrice = {COMPANY_CODE, COMPANY_UNICO};

    public final static List<String> COMPANY_OPTIONAL = Arrays.asList(companyMatrice);

    public static List<Class> MENU_CLAZZ_LIST = new ArrayList<>();

    public static String LAYOUT_TITLE = "";

    public static String PROJECT_NAME;

    public static String PROJECT_VERSION;

    public static LocalDate PROJECT_DATE;

    public static String PROJECT_NOTE;

}// end of static class