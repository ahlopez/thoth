package com.f.thoth.app;

import java.net.UnknownHostException;
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
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
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
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.Type;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.UserGroup;
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
import com.f.thoth.backend.repositories.TenantRepository;
import com.f.thoth.backend.repositories.UserGroupRepository;
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
   private RetentionRepository           retentionRepository;
   private UserGroupRepository           userGroupRepository;
   private Repository                    repo;
   private Session                       jcrSession;
   private Node                          workspace;

   @Autowired
   public DataGenerator(TenantService tenantService, OrderRepository orderRepository, UserRepository userRepository,
         ProductRepository productRepository, PickupLocationRepository pickupLocationRepository,
         TenantRepository tenantRepository, RoleRepository roleRepository, OperationRepository operationRepository,
         ClassificationRepository claseRepository, MetadataRepository metadataRepository, FieldRepository fieldRepository,
         SchemaRepository schemaRepository, LevelRepository levelRepository, 
         RetentionRepository retentionRepository, UserGroupRepository userGroupRepository,
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
      this.retentionRepository           = retentionRepository;
      this.userGroupRepository           = userGroupRepository;

   }//DataGenerator

   @SuppressWarnings("unused")
   @PostConstruct
   public void loadData() 
   {
      try
      {
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
         
         // ----------- Inicialice el repositorio documental ----------------------
         getLogger().info("Initializing jcr repository");  
         
         repo = initJCRRepo();
         
         getLogger().info("... acquiring a repo session");
         jcrSession = loginToRepo(repo, "admin", "admin");
         
         getLogger().info("... creating default workspace");
         initWorkspace("FCN", tenant1);
         
         // ----------- Respetar este orden para la inicialización de estos default -------------
         getLogger().info("... generating defaults");
         
         Schema.EMPTY.setTenant(tenant1);
         Schema.EMPTY.buildCode();
         schemaRepository.saveAndFlush(Schema.EMPTY);
         
         Level.DEFAULT.setTenant(tenant1);
         Level.DEFAULT.buildCode();
         levelRepository.saveAndFlush(Level.DEFAULT);
         
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
         Field    securityField = createField("Seguridad", claseMeta, true, false, true, 5, 1);

         Metadata intMeta   = createMeta("Entero", Type.INTEGER, " >0; < 100");
         Field    cantField = createField("Cantidad", intMeta, true, false, true, 5, 1);
         Field    edadField = createField("Edad",     intMeta, true, true,  true, 6, 1);

         Metadata decMeta   = createMeta("Decimal", Type.DECIMAL," >= 0.0");
         Field    ratioField= createField("Razon", decMeta, true, false, true, 7, 1);

         Schema  sedeSchema = createSchema("Sede");
         sedeSchema.addField(fromField);
         sedeSchema.addField(toField);
         sedeSchema.addField(colorField);
         sedeSchema.addField(securityField);
         schemaRepository.saveAndFlush(sedeSchema);

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

         Level level0 = new Level("Sede",     0, sedeSchema);
         Level level1 = new Level("Oficina",  1, officeSchema);
         Level level2 = new Level("Serie",    2, seriesSchema);
         Level level3 = new Level("Subserie", 3, seriesSchema);
         
         Level levels[]  = { level0, level1, level2, level3};

         getLogger().info("... generating Operations" );
         OperationGenerator  opGenerator = new OperationGenerator(operationRepository);
         opGenerator.registerOperations(tenant1);

         getLogger().info("... generating Classification classes" );
         ClassificationGenerator classificationGenerator = new ClassificationGenerator(claseRepository, levelRepository, schemaRepository, levels);
         classificationGenerator.registerClasses(tenant1);
         

         getLogger().info("... generating users");
         User baker = createBaker(userRepository, passwordEncoder);
         User barista = createBarista(userRepository, passwordEncoder);
         createAdmin(userRepository, passwordEncoder);
         // A set of products without constrains that can be deleted
         createDeletableUsers(userRepository, passwordEncoder);
         
         getLogger().info("... generating user groups");
         LocalDate now = LocalDate.now();
         LocalDate yearStart =  now.minusDays(now.getDayOfYear());
         LocalDate yearEnd   =  yearStart.plusMonths(12);
         UserGroup g0100 = createUserGroup(tenant1, "Grupo 0100", Constant.DEFAULT_CATEGORY, null,  yearStart, yearEnd, false);
         UserGroup g0110 = createUserGroup(tenant1, "Grupo 0101", Constant.DEFAULT_CATEGORY, g0100, yearStart, yearEnd, false);
         UserGroup g0120 = createUserGroup(tenant1, "Grupo 0102", Constant.DEFAULT_CATEGORY, g0100, yearStart, yearEnd, false);
         UserGroup g0130 = createUserGroup(tenant1, "Grupo 0103", Constant.DEFAULT_CATEGORY, g0100, yearStart, yearEnd, false);
         UserGroup g0200 = createUserGroup(tenant1, "Grupo 0200", Constant.DEFAULT_CATEGORY, null,  yearStart, yearEnd, false);
         UserGroup g0210 = createUserGroup(tenant1, "Grupo 0201", Constant.DEFAULT_CATEGORY, g0200, yearStart, yearEnd, false);

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
         
      } catch (Exception e)
      {
         getLogger().error("*** No pudo inicializar la aplicación. Causa\n"+ e.getMessage());
         System.exit(-1);
      }

   }//loadData
   
   private Repository initJCRRepo()throws UnknownHostException
   {
      Repository repo = initRepo("127.0.0.1", 27017, "evidentia");
      // Gets an in-memory repo
      // repo = new Jcr(new Oak()).createRepository(); 
      return repo;
      
   }//initJCRRepo
   
   private Repository initRepo (String host, final int port, String dbName) throws UnknownHostException
   {   
        String uri = "mongodb://" + host + ":" + port;
        getLogger().info("... "+ uri+ "  db="+ dbName);
        System.setProperty("oak.documentMK.disableLeaseCheck", "true");
        getLogger().info("... get the node store");
        DocumentNodeStore ns = new DocumentMK.Builder().setMongoDB(uri, "evidentia", 16).getNodeStore();
        getLogger().info("... create the Oak repository["+ dbName+ "]");
        Repository repo = new Jcr(new Oak(ns)).createRepository();
        getLogger().info("oak.documentMK.disableLeaseCheck=" + System.getProperty("oak.documentMK.disableLeaseCheck"));
        return repo;
     
   }//initRepo
   
   
   private Session loginToRepo(Repository jcrRepo, String userCode, String passwordHash) throws RepositoryException
   {
      
      if (jcrRepo != null)
      {
         Session session = jcrRepo.login(new SimpleCredentials(userCode, passwordHash.toCharArray()));
         getLogger().info("... acquired session to repo["+ jcrRepo.toString()+ "], user["+ userCode+ "], pwd["+ passwordHash+ "]");
         return session;
      } else
         throw new NullPointerException("Repositorio no inicializado");

      //   jcr spec:    return  Repository.login(Credentials credentials, workspaceName);
   }//loginToRepo
   
   private void initWorkspace(String name, Tenant tenant) throws RepositoryException
   {
      String path = "/"+name;
      if (jcrSession.nodeExists(path))
         return;
      
      Node node = jcrSession.getNode("/");
      workspace = node.addNode(name);
      String workspaceId = workspace.getIdentifier();
      tenant.setWorkspace(workspaceId);
      getLogger().info("... workspace["+ workspaceId+ "]");
      
   }//initWorkspace


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
