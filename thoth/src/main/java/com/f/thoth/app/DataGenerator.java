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
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.metadata.Type;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Operation;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.FieldRepository;
import com.f.thoth.backend.repositories.LevelRepository;
import com.f.thoth.backend.repositories.MetadataRepository;
import com.f.thoth.backend.repositories.OperationRepository;
import com.f.thoth.backend.repositories.OrderRepository;
import com.f.thoth.backend.repositories.PickupLocationRepository;
import com.f.thoth.backend.repositories.ProductRepository;
import com.f.thoth.backend.repositories.RetentionRepository;
import com.f.thoth.backend.repositories.RoleRepository;
import com.f.thoth.backend.repositories.SchemaRepository;
import com.f.thoth.backend.repositories.SchemaValuesRepository;
import com.f.thoth.backend.repositories.TenantRepository;
import com.f.thoth.backend.repositories.UserRepository;
import com.f.thoth.backend.service.TenantService;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class DataGenerator implements HasLogger
{
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
   private OperationRepository           operationRepository;
   private PickupLocationRepository      pickupLocationRepository;
   private PasswordEncoder               passwordEncoder;
   private ClassificationRepository      claseRepository;
   private LevelRepository               levelRepository;
   private SchemaRepository              schemaRepository;
   private MetadataRepository            metadataRepository;
   private FieldRepository               fieldRepository;
   private SchemaValuesRepository        valuesRepository;
   private RetentionRepository           retentionRepository;

   @Autowired
   public DataGenerator(TenantService tenantService, OrderRepository orderRepository, UserRepository userRepository,
         ProductRepository productRepository, PickupLocationRepository pickupLocationRepository,
         TenantRepository tenantRepository, RoleRepository roleRepository, OperationRepository operationRepository,
         ClassificationRepository claseRepository, MetadataRepository metadataRepository, FieldRepository fieldRepository,
         SchemaRepository schemaRepository, LevelRepository levelRepository, SchemaValuesRepository valuesRepository,
         RetentionRepository retentionRepository, 
         PasswordEncoder passwordEncoder)
   {
      this.tenantService                 = tenantService;
      this.orderRepository               = orderRepository;
      this.userRepository                = userRepository;
      this.productRepository             = productRepository;
      this.pickupLocationRepository      = pickupLocationRepository;
      this.tenantRepository              = tenantRepository;
      this.roleRepository                = roleRepository;
      this.operationRepository           = operationRepository;
      this.claseRepository               = claseRepository;
      this.levelRepository               = levelRepository;
      this.schemaRepository              = schemaRepository;
      this.fieldRepository               = fieldRepository;
      this.metadataRepository            = metadataRepository;
      this.passwordEncoder               = passwordEncoder;
      this.valuesRepository              = valuesRepository;
      this.retentionRepository           = retentionRepository;

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
      
      // ----------- Respetar este orden para la inicialización de estos default -------------
      getLogger().info("... generating defaults");
      
      Schema.EMPTY.setTenant(tenant1);
      Schema.EMPTY.buildCode();
      schemaRepository.saveAndFlush(Schema.EMPTY);
      
      Level.DEFAULT.setTenant(tenant1);
      Level.DEFAULT.buildCode();
      levelRepository.saveAndFlush(Level.DEFAULT);
      
      SchemaValues.EMPTY.setTenant(tenant1);
      SchemaValues.EMPTY.buildCode();
      valuesRepository.saveAndFlush(SchemaValues.EMPTY);
      
      Retention.DEFAULT.setTenant(tenant1);
      Retention.DEFAULT.buildCode();
      retentionRepository.saveAndFlush(Retention.DEFAULT);
      
      

      getLogger().info("... generating metadata");
      Metadata nameMeta  = createMeta("String", Type.STRING, "length > 0");
      Field    nameField = createField("Nombre", nameMeta, true, false, true, 1, 2);
      Field    bossField = createField("Jefe",   nameMeta, true, false, true, 2, 2);

      Metadata dateMeta  = createMeta("Fecha", Type.DATETIME, "not null");
      Field    fromField = createField("Desde", dateMeta, true, false, true, 3, 2);
      Field    toField   = createField("Hasta", dateMeta, true, false, true, 4, 2);

      Metadata enumMeta   = createMeta("Color",    Type.ENUM, "Verde;Rojo;Azul;Magenta;Cyan");
      Field    colorField = createField("Colores",   enumMeta, true, false, true, 5, 1);

      Metadata claseMeta     = createMeta("Security", Type.ENUM, "Restringido;Confidencial;Interno;Público");
      Field    securityField = createField("Seguridad", enumMeta, true, false, true, 5, 1);

      Metadata intMeta   = createMeta("Entero", Type.INTEGER, " >0; < 100");
      Field    cantField = createField("Cantidad", intMeta, true, false, true, 5, 1);
      Field    edadField = createField("Edad",     intMeta, true, true,  true, 6, 1);

      Metadata decMeta   = createMeta("Decimal", Type.DECIMAL," >= 0.0");
      Field    ratioField= createField("Razon", decMeta, true, false, true, 7, 1);

      Schema  sedeSchema = createSchema("Sede");
      sedeSchema.addField(nameField);
      sedeSchema.addField(fromField);
      sedeSchema.addField(toField);
      sedeSchema.addField(colorField);
      sedeSchema.addField(securityField);

      Schema   officeSchema = createSchema("Office");
      officeSchema.addField(nameField);
      officeSchema.addField(bossField);
      officeSchema.addField(fromField);
      officeSchema.addField(toField);
      officeSchema.addField(colorField);
      schemaRepository.saveAndFlush(officeSchema);

      Schema   seriesSchema = createSchema("Series");
      seriesSchema.addField(nameField);
      seriesSchema.addField(fromField);
      seriesSchema.addField(toField);
      seriesSchema.addField(securityField);

      Schema   otherSchema = createSchema("Other");
      otherSchema.addField(nameField);
      otherSchema.addField(bossField);
      otherSchema.addField(fromField);
      otherSchema.addField(toField);
      otherSchema.addField(cantField);
      otherSchema.addField(edadField);
      otherSchema.addField(ratioField);
      schemaRepository.saveAndFlush(otherSchema);

      Level level0 = new Level("Sede",     0, sedeSchema);
      Level level1 = new Level("Oficina",  1, officeSchema);
      Level level2 = new Level("Serie",    2, seriesSchema);
      Level level3 = new Level("Subserie", 3, seriesSchema);


      getLogger().info("... generating Operations" );
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
      Operation obj13 = createOperation( tenant1, Constant.TITLE_PROPIEDADES                             , null );  // Propiedades
      Operation obj14 = createOperation( tenant1,    Constant.TITLE_METADATA                             , obj13);  // Metadatos
      Operation obj15 = createOperation( tenant1,    Constant.TITLE_ESQUEMAS_METADATA                    , obj13);  // Esquemas de metadatos
      Operation obj16 = createOperation( tenant1,    Constant.TITLE_TIPOS_DOCUMENTALES                   , obj13);  // Tipos documentales
      Operation obj17 = createOperation( tenant1, Constant.TITLE_CLASIFICACION                           , null );  // Clasificacion
      Operation obj18 = createOperation( tenant1,    Constant.TITLE_NIVELES                              , obj17);  // Niveles
      Operation obj19 = createOperation( tenant1,    Constant.TITLE_RETENCION                            , obj17);  // Niveles
      Operation obj20 = createOperation( tenant1,    Constant.TITLE_ESQUEMAS_CLASIFICACION               , obj17);  // Esquemas de clasificación
      Operation obj21 = createOperation( tenant1, Constant.TITLE_ADMIN_EXPEDIENTES                       , null );  // Gestion expedientes
      Operation obj22 = createOperation( tenant1,    Constant.TITLE_EXPEDIENTES                          , obj21);  // Expedientes mayores
      Operation obj23 = createOperation( tenant1,    Constant.TITLE_SUBEXPEDIENTES                       , obj21);  // Sub-expedientes
      Operation obj24 = createOperation( tenant1,    Constant.TITLE_VOLUMENES                            , obj21);  // Volumenes
      Operation obj25 = createOperation( tenant1,    Constant.TITLE_INDICE                               , obj21);  // Indice de expedientes
      Operation obj26 = createOperation( tenant1,    Constant.TITLE_EXPORTACION                          , obj21);  // Exportacion de expedientes
      Operation obj27 = createOperation( tenant1,    Constant.TITLE_IMPORTACION                          , obj21);  // Importacion de expedientes
      Operation obj28 = createOperation( tenant1,    Constant.TITLE_COPIA_DOCUMENTOS                     , obj21);  // Copia documento a otro expediente
      Operation obj29 = createOperation( tenant1,    Constant.TITLE_TRANSER_DOCUMENTOS                   , obj21);  // Transferencia de documento a otro expediente
      Operation obj30 = createOperation( tenant1, Constant.TITLE_TRAMITE                                 , null );  // Tramite de documentos
      Operation obj31 = createOperation( tenant1,    Constant.TITLE_BANDEJA                              , obj30);  // Bandeja personal
      Operation obj32 = createOperation( tenant1,    Constant.TITLE_CLASIFICACION_DOCUMENTOS             , obj30);  // Clasificacion de documento
      Operation obj33 = createOperation( tenant1,    Constant.TITLE_RETORNO                              , obj30);  // Devolucion de documento
      Operation obj34 = createOperation( tenant1,    Constant.TITLE_RE_ENVIO                             , obj30);  // Re-envio de documento
      Operation obj35 = createOperation( tenant1,    Constant.TITLE_BORRADORES                           , obj30);  // Carga borrador de documento
      Operation obj36 = createOperation( tenant1,    Constant.TITLE_FIRMA                                , obj30);  // Firma de documento
      Operation obj37 = createOperation( tenant1,    Constant.TITLE_ENVIO                                , obj30);  // Ordena envio de documento
      Operation obj38 = createOperation( tenant1, Constant.TITLE_RECEPCION                               , null );  // Recepcion de documentos
      Operation obj39 = createOperation( tenant1,    Constant.TITLE_RECEPCION_DOCUMENTOS                 , obj38);  // Recepcion en ventanilla
      Operation obj40 = createOperation( tenant1,    Constant.TITLE_RECEPCION_E_MAIL                     , obj38);  // Recepcion correo electronico
      Operation obj41 = createOperation( tenant1,    Constant.TITLE_DIGITALIZACION                       , obj38);  // Digitalizacion
      Operation obj42 = createOperation( tenant1,    Constant.TITLE_DIRECCIONAMIENTO                     , obj38);  // Enrutamiento de documentos
      Operation obj43 = createOperation( tenant1, Constant.TITLE_CORRESPONDENCIA_EXTERNA                 , null );  // Envio de documentos
      Operation obj44 = createOperation( tenant1,    Constant.TITLE_REGISTRO_ENVIOS                      , obj43);  // Consolidacion envoos externos
      Operation obj45 = createOperation( tenant1,    Constant.TITLE_ENVIO_EXTERNO                        , obj43);  // Envia correspondencia externa
      Operation obj46 = createOperation( tenant1,    Constant.TITLE_CONFIRMACION_ENVIO                   , obj43);  // Confirmacion de recepcion
      Operation obj47 = createOperation( tenant1, Constant.TITLE_CONSULTA                                , null );  // Consulta
      Operation obj48 = createOperation( tenant1,    Constant.TITLE_DOCUMENTOS                           , obj47);  // Consulta de documentos
      Operation obj49 = createOperation( tenant1,       Constant.TITLE_CONSULTA_LIBRE                    , obj48);  // Consulta libre documentos
      Operation obj50 = createOperation( tenant1,       Constant.TITLE_CONSULTA_METADATOS                , obj48);  // Consulta documentos segun metadatos
      Operation obj51 = createOperation( tenant1,    Constant.TITLE_CONSULTA_EXPEDIENTES                 , obj47);  // Consulta de expedientes
      Operation obj52 = createOperation( tenant1,       Constant.TITLE_CONSULTA_EXPEDIENTES_LIBRE        , obj51);  // Consulta de expedientes segun texto libre
      Operation obj53 = createOperation( tenant1,       Constant.TITLE_CONSULTA_EXPEDIENTES_METADATOS    , obj51);  // Consulta de expedientes segun metadatos
      Operation obj54 = createOperation( tenant1,       Constant.TITLE_CONSULTA_EXPEDIENTES_CLASIFICACION, obj51);  // Consulta de expedientes segun clasificacion
      Operation obj55 = createOperation( tenant1, Constant.TITLE_PROCESOS                                , null );  // Procesos
      Operation obj56 = createOperation( tenant1,    Constant.TITLE_EJECUCION_PROCESO                    , obj55);  // Ejecucion de proceso
      Operation obj57 = createOperation( tenant1,    Constant.TITLE_DEFINICION_PROCESO                   , obj55);  // Definicion de proceso
      Operation obj58 = createOperation( tenant1, Constant.TITLE_ARCHIVO                                 , null );  // Archivo
      Operation obj59 = createOperation( tenant1,    Constant.TITLE_LOCALES                              , obj58);  // Locales
      Operation obj60 = createOperation( tenant1,    Constant.TITLE_TRANSFERENCIA                        , obj58);  // Preparacion de transferencia
      Operation obj61 = createOperation( tenant1,    Constant.TITLE_RECIBO_TRANSFERENCIA                 , obj58);  // Recepcion de transferencia
      Operation obj62 = createOperation( tenant1,    Constant.TITLE_LOCALIZACION                         , obj58);  // Localizacion de documentos
      Operation obj63 = createOperation( tenant1,    Constant.TITLE_PRESTAMO                             , obj58);  // Prestamos
      Operation obj64 = createOperation( tenant1,       Constant.TITLE_PRESTAMO_EXPEDIENTE               , obj63);  // Prestamo de expedientes
      Operation obj65 = createOperation( tenant1,       Constant.TITLE_DEVOLUCION                        , obj63);  // Retorno de expediente
      Operation obj66 = createOperation( tenant1,    Constant.TITLE_INDICES_ARCHIVO                      , obj58);  // Indice de archivo

      getLogger().info("... generating Classification classes" );
      Classification clase001 = createClass( tenant1, Constant.TITLE_SEDE_CORPORATIVA                                , level0, null);      //   Sede Corporativa
      Classification clase002 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_GERENCIA_GENERAL                  , level1, clase001);  //   Corporativa, Gerencia_general
      Classification clase003 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_ACTAS                             , level2, clase002);  //   Corporativa, Actas
      Classification clase004 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , level3, clase003);  //   Corporativa, Actas_junta_directiva
      Classification clase005 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_GERENCIA        , level3, clase003);  //   Corporativa, Actas_comite_gerencia
      Classification clase006 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_FINANCIERO      , level3, clase003);  //   Corporativa, Actas_comite_financiero
      Classification clase007 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , level3, clase003);  //   Corporativa, Actas_comite_administrativo
      Classification clase008 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_OPERACIONES     , level3, clase003);  //   Corporativa, Actas_comite_operaciones
      Classification clase010 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_PLANES                            , level2, clase001);  //   Corporativa, Planes
      Classification clase011 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_PLAN_OPERATIVO               , level3, clase010);  //   Corporativa, Plan_operativo
      Classification clase012 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_PLAN_FINANCIERO              , level3, clase010);  //   Corporativa, Plan_financiero
      Classification clase013 = createClass( tenant1,       Constant.TITLE_CRP_SUBSERIE_PRESUPUESTO                  , level3, clase010);  //   Corporativa, Presupuesto
      Classification clase014 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_OPERACIONES                       , level1, clase001);  //   Corporativa, Subgerencia de Operaciones
      Classification clase015 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_ACTAS_OPERACIONES                 , level2, clase014);  //   Corporativa, Actas Operaciones
      Classification clase016 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , level3, clase015);  //   Corporativa, Actas Comite calidad
      Classification clase017 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , level3, clase015);  //   Corporativa, Actas Comite planeacion
      Classification clase018 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_CONTRATOS                         , level2, clase014);  //   Corporativa, Contratos
      Classification clase019 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_OPER_CONTRATOS_OPERACION    , level3, clase018);  //   Corporativa, Contratos de Operacion
      Classification clase020 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_OPER_CONTRATOS_INVERSION    , level3, clase018);  //   Corporativa, Contratos de Inversion
      Classification clase021 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_FINANCIERA                        , level1, clase001);  //   Corporativa, Subgerencia Financiera
      Classification clase022 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_PRESUPUESTO                       , level2, clase021);  //   Corporativa, Presupuesto
      Classification clase023 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_PLANEACION_PPTAL        , level3, clase022);  //   Corporativa, Planeacion Presupuestal
      Classification clase024 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_EJECUCION_PPTAL         , level3, clase022);  //   Corporativa, Ejecucion Presupuestal
      Classification clase025 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_TESORERIA                         , level2, clase021);  //   Corporativa, Tesoreria
      Classification clase026 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_PAGADURIA               , level3, clase025);  //   Corporativa, Pagaduria
      Classification clase027 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_INVERSIONES             , level3, clase025);  //   Corporativa, Inversiones
      Classification clase028 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_CONTABILIDAD                      , level2, clase021);  //   Corporativa, Contabilidad
      Classification clase029 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , level3, clase028);  //   Corporativa, Estados Financieros
      Classification clase030 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_FIN_LIBROS_CONTABLES        , level3, clase028);  //   Corporativa, Libros contables
      Classification clase031 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_PERSONAL                          , level1, clase001);  //   Corporativa, Subgerencia de Personal
      Classification clase032 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_HOJAS_DE_VIDA                     , level2, clase031);  //   Corporativa, Hojas de vida
      Classification clase033 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_CANDIDATOS              , level3, clase032);  //   Corporativa, Candidatos de personal
      Classification clase034 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_PERSONAL_ACTIVO         , level3, clase032);  //   Corporativa, Personal activo
      Classification clase035 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_PENSIONADOS             , level3, clase032);  //   Corporativa, Pensionados
      Classification clase036 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_SANCIONES                         , level2, clase031);  //   Corporativa, Sanciones de personal
      Classification clase037 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_INVESTIGACIONES         , level3, clase036);  //   Corporativa, Investigaciones disciplinarias
      Classification clase038 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_FALLOS_DE_PERSONAL      , level3, clase036);  //   Corporativa, Fallos de personal
      Classification clase039 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_EVALUACIONES                      , level2, clase031);  //   Corporativa, Evaluaciones de personal
      Classification clase040 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_PER_DESEMPENO               , level3, clase039);  //   Corporativa, Evaluaciones de desempeeo
      Classification clase041 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_JURIDICA                          , level1, clase001);  //   Corporativa, Subgerencia Juridica
      Classification clase042 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_PROCESOS                          , level2, clase041);  //   Corporativa, Procesos juridicos
      Classification clase043 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_JUR_DEMANDAS                , level3, clase042);  //   Corporativa, Demandas en curso
      Classification clase044 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_JUR_FALLOS_JUDICIALES       , level3, clase042);  //   Corporativa, Demandas en curso
      Classification clase045 = createClass( tenant1,   Constant.TITLE_CRP_OFICINA_ADMINISTRACION                    , level1, clase001);  //   Corporativa, Subgerencia Administrativa
      Classification clase046 = createClass( tenant1,     Constant.TITLE_CRP_SERIE_ACTIVOS_FIJOS                     , level2, clase045);  //   Corporativa, Activos fijos
      Classification clase047 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_ADM_EDIFICACIONES           , level3, clase046);  //   Corporativa, Edificaciones
      Classification clase048 = createClass( tenant1,        Constant.TITLE_CRP_SUBSERIE_ADM_SERVICIOS               , level3, clase046);  //   Corporativa, Servicios publicos
      Classification clase049 = createClass( tenant1, Constant.TITLE_SEDE_BOGOTA                                     , level0, null);      //   Sede Bogota
      Classification clase050 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_SUBGERENCIA                       , level1, clase049);  //   Gerencia Bogota
      Classification clase051 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_ACTAS                             , level2, clase050);  //   Bogota, Actas
      Classification clase052 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , level3, clase051);  //   Bogota, Actas_junta_directiva
      Classification clase053 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_GERENCIA        , level3, clase051);  //   Bogota, Actas_comite_gerencia
      Classification clase054 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_FINANCIERO      , level3, clase051);  //   Bogota, Actas_comite_financiero
      Classification clase055 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , level3, clase051);  //   Bogota, Actas_comite_administrativo
      Classification clase056 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_OPERACIONES     , level3, clase051);  //   Bogota, Actas_comite_operaciones
      Classification clase058 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_PLANES                            , level2, clase050);  //   Bogota, Planes
      Classification clase059 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_PLAN_OPERATIVO               , level3, clase058);  //   Bogota, Plan_operativo
      Classification clase060 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_PLAN_FINANCIERO              , level3, clase058);  //   Bogota, Plan_financiero
      Classification clase061 = createClass( tenant1,       Constant.TITLE_BOG_SUBSERIE_PRESUPUESTO                  , level3, clase058);  //   Bogota, Presupuesto
      Classification clase062 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_OPERACIONES                       , level1, clase001);  //   Bogota, Subgerencia de Operaciones
      Classification clase063 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_ACTAS_OPERACIONES                 , level2, clase062);  //   Bogota, Actas Operaciones
      Classification clase064 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , level3, clase063);  //   Bogota, Actas Comite calidad
      Classification clase065 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , level3, clase063);  //   Bogota, Actas Comite planeacion
      Classification clase066 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_CONTRATOS                         , level2, clase062);  //   Bogota, Contratos
      Classification clase067 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_OPER_CONTRATOS_OPERACION    , level3, clase066);  //   Bogota, Contratos de Operacion
      Classification clase068 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_OPER_CONTRATOS_INVERSION    , level3, clase066);  //   Bogota, Contratos de Inversion
      Classification clase069 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_FINANCIERA                        , level1, clase001);  //   Bogota, Subgerencia Financiera
      Classification clase070 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_PRESUPUESTO                       , level2, clase069);  //   Bogota, Presupuesto
      Classification clase071 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_PLANEACION_PPTAL        , level3, clase070);  //   Bogota, Planeacion Presupuestal
      Classification clase072 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_EJECUCION_PPTAL         , level3, clase070);  //   Bogota, Ejecucion Presupuestal
      Classification clase073 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_TESORERIA                         , level2, clase069);  //   Bogota, Tesoreria
      Classification clase074 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_PAGADURIA               , level3, clase073);  //   Bogota, Pagaduria
      Classification clase075 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_INVERSIONES             , level3, clase073);  //   Bogota, Inversiones
      Classification clase076 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_CONTABILIDAD                      , level2, clase069);  //   Bogota, Contabilidad
      Classification clase077 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , level3, clase076);  //   Bogota, Estados Financieros
      Classification clase078 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_FIN_LIBROS_CONTABLES        , level3, clase076);  //   Bogota, Libros contables
      Classification clase079 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_PERSONAL                          , level1, clase001);  //   Bogota, Subgerencia de Personal
      Classification clase080 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_HOJAS_DE_VIDA                     , level2, clase079);  //   Bogota, Hojas de vida
      Classification clase081 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_CANDIDATOS              , level3, clase080);  //   Bogota, Candidatos de personal
      Classification clase082 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_PERSONAL_ACTIVO         , level3, clase080);  //   Bogota, Personal activo
      Classification clase083 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_PENSIONADOS             , level3, clase080);  //   Bogota, Pensionados
      Classification clase084 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_SANCIONES                         , level2, clase079);  //   Bogota, Sanciones de personal
      Classification clase085 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_INVESTIGACIONES         , level3, clase084);  //   Bogota, Investigaciones disciplinarias
      Classification clase086 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_FALLOS_DE_PERSONAL      , level3, clase084);  //   Bogota, Fallos de personal
      Classification clase087 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_EVALUACIONES                      , level2, clase079);  //   Bogota, Evaluaciones de personal
      Classification clase088 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_PER_DESEMPENO               , level3, clase087);  //   Bogota, Evaluaciones de desempeeo
      Classification clase089 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_JURIDICA                          , level1, clase001);  //   Bogota, Subgerencia Juridica
      Classification clase090 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_PROCESOS                          , level2, clase089);  //   Bogota, Procesos juridicos
      Classification clase091 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_JUR_DEMANDAS                , level3, clase090);  //   Bogota, Demandas en curso
      Classification clase092 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_JUR_FALLOS_JUDICIALES       , level3, clase090);  //   Bogota, Demandas en curso
      Classification clase093 = createClass( tenant1,   Constant.TITLE_BOG_OFICINA_ADMINISTRACION                    , level1, clase001);  //   Bogota, Subgerencia Administrativa
      Classification clase094 = createClass( tenant1,     Constant.TITLE_BOG_SERIE_ACTIVOS_FIJOS                     , level2, clase093);  //   Bogota, Activos fijos
      Classification clase095 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_ADM_EDIFICACIONES           , level3, clase094);  //   Bogota, Edificaciones
      Classification clase096 = createClass( tenant1,        Constant.TITLE_BOG_SUBSERIE_ADM_SERVICIOS               , level3, clase094);  //   Bogota, Servicios publicos
      Classification clase097 = createClass( tenant1, Constant.TITLE_SEDE_MEDELLIN                                   , level0, null);      //   Sede Medellin
      Classification clase098 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_SUBGERENCIA                       , level1, clase097);  //   Gerencia Medellin
      Classification clase099 = createClass( tenant1,     Constant.TITLE_MED_SERIE_ACTAS                             , level2, clase098);  //   Medellin, Actas
      Classification clase100 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , level3, clase099);  //   Medellin, Actas_junta_directiva
      Classification clase101 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_GERENCIA        , level3, clase099);  //   Medellin, Actas_comite_gerencia
      Classification clase102 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_FINANCIERO      , level3, clase099);  //   Medellin, Actas_comite_financiero
      Classification clase103 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , level3, clase099);  //   Medellin, Actas_comite_administrativo
      Classification clase104 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_OPERACIONES     , level3, clase099);  //   Medellin, Actas_comite_operaciones
      Classification clase106 = createClass( tenant1,     Constant.TITLE_MED_SERIE_PLANES                            , level2, clase098);  //   Medellin, Planes
      Classification clase107 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_PLAN_OPERATIVO               , level3, clase106);  //   Medellin, Plan_operativo
      Classification clase108 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_PLAN_FINANCIERO              , level3, clase106);  //   Medellin, Plan_financiero
      Classification clase109 = createClass( tenant1,       Constant.TITLE_MED_SUBSERIE_PRESUPUESTO                  , level3, clase106);  //   Medellin, Presupuesto
      Classification clase110 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_OPERACIONES                       , level1, clase001);  //   Medellin, Subgerencia de Operaciones
      Classification clase111 = createClass( tenant1,     Constant.TITLE_MED_SERIE_ACTAS_OPERACIONES                 , level2, clase110);  //   Medellin, Actas Operaciones
      Classification clase112 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , level3, clase111);  //   Medellin, Actas Comite calidad
      Classification clase113 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , level3, clase111);  //   Medellin, Actas Comite planeacion
      Classification clase114 = createClass( tenant1,     Constant.TITLE_MED_SERIE_CONTRATOS                         , level2, clase110);  //   Medellin, Contratos
      Classification clase115 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_OPER_CONTRATOS_OPERACION    , level3, clase114);  //   Medellin, Contratos de Operacion
      Classification clase116 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_OPER_CONTRATOS_INVERSION    , level3, clase114);  //   Medellin, Contratos de Inversion
      Classification clase117 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_FINANCIERA                        , level1, clase001);  //   Medellin, Subgerencia Financiera
      Classification clase118 = createClass( tenant1,     Constant.TITLE_MED_SERIE_PRESUPUESTO                       , level2, clase117);  //   Medellin, Presupuesto
      Classification clase119 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_PLANEACION_PPTAL        , level3, clase118);  //   Medellin, Planeacion Presupuestal
      Classification clase120 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_EJECUCION_PPTAL         , level3, clase118);  //   Medellin, Ejecucion Presupuestal
      Classification clase121 = createClass( tenant1,     Constant.TITLE_MED_SERIE_TESORERIA                         , level2, clase117);  //   Medellin, Tesoreria
      Classification clase122 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_PAGADURIA               , level3, clase121);  //   Medellin, Pagaduria
      Classification clase123 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_INVERSIONES             , level3, clase121);  //   Medellin, Inversiones
      Classification clase124 = createClass( tenant1,     Constant.TITLE_MED_SERIE_CONTABILIDAD                      , level2, clase117);  //   Medellin, Contabilidad
      Classification clase125 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , level3, clase124);  //   Medellin, Estados Financieros
      Classification clase126 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_FIN_LIBROS_CONTABLES        , level3, clase124);  //   Medellin, Libros contables
      Classification clase127 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_PERSONAL                          , level1, clase001);  //   Medellin, Subgerencia de Personal
      Classification clase128 = createClass( tenant1,     Constant.TITLE_MED_SERIE_HOJAS_DE_VIDA                     , level2, clase127);  //   Medellin, Hojas de vida
      Classification clase129 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_CANDIDATOS              , level3, clase128);  //   Medellin, Candidatos de personal
      Classification clase130 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_PERSONAL_ACTIVO         , level3, clase128);  //   Medellin, Personal activo
      Classification clase131 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_PENSIONADOS             , level3, clase128);  //   Medellin, Pensionados
      Classification clase132 = createClass( tenant1,     Constant.TITLE_MED_SERIE_SANCIONES                         , level2, clase127);  //   Medellin, Sanciones de personal
      Classification clase133 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_INVESTIGACIONES         , level3, clase132);  //   Medellin, Investigaciones disciplinarias
      Classification clase134 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_FALLOS_DE_PERSONAL      , level3, clase132);  //   Medellin, Fallos de personal
      Classification clase135 = createClass( tenant1,     Constant.TITLE_MED_SERIE_EVALUACIONES                      , level2, clase127);  //   Medellin, Evaluaciones de personal
      Classification clase136 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_PER_DESEMPENO               , level3, clase135);  //   Medellin, Evaluaciones de desempeeo
      Classification clase137 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_JURIDICA                          , level1, clase001);  //   Medellin, Subgerencia Juridica
      Classification clase138 = createClass( tenant1,     Constant.TITLE_MED_SERIE_PROCESOS                          , level2, clase137);  //   Medellin, Procesos juridicos
      Classification clase139 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_JUR_DEMANDAS                , level3, clase138);  //   Medellin, Demandas en curso
      Classification clase140 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_JUR_FALLOS_JUDICIALES       , level3, clase138);  //   Medellin, Demandas en curso
      Classification clase141 = createClass( tenant1,   Constant.TITLE_MED_OFICINA_ADMINISTRACION                    , level1, clase001);  //   Medellin, Subgerencia Administrativa
      Classification clase142 = createClass( tenant1,     Constant.TITLE_MED_SERIE_ACTIVOS_FIJOS                     , level2, clase141);  //   Medellin, Activos fijos
      Classification clase143 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_ADM_EDIFICACIONES           , level3, clase142);  //   Medellin, Edificaciones
      Classification clase144 = createClass( tenant1,        Constant.TITLE_MED_SUBSERIE_ADM_SERVICIOS               , level3, clase142);  //   Medellin, Servicios publicos
      Classification clase145 = createClass( tenant1, Constant.TITLE_SEDE_CALI                                       , level0, null);      //   Sede Cali
      Classification clase146 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_SUBGERENCIA                       , level1, clase145);  //   Gerencia Cali
      Classification clase147 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_ACTAS                             , level2, clase146);  //   Cali, Actas
      Classification clase148 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , level3, clase147);  //   Cali, Actas_junta_directiva
      Classification clase149 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_GERENCIA        , level3, clase147);  //   Cali, Actas_comite_gerencia
      Classification clase150 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_FINANCIERO      , level3, clase147);  //   Cali, Actas_comite_financiero
      Classification clase151 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , level3, clase147);  //   Cali, Actas_comite_administrativo
      Classification clase152 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_OPERACIONES     , level3, clase147);  //   Cali, Actas_comite_operaciones
      Classification clase154 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_PLANES                            , level2, clase146);  //   Cali, Planes
      Classification clase155 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_PLAN_OPERATIVO               , level3, clase154);  //   Cali, Plan_operativo
      Classification clase156 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_PLAN_FINANCIERO              , level3, clase154);  //   Cali, Plan_financiero
      Classification clase157 = createClass( tenant1,       Constant.TITLE_CAL_SUBSERIE_PRESUPUESTO                  , level3, clase154);  //   Cali, Presupuesto
      Classification clase158 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_OPERACIONES                       , level1, clase001);  //   Cali, Subgerencia de Operaciones
      Classification clase159 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_ACTAS_OPERACIONES                 , level2, clase158);  //   Cali, Actas Operaciones
      Classification clase160 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , level3, clase159);  //   Cali, Actas Comite calidad
      Classification clase161 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , level3, clase159);  //   Cali, Actas Comite planeacion
      Classification clase162 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_CONTRATOS                         , level2, clase158);  //   Cali, Contratos
      Classification clase163 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_OPER_CONTRATOS_OPERACION    , level3, clase162);  //   Cali, Contratos de Operacion
      Classification clase164 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_OPER_CONTRATOS_INVERSION    , level3, clase162);  //   Cali, Contratos de Inversion
      Classification clase165 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_FINANCIERA                        , level1, clase001);  //   Cali, Subgerencia Financiera
      Classification clase166 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_PRESUPUESTO                       , level2, clase165);  //   Cali, Presupuesto
      Classification clase167 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_PLANEACION_PPTAL        , level3, clase166);  //   Cali, Planeacion Presupuestal
      Classification clase168 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_EJECUCION_PPTAL         , level3, clase166);  //   Cali, Ejecucion Presupuestal
      Classification clase169 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_TESORERIA                         , level2, clase165);  //   Cali, Tesoreria
      Classification clase170 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_PAGADURIA               , level3, clase169);  //   Cali, Pagaduria
      Classification clase171 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_INVERSIONES             , level3, clase169);  //   Cali, Inversiones
      Classification clase172 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_CONTABILIDAD                      , level2, clase165);  //   Cali, Contabilidad
      Classification clase173 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , level3, clase172);  //   Cali, Estados Financieros
      Classification clase174 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_FIN_LIBROS_CONTABLES        , level3, clase172);  //   Cali, Libros contables
      Classification clase175 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_PERSONAL                          , level1, clase001);  //   Cali, Subgerencia de Personal
      Classification clase176 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_HOJAS_DE_VIDA                     , level2, clase175);  //   Cali, Hojas de vida
      Classification clase177 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_CANDIDATOS              , level3, clase176);  //   Cali, Candidatos de personal
      Classification clase178 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_PERSONAL_ACTIVO         , level3, clase176);  //   Cali, Personal activo
      Classification clase179 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_PENSIONADOS             , level3, clase176);  //   Cali, Pensionados
      Classification clase180 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_SANCIONES                         , level2, clase175);  //   Cali, Sanciones de personal
      Classification clase181 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_INVESTIGACIONES         , level3, clase180);  //   Cali, Investigaciones disciplinarias
      Classification clase182 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_FALLOS_DE_PERSONAL      , level3, clase180);  //   Cali, Fallos de personal
      Classification clase183 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_EVALUACIONES                      , level2, clase175);  //   Cali, Evaluaciones de personal
      Classification clase184 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_PER_DESEMPENO               , level3, clase183);  //   Cali, Evaluaciones de desempeeo
      Classification clase185 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_JURIDICA                          , level1, clase001);  //   Cali, Subgerencia Juridica
      Classification clase186 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_PROCESOS                          , level2, clase185);  //   Cali, Procesos juridicos
      Classification clase187 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_JUR_DEMANDAS                , level3, clase186);  //   Cali, Demandas en curso
      Classification clase188 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_JUR_FALLOS_JUDICIALES       , level3, clase186);  //   Cali, Demandas en curso
      Classification clase189 = createClass( tenant1,   Constant.TITLE_CAL_OFICINA_ADMINISTRACION                    , level1, clase001);  //   Cali, Subgerencia Administrativa
      Classification clase190 = createClass( tenant1,     Constant.TITLE_CAL_SERIE_ACTIVOS_FIJOS                     , level2, clase189);  //   Cali, Activos fijos
      Classification clase191 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_ADM_EDIFICACIONES           , level3, clase190);  //   Cali, Edificaciones
      Classification clase192 = createClass( tenant1,        Constant.TITLE_CAL_SUBSERIE_ADM_SERVICIOS               , level3, clase190);  //   Cali, Servicios publicos
      Classification clase193 = createClass( tenant1, Constant.TITLE_SEDE_BARRANQUILLA                               , level3, null);      //   Sede Barranquilla
      Classification clase194 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_SUBGERENCIA                       , level1, clase193);  //   Gerencia Barranquilla
      Classification clase195 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_ACTAS                             , level2, clase194);  //   Barranquilla, Actas
      Classification clase196 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , level3, clase195);  //   Barranquilla, Actas_junta_directiva
      Classification clase197 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_GERENCIA        , level3, clase195);  //   Barranquilla, Actas_comite_gerencia
      Classification clase198 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_FINANCIERO      , level3, clase195);  //   Barranquilla, Actas_comite_financiero
      Classification clase199 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , level3, clase195);  //   Barranquilla, Actas_comite_administrativo
      Classification clase200 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_OPERACIONES     , level3, clase195);  //   Barranquilla, Actas_comite_operaciones
      Classification clase202 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_PLANES                            , level2, clase194);  //   Barranquilla, Planes
      Classification clase203 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_PLAN_OPERATIVO               , level3, clase202);  //   Barranquilla, Plan_operativo
      Classification clase204 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_PLAN_FINANCIERO              , level3, clase202);  //   Barranquilla, Plan_financiero
      Classification clase205 = createClass( tenant1,       Constant.TITLE_BAQ_SUBSERIE_PRESUPUESTO                  , level3, clase202);  //   Barranquilla, Presupuesto
      Classification clase206 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_OPERACIONES                       , level1, clase001);  //   Barranquilla, Subgerencia de Operaciones
      Classification clase207 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_ACTAS_OPERACIONES                 , level2, clase206);  //   Barranquilla, Actas Operaciones
      Classification clase208 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , level3, clase207);  //   Barranquilla, Actas Comite calidad
      Classification clase209 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , level3, clase207);  //   Barranquilla, Actas Comite planeacion
      Classification clase210 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_CONTRATOS                         , level2, clase206);  //   Barranquilla, Contratos
      Classification clase211 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_OPER_CONTRATOS_OPERACION    , level3, clase210);  //   Barranquilla, Contratos de Operacion
      Classification clase212 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_OPER_CONTRATOS_INVERSION    , level3, clase210);  //   Barranquilla, Contratos de Inversion
      Classification clase213 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_FINANCIERA                        , level1, clase001);  //   Barranquilla, Subgerencia Financiera
      Classification clase214 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_PRESUPUESTO                       , level2, clase213);  //   Barranquilla, Presupuesto
      Classification clase215 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_PLANEACION_PPTAL        , level3, clase214);  //   Barranquilla, Planeacion Presupuestal
      Classification clase216 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_EJECUCION_PPTAL         , level3, clase214);  //   Barranquilla, Ejecucion Presupuestal
      Classification clase217 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_TESORERIA                         , level2, clase213);  //   Barranquilla, Tesoreria
      Classification clase218 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_PAGADURIA               , level3, clase217);  //   Barranquilla, Pagaduria
      Classification clase219 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_INVERSIONES             , level3, clase217);  //   Barranquilla, Inversiones
      Classification clase220 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_CONTABILIDAD                      , level2, clase213);  //   Barranquilla, Contabilidad
      Classification clase221 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , level3, clase220);  //   Barranquilla, Estados Financieros
      Classification clase222 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_FIN_LIBROS_CONTABLES        , level3, clase220);  //   Barranquilla, Libros contables
      Classification clase223 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_PERSONAL                          , level1, clase001);  //   Barranquilla, Subgerencia de Personal
      Classification clase224 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_HOJAS_DE_VIDA                     , level2, clase223);  //   Barranquilla, Hojas de vida
      Classification clase225 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_CANDIDATOS              , level3, clase224);  //   Barranquilla, Candidatos de personal
      Classification clase226 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_PERSONAL_ACTIVO         , level3, clase224);  //   Barranquilla, Personal activo
      Classification clase227 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_PENSIONADOS             , level3, clase224);  //   Barranquilla, Pensionados
      Classification clase228 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_SANCIONES                         , level2, clase223);  //   Barranquilla, Sanciones de personal
      Classification clase229 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_INVESTIGACIONES         , level3, clase228);  //   Barranquilla, Investigaciones disciplinarias
      Classification clase230 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_FALLOS_DE_PERSONAL      , level3, clase228);  //   Barranquilla, Fallos de personal
      Classification clase231 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_EVALUACIONES                      , level2, clase223);  //   Barranquilla, Evaluaciones de personal
      Classification clase232 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_PER_DESEMPENO               , level3, clase231);  //   Barranquilla, Evaluaciones de desempeeo
      Classification clase233 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_JURIDICA                          , level1, clase001);  //   Barranquilla, Subgerencia Juridica
      Classification clase234 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_PROCESOS                          , level2, clase233);  //   Barranquilla, Procesos juridicos
      Classification clase235 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_JUR_DEMANDAS                , level3, clase234);  //   Barranquilla, Demandas en curso
      Classification clase236 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_JUR_FALLOS_JUDICIALES       , level3, clase234);  //   Barranquilla, Demandas en curso
      Classification clase237 = createClass( tenant1,   Constant.TITLE_BAQ_OFICINA_ADMINISTRACION                    , level1, clase001);  //   Barranquilla, Subgerencia Administrativa
      Classification clase238 = createClass( tenant1,     Constant.TITLE_BAQ_SERIE_ACTIVOS_FIJOS                     , level2, clase237);  //   Barranquilla, Activos fijos
      Classification clase239 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_ADM_EDIFICACIONES           , level3, clase238);  //   Barranquilla, Edificaciones
      Classification clase240 = createClass( tenant1,        Constant.TITLE_BAQ_SUBSERIE_ADM_SERVICIOS               , level3, clase238);  //   Barranquilla, Servicios publicos
      Classification clase241 = createClass( tenant1, Constant.TITLE_SEDE_BUCARAMANGA                                , level3, null);      //   Sede Bucaramanga
      Classification clase242 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_SUBGERENCIA                       , level1, clase241);  //   Gerencia Bucaramanga
      Classification clase243 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_ACTAS                             , level2, clase242);  //   Bucaramanga, Actas
      Classification clase244 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , level3, clase243);  //   Bucaramanga, Actas_junta_directiva
      Classification clase245 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_GERENCIA        , level3, clase243);  //   Bucaramanga, Actas_comite_gerencia
      Classification clase246 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_FINANCIERO      , level3, clase243);  //   Bucaramanga, Actas_comite_financiero
      Classification clase247 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , level3, clase243);  //   Bucaramanga, Actas_comite_administrativo
      Classification clase248 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_OPERACIONES     , level3, clase243);  //   Bucaramanga, Actas_comite_operaciones
      Classification clase250 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_PLANES                            , level2, clase242);  //   Bucaramanga, Planes
      Classification clase251 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_PLAN_OPERATIVO               , level3, clase250);  //   Bucaramanga, Plan_operativo
      Classification clase252 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_PLAN_FINANCIERO              , level3, clase250);  //   Bucaramanga, Plan_financiero
      Classification clase253 = createClass( tenant1,       Constant.TITLE_BUC_SUBSERIE_PRESUPUESTO                  , level3, clase250);  //   Bucaramanga, Presupuesto
      Classification clase254 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_OPERACIONES                       , level1, clase001);  //   Bucaramanga, Subgerencia de Operaciones
      Classification clase255 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_ACTAS_OPERACIONES                 , level2, clase254);  //   Bucaramanga, Actas Operaciones
      Classification clase256 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , level3, clase255);  //   Bucaramanga, Actas Comite calidad
      Classification clase257 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , level3, clase255);  //   Bucaramanga, Actas Comite planeacion
      Classification clase258 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_CONTRATOS                         , level2, clase254);  //   Bucaramanga, Contratos
      Classification clase259 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_OPER_CONTRATOS_OPERACION    , level3, clase258);  //   Bucaramanga, Contratos de Operacion
      Classification clase260 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_OPER_CONTRATOS_INVERSION    , level3, clase258);  //   Bucaramanga, Contratos de Inversion
      Classification clase261 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_FINANCIERA                        , level1, clase001);  //   Bucaramanga, Subgerencia Financiera
      Classification clase262 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_PRESUPUESTO                       , level2, clase261);  //   Bucaramanga, Presupuesto
      Classification clase263 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_PLANEACION_PPTAL        , level3, clase262);  //   Bucaramanga, Planeacion Presupuestal
      Classification clase264 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_EJECUCION_PPTAL         , level3, clase262);  //   Bucaramanga, Ejecucion Presupuestal
      Classification clase265 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_TESORERIA                         , level2, clase261);  //   Bucaramanga, Tesoreria
      Classification clase266 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_PAGADURIA               , level3, clase265);  //   Bucaramanga, Pagaduria
      Classification clase267 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_INVERSIONES             , level3, clase265);  //   Bucaramanga, Inversiones
      Classification clase268 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_CONTABILIDAD                      , level2, clase261);  //   Bucaramanga, Contabilidad
      Classification clase269 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , level3, clase268);  //   Bucaramanga, Estados Financieros
      Classification clase270 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_FIN_LIBROS_CONTABLES        , level3, clase268);  //   Bucaramanga, Libros contables
      Classification clase271 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_PERSONAL                          , level1, clase001);  //   Bucaramanga, Subgerencia de Personal
      Classification clase272 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_HOJAS_DE_VIDA                     , level2, clase271);  //   Bucaramanga, Hojas de vida
      Classification clase273 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_CANDIDATOS              , level3, clase272);  //   Bucaramanga, Candidatos de personal
      Classification clase274 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_PERSONAL_ACTIVO         , level3, clase272);  //   Bucaramanga, Personal activo
      Classification clase275 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_PENSIONADOS             , level3, clase272);  //   Bucaramanga, Pensionados
      Classification clase276 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_SANCIONES                         , level2, clase271);  //   Bucaramanga, Sanciones de personal
      Classification clase277 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_INVESTIGACIONES         , level3, clase276);  //   Bucaramanga, Investigaciones disciplinarias
      Classification clase278 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_FALLOS_DE_PERSONAL      , level3, clase276);  //   Bucaramanga, Fallos de personal
      Classification clase279 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_EVALUACIONES                      , level2, clase271);  //   Bucaramanga, Evaluaciones de personal
      Classification clase280 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_PER_DESEMPENO               , level3, clase279);  //   Bucaramanga, Evaluaciones de desempeeo
      Classification clase281 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_JURIDICA                          , level1, clase001);  //   Bucaramanga, Subgerencia Juridica
      Classification clase282 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_PROCESOS                          , level2, clase281);  //   Bucaramanga, Procesos juridicos
      Classification clase283 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_JUR_DEMANDAS                , level3, clase282);  //   Bucaramanga, Demandas en curso
      Classification clase284 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_JUR_FALLOS_JUDICIALES       , level3, clase282);  //   Bucaramanga, Demandas en curso
      Classification clase285 = createClass( tenant1,   Constant.TITLE_BUC_OFICINA_ADMINISTRACION                    , level1, clase001);  //   Bucaramanga, Subgerencia Administrativa
      Classification clase286 = createClass( tenant1,     Constant.TITLE_BUC_SERIE_ACTIVOS_FIJOS                     , level2, clase285);  //   Bucaramanga, Activos fijos
      Classification clase287 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_ADM_EDIFICACIONES           , level3, clase286);  //   Bucaramanga, Edificaciones
      Classification clase288 = createClass( tenant1,        Constant.TITLE_BUC_SUBSERIE_ADM_SERVICIOS               , level3, clase286);  //   Bucaramanga, Servicios publicos
      Classification clase289 = createClass( tenant1, Constant.TITLE_SEDE_CARTAGENA                                  , level3, null);      //   Sede Cartagena
      Classification clase290 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_SUBGERENCIA                       , level1, clase289);  //   Gerencia Cartagena
      Classification clase291 = createClass( tenant1,     Constant.TITLE_CTG_SERIE__ACTAS                            , level2, clase290);  //   Cartagena, Actas
      Classification clase292 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , level3, clase291);  //   Cartagena, Actas_junta_directiva
      Classification clase293 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_GERENCIA        , level3, clase291);  //   Cartagena, Actas_comite_gerencia
      Classification clase294 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_FINANCIERO      , level3, clase291);  //   Cartagena, Actas_comite_financiero
      Classification clase295 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , level3, clase291);  //   Cartagena, Actas_comite_administrativo
      Classification clase296 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_OPERACIONES     , level3, clase291);  //   Cartagena, Actas_comite_operaciones
      Classification clase298 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_PLANES                            , level2, clase290);  //   Cartagena, Planes
      Classification clase299 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_PLAN_OPERATIVO               , level3, clase298);  //   Cartagena, Plan_operativo
      Classification clase300 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_PLAN_FINANCIERO              , level3, clase298);  //   Cartagena, Plan_financiero
      Classification clase301 = createClass( tenant1,       Constant.TITLE_CTG_SUBSERIE_PRESUPUESTO                  , level3, clase298);  //   Cartagena, Presupuesto
      Classification clase302 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_OPERACIONES                       , level1, clase001);  //   Cartagena, Subgerencia de Operaciones
      Classification clase303 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_ACTAS_OPERACIONES                 , level2, clase302);  //   Cartagena, Actas Operaciones
      Classification clase304 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , level3, clase303);  //   Cartagena, Actas Comite calidad
      Classification clase305 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , level3, clase303);  //   Cartagena, Actas Comite planeacion
      Classification clase306 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_CONTRATOS                         , level2, clase302);  //   Cartagena, Contratos
      Classification clase307 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_OPER_CONTRATOS_OPERACION    , level3, clase306);  //   Cartagena, Contratos de Operacion
      Classification clase308 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_OPER_CONTRATOS_INVERSION    , level3, clase306);  //   Cartagena, Contratos de Inversion
      Classification clase309 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_FINANCIERA                        , level1, clase001);  //   Cartagena, Subgerencia Financiera
      Classification clase310 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_PRESUPUESTO                       , level2, clase309);  //   Cartagena, Presupuesto
      Classification clase311 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_PLANEACION_PPTAL        , level3, clase310);  //   Cartagena, Planeacion Presupuestal
      Classification clase312 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_EJECUCION_PPTAL         , level3, clase310);  //   Cartagena, Ejecucion Presupuestal
      Classification clase313 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_TESORERIA                         , level2, clase309);  //   Cartagena, Tesoreria
      Classification clase314 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_PAGADURIA               , level3, clase313);  //   Cartagena, Pagaduria
      Classification clase315 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_INVERSIONES             , level3, clase313);  //   Cartagena, Inversiones
      Classification clase316 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_CONTABILIDAD                      , level2, clase309);  //   Cartagena, Contabilidad
      Classification clase317 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , level3, clase316);  //   Cartagena, Estados Financieros
      Classification clase318 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_FIN_LIBROS_CONTABLES        , level3, clase316);  //   Cartagena, Libros contables
      Classification clase319 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_PERSONAL                          , level1, clase001);  //   Cartagena, Subgerencia de Personal
      Classification clase320 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_HOJAS_DE_VIDA                     , level2, clase319);  //   Cartagena, Hojas de vida
      Classification clase321 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_CANDIDATOS              , level3, clase320);  //   Cartagena, Candidatos de personal
      Classification clase322 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_PERSONAL_ACTIVO         , level3, clase320);  //   Cartagena, Personal activo
      Classification clase323 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_PENSIONADOS             , level3, clase320);  //   Cartagena, Pensionados
      Classification clase324 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_SANCIONES                         , level2, clase319);  //   Cartagena, Sanciones de personal
      Classification clase325 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_INVESTIGACIONES         , level3, clase324);  //   Cartagena, Investigaciones disciplinarias
      Classification clase326 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_FALLOS_DE_PERSONAL      , level3, clase324);  //   Cartagena, Fallos de personal
      Classification clase327 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_EVALUACIONES                      , level2, clase319);  //   Cartagena, Evaluaciones de personal
      Classification clase328 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_PER_DESEMPENO               , level3, clase327);  //   Cartagena, Evaluaciones de desempeeo
      Classification clase329 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_JURIDICA                          , level1, clase001);  //   Cartagena, Subgerencia Juridica
      Classification clase330 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_PROCESOS                          , level2, clase329);  //   Cartagena, Procesos juridicos
      Classification clase331 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_JUR_DEMANDAS                , level3, clase330);  //   Cartagena, Demandas en curso
      Classification clase332 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_JUR_FALLOS_JUDICIALES       , level3, clase330);  //   Cartagena, Demandas en curso
      Classification clase333 = createClass( tenant1,   Constant.TITLE_CTG_OFICINA_ADMINISTRACION                    , level1, clase001);  //   Cartagena, Subgerencia Administrativa
      Classification clase334 = createClass( tenant1,     Constant.TITLE_CTG_SERIE_ACTIVOS_FIJOS                     , level2, clase333);  //   Cartagena, Activos fijos
      Classification clase335 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_ADM_EDIFICACIONES           , level3, clase334);  //   Cartagena, Edificaciones
      Classification clase336 = createClass( tenant1,        Constant.TITLE_CTG_SUBSERIE_ADM_SERVICIOS               , level3, clase334);  //   Cartagena, Servicios publicos


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
    //  objectToProtectRepository.saveAndFlush( operation.getObjectToProtect());
      Operation savedOperation = operationRepository.save(operation);
      return savedOperation;
   }//createObject


   private Classification createClass( Tenant tenant,  String name, Level level, Classification parent)
   {
      Classification classificationClass = new Classification( level, name, parent, new ObjectToProtect());

      classificationClass.setTenant(tenant);

      Level nivel = classificationClass.getLevel();
      if ( !nivel.isPersisted())
      {
         Level newLevel = levelRepository.findByLevel(nivel.getOrden());
         if( newLevel == null || !nivel.getOrden().equals(newLevel.getOrden()))
         {
            nivel.setTenant(tenant);
            Schema schema = nivel.getSchema();
            if ( !schema.isPersisted())
            {
               schema.setTenant(tenant);
               schemaRepository.saveAndFlush(schema);
            }
            levelRepository.saveAndFlush(nivel);
         }else {
            classificationClass.setLevel(newLevel);
         }
      }
      claseRepository.saveAndFlush(classificationClass);
      return classificationClass;
   }//createClass

   private Metadata createMeta(String name, Type type, String range)
   {
      Metadata meta = new Metadata(name, type, range);
      metadataRepository.saveAndFlush(meta);
      return meta;
   }//createMeta

   private Field createField(String name, Metadata meta, boolean visible, boolean readOnly, boolean required, int sortOrder, int columns)
   {
      Field field = new Field(name, meta, visible, readOnly, required, sortOrder, columns);
      fieldRepository.saveAndFlush(field);
      return field;
   }//createField

   private Schema createSchema(String name)
   {
      Schema schema = new Schema(name, new TreeSet<>());
      schemaRepository.saveAndFlush(schema);
      return schema;
   }//createSchema




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
