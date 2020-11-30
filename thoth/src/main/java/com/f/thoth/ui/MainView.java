package com.f.thoth.ui;

import static com.f.thoth.ui.utils.Constant.VIEWPORT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.f.thoth.app.security.SecurityUtils;
import com.f.thoth.ui.utils.Constant;
import com.f.thoth.ui.views.HasConfirmation;
import com.f.thoth.ui.views.admin.objects.OperationView;
import com.f.thoth.ui.views.admin.products.ProductsView;
import com.f.thoth.ui.views.admin.roles.RoleView;
import com.f.thoth.ui.views.admin.tenants.TenantsView;
import com.f.thoth.ui.views.admin.users.SingleUserView;
import com.f.thoth.ui.views.admin.users.UserGroupView;
import com.f.thoth.ui.views.admin.users.UsersView;
import com.f.thoth.ui.views.classification.ClassificationView;
import com.f.thoth.ui.views.classification.LevelView;
import com.f.thoth.ui.views.classification.RetentionView;
import com.f.thoth.ui.views.dashboard.DashboardView;
import com.f.thoth.ui.views.expediente.ExpedienteView;
import com.f.thoth.ui.views.metadata.DocumentTypeView;
import com.f.thoth.ui.views.metadata.MetadataSchemaView;
import com.f.thoth.ui.views.metadata.MetadataView;
import com.f.thoth.ui.views.security.permission.AccessPermissionView;
import com.f.thoth.ui.views.security.permission.ExecutePermissionView;
import com.f.thoth.ui.views.storefront.StorefrontView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
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
   private VerticalLayout mainMenu;
   private final Tabs menu;

   public MainView()
   {
      confirmDialog.setCancelable(true);
      confirmDialog.setConfirmButtonTheme("raised tertiary error");
      confirmDialog.setCancelButtonTheme ("raised tertiary");
      createHeader();
      createDrawer();
      this.setDrawerOpened(false);

      menu = createMenuTabs();

      this.addToNavbar(true, menu);
      getElement().appendChild(confirmDialog.getElement());
      getElement().addEventListener("search-focus", e -> getElement().getClassList().add("hide-navbar") );
      getElement().addEventListener("search-blur",  e -> getElement().getClassList().remove("hide-navbar"));

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
      mainMenu = new VerticalLayout();
      createClientsMenu();
      createSecurityMenu();
      createAdminMenu();
      createPropertiesMenu();
      createClasificationMenu();
      createExpedientesMenu();
      createTramiteMenu();
      createRecepcionMenu();
      createMailMenu();
      createSearchMenu();
      createProcessMenu();
      createArchiveMenu();
      final String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
      mainMenu.add(createLogoutLink(contextPath));
      addToDrawer(mainMenu);

   }//createDrawer


   private RouterLink createRoute(VaadinIcon icon, String title, Class<? extends Component> viewClass)
   {
      RouterLink  route = populateLink(new RouterLink(null, viewClass), icon, title);
      route.setHighlightCondition(HighlightConditions.sameLocation());
      return route;
   }//createRoute

   private void createClientsMenu()
   {
      Div clients = new Div(VaadinIcon.HOSPITAL.create(), new Label(Constant.TITLE_CLIENTES));
      ContextMenu clientsMenu = new ContextMenu(clients);
      clientsMenu.setOpenOnClick(true);
      if (SecurityUtils.isAccessGranted(TenantsView.class))
         clientsMenu.addItem(createRoute(VaadinIcon.BUILDING, Constant.TITLE_TENANTS, TenantsView.class));

      if ( clientsMenu.getItems().size() > 0)
         mainMenu.add(clients);

   }//createClientsMenu


   private void createSecurityMenu( )
   {
      Div security = new Div(VaadinIcon.KEY.create(), new Label(Constant.TITLE_SEGURIDAD));
      ContextMenu securityMenu = new ContextMenu(security);
      securityMenu.setOpenOnClick(true);

      if (SecurityUtils.isAccessGranted(OperationView.class))
         securityMenu.addItem(createRoute(VaadinIcon.COG, Constant.TITLE_OPERATIONS, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         securityMenu.addItem(createRoute(VaadinIcon.BOOK, Constant.TITLE_INFORMACION, OperationView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         securityMenu.addItem(createRoute(VaadinIcon.ACADEMY_CAP, Constant.TITLE_ROLES, RoleView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         securityMenu.addItem(createRoute(VaadinIcon.CHECK_CIRCLE, Constant.TITLE_PERMISOS_EJECUCION, ExecutePermissionView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         securityMenu.addItem(createRoute(VaadinIcon.CHECK_SQUARE, Constant.TITLE_PERMISOS_ACCESO,AccessPermissionView.class));

      if ( securityMenu.getItems().size() > 0)
         mainMenu.add(security);

   }//createSecurityMenu

   private void createAdminMenu()
   {
      Div admin = new Div(VaadinIcon.ASTERISK.create(), new Label(Constant.TITLE_ADMINISTRACION));
      ContextMenu adminMenu = new ContextMenu(admin);
      adminMenu.setOpenOnClick(true);

      if (SecurityUtils.isAccessGranted(OperationView.class))
         adminMenu.addItem(createRoute(VaadinIcon.TABLE, Constant.TITLE_PARAMETROS, OperationView.class));

      if (SecurityUtils.isAccessGranted(SingleUserView.class))
         adminMenu.addItem(createRoute(VaadinIcon.USER, Constant.TITLE_USUARIOS, SingleUserView.class));

      if (SecurityUtils.isAccessGranted(UserGroupView.class))
         adminMenu.addItem(createRoute(VaadinIcon.GROUP, Constant.TITLE_GRUPOS_USUARIOS, UserGroupView.class));

      if ( adminMenu.getItems().size() > 0)
         mainMenu.add(admin);

   }//createAdminMenu
   
   private void createPropertiesMenu()
   {
      Div properties = new Div(VaadinIcon.ABACUS.create(), new Label(Constant.TITLE_PROPIEDADES));
      ContextMenu propertiesMenu = new ContextMenu(properties);
      propertiesMenu.setOpenOnClick(true);

      if (SecurityUtils.isAccessGranted(MetadataView.class))
         propertiesMenu.addItem(createRoute(VaadinIcon.CUBE, Constant.TITLE_METADATA, MetadataView.class));

      if (SecurityUtils.isAccessGranted(MetadataSchemaView.class))
         propertiesMenu.addItem(createRoute(VaadinIcon.CUBES, Constant.TITLE_ESQUEMAS_METADATA, MetadataSchemaView.class));

      if (SecurityUtils.isAccessGranted(DocumentTypeView.class))
         propertiesMenu.addItem(createRoute(VaadinIcon.GRID_BIG_O, Constant.TITLE_TIPOS_DOCUMENTALES, DocumentTypeView.class));

      if ( propertiesMenu.getItems().size() > 0)
         mainMenu.add(properties);
      
   }//createPropertiesMenu

   private void createClasificationMenu()
   {
      Div classification = new Div(VaadinIcon.SITEMAP.create(), new Label(Constant.TITLE_CLASIFICACION));
      ContextMenu classificationMenu = new ContextMenu(classification);
      classificationMenu.setOpenOnClick(true);

      if (SecurityUtils.isAccessGranted(LevelView.class))
         classificationMenu.addItem(createRoute(VaadinIcon.LEVEL_RIGHT_BOLD, Constant.TITLE_NIVELES, LevelView.class));

      if (SecurityUtils.isAccessGranted(RetentionView.class))
         classificationMenu.addItem(createRoute(VaadinIcon.CALENDAR_O, Constant.TITLE_RETENCION, RetentionView.class));

      if (SecurityUtils.isAccessGranted(ClassificationView.class))
         classificationMenu.addItem(createRoute(VaadinIcon.SPLIT, Constant.TITLE_ESQUEMAS_CLASIFICACION, ClassificationView.class));

      if ( classificationMenu.getItems().size() > 0)
         mainMenu.add(classification);

   }//createClasificationMenu

   private void createExpedientesMenu()
   {
      Div expediente = new Div(VaadinIcon.FOLDER.create(), new Label(Constant.TITLE_ADMIN_EXPEDIENTES));
      ContextMenu expedienteMenu = new ContextMenu(expediente);
      expedienteMenu.setOpenOnClick(true);

      if (SecurityUtils.isAccessGranted(OperationView.class))
         expedienteMenu.addItem(createRoute(VaadinIcon.FOLDER_O, Constant.TITLE_EXPEDIENTES, ExpedienteView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         expedienteMenu.addItem(createRoute(VaadinIcon.FILE_TREE_SMALL, Constant.TITLE_SUBEXPEDIENTES, OperationView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         expedienteMenu.addItem(createRoute(VaadinIcon.FILE_TREE, Constant.TITLE_VOLUMENES, RoleView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         expedienteMenu.addItem(createRoute(VaadinIcon.LIST_OL, Constant.TITLE_INDICE, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         expedienteMenu.addItem(createRoute(VaadinIcon.DOWNLOAD, Constant.TITLE_EXPORTACION,OperationView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         expedienteMenu.addItem(createRoute(VaadinIcon.UPLOAD, Constant.TITLE_IMPORTACION, RoleView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         expedienteMenu.addItem(createRoute(VaadinIcon.COPY_O, Constant.TITLE_COPIA_DOCUMENTOS, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         expedienteMenu.addItem(createRoute(VaadinIcon.PASTE, Constant.TITLE_TRANSER_DOCUMENTOS,OperationView.class));

      if ( expedienteMenu.getItems().size() > 0)
         mainMenu.add(expediente);

   }//createExpedientesMenu

   private void createTramiteMenu()
   {
      Div tramite = new Div(VaadinIcon.TOOLS.create(), new Label(Constant.TITLE_TRAMITE));
      ContextMenu tramiteMenu = new ContextMenu(tramite);
      tramiteMenu.setOpenOnClick(true);

      if (SecurityUtils.isAccessGranted(OperationView.class))
         tramiteMenu.addItem(createRoute(VaadinIcon.LINES, Constant.TITLE_BANDEJA, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         tramiteMenu.addItem(createRoute(VaadinIcon.SPLIT, Constant.TITLE_CLASIFICACION_DOCUMENTOS, OperationView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         tramiteMenu.addItem(createRoute(VaadinIcon.ARROW_BACKWARD, Constant.TITLE_RETORNO, RoleView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         tramiteMenu.addItem(createRoute(VaadinIcon.ROAD_BRANCH, Constant.TITLE_RE_ENVIO, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         tramiteMenu.addItem(createRoute(VaadinIcon.CLIPBOARD_TEXT, Constant.TITLE_BORRADORES,OperationView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         tramiteMenu.addItem(createRoute(VaadinIcon.KEY_O, Constant.TITLE_FIRMA, RoleView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         tramiteMenu.addItem(createRoute(VaadinIcon.ARROW_FORWARD, Constant.TITLE_ENVIO, OperationView.class));

      if ( tramiteMenu.getItems().size() > 0)
         mainMenu.add(tramite);

   }//createTramiteMenu

   private void createRecepcionMenu()
   {
      Div recepcion = new Div(VaadinIcon.ANGLE_DOUBLE_LEFT.create(), new Label(Constant.TITLE_RECEPCION));
      ContextMenu recepcionMenu = new ContextMenu(recepcion);
      recepcionMenu.setOpenOnClick(true);

      if (SecurityUtils.isAccessGranted(OperationView.class))
         recepcionMenu.addItem(createRoute(VaadinIcon.LEVEL_DOWN_BOLD, Constant.TITLE_RECEPCION_DOCUMENTOS, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         recepcionMenu.addItem(createRoute(VaadinIcon.ENVELOPE, Constant.TITLE_RECEPCION_E_MAIL, OperationView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         recepcionMenu.addItem(createRoute(VaadinIcon.BARCODE, Constant.TITLE_DIGITALIZACION, RoleView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         recepcionMenu.addItem(createRoute(VaadinIcon.ROAD_BRANCH, Constant.TITLE_DIRECCIONAMIENTO, OperationView.class));

      if ( recepcionMenu.getItems().size() > 0)
         mainMenu.add(recepcion);

   }//createRecepcionMenu

   private void createMailMenu()
   {
      Div envio = new Div(VaadinIcon.ANGLE_DOUBLE_RIGHT.create(), new Label(Constant.TITLE_CORRESPONDENCIA_EXTERNA));
      ContextMenu envioMenu = new ContextMenu(envio);
      envioMenu.setOpenOnClick(true);

      if (SecurityUtils.isAccessGranted(OperationView.class))
         envioMenu.addItem(createRoute(VaadinIcon.BULLETS, Constant.TITLE_REGISTRO_ENVIOS, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         envioMenu.addItem(createRoute(VaadinIcon.FLIGHT_TAKEOFF, Constant.TITLE_ENVIO_EXTERNO, OperationView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         envioMenu.addItem(createRoute(VaadinIcon.CHECK_CIRCLE_O, Constant.TITLE_CONFIRMACION_ENVIO, RoleView.class));

      if ( envioMenu.getItems().size() > 0)
         mainMenu.add(envio);

   }//createMailMenu

   private void createSearchMenu()
   {
      Div consulta = new Div(VaadinIcon.SEARCH.create(), new Label(Constant.TITLE_CONSULTA));
      ContextMenu consultaMenu = new ContextMenu(consulta);
      consultaMenu.setOpenOnClick(true);

      MenuItem docsMenu = consultaMenu.addItem(new Div(VaadinIcon.COPY_O.create(), new Label(Constant.TITLE_DOCUMENTOS)));
      SubMenu consultaDocsMenu = docsMenu.getSubMenu();

      if (SecurityUtils.isAccessGranted(OperationView.class))
         consultaDocsMenu.addItem(createRoute(VaadinIcon.BUTTON, Constant.TITLE_CONSULTA_LIBRE, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         consultaDocsMenu.addItem(createRoute(VaadinIcon.FORM, Constant.TITLE_CONSULTA_METADATOS, OperationView.class));

      MenuItem expedienteMenu = consultaMenu.addItem(new Div(VaadinIcon.FILE_O.create(), new Label(Constant.TITLE_CONSULTA_EXPEDIENTES)));
      SubMenu consultaExpedienteMenu = expedienteMenu.getSubMenu();

      if (SecurityUtils.isAccessGranted(OperationView.class))
         consultaExpedienteMenu.addItem(createRoute(VaadinIcon.NATIVE_BUTTON, Constant.TITLE_CONSULTA_EXPEDIENTES_LIBRE, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         consultaExpedienteMenu.addItem(createRoute(VaadinIcon.BULLETS, Constant.TITLE_CONSULTA_EXPEDIENTES_METADATOS, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         consultaExpedienteMenu.addItem(createRoute(VaadinIcon.CONNECT, Constant.TITLE_CONSULTA_EXPEDIENTES_CLASIFICACION, OperationView.class));

      if ( consultaMenu.getItems().size() > 0)
         mainMenu.add(consulta);

   }//createSearchMenu

   private void createProcessMenu()
   {
      Div procesos = new Div(VaadinIcon.COGS.create(), new Label(Constant.TITLE_PROCESOS));
      ContextMenu procesosMenu = new ContextMenu(procesos);
      procesosMenu.setOpenOnClick(true);

      if (SecurityUtils.isAccessGranted(OperationView.class))
         procesosMenu.addItem(createRoute(VaadinIcon.BOLT, Constant.TITLE_EJECUCION_PROCESO, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         procesosMenu.addItem(createRoute(VaadinIcon.ABACUS, Constant.TITLE_DEFINICION_PROCESO, OperationView.class));

      if ( procesosMenu.getItems().size() > 0)
         mainMenu.add(procesos);

   }//createProcessMenu

   private void createArchiveMenu()
   {
      Div archivo = new Div(VaadinIcon.ARCHIVES.create(), new Label(Constant.TITLE_ARCHIVO));
      ContextMenu archivoMenu = new ContextMenu(archivo);
      archivoMenu.setOpenOnClick(true);

      if (SecurityUtils.isAccessGranted(OperationView.class))
         archivoMenu.addItem(createRoute(VaadinIcon.CUBES, Constant.TITLE_LOCALES, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         archivoMenu.addItem(createRoute(VaadinIcon.STEP_FORWARD, Constant.TITLE_TRANSFERENCIA, OperationView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         archivoMenu.addItem(createRoute(VaadinIcon.STEP_BACKWARD, Constant.TITLE_RECIBO_TRANSFERENCIA, RoleView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         archivoMenu.addItem(createRoute(VaadinIcon.LOCATION_ARROW, Constant.TITLE_LOCALIZACION, OperationView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         archivoMenu.addItem(createRoute(VaadinIcon.LEVEL_DOWN_BOLD, Constant.TITLE_PRESTAMO_EXPEDIENTE,OperationView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         archivoMenu.addItem(createRoute(VaadinIcon.LEVEL_UP_BOLD, Constant.TITLE_DEVOLUCION, RoleView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         archivoMenu.addItem(createRoute(VaadinIcon.LINES_LIST, Constant.TITLE_INDICES_ARCHIVO, OperationView.class));

      if ( archivoMenu.getItems().size() > 0)
         mainMenu.add(archivo);

   }//createArchiveMenu

/*
   private void createLink(VaadinIcon icon, String title, Class<? extends Component> viewClass, VerticalLayout mainMenu)
   {
      if (SecurityUtils.isAccessGranted(viewClass))
      {
         RouterLink link = populateLink(new RouterLink(null, viewClass), icon, title);
         link.setHighlightCondition(HighlightConditions.sameLocation());
         mainMenu.add(link);
      }
   }//createLink
*/

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
      PAGE_CLIENTES                                 = "cliente";
         PAGE_TENANTS                               = "tenants";
      PAGE_SEGURIDAD                                = "seguridad";
         PAGE_OBJETOS                               = "objetos_a-proteger";
         PAGE_ROLES                                 = "roles";
         PAGE_PERMISOS_EJECUCION                    = "permisos_ejecucion";
         PAGE_PERMISOS_ACCESO                       = "permisos_acceso";
      PAGE_ADMINISTRACION                           = "administracion";
         PAGE_PARAMETROS                            = "parametros";
         PAGE_USUARIOS                              = "usuario";
         PAGE_GRUPOS_USUARIOS                       = "grupos_usuarios";
      PAGE_CLASIFICACION                            = "clasificacion";
         PAGE_FONDOS                                = "fondo";
         PAGE_OFICINAS                              = "oficina";
         PAGE_SERIES                                = "serie";
         PAGE_SUBSERIES                             = "subserie";
         PAGE_TIPOS_DOCUMENTALES                    = "tipos_documentales";
      PAGE_ADMIN_EXPEDIENTES                        = "admin_expediente";
         PAGE_EXPEDIENTES                           = "expediente";
         PAGE_SUBEXPEDIENTES                        = "sub_expediente";
         PAGE_VOLUMENES                             = "volumen";
         PAGE_INDICE                                = "indice";
         PAGE_EXPORTACION                           = "exporta_expedientes";
         PAGE_IMPORTACION                           = "importa_expedientes";
         PAGE_COPIA_DOCUMENTOS                      = "copia_documento";
         PAGE_TRANSFER_DOCUMENTOS                   = "transferencia_documento";
      PAGE_TRAMITE                                  = "tramite";
         PAGE_BANDEJA                               = "bandeja";
         PAGE_CLASIFICACION_DOCUMENTOS              = "clasifica_documento";
         PAGE_RETORNO                               = "retorno";
         PAGE_RE_ENVIO                              = "reEnvio";
         PAGE_BORRADORES                            = "borrador";
         PAGE_FIRMA                                 = "firma_documento";
         PAGE_ENVIO                                 = "envio_interno";
      PAGE_RECEPCION                                = "recepcion";
         PAGE_RECEPCION_DOCUMENTOS                  = "recibe_doc";
         PAGE_RECEPCION_E_MAIL                      = "recibe_email";
         PAGE_DIGITALIZACION                        = "digitalizacion";
         PAGE_DIRECCIONAMIENTO                      = "enruta_documento";
      PAGE_CORRESPONDENCIA_EXTERNA                  = "correspondencia_externa";
         PAGE_REGISTRO_ENVIOS                       = "consolida_envios";
         PAGE_ENVIO_EXTERNO                         = "envio_externo";
         PAGE_CONFIRMACION_ENVIO                    = "confirmacion_envio";
      PAGE_CONSULTA                                 = "consulta";
         PAGE_CONSULTA_DOCUMENTOS                   = "consulta_documentos";
            PAGE_CONSULTA_LIBRE                     = "consulta_documentos_libre";
            PAGE_CONSULTA_METADATOS                 = "consulta_documentos_metadatos";
         PAGE_CONSULTA_EXPEDIENTES                  = "consulta_expedientes";
            PAGE_CONSULTA_EXPEDIENTES_LIBRE         = "consulta_expedientes_libre";
            PAGE_CONSULTA_EXPEDIENTES_METADATOS     = "consulta_expedientes_metadatos";
            PAGE_CONSULTA_EXPEDIENTES_CLASIFICACION = "consulta_expedientes_clasificacion";
      PAGE_PROCESOS                                 = "proceso";
         PAGE_EJECUCION_PROCESO                     = "ejecucion_proceso";
         PAGE_DEFINICION_PROCESO                    = "definicion_proceso";
      PAGE_ARCHIVO                                  = "archivo";
         PAGE_LOCALES                               = "local";
         PAGE_TRANSFERENCIA                         = "prepara_transferencia_archivo";
         PAGE_RECIBO_TRANSFERENCIA                  = "recibe_transferencia_archivo";
         PAGE_LOCALIZACION                          = "localizacion";
         PAGE_PRESTAMO                              = "prestamos";
            PAGE_PRESTAMO_EXPEDIENTE                = "prestamo_expediente";
            PAGE_DEVOLUCION                         = "retorno_expediente";
         PAGE_INDICES_ARCHIVO                       = "indice_archivo";


      TITLE_CLIENTES                                 = "Clientes";
         TITLE_TENANTS                               = "Tenants";
      TITLE_SEGURIDAD                                = "Seguridad";
         TITLE_OBJETOS                               = "Objetos a proteger";
         TITLE_ROLES                                 = "Roles";
         TITLE_PERMISOS_EJECUCION                    = "Permisos de ejecucien";
         TITLE_PERMISOS_ACCESO                       = "Permisos de acceso";
      TITLE_ADMINISTRACION                           = "Administracien";
         TITLE_PARAMETROS                            = "Paremetros";
         TITLE_USUARIOS                              = "Usuarios";
         TITLE_GRUPOS_USUARIOS                       = "Grupos de usuarios";
      TITLE_CLASIFICACION                            = "Clasificacien";
         TITLE_FONDOS                                = "Fondos";
         TITLE_OFICINAS                              = "Oficinas";
         TITLE_SERIES                                = "Series";
         TITLE_SUBSERIES                             = "Subseries";
         TITLE_TIPOS_DOCUMENTALES                    = "Tipos documentales";
      TITLE_ADMIN_EXPEDIENTES                        = "Administracien expedientes";
         TITLE_EXPEDIENTES                           = "Expedientes";
         TITLE_SUBEXPEDIENTES                        = "Sub-expedientes";
         TITLE_VOLUMENES                             = "Volemenes";
         TITLE_INDICE                                = "endice de expedientes";
         TITLE_EXPORTACION                           = "Exportacien de expedientes";
         TITLE_IMPORTACION                           = "Importacien de expedientes";
         TITLE_COPIA_DOCUMENTOS                      = "Copia documento a otro expediente";
         TITLE_TRANSER_DOCUMENTOS                    = "Transferencia de documento a otro expediente";
      TITLE_TRAMITE                                  = "Tramite de documentos";
         TITLE_BANDEJA                               = "Bandeja personal";
         TITLE_CLASIFICACION_DOCUMENTOS              = "Clasificacien de documento";
         TITLE_RETORNO                               = "Devolucien de documento";
         TITLE_RE_ENVIO                              = "Re-enveo de documento";
         TITLE_BORRADORES                            = "Carga borrador de documento";
         TITLE_FIRMA                                 = "Firma de documento";
         TITLE_ENVIO                                 = "Enveo de documento";
      TITLE_RECEPCION                                = "Recepcien de documentos";
         TITLE_RECEPCION_DOCUMENTOS                  = "Recepcien en ventanilla";
         TITLE_RECEPCION_E_MAIL                      = "Recepcien correo electrenico";
         TITLE_DIGITALIZACION                        = "Digitalizacien";
         TITLE_DIRECCIONAMIENTO                      = "Enrutamiento de documentos";
      TITLE_CORRESPONDENCIA_EXTERNA                  = "Correspondencia externa";
         TITLE_REGISTRO_ENVIOS                       = "Consolidacien enveos externos";
         TITLE_ENVIO_EXTERNO                         = "Prepara planillas enveo";
         TITLE_CONFIRMACIeN_ENVIO                    = "Confirmacien de recepcien";
      TITLE_CONSULTA                                 = "Consulta";
         TITLE_DOCUMENTOS                            = "Consulta de documentos";
            TITLE_CONSULTA_LIBRE                     = "Consulta libre documentos";
            TITLE_CONSULTA_METADATOS                 = "Consulta documentos segen metadatos";
         TITLE_CONSULTA_EXPEDIENTES                  = "Consulta de expedientes";
            TITLE_CONSULTA_EXPEDIENTES_LIBRE         = "Consulta de expedientes segen texto libre";
            TITLE_CONSULTA_EXPEDIENTES_METADATOS     = "Consulta de expedientes segen metadatos";
            TITLE_CONSULTA_EXPEDIENTES_CLASIFICACION = "Consulta de expedientes segen clasificacien";
      TITLE_PROCESOS                                 = "Procesos";
         TITLE_EJECUCION_PROCESO                     = "Ejecucien de proceso";
         TITLE_DEFINICION_PROCESO                    = "Definicien de proceso";
      TITLE_ARCHIVO                                  = "Archivo";
         TITLE_LOCALES                               = "Locales";
         TITLE_TRANSFERENCIA                         = "Preparacien de transferencia";
         TITLE_RECIBO_TRANSFERENCIA                  = "Recepcien de transferencia";
         TITLE_LOCALIZACION                          = "Localizacien de documentos";
         TITLE_PRESTAMO                              = "Prestamos";
            TITLE_PRESTAMO_EXPEDIENTE                = "Prestamo de expedientes";
            TITLE_DEVOLUCION                         = "Retorno de expediente";
         TITLE_INDICES_ARCHIVO                       = "Indice de archivo";

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
      tabs.add(createTab(VaadinIcon.EDIT, Constant.TITLE_STOREFRONT, StorefrontView.class));
      tabs.add(createTab(VaadinIcon.CLOCK,Constant.TITLE_DASHBOARD, DashboardView.class));

      if (SecurityUtils.isAccessGranted(UsersView.class))
         tabs.add(createTab(VaadinIcon.KEY,Constant.TITLE_ADMINISTRATION, UsersView.class));

      if (SecurityUtils.isAccessGranted(ProductsView.class))
         tabs.add(createTab(VaadinIcon.CALENDAR, Constant.TITLE_PRODUCTS, ProductsView.class));

      if (SecurityUtils.isAccessGranted(TenantsView.class))
         tabs.add(createTab(VaadinIcon.HOSPITAL, Constant.TITLE_TENANTS, TenantsView.class));

      if (SecurityUtils.isAccessGranted(OperationView.class))
         tabs.add(createTab(VaadinIcon.COG, Constant.TITLE_OBJECT_TO_PROTECT, OperationView.class));

      if (SecurityUtils.isAccessGranted(RoleView.class))
         tabs.add(createTab(VaadinIcon.ACADEMY_CAP, Constant.TITLE_ROLES, RoleView.class));

      if (SecurityUtils.isAccessGranted(UserGroupView.class))
         tabs.add(createTab(VaadinIcon.USERS, Constant.TITLE_USER_GROUPS, UserGroupView.class));

      if (SecurityUtils.isAccessGranted(SingleUserView.class))
         tabs.add(createTab(VaadinIcon.USER, Constant.TITLE_SINGLE_USERS, SingleUserView.class));

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
      final Anchor a = populateLink(new Anchor(), VaadinIcon.ARROW_RIGHT, Constant.TITLE_LOGOUT);
      a.setHref(contextPath + "/logout");
      return a;
   }//createLogoutLink

   private static <T extends HasComponents> T populateLink(T a, VaadinIcon icon, String title)
   {
      a.add(icon.create());
      if (title.equals(Constant.TITLE_ADMINISTRATION) )
         a.add(Constant.TITLE_USERS);
      else
         a.add(title);

      return a;
   }//populateLink

   /*
   Ejemplo de menu jerarquico usando ContextMenu

         ContextMenu contextMenu = new ContextMenu();

         Component target = createTargetComponent();
         contextMenu.setTarget(target);

         Label message = new Label("-");

         contextMenu.addItem("First menu item",
                 event -> message.setText("Clicked on the first item"));

         MenuItem parent = contextMenu.addItem("Parent item");
         SubMenu subMenu = parent.getSubMenu();

         subMenu.addItem("Second menu item",
                 event -> message.setText("Clicked on the second item"));

         subMenu = subMenu.addItem("Parent item").getSubMenu();
         subMenu.addItem("Third menu item",
                 event -> message.setText("Clicked on the third item"));
         add(target, message);
    */



}//MainView