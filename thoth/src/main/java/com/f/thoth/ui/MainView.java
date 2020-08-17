package com.f.thoth.ui;

import static com.f.thoth.ui.utils.BakeryConst.TITLE_ADMINISTRATION;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_DASHBOARD;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_LOGOUT;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_OBJECT_TO_PROTECT;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_PRODUCTS;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_ROLES;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_SINGLE_USERS;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_STOREFRONT;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_TENANTS;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_USERS;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_USER_GROUPS;
import static com.f.thoth.ui.utils.BakeryConst.VIEWPORT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.f.thoth.app.security.SecurityUtils;
import com.f.thoth.ui.views.HasConfirmation;
import com.f.thoth.ui.views.admin.objects.ObjectToProtectView;
import com.f.thoth.ui.views.admin.products.ProductsView;
import com.f.thoth.ui.views.admin.roles.RoleView;
import com.f.thoth.ui.views.admin.tenants.TenantsView;
import com.f.thoth.ui.views.admin.users.SingleUserView;
import com.f.thoth.ui.views.admin.users.UserGroupView;
import com.f.thoth.ui.views.admin.users.UsersView;
import com.f.thoth.ui.views.dashboard.DashboardView;
import com.f.thoth.ui.views.storefront.StorefrontView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinServlet;

@Viewport(VIEWPORT)
@PWA(name = "Evidentia", shortName = "Evidentia",
     startPath = "login",
     backgroundColor = "#227aef", themeColor = "#227aef",
     offlinePath = "offline-page.html",
     offlineResources = {"images/offline-login-banner.jpg"},
     enableInstallPrompt = false)
public class MainView extends AppLayout
{
   private final ConfirmDialog confirmDialog = new ConfirmDialog();
   private final Tabs menu;

   public MainView()
   {
      confirmDialog.setCancelable(true);
      confirmDialog.setConfirmButtonTheme("raised tertiary error");
      confirmDialog.setCancelButtonTheme ("raised tertiary");
      createHeader();
      createDrawer();

      this.setDrawerOpened(false);
      //   Span appName = new Span("Evidentia");
      //   appName.addClassName("hide-on-mobile");

      menu = createMenuTabs();

      //   this.addToNavbar(appName);
      this.addToNavbar(true, menu);
      this.getElement().appendChild(confirmDialog.getElement());

      getElement().addEventListener("search-focus", e ->
      {
         getElement().getClassList().add("hide-navbar");
      });

      getElement().addEventListener("search-blur", e ->
      {
         getElement().getClassList().remove("hide-navbar");
      });

   }//MainView

   private void createHeader()
   {
      H2 logo = new H2("Evidentia");
      logo.addClassName("hide-on-mobile");
      HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);

