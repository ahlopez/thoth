package com.f.thoth.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.f.thoth.backend.data.OrderState;
import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.Customer;
import com.f.thoth.backend.data.entity.HistoryItem;
import com.f.thoth.backend.data.entity.Order;
import com.f.thoth.backend.data.entity.OrderItem;
import com.f.thoth.backend.data.entity.PickupLocation;
import com.f.thoth.backend.data.entity.Product;
import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.classification.ClassificationClass;
import com.f.thoth.backend.data.gdoc.classification.ClassificationLevel;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Operation;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ClassificationClassRepository;
import com.f.thoth.backend.repositories.ClassificationLevelRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.OperationRepository;
import com.f.thoth.backend.repositories.OrderRepository;
import com.f.thoth.backend.repositories.PickupLocationRepository;
import com.f.thoth.backend.repositories.ProductRepository;
import com.f.thoth.backend.repositories.RoleRepository;
import com.f.thoth.backend.repositories.SchemaRepository;
import com.f.thoth.backend.repositories.TenantRepository;
import com.f.thoth.backend.repositories.UserRepository;
import com.f.thoth.backend.service.TenantService;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class DataGenerator implements HasLogger {

   private static final String[] FILLING = new String[] { "Strawberry", "Chocolate", "Blueberry", "Raspberry",
   "Vanilla" };
   private static final String[] TYPE = new String[] { "Cake", "Pastry", "Tart", "Muffin", "Biscuit", "Bread", "Bagel",
         "Bun", "Brownie", "Cookie", "Cracker", "Cheese Cake" };
   private static final String[] FIRST_NAME = new String[] { "Ori", "Amanda", "Octavia", "Laurel", "Lael", "Delilah",
         "Jason", "Skyler", "Arsenio", "Haley", "Lionel", "Sylvia", "Jessica", "Lester", "Ferdinand", "Elaine",
         "Griffin", "Kerry", "Dominique" };
   private static final String[] LAST_NAME = new String[] { "Carter", "Castro", "Rich", "Irwin", "Moore", "Hendricks",
         "Huber", "Patton", "Wilkinson", "Thornton", "Nunez", "Macias", "Gallegos", "Blevins", "Mejia", "Pickett",
         "Whitney", "Farmer", "Henry", "Chen", "Macias", "Rowland", "Pierce", "Cortez", "Noble", "Howard", "Nixon",
         "Mcbride", "Leblanc", "Russell", "Carver", "Benton", "Maldonado", "Lyons" };

   private final Random random = new Random(1L);

   private TenantService                 tenantService;
   private TenantRepository              tenantRepository;
   private RoleRepository                roleRepository;
   private OrderRepository               orderRepository;
   private UserRepository                userRepository;
   private ProductRepository             productRepository;
   private ObjectToProtectRepository     objectToProtectRepository;
   private OperationRepository           operationRepository;
   private PickupLocationRepository      pickupLocationRepository;
   private PasswordEncoder               passwordEncoder;
   private ClassificationClassRepository claseRepository;
   private ClassificationLevelRepository classificationLevelRepository;
   private SchemaRepository              schemaRepository;

   @Autowired
   public DataGenerator(TenantService tenantService, OrderRepository orderRepository, UserRepository userRepository,
         ProductRepository productRepository, PickupLocationRepository pickupLocationRepository,
         TenantRepository tenantRepository, RoleRepository roleRepository, OperationRepository operationRepository,
         ClassificationClassRepository claseRepository, ObjectToProtectRepository objectToProtectRepository,
         ClassificationLevelRepository classificationLevelRepository, SchemaRepository schemaRepository,
         PasswordEncoder passwordEncoder)
   {
      this.tenantService                 = tenantService;
      this.orderRepository               = orderRepository;
      this.userRepository                = userRepository;
      this.productRepository             = productRepository;
      this.pickupLocationRepository      = pickupLocationRepository;
      this.tenantRepository              = tenantRepository;
      this.roleRepository                = roleRepository;
      this.objectToProtectRepository     = objectToProtectRepository;
      this.operationRepository           = operationRepository;
      this.claseRepository               = claseRepository;
      this.classificationLevelRepository = classificationLevelRepository;
      this.schemaRepository              = schemaRepository;
      this.passwordEncoder               = passwordEncoder;

   }//DataGenerator

   @SuppressWarnings("unused")
   @PostConstruct
   public void loadData() {
      if (userRepository.count() != 0L)
      {
         getLogger().info("Using existing database");
         return;
      }

      getLogger().info("Generating demo data");

      getLogger().info("... generating tenants");
      ThothSession session = new ThothSession(tenantService);
      Tenant tenant1 = createTenant(tenantRepository, "FCN");
      ThothSession.setTenant(tenant1);

      Tenant tenant2 = createTenant(tenantRepository,"SEI");
      getLogger().info("... generating roles");
      com.f.thoth.backend.data.security.Role role1 = createRole(tenant1, "Gerente");
      com.f.thoth.backend.data.security.Role role2 = createRole(tenant1, "Admin");
      com.f.thoth.backend.data.security.Role role3 = createRole(tenant1, "Supervisor");
      com.f.thoth.backend.data.security.Role role4 = createRole(tenant1, "Operador");
      com.f.thoth.backend.data.security.Role role5 = createRole(tenant1, "Publico");

      tenant1.addRole(role1);
      tenant1.addRole(role2);
      tenant1.addRole(role3);
      tenant1.addRole(role4);
      tenant1.addRole(role5);

      com.f.thoth.backend.data.security.Role role6  = createRole(tenant2, "CEO");
      com.f.thoth.backend.data.security.Role role7  = createRole(tenant2, "Admin2");
      com.f.thoth.backend.data.security.Role role8  = createRole(tenant2, "CFO");
      com.f.thoth.backend.data.security.Role role9  = createRole(tenant2, "CIO");
      com.f.thoth.backend.data.security.Role role10 = createRole(tenant2, "COO");

      tenant2.addRole(role6);
      tenant2.addRole(role7);
      tenant2.addRole(role8);
      tenant2.addRole(role9);
      tenant2.addRole(role10);

      getLogger().info("... generating Objects to protect" );
      Operation obj01 = createOperation( tenant1, Constant.TITLE_CLIENTES                                , null );  // Clientes
      Operation obj02 = createOperation( tenant1,    Constant.TITLE_TENANTS                              , obj01);  // Fondo
      Operation obj03 = createOperation( tenant1, Constant.TITLE_SEGURIDAD                               , null );  // Seguridad
      Operation obj04 = createOperation( tenant1,    Constant.TITLE_OPERATIONS                           , obj03);  // Operaciones
      Operation obj05 = createOperation( tenant1,    Constant.TITLE_INFORMACION                          , obj03);  // Informacion
      Operation obj06 = createOperation( tenant1,    Constant.TITLE_ROLES                                , obj03);  // Roles
      Operation obj07 = createOperation( tenant1,    Constant.TITLE_PERMISOS_EJECUCION                   , obj03);  // Permisos de ejecucion
      Operation obj08 = createOperation( tenant1,    Constant.TITLE_PERMISOS_ACCESO                      , obj03);  // Permisos de acceso
      Operation obj09 = createOperation( tenant1, Constant.TITLE_ADMINISTRACION                          , null );  // Administracion
      Operation obj10 = createOperation( tenant1,    Constant.TITLE_PARAMETROS                           , obj09);  // Parametros
      Operation obj11 = createOperation( tenant1,    Constant.TITLE_USUARIOS                             , obj09);  // Usuarios
      Operation obj12 = createOperation( tenant1,    Constant.TITLE_GRUPOS_USUARIOS                      , obj09);  // Grupos de usuarios
      Operation obj13 = createOperation( tenant1, Constant.TITLE_CLASIFICACION                           , null );  // Clasificacion
      Operation obj14 = createOperation( tenant1,    Constant.TITLE_FONDOS                               , obj13);  // Fondos
      Operation obj15 = createOperation( tenant1,    Constant.TITLE_OFICINAS                             , obj13);  // Oficinas
      Operation obj16 = createOperation( tenant1,    Constant.TITLE_SERIES                               , obj13);  // Series
      Operation obj17 = createOperation( tenant1,    Constant.TITLE_SUBSERIES                            , obj13);  // Subseries
      Operation obj18 = createOperation( tenant1,    Constant.TITLE_TIPOS_DOCUMENTALES                   , obj13);  // Tipos documentales
      Operation obj19 = createOperation( tenant1, Constant.TITLE_ADMIN_EXPEDIENTES                       , null );  // Gestion expedientes
      Operation obj20 = createOperation( tenant1,    Constant.TITLE_EXPEDIENTES                          , obj19);  // Expedientes mayores
      Operation obj21 = createOperation( tenant1,    Constant.TITLE_SUBEXPEDIENTES                       , obj19);  // Sub-expedientes
      Operation obj22 = createOperation( tenant1,    Constant.TITLE_VOLUMENES                            , obj19);  // Volumenes
      Operation obj23 = createOperation( tenant1,    Constant.TITLE_INDICE                               , obj19);  // Indice de expedientes
      Operation obj24 = createOperation( tenant1,    Constant.TITLE_EXPORTACION                          , obj19);  // Exportacion de expedientes
      Operation obj25 = createOperation( tenant1,    Constant.TITLE_IMPORTACION                          , obj19);  // Importacion de expedientes
      Operation obj26 = createOperation( tenant1,    Constant.TITLE_COPIA_DOCUMENTOS                     , obj19);  // Copia documento a otro expediente
      Operation obj27 = createOperation( tenant1,    Constant.TITLE_TRANSER_DOCUMENTOS                   , obj19);  // Transferencia de documento a otro expediente
      Operation obj28 = createOperation( tenant1, Constant.TITLE_TRAMITE                                 , null );  // Tramite de documentos
      Operation obj29 = createOperation( tenant1,    Constant.TITLE_BANDEJA                              , obj28);  // Bandeja personal
      Operation obj30 = createOperation( tenant1,    Constant.TITLE_CLASIFICACION_DOCUMENTOS             , obj28);  // Clasificacion de documento
      Operation obj31 = createOperation( tenant1,    Constant.TITLE_RETORNO                              , obj28);  // Devolucion de documento
      Operation obj32 = createOperation( tenant1,    Constant.TITLE_RE_ENVIO                             , obj28);  // Re-envio de documento
      Operation obj33 = createOperation( tenant1,    Constant.TITLE_BORRADORES                           , obj28);  // Carga borrador de documento
      Operation obj34 = createOperation( tenant1,    Constant.TITLE_FIRMA                                , obj28);  // Firma de documento
      Operation obj35 = createOperation( tenant1,    Constant.TITLE_ENVIO                                , obj28);  // Ordena envio de documento
      Operation obj36 = createOperation( tenant1, Constant.TITLE_RECEPCION                               , null );  // Recepcion de documentos
      Operation obj37 = createOperation( tenant1,    Constant.TITLE_RECEPCION_DOCUMENTOS                 , obj36);  // Recepcion en ventanilla
      Operation obj38 = createOperation( tenant1,    Constant.TITLE_RECEPCION_E_MAIL                     , obj36);  // Recepcion correo electronico
      Operation obj39 = createOperation( tenant1,    Constant.TITLE_DIGITALIZACION                       , obj36);  // Digitalizacion
      Operation obj40 = createOperation( tenant1,    Constant.TITLE_DIRECCIONAMIENTO                     , obj36);  // Enrutamiento de documentos
      Operation obj41 = createOperation( tenant1, Constant.TITLE_CORRESPONDENCIA_EXTERNA                 , null );  // Envio de documentos
      Operation obj42 = createOperation( tenant1,    Constant.TITLE_REGISTRO_ENVIOS                      , obj41);  // Consolidacion envoos externos
      Operation obj43 = createOperation( tenant1,    Constant.TITLE_ENVIO_EXTERNO                        , obj41);  // Envia correspondencia externa
      Operation obj44 = createOperation( tenant1,    Constant.TITLE_CONFIRMACION_ENVIO                   , obj41);  // Confirmacion de recepcion
      Operation obj45 = createOperation( tenant1, Constant.TITLE_CONSULTA                                , null );  // Consulta
      Operation obj46 = createOperation( tenant1,    Constant.TITLE_DOCUMENTOS                           , obj45);  // Consulta de documentos
      Operation obj47 = createOperation( tenant1,       Constant.TITLE_CONSULTA_LIBRE                    , obj46);  // Consulta libre documentos
      Operation obj48 = createOperation( tenant1,       Constant.TITLE_CONSULTA_METADATOS                , obj46);  // Consulta documentos segun metadatos
      Operation obj49 = createOperation( tenant1,    Constant.TITLE_CONSULTA_EXPEDIENTES                 , obj45);  // Consulta de expedientes
      Operation obj50 = createOperation( tenant1,       Constant.TITLE_CONSULTA_EXPEDIENTES_LIBRE        , obj49);  // Consulta de expedientes segun texto libre
      Operation obj51 = createOperation( tenant1,       Constant.TITLE_CONSULTA_EXPEDIENTES_METADATOS    , obj49);  // Consulta de expedientes segun metadatos
      Operation obj52 = createOperation( tenant1,       Constant.TITLE_CONSULTA_EXPEDIENTES_CLASIFICACION, obj49);  // Consulta de expedientes segun clasificacion
      Operation obj53 = createOperation( tenant1, Constant.TITLE_PROCESOS                                , null );  // Procesos
      Operation obj54 = createOperation( tenant1,    Constant.TITLE_EJECUCION_PROCESO                    , obj53);  // Ejecucion de proceso
      Operation obj55 = createOperation( tenant1,    Constant.TITLE_DEFINICION_PROCESO                   , obj53);  // Definicion de proceso
      Operation obj56 = createOperation( tenant1, Constant.TITLE_ARCHIVO                                 , null );  // Archivo
      Operation obj57 = createOperation( tenant1,    Constant.TITLE_LOCALES                              , obj56);  // Locales
      Operation obj58 = createOperation( tenant1,    Constant.TITLE_TRANSFERENCIA                        , obj56);  // Preparacion de transferencia
      Operation obj59 = createOperation( tenant1,    Constant.TITLE_RECIBO_TRANSFERENCIA                 , obj56);  // Recepcion de transferencia
      Operation obj60 = createOperation( tenant1,    Constant.TITLE_LOCALIZACION                         , obj56);  // Localizacion de documentos
      Operation obj61 = createOperation( tenant1,    Constant.TITLE_PRESTAMO                             , obj56);  // Prestamos
      Operation obj62 = createOperation( tenant1,       Constant.TITLE_PRESTAMO_EXPEDIENTE               , obj61);  // Prestamo de expedientes
      Operation obj63 = createOperation( tenant1,       Constant.TITLE_DEVOLUCION                        , obj61);  // Retorno de expediente
      Operation obj64 = createOperation( tenant1,    Constant.TITLE_INDICES_ARCHIVO                      , obj56);  // Indice de archivo

      ClassificationClass clase001 = createClass( tenant1, Constant.TITLE_SEDE_CORPORATIVA                                , 0, null);      //   Sede Corporativa
      ClassificationClass clase002 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_GERENCIA_GENERAL                  , 1, clase001);  //   Corporativa, Gerencia_general
      ClassificationClass clase003 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_ACTAS                             , 2, clase002);  //   Corporativa, Actas
      ClassificationClass clase004 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , 3, clase003);  //   Corporativa, Actas_junta_directiva
      ClassificationClass clase005 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_GERENCIA        , 3, clase003);  //   Corporativa, Actas_comit�_gerencia
      ClassificationClass clase006 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_FINANCIERO      , 3, clase003);  //   Corporativa, Actas_comit�_financiero
      ClassificationClass clase007 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , 3, clase003);  //   Corporativa, Actas_comit�_administrativo
      ClassificationClass clase008 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_OPERACIONES     , 3, clase003);  //   Corporativa, Actas_comit�_operaciones
      ClassificationClass clase010 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_PLANES                            , 2, clase001);  //   Corporativa, Planes
      ClassificationClass clase011 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_PLAN_OPERATIVO               , 3, clase010);  //   Corporativa, Plan_operativo
      ClassificationClass clase012 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_PLAN_FINANCIERO              , 3, clase010);  //   Corporativa, Plan_financiero
      ClassificationClass clase013 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_PRESUPUESTO                  , 3, clase010);  //   Corporativa, Presupuesto
      ClassificationClass clase014 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_OPERACIONES                       , 1, clase001);  //   Corporativa, Subgerencia de Operaciones
      ClassificationClass clase015 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_ACTAS_OPERACIONES                 , 3, clase014);  //   Corporativa, Actas Operaciones
      ClassificationClass clase016 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , 3, clase015);  //   Corporativa, Actas Comit� calidad
      ClassificationClass clase017 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , 3, clase015);  //   Corporativa, Actas Comit� planeaci�n
      ClassificationClass clase018 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_CONTRATOS                         , 2, clase014);  //   Corporativa, Contratos
      ClassificationClass clase019 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_OPER_CONTRATOS_OPERACION    , 3, clase018);  //   Corporativa, Contratos de Operaci�n
      ClassificationClass clase020 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_OPER_CONTRATOS_INVERSION    , 3, clase018);  //   Corporativa, Contratos de Inversi�n
      ClassificationClass clase021 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_FINANCIERA                        , 1, clase001);  //   Corporativa, Subgerencia Financiera
      ClassificationClass clase022 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_PRESUPUESTO                       , 2, clase021);  //   Corporativa, Presupuesto
      ClassificationClass clase023 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_PLANEACION_PPTAL        , 3, clase022);  //   Corporativa, Planeaci�n Presupuestal
      ClassificationClass clase024 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_EJECUCION_PPTAL         , 3, clase022);  //   Corporativa, Ejecuci�n Presupuestal
      ClassificationClass clase025 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_TESORERIA                         , 2, clase021);  //   Corporativa, Tesorer�a
      ClassificationClass clase026 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_PAGADURIA               , 3, clase025);  //   Corporativa, Pagadur�a
      ClassificationClass clase027 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_INVERSIONES             , 3, clase025);  //   Corporativa, Inversiones
      ClassificationClass clase028 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_CONTABILIDAD                      , 2, clase021);  //   Corporativa, Contabilidad
      ClassificationClass clase029 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , 3, clase028);  //   Corporativa, Estados Financieros
      ClassificationClass clase030 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_LIBROS_CONTABLES        , 3, clase028);  //   Corporativa, Libros contables
      ClassificationClass clase031 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_PERSONAL                          , 1, clase001);  //   Corporativa, Subgerencia de Personal
      ClassificationClass clase032 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_HOJAS_DE_VIDA                     , 2, clase031);  //   Corporativa, Hojas de vida
      ClassificationClass clase033 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_CANDIDATOS              , 3, clase032);  //   Corporativa, Candidatos de personal
      ClassificationClass clase034 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_PERSONAL_ACTIVO         , 3, clase032);  //   Corporativa, Personal activo
      ClassificationClass clase035 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_PENSIONADOS             , 3, clase032);  //   Corporativa, Pensionados
      ClassificationClass clase036 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_SANCIONES                         , 2, clase031);  //   Corporativa, Sanciones de personal
      ClassificationClass clase037 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_INVESTIGACIONES         , 3, clase036);  //   Corporativa, Investigaciones disciplinarias
      ClassificationClass clase038 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_FALLOS_DE_PERSONAL      , 3, clase036);  //   Corporativa, Fallos de personal
      ClassificationClass clase039 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_EVALUACIONES                      , 2, clase031);  //   Corporativa, Evaluaciones de personal
      ClassificationClass clase040 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_DESEMPENO               , 3, clase039);  //   Corporativa, Evaluaciones de desempe�o
      ClassificationClass clase041 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_JURIDICA                          , 1, clase001);  //   Corporativa, Subgerencia Jur�dica
      ClassificationClass clase042 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_PROCESOS                          , 2, clase041);  //   Corporativa, Procesos jur�dicos
      ClassificationClass clase043 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_JUR_DEMANDAS                , 3, clase042);  //   Corporativa, Demandas en curso
      ClassificationClass clase044 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_JUR_FALLOS_JUDICIALES       , 3, clase042);  //   Corporativa, Demandas en curso
      ClassificationClass clase045 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_ADMINISTRACION                    , 1, clase001);  //   Corporativa, Subgerencia Administrativa
      ClassificationClass clase046 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_ACTIVOS_FIJOS                     , 2, clase045);  //   Corporativa, Activos fijos
      ClassificationClass clase047 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_ADM_EDIFICACIONES           , 3, clase046);  //   Corporativa, Edificaciones
      ClassificationClass clase048 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_ADM_SERVICIOS               , 3, clase046);  //   Corporativa, Servicios p�blicos
      ClassificationClass clase049 = createClass( tenant1, Constant.TITLE_SEDE_BOGOTA                                     , 0, null);      //   Sede Bogot�
      ClassificationClass clase050 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_SUBGERENCIA                       , 1, clase001);  //   Gerencia Bogot�
      ClassificationClass clase051 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_ACTAS                             , 2, clase050);  //   Bogot�, Actas
      ClassificationClass clase052 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , 3, clase051);  //   Bogot�, Actas_junta_directiva
      ClassificationClass clase053 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_GERENCIA        , 3, clase051);  //   Bogot�, Actas_comit�_gerencia
      ClassificationClass clase054 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_FINANCIERO      , 3, clase051);  //   Bogot�, Actas_comit�_financiero
      ClassificationClass clase055 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , 3, clase051);  //   Bogot�, Actas_comit�_administrativo
      ClassificationClass clase056 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_OPERACIONES     , 3, clase051);  //   Bogot�, Actas_comit�_operaciones
      ClassificationClass clase058 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_PLANES                            , 2, clase050);  //   Bogot�, Planes
      ClassificationClass clase059 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_PLAN_OPERATIVO               , 3, clase058);  //   Bogot�, Plan_operativo
      ClassificationClass clase060 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_PLAN_FINANCIERO              , 3, clase058);  //   Bogot�, Plan_financiero
      ClassificationClass clase061 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_PRESUPUESTO                  , 3, clase058);  //   Bogot�, Presupuesto
      ClassificationClass clase062 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_OPERACIONES                       , 1, clase001);  //   Bogot�, Subgerencia de Operaciones
      ClassificationClass clase063 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_ACTAS_OPERACIONES                 , 2, clase062);  //   Bogot�, Actas Operaciones
      ClassificationClass clase064 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , 3, clase063);  //   Bogot�, Actas Comit� calidad
      ClassificationClass clase065 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , 3, clase063);  //   Bogot�, Actas Comit� planeaci�n
      ClassificationClass clase066 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_CONTRATOS                         , 2, clase062);  //   Bogot�, Contratos
      ClassificationClass clase067 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_OPER_CONTRATOS_OPERACION    , 3, clase066);  //   Bogot�, Contratos de Operaci�n
      ClassificationClass clase068 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_OPER_CONTRATOS_INVERSION    , 3, clase066);  //   Bogot�, Contratos de Inversi�n
      ClassificationClass clase069 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_FINANCIERA                        , 1, clase001);  //   Bogot�, Subgerencia Financiera
      ClassificationClass clase070 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_PRESUPUESTO                       , 2, clase069);  //   Bogot�, Presupuesto
      ClassificationClass clase071 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_PLANEACION_PPTAL        , 3, clase070);  //   Bogot�, Planeaci�n Presupuestal
      ClassificationClass clase072 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_EJECUCION_PPTAL         , 3, clase070);  //   Bogot�, Ejecuci�n Presupuestal
      ClassificationClass clase073 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_TESORERIA                         , 2, clase069);  //   Bogot�, Tesorer�a
      ClassificationClass clase074 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_PAGADURIA               , 3, clase073);  //   Bogot�, Pagadur�a
      ClassificationClass clase075 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_INVERSIONES             , 3, clase073);  //   Bogot�, Inversiones
      ClassificationClass clase076 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_CONTABILIDAD                      , 2, clase069);  //   Bogot�, Contabilidad
      ClassificationClass clase077 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , 3, clase076);  //   Bogot�, Estados Financieros
      ClassificationClass clase078 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_LIBROS_CONTABLES        , 3, clase076);  //   Bogot�, Libros contables
      ClassificationClass clase079 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_PERSONAL                          , 1, clase001);  //   Bogot�, Subgerencia de Personal
      ClassificationClass clase080 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_HOJAS_DE_VIDA                     , 2, clase079);  //   Bogot�, Hojas de vida
      ClassificationClass clase081 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_CANDIDATOS              , 3, clase080);  //   Bogot�, Candidatos de personal
      ClassificationClass clase082 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_PERSONAL_ACTIVO         , 3, clase080);  //   Bogot�, Personal activo
      ClassificationClass clase083 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_PENSIONADOS             , 3, clase080);  //   Bogot�, Pensionados
      ClassificationClass clase084 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_SANCIONES                         , 2, clase079);  //   Bogot�, Sanciones de personal
      ClassificationClass clase085 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_INVESTIGACIONES         , 3, clase084);  //   Bogot�, Investigaciones disciplinarias
      ClassificationClass clase086 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_FALLOS_DE_PERSONAL      , 3, clase084);  //   Bogot�, Fallos de personal
      ClassificationClass clase087 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_EVALUACIONES                      , 2, clase079);  //   Bogot�, Evaluaciones de personal
      ClassificationClass clase088 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_DESEMPENO               , 3, clase087);  //   Bogot�, Evaluaciones de desempe�o
      ClassificationClass clase089 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_JURIDICA                          , 1, clase001);  //   Bogot�, Subgerencia Jur�dica
      ClassificationClass clase090 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_PROCESOS                          , 2, clase089);  //   Bogot�, Procesos jur�dicos
      ClassificationClass clase091 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_JUR_DEMANDAS                , 3, clase090);  //   Bogot�, Demandas en curso
      ClassificationClass clase092 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_JUR_FALLOS_JUDICIALES       , 3, clase090);  //   Bogot�, Demandas en curso
      ClassificationClass clase093 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_ADMINISTRACION                    , 1, clase001);  //   Bogot�, Subgerencia Administrativa
      ClassificationClass clase094 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_ACTIVOS_FIJOS                     , 2, clase093);  //   Bogot�, Activos fijos
      ClassificationClass clase095 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_ADM_EDIFICACIONES           , 3, clase094);  //   Bogot�, Edificaciones
      ClassificationClass clase096 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_ADM_SERVICIOS               , 3, clase094);  //   Bogot�, Servicios p�blicos
      ClassificationClass clase097 = createClass( tenant1, Constant.TITLE_SEDE_MEDELLIN                                   , 0, null);      //   Sede Medell�n
      ClassificationClass clase098 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_SUBGERENCIA                       , 1, clase001);  //   Gerencia Medell�n
      ClassificationClass clase099 = createClass( tenant1,     Constant.TITLE_MED_SERIE_ACTAS                             , 2, clase098);  //   Medell�n, Actas
      ClassificationClass clase100 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , 3, clase099);  //   Medell�n, Actas_junta_directiva
      ClassificationClass clase101 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_GERENCIA        , 3, clase099);  //   Medell�n, Actas_comit�_gerencia
      ClassificationClass clase102 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_FINANCIERO      , 3, clase099);  //   Medell�n, Actas_comit�_financiero
      ClassificationClass clase103 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , 3, clase099);  //   Medell�n, Actas_comit�_administrativo
      ClassificationClass clase104 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_OPERACIONES     , 3, clase099);  //   Medell�n, Actas_comit�_operaciones
      ClassificationClass clase106 = createClass( tenant1,     Constant.TITLE_MED_SERIE_PLANES                            , 2, clase098);  //   Medell�n, Planes
      ClassificationClass clase107 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_PLAN_OPERATIVO               , 3, clase106);  //   Medell�n, Plan_operativo
      ClassificationClass clase108 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_PLAN_FINANCIERO              , 3, clase106);  //   Medell�n, Plan_financiero
      ClassificationClass clase109 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_PRESUPUESTO                  , 3, clase106);  //   Medell�n, Presupuesto
      ClassificationClass clase110 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_OPERACIONES                       , 1, clase001);  //   Medell�n, Subgerencia de Operaciones
      ClassificationClass clase111 = createClass( tenant1,     Constant.TITLE_MED_SERIE_ACTAS_OPERACIONES                 , 2, clase110);  //   Medell�n, Actas Operaciones
      ClassificationClass clase112 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , 3, clase111);  //   Medell�n, Actas Comit� calidad
      ClassificationClass clase113 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , 3, clase111);  //   Medell�n, Actas Comit� planeaci�n
      ClassificationClass clase114 = createClass( tenant1,     Constant.TITLE_MED_SERIE_CONTRATOS                         , 2, clase110);  //   Medell�n, Contratos
      ClassificationClass clase115 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_OPER_CONTRATOS_OPERACION    , 3, clase114);  //   Medell�n, Contratos de Operaci�n
      ClassificationClass clase116 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_OPER_CONTRATOS_INVERSION    , 3, clase114);  //   Medell�n, Contratos de Inversi�n
      ClassificationClass clase117 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_FINANCIERA                        , 1, clase001);  //   Medell�n, Subgerencia Financiera
      ClassificationClass clase118 = createClass( tenant1,     Constant.TITLE_MED_SERIE_PRESUPUESTO                       , 2, clase117);  //   Medell�n, Presupuesto
      ClassificationClass clase119 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_PLANEACION_PPTAL        , 3, clase118);  //   Medell�n, Planeaci�n Presupuestal
      ClassificationClass clase120 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_EJECUCION_PPTAL         , 3, clase118);  //   Medell�n, Ejecuci�n Presupuestal
      ClassificationClass clase121 = createClass( tenant1,     Constant.TITLE_MED_SERIE_TESORERIA                         , 2, clase117);  //   Medell�n, Tesorer�a
      ClassificationClass clase122 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_PAGADURIA               , 3, clase121);  //   Medell�n, Pagadur�a
      ClassificationClass clase123 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_INVERSIONES             , 3, clase121);  //   Medell�n, Inversiones
      ClassificationClass clase124 = createClass( tenant1,     Constant.TITLE_MED_SERIE_CONTABILIDAD                      , 2, clase117);  //   Medell�n, Contabilidad
      ClassificationClass clase125 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , 3, clase124);  //   Medell�n, Estados Financieros
      ClassificationClass clase126 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_LIBROS_CONTABLES        , 3, clase124);  //   Medell�n, Libros contables
      ClassificationClass clase127 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_PERSONAL                          , 1, clase001);  //   Medell�n, Subgerencia de Personal
      ClassificationClass clase128 = createClass( tenant1,     Constant.TITLE_MED_SERIE_HOJAS_DE_VIDA                     , 2, clase127);  //   Medell�n, Hojas de vida
      ClassificationClass clase129 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_CANDIDATOS              , 3, clase128);  //   Medell�n, Candidatos de personal
      ClassificationClass clase130 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_PERSONAL_ACTIVO         , 3, clase128);  //   Medell�n, Personal activo
      ClassificationClass clase131 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_PENSIONADOS             , 3, clase128);  //   Medell�n, Pensionados
      ClassificationClass clase132 = createClass( tenant1,     Constant.TITLE_MED_SERIE_SANCIONES                         , 2, clase127);  //   Medell�n, Sanciones de personal
      ClassificationClass clase133 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_INVESTIGACIONES         , 3, clase132);  //   Medell�n, Investigaciones disciplinarias
      ClassificationClass clase134 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_FALLOS_DE_PERSONAL      , 3, clase132);  //   Medell�n, Fallos de personal
      ClassificationClass clase135 = createClass( tenant1,     Constant.TITLE_MED_SERIE_EVALUACIONES                      , 2, clase127);  //   Medell�n, Evaluaciones de personal
      ClassificationClass clase136 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_DESEMPENO               , 3, clase135);  //   Medell�n, Evaluaciones de desempe�o
      ClassificationClass clase137 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_JURIDICA                          , 1, clase001);  //   Medell�n, Subgerencia Jur�dica
      ClassificationClass clase138 = createClass( tenant1,     Constant.TITLE_MED_SERIE_PROCESOS                          , 2, clase137);  //   Medell�n, Procesos jur�dicos
      ClassificationClass clase139 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_JUR_DEMANDAS                , 3, clase138);  //   Medell�n, Demandas en curso
      ClassificationClass clase140 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_JUR_FALLOS_JUDICIALES       , 3, clase138);  //   Medell�n, Demandas en curso
      ClassificationClass clase141 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_ADMINISTRACION                    , 1, clase001);  //   Medell�n, Subgerencia Administrativa
      ClassificationClass clase142 = createClass( tenant1,     Constant.TITLE_MED_SERIE_ACTIVOS_FIJOS                     , 2, clase141);  //   Medell�n, Activos fijos
      ClassificationClass clase143 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_ADM_EDIFICACIONES           , 3, clase142);  //   Medell�n, Edificaciones
      ClassificationClass clase144 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_ADM_SERVICIOS               , 3, clase142);  //   Medell�n, Servicios p�blicos
      ClassificationClass clase145 = createClass( tenant1, Constant.TITLE_SEDE_CALI                                       , 0, null);      //   Sede Cali
      ClassificationClass clase146 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_SUBGERENCIA                       , 1, clase001);  //   Gerencia Cali
      ClassificationClass clase147 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_ACTAS                             , 2, clase146);  //   Cali, Actas
      ClassificationClass clase148 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , 3, clase147);  //   Cali, Actas_junta_directiva
      ClassificationClass clase149 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_GERENCIA        , 3, clase147);  //   Cali, Actas_comit�_gerencia
      ClassificationClass clase150 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_FINANCIERO      , 3, clase147);  //   Cali, Actas_comit�_financiero
      ClassificationClass clase151 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , 3, clase147);  //   Cali, Actas_comit�_administrativo
      ClassificationClass clase152 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_OPERACIONES     , 3, clase147);  //   Cali, Actas_comit�_operaciones
      ClassificationClass clase154 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_PLANES                            , 2, clase146);  //   Cali, Planes
      ClassificationClass clase155 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_PLAN_OPERATIVO               , 3, clase154);  //   Cali, Plan_operativo
      ClassificationClass clase156 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_PLAN_FINANCIERO              , 3, clase154);  //   Cali, Plan_financiero
      ClassificationClass clase157 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_PRESUPUESTO                  , 3, clase154);  //   Cali, Presupuesto
      ClassificationClass clase158 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_OPERACIONES                       , 1, clase001);  //   Cali, Subgerencia de Operaciones
      ClassificationClass clase159 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_ACTAS_OPERACIONES                 , 2, clase158);  //   Cali, Actas Operaciones
      ClassificationClass clase160 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , 3, clase159);  //   Cali, Actas Comit� calidad
      ClassificationClass clase161 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , 3, clase159);  //   Cali, Actas Comit� planeaci�n
      ClassificationClass clase162 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_CONTRATOS                         , 2, clase158);  //   Cali, Contratos
      ClassificationClass clase163 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_OPER_CONTRATOS_OPERACION    , 3, clase162);  //   Cali, Contratos de Operaci�n
      ClassificationClass clase164 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_OPER_CONTRATOS_INVERSION    , 3, clase162);  //   Cali, Contratos de Inversi�n
      ClassificationClass clase165 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_FINANCIERA                        , 1, clase001);  //   Cali, Subgerencia Financiera
      ClassificationClass clase166 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_PRESUPUESTO                       , 2, clase165);  //   Cali, Presupuesto
      ClassificationClass clase167 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_PLANEACION_PPTAL        , 3, clase166);  //   Cali, Planeaci�n Presupuestal
      ClassificationClass clase168 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_EJECUCION_PPTAL         , 3, clase166);  //   Cali, Ejecuci�n Presupuestal
      ClassificationClass clase169 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_TESORERIA                         , 2, clase165);  //   Cali, Tesorer�a
      ClassificationClass clase170 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_PAGADURIA               , 3, clase169);  //   Cali, Pagadur�a
      ClassificationClass clase171 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_INVERSIONES             , 3, clase169);  //   Cali, Inversiones
      ClassificationClass clase172 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_CONTABILIDAD                      , 2, clase165);  //   Cali, Contabilidad
      ClassificationClass clase173 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , 3, clase172);  //   Cali, Estados Financieros
      ClassificationClass clase174 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_LIBROS_CONTABLES        , 3, clase172);  //   Cali, Libros contables
      ClassificationClass clase175 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_PERSONAL                          , 1, clase001);  //   Cali, Subgerencia de Personal
      ClassificationClass clase176 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_HOJAS_DE_VIDA                     , 2, clase175);  //   Cali, Hojas de vida
      ClassificationClass clase177 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_CANDIDATOS              , 3, clase176);  //   Cali, Candidatos de personal
      ClassificationClass clase178 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_PERSONAL_ACTIVO         , 3, clase176);  //   Cali, Personal activo
      ClassificationClass clase179 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_PENSIONADOS             , 3, clase176);  //   Cali, Pensionados
      ClassificationClass clase180 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_SANCIONES                         , 2, clase175);  //   Cali, Sanciones de personal
      ClassificationClass clase181 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_INVESTIGACIONES         , 3, clase180);  //   Cali, Investigaciones disciplinarias
      ClassificationClass clase182 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_FALLOS_DE_PERSONAL      , 3, clase180);  //   Cali, Fallos de personal
      ClassificationClass clase183 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_EVALUACIONES                      , 2, clase175);  //   Cali, Evaluaciones de personal
      ClassificationClass clase184 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_DESEMPENO               , 3, clase183);  //   Cali, Evaluaciones de desempe�o
      ClassificationClass clase185 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_JURIDICA                          , 1, clase001);  //   Cali, Subgerencia Jur�dica
      ClassificationClass clase186 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_PROCESOS                          , 2, clase185);  //   Cali, Procesos jur�dicos
      ClassificationClass clase187 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_JUR_DEMANDAS                , 3, clase186);  //   Cali, Demandas en curso
      ClassificationClass clase188 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_JUR_FALLOS_JUDICIALES       , 3, clase186);  //   Cali, Demandas en curso
      ClassificationClass clase189 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_ADMINISTRACION                    , 1, clase001);  //   Cali, Subgerencia Administrativa
      ClassificationClass clase190 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_ACTIVOS_FIJOS                     , 2, clase189);  //   Cali, Activos fijos
      ClassificationClass clase191 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_ADM_EDIFICACIONES           , 3, clase190);  //   Cali, Edificaciones
      ClassificationClass clase192 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_ADM_SERVICIOS               , 3, clase190);  //   Cali, Servicios p�blicos
      ClassificationClass clase193 = createClass( tenant1, Constant.TITLE_SEDE_BARRANQUILLA                               , 3, null);      //   Sede Barranquilla
      ClassificationClass clase194 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_SUBGERENCIA                       , 1, clase001);  //   Gerencia Barranquilla
      ClassificationClass clase195 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_ACTAS                             , 2, clase194);  //   Barranquilla, Actas
      ClassificationClass clase196 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , 3, clase195);  //   Barranquilla, Actas_junta_directiva
      ClassificationClass clase197 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_GERENCIA        , 3, clase195);  //   Barranquilla, Actas_comit�_gerencia
      ClassificationClass clase198 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_FINANCIERO      , 3, clase195);  //   Barranquilla, Actas_comit�_financiero
      ClassificationClass clase199 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , 3, clase195);  //   Barranquilla, Actas_comit�_administrativo
      ClassificationClass clase200 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_OPERACIONES     , 3, clase195);  //   Barranquilla, Actas_comit�_operaciones
      ClassificationClass clase202 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_PLANES                            , 2, clase194);  //   Barranquilla, Planes
      ClassificationClass clase203 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_PLAN_OPERATIVO               , 3, clase202);  //   Barranquilla, Plan_operativo
      ClassificationClass clase204 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_PLAN_FINANCIERO              , 3, clase202);  //   Barranquilla, Plan_financiero
      ClassificationClass clase205 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_PRESUPUESTO                  , 3, clase202);  //   Barranquilla, Presupuesto
      ClassificationClass clase206 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_OPERACIONES                       , 1, clase001);  //   Barranquilla, Subgerencia de Operaciones
      ClassificationClass clase207 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_ACTAS_OPERACIONES                 , 2, clase206);  //   Barranquilla, Actas Operaciones
      ClassificationClass clase208 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , 3, clase207);  //   Barranquilla, Actas Comit� calidad
      ClassificationClass clase209 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , 3, clase207);  //   Barranquilla, Actas Comit� planeaci�n
      ClassificationClass clase210 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_CONTRATOS                         , 2, clase206);  //   Barranquilla, Contratos
      ClassificationClass clase211 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_OPER_CONTRATOS_OPERACION    , 3, clase210);  //   Barranquilla, Contratos de Operaci�n
      ClassificationClass clase212 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_OPER_CONTRATOS_INVERSION    , 3, clase210);  //   Barranquilla, Contratos de Inversi�n
      ClassificationClass clase213 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_FINANCIERA                        , 1, clase001);  //   Barranquilla, Subgerencia Financiera
      ClassificationClass clase214 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_PRESUPUESTO                       , 2, clase213);  //   Barranquilla, Presupuesto
      ClassificationClass clase215 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_PLANEACION_PPTAL        , 3, clase214);  //   Barranquilla, Planeaci�n Presupuestal
      ClassificationClass clase216 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_EJECUCION_PPTAL         , 3, clase214);  //   Barranquilla, Ejecuci�n Presupuestal
      ClassificationClass clase217 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_TESORERIA                         , 2, clase213);  //   Barranquilla, Tesorer�a
      ClassificationClass clase218 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_PAGADURIA               , 3, clase217);  //   Barranquilla, Pagadur�a
      ClassificationClass clase219 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_INVERSIONES             , 3, clase217);  //   Barranquilla, Inversiones
      ClassificationClass clase220 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_CONTABILIDAD                      , 2, clase213);  //   Barranquilla, Contabilidad
      ClassificationClass clase221 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , 3, clase220);  //   Barranquilla, Estados Financieros
      ClassificationClass clase222 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_LIBROS_CONTABLES        , 3, clase220);  //   Barranquilla, Libros contables
      ClassificationClass clase223 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_PERSONAL                          , 1, clase001);  //   Barranquilla, Subgerencia de Personal
      ClassificationClass clase224 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_HOJAS_DE_VIDA                     , 2, clase223);  //   Barranquilla, Hojas de vida
      ClassificationClass clase225 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_CANDIDATOS              , 3, clase224);  //   Barranquilla, Candidatos de personal
      ClassificationClass clase226 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_PERSONAL_ACTIVO         , 3, clase224);  //   Barranquilla, Personal activo
      ClassificationClass clase227 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_PENSIONADOS             , 3, clase224);  //   Barranquilla, Pensionados
      ClassificationClass clase228 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_SANCIONES                         , 2, clase223);  //   Barranquilla, Sanciones de personal
      ClassificationClass clase229 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_INVESTIGACIONES         , 3, clase228);  //   Barranquilla, Investigaciones disciplinarias
      ClassificationClass clase230 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_FALLOS_DE_PERSONAL      , 3, clase228);  //   Barranquilla, Fallos de personal
      ClassificationClass clase231 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_EVALUACIONES                      , 2, clase223);  //   Barranquilla, Evaluaciones de personal
      ClassificationClass clase232 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_DESEMPENO               , 3, clase231);  //   Barranquilla, Evaluaciones de desempe�o
      ClassificationClass clase233 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_JURIDICA                          , 1, clase001);  //   Barranquilla, Subgerencia Jur�dica
      ClassificationClass clase234 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_PROCESOS                          , 2, clase233);  //   Barranquilla, Procesos jur�dicos
      ClassificationClass clase235 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_JUR_DEMANDAS                , 3, clase234);  //   Barranquilla, Demandas en curso
      ClassificationClass clase236 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_JUR_FALLOS_JUDICIALES       , 3, clase234);  //   Barranquilla, Demandas en curso
      ClassificationClass clase237 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_ADMINISTRACION                    , 1, clase001);  //   Barranquilla, Subgerencia Administrativa
      ClassificationClass clase238 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_ACTIVOS_FIJOS                     , 2, clase237);  //   Barranquilla, Activos fijos
      ClassificationClass clase239 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_ADM_EDIFICACIONES           , 3, clase238);  //   Barranquilla, Edificaciones
      ClassificationClass clase240 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_ADM_SERVICIOS               , 3, clase238);  //   Barranquilla, Servicios p�blicos
      ClassificationClass clase241 = createClass( tenant1, Constant.TITLE_SEDE_BUCARAMANGA                                , 3, null);      //   Sede Bucaramanga
      ClassificationClass clase242 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_SUBGERENCIA                       , 1, clase001);  //   Gerencia Bucaramanga
      ClassificationClass clase243 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_ACTAS                             , 2, clase242);  //   Bucaramanga, Actas
      ClassificationClass clase244 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , 3, clase243);  //   Bucaramanga, Actas_junta_directiva
      ClassificationClass clase245 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_GERENCIA        , 3, clase243);  //   Bucaramanga, Actas_comit�_gerencia
      ClassificationClass clase246 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_FINANCIERO      , 3, clase243);  //   Bucaramanga, Actas_comit�_financiero
      ClassificationClass clase247 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , 3, clase243);  //   Bucaramanga, Actas_comit�_administrativo
      ClassificationClass clase248 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_OPERACIONES     , 3, clase243);  //   Bucaramanga, Actas_comit�_operaciones
      ClassificationClass clase250 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_PLANES                            , 2, clase242);  //   Bucaramanga, Planes
      ClassificationClass clase251 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_PLAN_OPERATIVO               , 3, clase250);  //   Bucaramanga, Plan_operativo
      ClassificationClass clase252 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_PLAN_FINANCIERO              , 3, clase250);  //   Bucaramanga, Plan_financiero
      ClassificationClass clase253 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_PRESUPUESTO                  , 3, clase250);  //   Bucaramanga, Presupuesto
      ClassificationClass clase254 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_OPERACIONES                       , 1, clase001);  //   Bucaramanga, Subgerencia de Operaciones
      ClassificationClass clase255 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_ACTAS_OPERACIONES                 , 2, clase254);  //   Bucaramanga, Actas Operaciones
      ClassificationClass clase256 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , 3, clase255);  //   Bucaramanga, Actas Comit� calidad
      ClassificationClass clase257 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , 3, clase255);  //   Bucaramanga, Actas Comit� planeaci�n
      ClassificationClass clase258 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_CONTRATOS                         , 2, clase254);  //   Bucaramanga, Contratos
      ClassificationClass clase259 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_OPER_CONTRATOS_OPERACION    , 3, clase258);  //   Bucaramanga, Contratos de Operaci�n
      ClassificationClass clase260 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_OPER_CONTRATOS_INVERSION    , 3, clase258);  //   Bucaramanga, Contratos de Inversi�n
      ClassificationClass clase261 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_FINANCIERA                        , 1, clase001);  //   Bucaramanga, Subgerencia Financiera
      ClassificationClass clase262 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_PRESUPUESTO                       , 2, clase261);  //   Bucaramanga, Presupuesto
      ClassificationClass clase263 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_PLANEACION_PPTAL        , 3, clase262);  //   Bucaramanga, Planeaci�n Presupuestal
      ClassificationClass clase264 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_EJECUCION_PPTAL         , 3, clase262);  //   Bucaramanga, Ejecuci�n Presupuestal
      ClassificationClass clase265 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_TESORERIA                         , 2, clase261);  //   Bucaramanga, Tesorer�a
      ClassificationClass clase266 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_PAGADURIA               , 3, clase265);  //   Bucaramanga, Pagadur�a
      ClassificationClass clase267 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_INVERSIONES             , 3, clase265);  //   Bucaramanga, Inversiones
      ClassificationClass clase268 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_CONTABILIDAD                      , 2, clase261);  //   Bucaramanga, Contabilidad
      ClassificationClass clase269 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , 3, clase268);  //   Bucaramanga, Estados Financieros
      ClassificationClass clase270 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_LIBROS_CONTABLES        , 3, clase268);  //   Bucaramanga, Libros contables
      ClassificationClass clase271 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_PERSONAL                          , 1, clase001);  //   Bucaramanga, Subgerencia de Personal
      ClassificationClass clase272 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_HOJAS_DE_VIDA                     , 2, clase271);  //   Bucaramanga, Hojas de vida
      ClassificationClass clase273 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_CANDIDATOS              , 3, clase272);  //   Bucaramanga, Candidatos de personal
      ClassificationClass clase274 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_PERSONAL_ACTIVO         , 3, clase272);  //   Bucaramanga, Personal activo
      ClassificationClass clase275 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_PENSIONADOS             , 3, clase272);  //   Bucaramanga, Pensionados
      ClassificationClass clase276 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_SANCIONES                         , 2, clase271);  //   Bucaramanga, Sanciones de personal
      ClassificationClass clase277 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_INVESTIGACIONES         , 3, clase276);  //   Bucaramanga, Investigaciones disciplinarias
      ClassificationClass clase278 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_FALLOS_DE_PERSONAL      , 3, clase276);  //   Bucaramanga, Fallos de personal
      ClassificationClass clase279 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_EVALUACIONES                      , 2, clase271);  //   Bucaramanga, Evaluaciones de personal
      ClassificationClass clase280 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_DESEMPENO               , 3, clase279);  //   Bucaramanga, Evaluaciones de desempe�o
      ClassificationClass clase281 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_JURIDICA                          , 1, clase001);  //   Bucaramanga, Subgerencia Jur�dica
      ClassificationClass clase282 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_PROCESOS                          , 2, clase281);  //   Bucaramanga, Procesos jur�dicos
      ClassificationClass clase283 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_JUR_DEMANDAS                , 3, clase282);  //   Bucaramanga, Demandas en curso
      ClassificationClass clase284 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_JUR_FALLOS_JUDICIALES       , 3, clase282);  //   Bucaramanga, Demandas en curso
      ClassificationClass clase285 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_ADMINISTRACION                    , 1, clase001);  //   Bucaramanga, Subgerencia Administrativa
      ClassificationClass clase286 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_ACTIVOS_FIJOS                     , 2, clase285);  //   Bucaramanga, Activos fijos
      ClassificationClass clase287 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_ADM_EDIFICACIONES           , 3, clase286);  //   Bucaramanga, Edificaciones
      ClassificationClass clase288 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_ADM_SERVICIOS               , 3, clase286);  //   Bucaramanga, Servicios p�blicos
      ClassificationClass clase289 = createClass( tenant1, Constant.TITLE_SEDE_CARTAGENA                                  , 3, null);      //   Sede Cartagena
      ClassificationClass clase290 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_SUBGERENCIA                       , 1, clase001);  //   Gerencia Cartagena
      ClassificationClass clase291 = createClass( tenant1,     Constant.TITLE_CTG_SERIE__ACTAS                            , 2, clase290);  //   Cartagena, Actas
      ClassificationClass clase292 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , 3, clase291);  //   Cartagena, Actas_junta_directiva
      ClassificationClass clase293 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_GERENCIA        , 3, clase291);  //   Cartagena, Actas_comit�_gerencia
      ClassificationClass clase294 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_FINANCIERO      , 3, clase291);  //   Cartagena, Actas_comit�_financiero
      ClassificationClass clase295 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , 3, clase291);  //   Cartagena, Actas_comit�_administrativo
      ClassificationClass clase296 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_OPERACIONES     , 3, clase291);  //   Cartagena, Actas_comit�_operaciones
      ClassificationClass clase298 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_PLANES                            , 2, clase290);  //   Cartagena, Planes
      ClassificationClass clase299 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_PLAN_OPERATIVO               , 3, clase298);  //   Cartagena, Plan_operativo
      ClassificationClass clase300 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_PLAN_FINANCIERO              , 3, clase298);  //   Cartagena, Plan_financiero
      ClassificationClass clase301 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_PRESUPUESTO                  , 3, clase298);  //   Cartagena, Presupuesto
      ClassificationClass clase302 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_OPERACIONES                       , 1, clase001);  //   Cartagena, Subgerencia de Operaciones
      ClassificationClass clase303 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_ACTAS_OPERACIONES                 , 2, clase302);  //   Cartagena, Actas Operaciones
      ClassificationClass clase304 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , 3, clase303);  //   Cartagena, Actas Comit� calidad
      ClassificationClass clase305 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , 3, clase303);  //   Cartagena, Actas Comit� planeaci�n
      ClassificationClass clase306 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_CONTRATOS                         , 2, clase302);  //   Cartagena, Contratos
      ClassificationClass clase307 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_OPER_CONTRATOS_OPERACION    , 3, clase306);  //   Cartagena, Contratos de Operaci�n
      ClassificationClass clase308 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_OPER_CONTRATOS_INVERSION    , 3, clase306);  //   Cartagena, Contratos de Inversi�n
      ClassificationClass clase309 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_FINANCIERA                        , 1, clase001);  //   Cartagena, Subgerencia Financiera
      ClassificationClass clase310 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_PRESUPUESTO                       , 2, clase309);  //   Cartagena, Presupuesto
      ClassificationClass clase311 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_PLANEACION_PPTAL        , 3, clase310);  //   Cartagena, Planeaci�n Presupuestal
      ClassificationClass clase312 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_EJECUCION_PPTAL         , 3, clase310);  //   Cartagena, Ejecuci�n Presupuestal
      ClassificationClass clase313 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_TESORERIA                         , 2, clase309);  //   Cartagena, Tesorer�a
      ClassificationClass clase314 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_PAGADURIA               , 3, clase313);  //   Cartagena, Pagadur�a
      ClassificationClass clase315 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_INVERSIONES             , 3, clase313);  //   Cartagena, Inversiones
      ClassificationClass clase316 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_CONTABILIDAD                      , 2, clase309);  //   Cartagena, Contabilidad
      ClassificationClass clase317 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , 3, clase316);  //   Cartagena, Estados Financieros
      ClassificationClass clase318 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_LIBROS_CONTABLES        , 3, clase316);  //   Cartagena, Libros contables
      ClassificationClass clase319 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_PERSONAL                          , 1, clase001);  //   Cartagena, Subgerencia de Personal
      ClassificationClass clase320 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_HOJAS_DE_VIDA                     , 2, clase319);  //   Cartagena, Hojas de vida
      ClassificationClass clase321 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_CANDIDATOS              , 3, clase320);  //   Cartagena, Candidatos de personal
      ClassificationClass clase322 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_PERSONAL_ACTIVO         , 3, clase320);  //   Cartagena, Personal activo
      ClassificationClass clase323 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_PENSIONADOS             , 3, clase320);  //   Cartagena, Pensionados
      ClassificationClass clase324 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_SANCIONES                         , 2, clase319);  //   Cartagena, Sanciones de personal
      ClassificationClass clase325 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_INVESTIGACIONES         , 3, clase324);  //   Cartagena, Investigaciones disciplinarias
      ClassificationClass clase326 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_FALLOS_DE_PERSONAL      , 3, clase324);  //   Cartagena, Fallos de personal
      ClassificationClass clase327 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_EVALUACIONES                      , 2, clase319);  //   Cartagena, Evaluaciones de personal
      ClassificationClass clase328 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_DESEMPENO               , 3, clase327);  //   Cartagena, Evaluaciones de desempe�o
      ClassificationClass clase329 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_JURIDICA                          , 1, clase001);  //   Cartagena, Subgerencia Jur�dica
      ClassificationClass clase330 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_PROCESOS                          , 2, clase329);  //   Cartagena, Procesos jur�dicos
      ClassificationClass clase331 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_JUR_DEMANDAS                , 3, clase330);  //   Cartagena, Demandas en curso
      ClassificationClass clase332 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_JUR_FALLOS_JUDICIALES       , 3, clase330);  //   Cartagena, Demandas en curso
      ClassificationClass clase333 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_ADMINISTRACION                    , 1, clase001);  //   Cartagena, Subgerencia Administrativa
      ClassificationClass clase334 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_ACTIVOS_FIJOS                     , 2, clase333);  //   Cartagena, Activos fijos
      ClassificationClass clase335 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_ADM_EDIFICACIONES           , 3, clase334);  //   Cartagena, Edificaciones
      ClassificationClass clase336 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_ADM_SERVICIOS               , 3, clase334);  //   Cartagena, Servicios p�blicos


      getLogger().info("... generating users");
      User baker = createBaker(userRepository, passwordEncoder);
      User barista = createBarista(userRepository, passwordEncoder);
      createAdmin(userRepository, passwordEncoder);
      // A set of products without constrains that can be deleted
      createDeletableUsers(userRepository, passwordEncoder);

      getLogger().info("... generating products");
      // A set of products that will be used for creating orders.
      Supplier<Product> productSupplier = createProducts(productRepository, 8);
      // A set of products without relationships that can be deleted
      createProducts(productRepository, 4);

      getLogger().info("... generating pickup locations");
      Supplier<PickupLocation> pickupLocationSupplier = createPickupLocations(pickupLocationRepository);

      getLogger().info("... generating orders");
      createOrders(orderRepository, productSupplier, pickupLocationSupplier, barista, baker);

      getLogger().info("Generated demo data");

   }//loadData

   private Operation createOperation( Tenant tenant, String name, Operation owner)
   {
      Operation operation = new Operation( name, new ObjectToProtect(), owner);
      operation.setTenant(tenant);
      objectToProtectRepository.saveAndFlush( operation.getObjectToProtect());
      Operation savedOperation = operationRepository.saveAndFlush(operation);
      return savedOperation;
   }//createObject


   private static int  levelSeq = 1;
   private ClassificationClass createClass( Tenant tenant,  String name, int level, ClassificationClass parent)
   {
      Schema newSchema = new Schema("Schema"+(levelSeq++), new TreeSet<>());
      ClassificationClass classificationClass = 
            new ClassificationClass( new ClassificationLevel(level, newSchema), name, parent, new ObjectToProtect());

      classificationClass.setTenant(tenant);    
      ObjectToProtect associatedObject = classificationClass.getObjectToProtect();
      objectToProtectRepository.saveAndFlush(associatedObject);

      ClassificationLevel nivel = classificationClass.getLevel();
      if ( !nivel.isPersisted())
      {
         ClassificationLevel newLevel = classificationLevelRepository.findByLevel(nivel.getLevel());
         if( newLevel == null || !nivel.getLevel().equals(newLevel.getLevel()))
         {
            nivel.setTenant(tenant);
            Schema schema = nivel.getSchema();
            if ( !schema.isPersisted())
            {
               schema.setTenant(tenant);
               schemaRepository.saveAndFlush(schema);
            }
            classificationLevelRepository.saveAndFlush(nivel);
         }else {
            classificationClass.setLevel(newLevel);
         }
      }    
      claseRepository.saveAndFlush(classificationClass);
      return classificationClass;
   }//createClass



   private Tenant createTenant(TenantRepository tenantRepository, String name)
   {
      Tenant tenant = new Tenant();
      tenant.setName(name);
      tenant.setLocked(false);
      tenant.setAdministrator("admin@vaadin.com");
      LocalDate now = LocalDate.now();
      tenant.setFromDate( now.minusMonths(random.nextInt(36)));
      tenant.setToDate(now.plusYears(random.nextInt(10)));
      tenantRepository.save(tenant);

      return tenant;
   }//createTenant

   private com.f.thoth.backend.data.security.Role createRole( Tenant tenant, String name)
   {
      com.f.thoth.backend.data.security.Role  role = new com.f.thoth.backend.data.security.Role();
      role.setTenant(tenant);
      role.setName(name);
      roleRepository.saveAndFlush(role);
      tenant.addRole(role);

      return role;
   }//createRole

   private void fillCustomer(Customer customer)
   {
      String first = getRandom(FIRST_NAME);
      String last = getRandom(LAST_NAME);
      customer.setFullName(first + " " + last);
      customer.setPhoneNumber(getRandomPhone());
      if (random.nextInt(10) == 0) {
         customer.setDetails("Very important customer");
      }
   }

   private String getRandomPhone() {
      return "+1-555-" + String.format("%04d", random.nextInt(10000));
   }

   private void createOrders(OrderRepository orderRepo, Supplier<Product> productSupplier,
         Supplier<PickupLocation> pickupLocationSupplier, User barista, User baker)
   {
      int yearsToInclude = 2;
      LocalDate now = LocalDate.now();
      LocalDate oldestDate = LocalDate.of(now.getYear() - yearsToInclude, 1, 1);
      LocalDate newestDate = now.plusMonths(1L);

      // Create first today's order
      Order order = createOrder(productSupplier, pickupLocationSupplier, barista, baker, now);
      order.setDueTime(LocalTime.of(8, 0));
      order.setHistory(order.getHistory().subList(0, 1));
      order.setItems(order.getItems().subList(0, 1));
      orderRepo.save(order);

      for (LocalDate dueDate = oldestDate; dueDate.isBefore(newestDate); dueDate = dueDate.plusDays(1))
      {
         // Create a slightly upwards trend - everybody wants to be
         // successful
         int relativeYear = dueDate.getYear() - now.getYear() + yearsToInclude;
         int relativeMonth = relativeYear * 12 + dueDate.getMonthValue();
         double multiplier = 1.0 + 0.03 * relativeMonth;
         int ordersThisDay = (int) (random.nextInt(10) + 1 * multiplier);
         for (int i = 0; i < ordersThisDay; i++) {
            orderRepo.save(createOrder(productSupplier, pickupLocationSupplier, barista, baker, dueDate));
         }
      }// for dueDate...
   }//createOrders

   private Order createOrder(Supplier<Product> productSupplier, Supplier<PickupLocation> pickupLocationSupplier,
         User barista, User baker, LocalDate dueDate)
   {
      Order order = new Order(barista);

      fillCustomer(order.getCustomer());
      order.setPickupLocation(pickupLocationSupplier.get());
      order.setDueDate(dueDate);
      order.setDueTime(getRandomDueTime());
      order.changeState(barista, getRandomState(order.getDueDate()));

      int itemCount = random.nextInt(3);
      List<OrderItem> items = new ArrayList<>();
      for (int i = 0; i <= itemCount; i++) {
         OrderItem item = new OrderItem();
         Product product;
         do {
            product = productSupplier.get();
         } while (containsProduct(items, product));
         item.setProduct(product);
         item.setQuantity(random.nextInt(10) + 1);
         if (random.nextInt(5) == 0) {
            if (random.nextBoolean()) {
               item.setComment("Lactose free");
            } else {
               item.setComment("Gluten free");
            }
         }
         items.add(item);
      }
      order.setItems(items);

      order.setHistory(createOrderHistory(order, barista, baker));

      return order;
   }//createOrder

   private List<HistoryItem> createOrderHistory(Order order, User barista, User baker)
   {
      ArrayList<HistoryItem> history = new ArrayList<>();
      HistoryItem item = new HistoryItem(barista, "Order placed");
      item.setNewState(OrderState.NEW);
      LocalDateTime orderPlaced = order.getDueDate().minusDays(random.nextInt(5) + 2L).atTime(random.nextInt(10) + 7, 00);
      item.setTimestamp(orderPlaced);
      history.add(item);
      if (order.getState() == OrderState.CANCELLED) {
         item = new HistoryItem(barista, "Order cancelled");
         item.setNewState(OrderState.CANCELLED);
         item.setTimestamp(orderPlaced.plusDays(random
               .nextInt((int) orderPlaced.until(order.getDueDate().atTime(order.getDueTime()), ChronoUnit.DAYS))));
         history.add(item);
      } else if (order.getState() == OrderState.CONFIRMED || order.getState() == OrderState.DELIVERED
            || order.getState() == OrderState.PROBLEM || order.getState() == OrderState.READY) {
         item = new HistoryItem(baker, "Order confirmed");
         item.setNewState(OrderState.CONFIRMED);
         item.setTimestamp(orderPlaced.plusDays(random.nextInt(2)).plusHours(random.nextInt(5)));
         history.add(item);

         if (order.getState() == OrderState.PROBLEM) {
            item = new HistoryItem(baker, "Can't make it. Did not get any ingredients this morning");
            item.setNewState(OrderState.PROBLEM);
            item.setTimestamp(order.getDueDate().atTime(random.nextInt(4) + 4, 0));
            history.add(item);
         } else if (order.getState() == OrderState.READY || order.getState() == OrderState.DELIVERED) {
            item = new HistoryItem(baker, "Order ready for pickup");
            item.setNewState(OrderState.READY);
            item.setTimestamp(order.getDueDate().atTime(random.nextInt(2) + 8, random.nextBoolean() ? 0 : 30));
            history.add(item);
            if (order.getState() == OrderState.DELIVERED) {
               item = new HistoryItem(baker, "Order delivered");
               item.setNewState(OrderState.DELIVERED);
               item.setTimestamp(order.getDueDate().atTime(order.getDueTime().minusMinutes(random.nextInt(120))));
               history.add(item);
            }
         }
      }

      return history;
   }//createOrderHistory

   private boolean containsProduct(List<OrderItem> items, Product product)
   {
      for (OrderItem item : items) {
         if (item.getProduct() == product) {
            return true;
         }
      }
      return false;
   }//containsProduct

   private LocalTime getRandomDueTime()
   {
      int time = 8 + 4 * random.nextInt(3);
      return LocalTime.of(time, 0);
   }//getRandomDueTime

   private OrderState getRandomState(LocalDate due)
   {
      LocalDate today = LocalDate.now();
      LocalDate tomorrow = today.plusDays(1);
      LocalDate twoDays = today.plusDays(2);

      if (due.isBefore(today)) {
         if (random.nextDouble() < 0.9) {
            return OrderState.DELIVERED;
         } else {
            return OrderState.CANCELLED;
         }
      } else {
         if (due.isAfter(twoDays)) {
            return OrderState.NEW;
         } else if (due.isAfter(tomorrow)) {
            // in 1-2 days
            double resolution = random.nextDouble();
            if (resolution < 0.8) {
               return OrderState.NEW;
            } else if (resolution < 0.9) {
               return OrderState.PROBLEM;
            } else {
               return OrderState.CANCELLED;
            }
         } else {
            double resolution = random.nextDouble();
            if (resolution < 0.6) {
               return OrderState.READY;
            } else if (resolution < 0.8) {
               return OrderState.DELIVERED;
            } else if (resolution < 0.9) {
               return OrderState.PROBLEM;
            } else {
               return OrderState.CANCELLED;
            }
         }

      }
   }//getRandomState

   private <T> T getRandom(T[] array)
   {
      return array[random.nextInt(array.length)];
   }

   private Supplier<PickupLocation> createPickupLocations(PickupLocationRepository pickupLocationRepository)
   {
      List<PickupLocation> pickupLocations = Arrays.asList(
            pickupLocationRepository.save(createPickupLocation("Store")),
            pickupLocationRepository.save(createPickupLocation("Bakery")));
      return () -> pickupLocations.get(random.nextInt(pickupLocations.size()));
   }//createPickupLocations

   private PickupLocation createPickupLocation(String name)
   {
      PickupLocation store = new PickupLocation();
      store.setName(name);
      return store;
   }//createPickupLocation

   private Supplier<Product> createProducts(ProductRepository productsRepo, int numberOfItems)
   {
      List<Product> products  = new ArrayList<>();
      for (int i = 0; i < numberOfItems; i++) {
         Product product = new Product();
         product.setName(getRandomProductName()+i);
         double doublePrice = 2.0 + random.nextDouble() * 100.0;
         product.setPrice((int) (doublePrice * 100.0));
         products.add(productsRepo.save(product));
      }
      return () -> {
         double cutoff = 2.5;
         double g = random.nextGaussian();
         g = Math.min(cutoff, g);
         g = Math.max(-cutoff, g);
         g += cutoff;
         g /= (cutoff * 2.0);
         return products.get((int) (g * (products.size() - 1)));
      };
   }//createProducts

   private String getRandomProductName()
   {
      String firstFilling = getRandom(FILLING);
      String name;
      if (random.nextBoolean()) {
         String secondFilling;
         do {
            secondFilling = getRandom(FILLING);
         } while (secondFilling.equals(firstFilling));

         name = firstFilling + " " + secondFilling;
      } else {
         name = firstFilling;
      }
      name += " " + getRandom(TYPE);

      return name;
   }//getRandomProductName

   private User createBaker(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      return userRepository.save(
            createUser("baker@vaadin.com", "Heidi", "Carter", passwordEncoder.encode("baker"), Role.BAKER, false));
   }//createBaker

   private User createBarista(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      return userRepository.save(createUser("barista@vaadin.com", "Malin", "Castro",
            passwordEncoder.encode("barista"), Role.BARISTA, true));
   }//createBarista

   private User createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      User admin =  userRepository.save(
            createUser("admin@vaadin.com", "Göran", "Rich", passwordEncoder.encode("admin"), Role.ADMIN, true));
      ThothSession.setUser(admin);
      return admin;

   }//createAdmin

   private void createDeletableUsers(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      userRepository.save(
            createUser("peter@vaadin.com", "Peter", "Bush", passwordEncoder.encode("peter"), Role.BARISTA, false));
      userRepository
      .save(createUser("mary@vaadin.com", "Mary", "Ocon", passwordEncoder.encode("mary"), Role.BAKER, true));
   }//createDeletableUsers

   private User createUser(String email, String firstName, String lastName, String passwordHash, String role, boolean locked)
   {
      User user = new User();
      user.setEmail(email);
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setPasswordHash(passwordHash);
      user.setRole(role);
      user.setLocked(locked);
      return user;
   }//createUser

}//DataGenerator
