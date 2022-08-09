package com.f.thoth.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.metadata.Type;
import com.f.thoth.backend.data.gdoc.numerator.Numerator;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.backend.jcr.Repo;
import com.f.thoth.backend.repositories.BaseExpedienteRepository;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.DocumentTypeRepository;
import com.f.thoth.backend.repositories.ExpedienteGroupRepository;
import com.f.thoth.backend.repositories.ExpedienteIndexRepository;
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
import com.f.thoth.backend.repositories.SingleUserRepository;
import com.f.thoth.backend.repositories.TenantRepository;
import com.f.thoth.backend.repositories.UserGroupRepository;
import com.f.thoth.backend.repositories.UserRepository;
import com.f.thoth.backend.repositories.VolumeInstanceRepository;
import com.f.thoth.backend.repositories.VolumeRepository;
import com.f.thoth.backend.service.TenantService;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class DataGenerator implements HasLogger
{
   private static com.f.thoth.backend.data.security.Role  adminRole;

   private final Random random = new Random(1L);

   private Tenant                        tenant1, tenant2;
   private TenantService                 tenantService;
   private TenantRepository              tenantRepository;
   private RoleRepository                roleRepository;
   private UserRepository                userRepository;
   private OperationRepository           operationRepository;
   private PasswordEncoder               passwordEncoder;
   private ClassificationRepository      claseRepository;
   private ExpedienteIndexRepository     expedienteIndexRepository;
   private ExpedienteGroupRepository     expedienteGroupRepository;
   private VolumeRepository              volumeRepository;
   private VolumeInstanceRepository      volumeInstanceRepository;
   private LevelRepository               levelRepository;
   private SchemaRepository              schemaRepository;
   private SchemaValuesRepository        schemaValuesRepository;
   private MetadataRepository            metadataRepository;
   private FieldRepository               fieldRepository;
   private DocumentTypeRepository        documentTypeRepository;
   private RetentionRepository           retentionRepository;
   private SingleUserRepository          singleUserRepository;
   private UserGroupRepository           userGroupRepository;
   private Numerator                     numerator;
   private Level[]                       levels;
   private com.f.thoth.backend.data.security.User  currentUser;

   @Autowired
   public DataGenerator(TenantService tenantService, OrderRepository orderRepository, UserRepository userRepository,
         ProductRepository productRepository, PickupLocationRepository pickupLocationRepository,
         TenantRepository tenantRepository, RoleRepository roleRepository, OperationRepository operationRepository,
         ClassificationRepository claseRepository, BaseExpedienteRepository baseExpedienteRepository, ExpedienteIndexRepository expedienteIndexRepository,
         ExpedienteGroupRepository expedienteGroupRepository, VolumeRepository volumeRepository, VolumeInstanceRepository volumeInstanceRepository,
         MetadataRepository metadataRepository, FieldRepository fieldRepository, SchemaRepository schemaRepository, DocumentTypeRepository documentTypeRepository,
         SchemaValuesRepository schemaValuesRepository, LevelRepository levelRepository, RetentionRepository retentionRepository,
         UserGroupRepository userGroupRepository, SingleUserRepository singleUserRepository, Numerator numerator, PasswordEncoder passwordEncoder)
   {
      this.tenantService                 = tenantService;
      this.userRepository                = userRepository;
      this.tenantRepository              = tenantRepository;
      this.roleRepository                = roleRepository;
      this.operationRepository           = operationRepository;
      this.claseRepository               = claseRepository;
      this.expedienteIndexRepository     = expedienteIndexRepository;
      this.expedienteGroupRepository     = expedienteGroupRepository;
      this.volumeRepository              = volumeRepository;
      this.volumeInstanceRepository      = volumeInstanceRepository;
      this.levelRepository               = levelRepository;
      this.schemaRepository              = schemaRepository;
      this.schemaValuesRepository        = schemaValuesRepository;
      this.fieldRepository               = fieldRepository;
      this.metadataRepository            = metadataRepository;
      this.documentTypeRepository        = documentTypeRepository;
      this.numerator                     = numerator;
      this.passwordEncoder               = passwordEncoder;
      this.retentionRepository           = retentionRepository;
      this.singleUserRepository          = singleUserRepository;
      this.userGroupRepository           = userGroupRepository;

   }//DataGenerator

   @SuppressWarnings("unused")
   @PostConstruct
   public void loadData()
   {
      try
      {
         if (userRepository.count() != 0L)
         {  getLogger().info("Using existing database");
            return;
         }

         getLogger().info("Generating demo data");

         // ------------ Cree los tenants y sus roles ----------------------------
         getLogger().info("... generating tenants");
         createTenants(tenantService);

         getLogger().info("... generating roles");
         String[] roles1 = {"gerente", "admin", "barista", "baker", "supervisor", "operador", "público"};
         createRoles(tenant1, roles1);

         String[] roles2 = {"CEO", "Admin2", "CFO", "CIO", "COO"};
         createRoles(tenant2, roles2);

         // ----------- Respetar este orden para la inicializacion de los siguientes default -------------
         //TODO:  Eliminar estos defaults tan pronto sea posible
         getLogger().info("... generating defaults");

         Schema.EMPTY.setTenant(tenant1);
         Schema.EMPTY.buildCode();
         schemaRepository.saveAndFlush(Schema.EMPTY);
         SchemaValues.EMPTY.setTenant(tenant1);
         schemaValuesRepository.saveAndFlush(SchemaValues.EMPTY);

         Level.DEFAULT.setTenant(tenant1);
         Level.DEFAULT.buildCode();
         levelRepository.saveAndFlush(Level.DEFAULT);

         Retention.DEFAULT.setTenant(tenant1);
         Retention.DEFAULT.buildCode();
         retentionRepository.saveAndFlush(Retention.DEFAULT);

         // ----------------------- Cree un conjunto de metadatos, campos y esquemas de metadatos ----------------------
         getLogger().info("... generating metadata");
         levels = createMetadata();

         // -----------------  Registre las operaciones posibles en el sistema ----------------------------
         getLogger().info("... generating operations" );
         OperationGenerator  opGenerator = new OperationGenerator(operationRepository);
         opGenerator.registerOperations(tenant1);


         // ------------------ Genere un conjunto de usuarios y grupos de usuarios -------------------------------
         getLogger().info("... generating users");
         createAdmin(userRepository, singleUserRepository, passwordEncoder);
         User baker   = createBaker  (userRepository, passwordEncoder);
         User barista = createBarista(userRepository, passwordEncoder);


         // -----------------  Inicialice el árbol de clasificacion documental -----------------------------
         getLogger().info("... generating classification classes" );
         ClassificationGenerator classificationGenerator =
             new ClassificationGenerator(currentUser, claseRepository, levelRepository, schemaRepository, numerator, levels);
         classificationGenerator.registerClasses(tenant1);

         // Create users and users groups
         getLogger().info("... generating users");
         createDeletableUsers(userRepository, passwordEncoder);

         getLogger().info("... generating user groups");
         LocalDate now = LocalDate.now();
         LocalDate yearStart =  now.minusDays(now.getDayOfYear());
         LocalDate yearEnd   =  yearStart.plusMonths(12);
         UserGroup g0100 = createUserGroup(tenant1, "Grupo 0100", Parm.DEFAULT_CATEGORY, null,  yearStart, yearEnd, false);
         UserGroup g0110 = createUserGroup(tenant1, "Grupo 0101", Parm.DEFAULT_CATEGORY, g0100, yearStart, yearEnd, false);
         UserGroup g0120 = createUserGroup(tenant1, "Grupo 0102", Parm.DEFAULT_CATEGORY, g0100, yearStart, yearEnd, false);
         UserGroup g0130 = createUserGroup(tenant1, "Grupo 0103", Parm.DEFAULT_CATEGORY, g0100, yearStart, yearEnd, false);
         UserGroup g0200 = createUserGroup(tenant1, "Grupo 0200", Parm.DEFAULT_CATEGORY, null,  yearStart, yearEnd, false);
         UserGroup g0210 = createUserGroup(tenant1, "Grupo 0201", Parm.DEFAULT_CATEGORY, g0200, yearStart, yearEnd, false);


         // -----------------  Generando expedientes y documentos de prueba
         getLogger().info("... generating expedientes and documents");
         BufferedReader expedienteNamesReader = openFile("data/theNames.txt");
         BufferedReader documentAsuntosReader = openFile("data/documentAsuntos.txt");
         ExpedienteGenerator  expedienteGenerator =
               new ExpedienteGenerator( tenant1, currentUser,
                     claseRepository, expedienteIndexRepository,
                     expedienteGroupRepository, documentTypeRepository,
                     volumeRepository, volumeInstanceRepository, schemaRepository,
                     expedienteNamesReader, documentAsuntosReader
                     );
         int nExpedientes = expedienteGenerator.registerExpedientes(tenant1);
         getLogger().info("    >>> End expedientes generation. "+ nExpedientes+ " expedientes generated");
         getLogger().info("... Generated evidentia test data");

      } catch (Exception e)
      {
         getLogger().error("*** No pudo inicializar la aplicacion. Causa\n"+ e.getMessage());
         System.exit(-1);
      }

   }//loadData


   private BufferedReader  openFile(String fileName)
   {
      /*
       * Paths may be used with the Files class to operate on files, directories, and other types of files.
       * For example, suppose we want a BufferedReader to read text from a file "access.log".
       * The file is located in a directory "logs" relative to the current working directory and is UTF-8 encoded.
       *
       * Path path = FileSystems.getDefault().getPath("logs", "access.log");
       * BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
       */
      BufferedReader theReader = null;
      try
      {
         Resource resource = new ClassPathResource(fileName);
         File theFile    = resource.getFile();
         theReader       = new BufferedReader( new FileReader(theFile));
         getLogger().info("    >>> Opened ["+ fileName+ "]");
      }catch( Exception e)
      {  throw new IllegalStateException("No pudo abrir archivo ["+ fileName+ "]. Causa\n"+ e.getMessage());
      }
      return theReader;
   }//openFile



   private void createTenants (TenantService tenantService)
         throws RepositoryException, UnknownHostException
   {
      tenant1 = createTenant(tenantRepository, "FCN");
      tenant2 = createTenant(tenantRepository, "SEI");
   }//createTenants


   private void createRoles( Tenant tenant, String[] roleName)
   {
      for( String r: roleName)
      {
         com.f.thoth.backend.data.security.Role role = createRole(tenant, r);
         roleRepository.saveAndFlush(role);
         tenant.addRole(role);
         if (role.getName().equals("admin"))
         adminRole = role;
      }
   }//createRoles


   private Level[] createMetadata()
   {
      Metadata nameMeta =        createMeta ("String", Type.STRING, "length > 0");
      Field    nameField       = createField("Nombre",         nameMeta, true, false, false, 1, 2);
      Field    tenantField     = createField("Tenant",         nameMeta, true, false, false, 1, 2);
      Field    bossField       = createField("Jefe",           nameMeta, true, false, false, 2, 2);
      Field    commitmentField = createField("Obligacion",     nameMeta, true, false, false, 1, 2);
      Field    dispatchField   = createField("Despacho",       nameMeta, true, true,  false, 2, 1);
      Field    idField         = createField("Identificacion", nameMeta, true, false, false, 1, 1);
      Field    createdByField  = createField("Autor",          nameMeta, true, false, false, 2, 1);
      Field    conceptField    = createField("Concepto",       nameMeta, false,false, false, 2, 1);
      Field    remiteField     = createField("Remitente",      nameMeta, true, false, false, 3, 2);
      Field    asuntoField     = createField("Asunto",         nameMeta, true, false, true,  3, 3);
      Field    referenceField  = createField("Referencia",     nameMeta, true, false, true,  4, 1);

      Metadata dateMeta        = createMeta ("Fecha", Type.DATETIME, "not null");
      Field    fromField       = createField("Desde",      dateMeta, true, false, false, 3, 2);
      Field    toField         = createField("Hasta",      dateMeta, true, false, false, 4, 2);
      Field    dateField       = createField("Fecha",      dateMeta, true, true,  false, 3, 2);
      Field    dueDate         = createField("APagarEn",   dateMeta, true, true,  false, 2, 2);
      Field    paidDate        = createField("PagadoEn",   dateMeta, true, false, false, 3, 2);
      Field    creationDateField  = createField("CreadoEn",   dateMeta, true, false, false, 3, 2);

      Metadata enumMeta   = createMeta ("Color",    Type.ENUM, "Verde;Rojo;Azul;Magenta;Cyan");
      Field    colorField = createField("Colores",   enumMeta, true, false, false, 5, 1);

      Metadata claseMeta     = createMeta ("Security", Type.ENUM, "Restringido;Confidencial;Interno;Público");
      Field    securityField = createField("Seguridad", claseMeta, true, false, false, 5, 1);

      Metadata intMeta   = createMeta ("Entero", Type.INTEGER, " >0; < 100");
      Field    cantField = createField("Cantidad", intMeta, true, false, false, 5, 1);
      Field    edadField = createField("Edad",     intMeta, true, true,  false, 6, 1);

      Metadata decMeta   = createMeta ("Decimal", Type.DECIMAL," >= 0.0");
      Field    ratioField= createField("Razon", decMeta, true, false, false, 7, 1);
      Field    valueField= createField("Valor", decMeta, true, false, false, 4, 1);

      Schema   docSchema =  createSchema("Document");
      docSchema.addField(tenantField);
      docSchema.addField(idField);
      docSchema.addField(createdByField);
      docSchema.addField(asuntoField);
      docSchema.addField(creationDateField);
      docSchema.addField(referenceField);
      schemaRepository.saveAndFlush(docSchema);

      Schema  sedeSchema = createSchema("Sede");
      sedeSchema.addField(fromField);
      sedeSchema.addField(toField);
      sedeSchema.addField(colorField);
      sedeSchema.addField(securityField);
      schemaRepository.saveAndFlush(sedeSchema);

      Schema  commitmentSchema = createSchema("Obligacion");
      commitmentSchema.addField(idField);
      commitmentSchema.addField(conceptField);
      commitmentSchema.addField(remiteField);
      commitmentSchema.addField(valueField);
      schemaRepository.saveAndFlush(commitmentSchema);

      Schema invoiceSchema= createSchema("Factura");
      invoiceSchema.addField(commitmentField);
      invoiceSchema.addField(dueDate);
      invoiceSchema.addField(valueField);
      schemaRepository.saveAndFlush(invoiceSchema);

      Schema paymentSchema= createSchema("Pago");
      paymentSchema.addField(idField);
      paymentSchema.addField(valueField);
      paymentSchema.addField(paidDate);
      schemaRepository.saveAndFlush(paymentSchema);

      Schema dispatchSchema=  createSchema("Remision");
      dispatchSchema.addField(idField);
      dispatchSchema.addField(commitmentField);
      dispatchSchema.addField(dateField);
      schemaRepository.saveAndFlush(dispatchSchema);

      Schema receiptSchema=  createSchema("Recibo");
      receiptSchema.addField(idField);
      receiptSchema.addField(dispatchField);
      receiptSchema.addField(dateField);
      schemaRepository.saveAndFlush(receiptSchema);

      Schema   officeSchema = createSchema("Office");
      officeSchema.addField(bossField);
      officeSchema.addField(fromField);
      officeSchema.addField(toField);
      officeSchema.addField(colorField);
      schemaRepository.saveAndFlush(officeSchema);

      Schema   seriesSchema = createSchema("Series");
      seriesSchema.addField(fromField);
      seriesSchema.addField(toField);
      seriesSchema.addField(securityField);
      schemaRepository.saveAndFlush(seriesSchema);

      Schema   otherSchema = createSchema("Other");
      otherSchema.addField(nameField);
      otherSchema.addField(bossField);
      otherSchema.addField(fromField);
      otherSchema.addField(toField);
      otherSchema.addField(cantField);
      otherSchema.addField(edadField);
      otherSchema.addField(ratioField);
      schemaRepository.saveAndFlush(otherSchema);

      Schema   shortSchema = createSchema("SHORT");
      shortSchema.addField(colorField);
      schemaRepository.saveAndFlush(shortSchema);

      DocumentType document   = createDocType( "Document",   docSchema,        null,     true);
      createDocType( "Obligacion", commitmentSchema, document, true);
      createDocType( "Factura",    invoiceSchema,    document, true);
      createDocType( "Pago",       paymentSchema,    document, true);
      createDocType( "Remision",   dispatchSchema,   document, true);
      createDocType( "Recibo",     receiptSchema,    document, true);

      Level level0 = new Level("Sede",     0, sedeSchema);
      Level level1 = new Level("Oficina",  1, officeSchema);
      Level level2 = new Level("Serie",    2, seriesSchema);
      Level level3 = new Level("Subserie", 3, seriesSchema);

      Level levels[]  = { level0, level1, level2, level3};
      return levels;
      
   }//createMeta


   private Metadata createMeta(String name, Type type, String range)
   {
      Metadata meta = new Metadata(name, type, range);
      meta.setTenant(tenant1);
      metadataRepository.saveAndFlush(meta);
      return meta;
   }//createMeta

   private Field createField(String name, Metadata meta, boolean visible, boolean readOnly, boolean required, int sortOrder, int columns)
   {
      Field field = new Field(tenant1, name, meta, visible, readOnly, required, sortOrder, columns);
      fieldRepository.saveAndFlush(field);
      return field;
   }//createField

   private Schema createSchema(String name)
   {
      Schema schema = new Schema(tenant1, name, new TreeSet<>());
      schemaRepository.saveAndFlush(schema);
      return schema;
   }//createSchema


   private DocumentType createDocType(String name, Schema schema, DocumentType parent, boolean requiresContent)
   {
      DocumentType docType = new DocumentType(tenant1, name, schema, parent, requiresContent);
      documentTypeRepository.save(docType);
      return docType;
   }//createDocType




   private UserGroup createUserGroup(Tenant tenant, String name, Integer category, UserGroup owner, LocalDate dateFrom, LocalDate dateTo, boolean locked)
   {
      UserGroup userGroup = new UserGroup();
      userGroup.setTenant(tenant);
      userGroup.setName(name);
      userGroup.setCategory(category);
      userGroup.setOwner(owner);
      userGroup.setFromDate(dateFrom);
      userGroup.setToDate(dateTo);
      userGroup.setLocked(locked);
      userGroupRepository.save(userGroup);
      return userGroup;
   }//createUserGroup




   private Tenant createTenant(TenantRepository tenantRepository, String name)
         throws RepositoryException, UnknownHostException
   {
      Tenant tenant = new Tenant(name);
      tenant.setLocked(false);
      tenant.setAdministrator("admin@vaadin.com");
      LocalDate now = LocalDate.now();
      tenant.setFromDate( now.minusMonths(random.nextInt(36)));
      tenant.setToDate(now.plusYears(random.nextInt(10)));
      tenantRepository.save(tenant);
      Repo.getInstance().initWorkspace(tenant.getWorkspace(), name);
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
   
   /* -----------------------------------------------------------------
    * Código heredado de creación de usuarios para el ejemplo de panadería de Vaadin
    */

   private User createBaker(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      return userRepository.save(createUser("baker@vaadin.com",   "Heidi", "Carter", passwordEncoder.encode("baker"),   Role.BAKER,  false));
   }//createBaker

   private User createBarista(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      return userRepository.save(createUser("barista@vaadin.com", "Malin", "Castro", passwordEncoder.encode("barista"), Role.BARISTA, true));
   }//createBarista

   private User createAdmin(UserRepository userRepository, SingleUserRepository singleUserRepository, PasswordEncoder passwordEncoder)
   {
     Set<com.f.thoth.backend.data.security.Role> roleSet = new TreeSet<>();
     roleSet.add(adminRole);
     Set<UserGroup> groups = new TreeSet<>();
     currentUser = createSingleUser ( tenant1, "admin@vaadin.com", "admin", "Lopez", "Alvaro", groups,
                        new Integer(5), LocalDate.now(), LocalDate.now().plusYears(5), roleSet, true);

     User admin = userRepository.save(createUser("admin@vaadin.com", "Lopez", "Alvaro", passwordEncoder.encode("admin"), "admin", true));
     return admin;

   }//createAdmin


   private com.f.thoth.backend.data.security.User createSingleUser (
         Tenant tenant, String email, String password, String lastName, String name, Set<UserGroup> groups,
         Integer userCategory, LocalDate fromDate, LocalDate toDate, Set<com.f.thoth.backend.data.security.Role>roles, boolean locked
         )
   {
      com.f.thoth.backend.data.security.User user = new com.f.thoth.backend.data.security.User();
      user.setTenant(tenant1);
      user.setEmail(email);
      user.buildCode();
      user.setPasswordHash(passwordEncoder.encode(password));
      user.setCategory(userCategory);
      user.setLastName(lastName);
      user.setGroups(groups);
      user.setName(name);
      user.setFromDate(fromDate);
      user.setToDate(toDate);
      user.setRoles(roles);
      user.setLocked(locked);

      ObjectToProtect userObject = new ObjectToProtect();
      userObject.setRoleOwner(adminRole);
      user.setObjectToProtect(userObject);

      singleUserRepository.saveAndFlush(user);
      return user;
   }//createSingleUser


   private void createDeletableUsers(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      userRepository.save( createUser("peter@vaadin.com", "Peter", "Bush", passwordEncoder.encode("peter"), Role.BARISTA, false));
      userRepository.save( createUser("mary@vaadin.com",  "Mary",  "Ocon", passwordEncoder.encode("mary"),  Role.BAKER,   true ));
   }//createDeletableUsers


   private User createUser(String email, String firstName, String lastName, String passwordHash, String role, boolean locked)
   {
      User user = new User();
      user.setEmail       (email);
      user.setFirstName   (firstName);
      user.setLastName    (lastName);
      user.setPasswordHash(passwordHash);
      user.setRole        (role);
      user.setLocked      (locked);
      return user;
   }//createUser

}//DataGenerator
