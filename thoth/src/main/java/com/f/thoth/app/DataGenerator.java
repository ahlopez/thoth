package com.f.thoth.app;

import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;

import org.springframework.beans.factory.annotation.Autowired;
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
/*
   private static final String[] FILLING = new String[] { "Strawberry", "Chocolate", "Blueberry", "Raspberry", "Vanilla" };

   private static final String[] TYPE = new String[] { "Cake", "Pastry", "Tart", "Muffin", "Biscuit", "Bread", "Bagel",
         "Bun", "Brownie", "Cookie", "Cracker", "Cheese Cake" };

   private static final String[] FIRST_NAME = new String[] { "Olga", "Amanda", "Octavia", "Cristina", "Marta", "Luis",
         "Eduardo", "Alvaro", "Arsenio", "German", "Cecilia", "Silvia", "Angela", "Maria", "Fernando", "Patricio",
         "David", "Lino", "Rafael" };

   private static final String[] LAST_NAME = new String[] { "Biden", "Castro", "Duque", "Lopez", "Perez", "Parias",
         "Umana", "Rueda", "Vergara", "Gonzalez", "Nunez", "Macias", "Gallegos", "Duarte", "Mejia", "Petro",
         "Gutierrez", "Vargas", "Puentes", "Holmes", "Macias", "Ospina", "Mutis", "Cortes", "Noble", "Rodriguez", "Arenas",
         "Trump", "Mogollon", "Samper", "Estrada", "Heredia", "Maldonado", "Reyes" };
*/
   private static com.f.thoth.backend.data.security.Role  adminRole;

   private final Random random = new Random(1L);

   private Tenant                        tenant1, tenant2;
   private TenantService                 tenantService;
   private TenantRepository              tenantRepository;
   private RoleRepository                roleRepository;
   private UserRepository                userRepository;
//   private OrderRepository               orderRepository;
//   private ProductRepository             productRepository;
//   private PickupLocationRepository      pickupLocationRepository;
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
//      this.orderRepository               = orderRepository;
//      this.productRepository             = productRepository;
//      this.pickupLocationRepository      = pickupLocationRepository;
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
         User baker   = createBaker(userRepository, passwordEncoder);
         User barista = createBarista(userRepository, passwordEncoder);


         // -----------------  Inicialice el árbol de clasificacion documental -----------------------------
         getLogger().info("... generating classification classes" );
         ClassificationGenerator classificationGenerator =
             new ClassificationGenerator(currentUser, claseRepository, levelRepository, schemaRepository, numerator, levels);
         classificationGenerator.registerClasses(tenant1);


         // A set of products without constrains that can be deleted
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
         ExpedienteGenerator  expedienteGenerator =
               new ExpedienteGenerator( tenant1, currentUser,
                     claseRepository, expedienteIndexRepository,
                     expedienteGroupRepository, documentTypeRepository,
                     volumeRepository, volumeInstanceRepository, schemaRepository
                     );

         int nExpedientes = expedienteGenerator.registerExpedientes(tenant1);
         getLogger().info("    >>> End expedientes generation. "+ nExpedientes+ " expedientes generated");
/*
         getLogger().info("... generating products");
         // A set of products that will be used for creating orders.
         Supplier<Product> productSupplier = createProducts(productRepository, 8);
         // A set of products without relationships that can be deleted
         createProducts(productRepository, 4);

         getLogger().info("... generating pickup locations");
         Supplier<PickupLocation> pickupLocationSupplier = createPickupLocations(pickupLocationRepository);

         getLogger().info("... generating orders");
         createOrders(orderRepository, productSupplier, pickupLocationSupplier, barista, baker);
*/
         getLogger().info("Generated evidentia test data");

      } catch (Exception e)
      {
         getLogger().error("*** No pudo inicializar la aplicacion. Causa\n"+ e.getMessage());
         System.exit(-1);
      }

   }//loadData


   private void createTenants (TenantService tenantService)
         throws RepositoryException, UnknownHostException
   {
      tenant1 = createTenant(tenantRepository, "FCN", "FCN");
      tenant2 = createTenant(tenantRepository,"SEI", "SEI");
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
      Field    nameField       = createField("Nombre",         nameMeta, true, false, true, 1, 2);
      Field    bossField       = createField("Jefe",           nameMeta, true, false, true, 2, 2);
      Field    commitmentField = createField("Obligacion",     nameMeta, true, false, true, 1, 2);
      Field    dispatchField   = createField("Despacho",       nameMeta, true, true,  true, 2, 1);
      Field    idField         = createField("Identificacion", nameMeta, true, false, true, 1, 1);
      Field    authorField     = createField("Autor",          nameMeta, true, false, true, 2, 1);
      Field    conceptField    = createField("Concepto",       nameMeta, false,false, true, 2, 1);
      Field    remiteField     = createField("Remitente",      nameMeta, true, false, true, 3, 2);

      Metadata dateMeta  = createMeta ("Fecha", Type.DATETIME, "not null");
      Field    fromField = createField("Desde",      dateMeta, true, false, true, 3, 2);
      Field    toField   = createField("Hasta",      dateMeta, true, false, true, 4, 2);
      Field    dateField = createField("Fecha",      dateMeta, true, true,  true, 3, 2);
      Field    dueDate   = createField("APagarEn",   dateMeta, true, true,  true, 2, 2);
      Field    paidDate  = createField("PagadoEn",   dateMeta, true, false, true, 3, 2);

      Metadata enumMeta   = createMeta ("Color",    Type.ENUM, "Verde;Rojo;Azul;Magenta;Cyan");
      Field    colorField = createField("Colores",   enumMeta, true, false, true, 5, 1);

      Metadata claseMeta     = createMeta ("Security", Type.ENUM, "Restringido;Confidencial;Interno;Público");
      Field    securityField = createField("Seguridad", claseMeta, true, false, true, 5, 1);

      Metadata intMeta   = createMeta ("Entero", Type.INTEGER, " >0; < 100");
      Field    cantField = createField("Cantidad", intMeta, true, false, true, 5, 1);
      Field    edadField = createField("Edad",     intMeta, true, true,  true, 6, 1);

      Metadata decMeta   = createMeta ("Decimal", Type.DECIMAL," >= 0.0");
      Field    ratioField= createField("Razon", decMeta, true, false, true, 7, 1);
      Field    valueField= createField("Valor", decMeta, true, false, true, 4, 1);

      Schema   docSchema =  createSchema("Documento");
      docSchema.addField(idField);
      docSchema.addField(authorField);
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




   private Tenant createTenant(TenantRepository tenantRepository, String name, String code)
         throws RepositoryException, UnknownHostException
   {
      Tenant tenant = new Tenant(name, code);
      tenant.setLocked(false);
      tenant.setAdministrator("admin@vaadin.com");
      LocalDate now = LocalDate.now();
      tenant.setFromDate( now.minusMonths(random.nextInt(36)));
      tenant.setToDate(now.plusYears(random.nextInt(10)));
      tenantRepository.save(tenant);
      Repo.getInstance().initWorkspace(tenant.getWorkspace(), name, code);
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

/*
   private void fillCustomer(Customer customer)
   {
      String first = getRandom(FIRST_NAME);
      String last = getRandom(LAST_NAME);
      customer.setFullName(first + " " + last);
      customer.setPhoneNumber(getRandomPhone());
      if (random.nextInt(10) == 0) {
         customer.setDetails("Very important customer");
      }
   }//fillCustomer

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
         Integer userCategory, LocalDate fromDate, LocalDate toDate, Set<com.f.thoth.backend.data.security.Role>roles, boolean locked)
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