      header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
      header.setWidth("100%");
      header.addClassName("header");
      addToNavbar(header);
   }//createHeader

   private void createDrawer()
   {
      VerticalLayout mainMenu = new VerticalLayout();
      createLink(VaadinIcon.EDIT,        TITLE_STOREFRONT,        StorefrontView.class,      mainMenu);
      createLink(VaadinIcon.CLOCK,       TITLE_DASHBOARD,         DashboardView.class,       mainMenu);
      createLink(VaadinIcon.KEY,         TITLE_ADMINISTRATION,    UsersView.class,           mainMenu);
      createLink(VaadinIcon.CALENDAR,    TITLE_PRODUCTS,          ProductsView.class,        mainMenu);
      createLink(VaadinIcon.HOSPITAL,    TITLE_TENANTS,           TenantsView.class,         mainMenu);
      createLink(VaadinIcon.COG,         TITLE_OBJECT_TO_PROTECT, ObjectToProtectView.class, mainMenu);
      createLink(VaadinIcon.ACADEMY_CAP, TITLE_ROLES,             RoleView.class,            mainMenu);
      createLink(VaadinIcon.USERS,       TITLE_USER_GROUPS,       UserGroupView.class,       mainMenu);
      createLink(VaadinIcon.USER,        TITLE_SINGLE_USERS,      SingleUserView.class,      mainMenu);

      final String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
      mainMenu.add(createLogoutLink(contextPath));
      addToDrawer(mainMenu);

   }//createDrawer


   private void createLink(VaadinIcon icon, String title, Class<? extends Component> viewClass, VerticalLayout mainMenu)
   {
      if (SecurityUtils.isAccessGranted(viewClass))
      {
         RouterLink link = populateLink(new RouterLink(null, viewClass), icon, title);
         link.setHighlightCondition(HighlightConditions.sameLocation());
         mainMenu.add(link);
      }
   }//createLink


   @Override
   protected void afterNavigation()
   {
      super.afterNavigation();
      confirmDialog.setOpened(false);
      if (getContent() instanceof HasConfirmation)
         ((HasConfirmation) getContent()).setConfirmDialog(confirmDialog);

      RouteConfiguration configuration = RouteConfiguration.forSessionScope();
      if (configuration.isRouteRegistered(this.getContent().getClass()))
      {
         String target = configuration.getUrl(this.getContent().getClass());
         Optional<Component> tabToSelect = menu.getChildren().filter(tab ->
         {
            Component child = tab.getChildren().findFirst().get();
            return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
         }).findFirst();
         tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
      }
      else
      {
         menu.setSelectedTab(null);
      }
   }//afterNavigation

   private static Tabs createMenuTabs()
   {
      final Tabs tabs = new Tabs();
      tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
      tabs.add(getAvailableTabs());
      return tabs;
   }//createMenuTabs

   private static Tab[] getAvailableTabs()
   {
      /*
  PAGE_CLIENTES                                 = "Cliente";
     PAGE_TENANTS                               = "Tenant";
  PAGE_SEGURIDAD                                = "Seguridad";
     PAGE_OBJETOS                               = "ObjetoProteger";
     PAGE_ROLES                                 = "Rol";
     PAGE_PERMISOS_EJECUCION                    = "PermisoEjecucion";
     PAGE_PERMISOS_ACCESO                       = "PermisoAcceso";
  PAGE_ADMINISTRACION                           = "Administracion";
     PAGE_PARAMETROS                            = "Parametros";
     PAGE_USUARIOS                              = "Usuario";
     PAGE_GRUPOS_USUARIOS                       = "GrupoUsuario";
  PAGE_CLASIFICACION                            = "Clasificacion";
     PAGE_FONDOS                                = "Fondo";
     PAGE_OFICINAS                              = "Oficina";
     PAGE_SERIES                                = "Serie";
     PAGE_SUBSERIES                             = "Subserie";
     PAGE_TIPOS_DOCUMENTALES                    = "TipoDocumental";
  PAGE_EXPEDIENTES                              = "AdminExpediente";
     PAGE_EXPEDIENTES                           = "Expediente";
     PAGE_SUBEXPEDIENTES                        = "SubExpediente";
     PAGE_VOLUMENES                             = "Volumen";
     PAGE_INDICE                                = "Indice";
     PAGE_EXPORTACION                           = "ExportaExpediente";
     PAGE_IMPORTACION                           = "ImportaExpediente";
     PAGE_COPIA_DOCUMENTOS                      = "CopiaDocumento";
     PAGE_TRANSFER_DOCUMENTOS                   = "TransferenciaDocumento";
  PAGE_TRAMITE                                  = "Tramite";
     PAGE_BANDEJA                               = "Bandeja";
     PAGE_CLASIFICACION_DOCUMENTOS              = "ClasificaDoc";
     PAGE_RETORNO                               = "Retorno";
     PAGE_RE_ENVIO                              = "ReEnvio";
     PAGE_BORRADORES                            = "Borrador";
     PAGE_FIRMA                                 = "FirmaDocumento";
     PAGE_ENVIO                                 = "EnvioInterno";
  PAGE_RECEPCION                                = "Recepcion";
     PAGE_DOCUMENTOS                            = "RecibeDoc";
     PAGE_E_MAIL                                = "RecibeEmail";
     PAGE_DIGITALIZACION                        = "Digitalizacion";
     PAGE_DIRECCIONAMIENTO                      = "EnrutadDoc";
  PAGE_CORRESPONDENCIA_EXTERNA                  = "CorrespondenciaExterna";
     PAGE_REGISTRO_ENVIOS                       = "ConsolidaEnvios";
     PAGE_ENVIO_EXTERNO                         = "EnvioExterno";
     PAGE_CONFIRMACI�N_ENVIO                    = "ConfirmacionEnvio";
  PAGE_CONSULTA                                 = "Consulta";
     PAGE_DOCUMENTOS                            = "ConsultaDoc";
        PAGE_CONSULTA_LIBRE                     = "ConsultaDocLibre";
        PAGE_CONSULTA_METADATOS                 = "ConsultaDocMetadatos";
     PAGE_EXPEDIENTES                           = "Expedientes";
        PAGE_CONSULTA_EXPEDIENTES_LIBRE         = "ConsultaExpedLibre";
        PAGE_CONSULTA_EXPEDIENTES_METADATOS     = "ConsultaExpedMeta";
        PAGE_CONSULTA_EXPEDIENTES_CLASIFICACION = "ConsultaExpedClasificacion";
  PAGE_PROCESOS                                 = "Proceso";
     PAGE_EJECUCION_PROCESO                     = "EjecucionProceso";
     PAGE_DEFINICION_PROCESO                    = "DefinicionProceso";
  PAGE_ARCHIVO                                  = "Archivo";
     PAGE_LOCALES                               = "Local";
     PAGE_TRANSFERENCIA                         = "PreparaTransferencia";
     PAGE_RECIBO_TRANSFERENCIA                  = "ReciboTransferencia";
     PAGE_LOCALIZACION                          = "Localizacion";
     PAGE_PRESTAMO                              = "Prestamo";
        PAGE_PRESTAMO                           = "PrestamoExpediente";
        PAGE_DEVOLUCION                         = "RetornoExpediente";
     PAGE_INDICES_ARCHIVO                       = "IndiceArchivo";
     
     MenuBar menuBar = new MenuBar();

menuBar.setOpenOnHover(true);

Text selected = new Text("");
Div message = new Div(new Text("Selected: "), selected);

MenuItem project = menuBar.addItem("Project");
MenuItem account = menuBar.addItem("Account");
menuBar.addItem("Sign Out", e -> selected.setText("Sign Out"));

SubMenu projectSubMenu = project.getSubMenu();
MenuItem users = projectSubMenu.addItem("Users");
MenuItem billing = projectSubMenu.addItem("Billing");

SubMenu usersSubMenu = users.getSubMenu();
usersSubMenu.addItem("List", e -> selected.setText("List"));
usersSubMenu.addItem("Add", e -> selected.setText("Add"));

SubMenu billingSubMenu = billing.getSubMenu();
billingSubMenu.addItem("Invoices", e -> selected.setText("Invoices"));
billingSubMenu.addItem("Balance Events",
        e -> selected.setText("Balance Events"));

account.getSubMenu().addItem("Edit Profile",
        e -> selected.setText("Edit Profile"));
account.getSubMenu().addItem("Privacy Settings",
        e -> selected.setText("Privacy Settings"));
add(menuBar, message);
       */
      final List<Tab> tabs = new ArrayList<>(6);
      tabs.add(createTab(VaadinIcon.EDIT, TITLE_STOREFRONT, StorefrontView.class));
      tabs.add(createTab(VaadinIcon.CLOCK,TITLE_DASHBOARD, DashboardView.class));

      if (SecurityUtils.isAccessGranted(UsersView.class))
         tabs.add(createTab(VaadinIcon.KEY,TITLE_ADMINISTRATION, UsersView.class));

      if (SecurityUtils.isAccessGranted(ProductsView.class))
         tabs.add(createTab(VaadinIcon.CALENDAR, TITLE_PRODUCTS, ProductsView.class));

      if (SecurityUtils.isAccessGranted(TenantsView.class))
         tabs.add(createTab(VaadinIcon.HOSPITAL, TITLE_TENANTS, TenantsView.class));

      if (SecurityUtils.isAccessGranted(ObjectToProtectView.class))
         tabs.add(createTab(VaadinIcon.COG, TITLE_OBJECT_TO_PROTECT, ObjectToProtectView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         tabs.add(createTab(VaadinIcon.ACADEMY_CAP, TITLE_ROLES, RoleView.class));

      if (SecurityUtils.isAccessGranted(UserGroupView.class))
         tabs.add(createTab(VaadinIcon.USERS, TITLE_USER_GROUPS, UserGroupView.class));

      if (SecurityUtils.isAccessGranted(SingleUserView.class))
         tabs.add(createTab(VaadinIcon.USER, TITLE_SINGLE_USERS, SingleUserView.class));

      final String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
      final Tab logoutTab = createTab(createLogoutLink(contextPath));
      tabs.add(logoutTab);
      return tabs.toArray(new Tab[tabs.size()]);

   }//getAvailableTabs

   private static Tab createTab(VaadinIcon icon, String title, Class<? extends Component> viewClass)
   {
      return createTab(populateLink(new RouterLink(null, viewClass), icon, title));
   }//createTab

   private static Tab createTab(Component content)
   {
      final Tab tab = new Tab();
      tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
      tab.add(content);
      return tab;
   }//createTab

   private static Anchor createLogoutLink(String contextPath)
   {
      final Anchor a = populateLink(new Anchor(), VaadinIcon.ARROW_RIGHT, TITLE_LOGOUT);
      a.setHref(contextPath + "/logout");
      return a;
   }//createLogoutLink

   private static <T extends HasComponents> T populateLink(T a, VaadinIcon icon, String title)
   {
      a.add(icon.create());
      if (title.equals(TITLE_ADMINISTRATION) )
         a.add(TITLE_USERS);
      else
         a.add(title);

      return a;
   }//populateLink

}//MainView